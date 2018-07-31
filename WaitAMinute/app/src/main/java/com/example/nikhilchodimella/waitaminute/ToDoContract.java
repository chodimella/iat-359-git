package com.example.nikhilchodimella.waitaminute;

import android.provider.BaseColumns;

public class ToDoContract {
    public static final String DB_NAME = "ToDo.db";
    public static final int DB_VERSION = 1;

    public class TaskEntry implements BaseColumns {
        public static final String TABLE = "ToDoItems";
        public static final String COL_TASK_TITLE = "Items";
    }
}