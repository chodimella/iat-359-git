package com.example.nikhilchodimella.waitaminute;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Home extends AppCompatActivity {

    //HOME SCREEN. Currently very rough with three buttons taking users to Notes, To Do List
    //and a settings activity not yet implemented. Possible ideas include either user preferences
    //and/or a login screen to meet shared preferences requirement.

    private Button noteButton;
    private Button todoButton;
    private Button settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Button implemented with listener to open Notes activity
        noteButton = (Button) findViewById(R.id.noteButton);
        noteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent note = new Intent(Home.this, NoteView.class);
                Home.this.startActivity(note);
            }
        });

        //Button implemented with listener to open To-do list activity
        todoButton = (Button) findViewById(R.id.todoButton);
        todoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent todo = new Intent(Home.this, ToDoMain.class);
                Home.this.startActivity(todo);
            }
        });

        //Button implemented with listener to open settings activity (to be implemented for final version)
        settingsButton = (Button) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settings = new Intent(Home.this, Settings.class);
                Home.this.startActivity(settings);
            }
        });

    }
}
