package io.first.lab7_notepad;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import static io.first.lab7_notepad.NoteDatabase.NoteDatabaseStructure.COLUMN_NAME_CONTENT;
import static io.first.lab7_notepad.NoteDatabase.NoteDatabaseStructure.COLUMN_NAME_TITLE;
import static io.first.lab7_notepad.NoteDatabase.NoteDatabaseStructure.TABLE_NAME;

public class NoteDatabaseManager {

    private NoteDatabase noteDatabase;

    private final Context context;

    private SQLiteDatabase database;

    public NoteDatabaseManager(Context context) {
        this.context = context;
    }

    public NoteDatabaseManager open() throws SQLException {
        noteDatabase = new NoteDatabase(context);
        database = noteDatabase.getWritableDatabase();
        return this;
    }

    public void close() {
        noteDatabase.close();
    }

    public void insert(String title, String content) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(COLUMN_NAME_TITLE, title);
        contentValue.put(COLUMN_NAME_CONTENT, content);
        database.insert(TABLE_NAME, null, contentValue);
    }

    public void insert(String title) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(COLUMN_NAME_TITLE, title);
        contentValue.put(COLUMN_NAME_CONTENT, "");
        database.insert(TABLE_NAME, null, contentValue);
    }

    public Cursor fetch() {
        String[] columns = new String[]{NoteDatabase.NoteDatabaseStructure._ID, COLUMN_NAME_TITLE, COLUMN_NAME_CONTENT};
        Cursor cursor = database.query(TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public List<Note> getAll() {
        Cursor cursor = fetch();
        cursor.moveToFirst();
        List<Note> notes = new ArrayList<>();
        while (cursor.moveToNext()) {
            Note note = new Note(
                    cursor.getLong(cursor.getColumnIndex(NoteDatabase.NoteDatabaseStructure._ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TITLE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CONTENT)));
            notes.add(note);
        }
        return notes;
    }

    public int update(long _id, String title, String content) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_TITLE, title);
        contentValues.put(COLUMN_NAME_CONTENT, content);
        int i = database.update(TABLE_NAME, contentValues, NoteDatabase.NoteDatabaseStructure._ID + " = " + _id, null);
        return i;
    }

    public void delete(long _id) {
        database.delete(TABLE_NAME, NoteDatabase.NoteDatabaseStructure._ID + "=" + _id, null);
    }
}
