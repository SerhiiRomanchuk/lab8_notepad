package io.first.lab7_notepad;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private NoteDatabaseManager noteDatabaseManager;
    private List<Note> notes;
    private Note selected;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        noteDatabaseManager = new NoteDatabaseManager(this);
        noteDatabaseManager.open();

        Button button = findViewById(R.id.saveNote);
        button.setOnClickListener(view -> {
            if(selected!=null) {
                EditText editText = findViewById(R.id.input);
                String content = editText.getText().toString();
                noteDatabaseManager.update(selected.getId(),selected.getTitle(), content);
                Toast.makeText(this,"Збережено", Toast.LENGTH_LONG).show();
                selected.setContent(content);
            }
        });
        refreshSpinner();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void refreshSpinner() {
        Spinner spinner = findViewById(R.id.spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String title = parent.getItemAtPosition(position).toString();
                notes.stream().filter(note -> note.getTitle().equals(title)).findFirst().ifPresent(note -> {
                    selected = note;
                    EditText editText = findViewById(R.id.input);
                    editText.setText(note.getContent() == null ? "" : note.getContent());
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        notes = noteDatabaseManager.getAll();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.support_simple_spinner_dropdown_item,
                notes.stream().map(Note::getTitle).collect(Collectors.toList()));
        spinner.setAdapter(adapter);
    }

    public void onMenuBackClicked(MenuItem menuItem) {
        EditText editText = findViewById(R.id.input);
        String input = editText.getText().toString();
        if (input.length() > 0) {
            input = input.substring(0, input.length() - 1);
            editText.setText(input);
        }
    }

    public void onMenuCancelClicked(MenuItem menuItem) {
        if (selected != null) {
            EditText editText = findViewById(R.id.input);
            editText.setText(selected.getContent());
        }
    }

    public void onMenuAboutClicked(MenuItem menuItem) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Про автора").setMessage("Романчук Сергій, ІПЗ-17-1")
                .create().show();
    }

    public void onMenuInstructionClicked(MenuItem menuItem) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Інструкція").setMessage("Для створення нового запису оберіть в меню відповідний розділ." +
                " Після створення, оберіть потрібний запис зі списку зверху екрану. Введіть необхідну інформацію у поле," +
                " та натисніть збереги.")
                .create().show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onMenuNewNoteClicked(MenuItem menuItem) {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Новий запис").setMessage("Дайте назву новому запису").setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = input.getText().toString();
                noteDatabaseManager.insert(title);
                refreshSpinner();
            }
        });
        builder.create().show();
    }

    public void onDeleteClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Видалення").setMessage("Натисніть ТАК, якщо бажаєте видалити запис.");
        builder.setPositiveButton("ТАК", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (selected != null) {
                    noteDatabaseManager.delete(selected.getId());
                    refreshSpinner();
                }
            }
        });
        builder.create().show();
    }
}
