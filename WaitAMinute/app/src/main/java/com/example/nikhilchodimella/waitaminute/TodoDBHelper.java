package com.example.nikhilchodimella.waitaminute;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//SQLite starter for To Do List database
public class TodoDBHelper extends SQLiteOpenHelper {

    public TodoDBHelper(Context context) {
        super(context, ToDoContract.DB_NAME, null, ToDoContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + ToDoContract.TaskEntry.TABLE + " ( " +
                ToDoContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ToDoContract.TaskEntry.COL_TASK_TITLE + " TEXT NOT NULL);";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ToDoContract.TaskEntry.TABLE);
        onCreate(db);
    }
}
