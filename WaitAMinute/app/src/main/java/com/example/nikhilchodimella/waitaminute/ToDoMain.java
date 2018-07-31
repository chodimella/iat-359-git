package com.example.nikhilchodimella.waitaminute;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import static android.os.Build.VERSION_CODES.M;

public class ToDoMain extends AppCompatActivity {

    private static final String TAG = "ToDoList";
    private TodoDBHelper dbHelper;
    private ListView listItems;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_main);

        dbHelper = new TodoDBHelper(this);
        listItems = (ListView) findViewById(R.id.todoItems);

        updateUI();
    }

    public void addNewToDoItem(View view) {
        final EditText itemEditText = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add New Item")
                .setMessage("What's next?")
                .setView(itemEditText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String task = String.valueOf(itemEditText.getText());
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put(ToDoContract.TaskEntry.COL_TASK_TITLE, task);
                        db.insertWithOnConflict(ToDoContract.TaskEntry.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                        db.close();
                        updateUI();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    public void deleteItem(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.itemName);
        String task = String.valueOf(taskTextView.getText());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(ToDoContract.TaskEntry.TABLE, ToDoContract.TaskEntry.COL_TASK_TITLE + " = ?", new String[]{task});
        db.close();
        updateUI();
    }

    private void updateUI() {
        ArrayList<String> taskList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ToDoContract.TaskEntry.TABLE,
                new String[]{ToDoContract.TaskEntry._ID, ToDoContract.TaskEntry.COL_TASK_TITLE}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int i = cursor.getColumnIndex(ToDoContract.TaskEntry.COL_TASK_TITLE);
            taskList.add(cursor.getString(i));
        }

        if (mAdapter == null) {
            mAdapter = new ArrayAdapter<>(this, R.layout.todo_row, R.id.itemName, taskList);
            listItems.setAdapter(mAdapter);
        } else {
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
        }

        cursor.close();
        db.close();
    }
}
