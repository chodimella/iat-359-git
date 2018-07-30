package com.example.nikhilchodimella.waitaminute;

import android.content.DialogInterface;
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
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import static android.os.Build.VERSION_CODES.M;

public class ToDoMain extends AppCompatActivity {

    TodoDBHelper dbHelper;
    ArrayAdapter<String> adapter;
    ListView lstItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_main);

        dbHelper = new TodoDBHelper(this);
        lstItems = (ListView) findViewById(R.id.todoItems);

        showItemList();
    }

    //Shows list of all items
    private void showItemList() {
        ArrayList<String> itemList = dbHelper.getTodoList();
        if (adapter == null) {
            adapter = new ArrayAdapter<String>(this, R.layout.todo_row, R.id.itemName, itemList);
            lstItems.setAdapter(adapter);
        }
        else {
            adapter.clear();
            adapter.addAll(itemList);
            adapter.notifyDataSetChanged();
        }
    }

    //Makes menu item visible for users to add new To Do Items
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.todo_menu, menu);

        Drawable icon = menu.getItem(0).getIcon();
        icon.mutate();

        if(Build.VERSION.SDK_INT >= M) {
            icon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        }

        return super.onCreateOptionsMenu(menu);
    }

    //Opens dialogue box for users to enter new To Do item
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()) {
            case R.id.action_add_item:
                final EditText itemEditText = new EditText(this);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Add New Item")
                        .setMessage("What's next?")
                        .setView(itemEditText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String item = String.valueOf(itemEditText.getText());
                                dbHelper.insertNewItem(item);
                                showItemList();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Function that deletes To Do item from the database
    public void deleteItem(View view) {
        View parent  = (View) view.getParent();
        TextView itemTextView = (TextView) findViewById(R.id.itemName);
        String item = String.valueOf(itemTextView.getText());
        dbHelper.deleteItem(item);
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
                        String item = String.valueOf(itemEditText.getText());
                        dbHelper.insertNewItem(item);
                        showItemList();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }
}
