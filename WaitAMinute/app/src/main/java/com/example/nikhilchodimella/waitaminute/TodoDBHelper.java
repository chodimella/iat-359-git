package com.example.nikhilchodimella.waitaminute;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class TodoDBHelper extends SQLiteOpenHelper {

    //SQLite helper class for To Do List database

    //Constants for db name and version
    private static final String DB_NAME = "ToDo.db";
    private static final int DB_VERSION = 1;

    //Constants for identifying table and columns
    public static final String TABLE_NAME = "ToDoItems";
    public static final String COLUMN_NAME = "Items";

    public TodoDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = String.format("CREATE TABLE %s (ID INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL);", TABLE_NAME, COLUMN_NAME);
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = String.format("DELETE TABLE IF EXISTS %s", TABLE_NAME);
        db.execSQL(query);
        onCreate(db);
    }

    public void insertNewItem(String item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, item);
        db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void deleteItem(String item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_NAME + " = ?", new String[]{item});
        db.close();
    }

    public ArrayList<String> getTodoList() {
        ArrayList<String> todoList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_NAME, new String[]{COLUMN_NAME}, null, null, null, null, null);

        while (c.moveToNext()) {
            int index = c.getColumnIndex(COLUMN_NAME);
            todoList.add(c.getString(index));
        }

        c.close();
        db.close();

        return todoList;
    }


}
