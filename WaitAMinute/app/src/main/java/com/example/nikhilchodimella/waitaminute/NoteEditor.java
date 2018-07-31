package com.example.nikhilchodimella.waitaminute;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class NoteEditor extends AppCompatActivity {

    private String action;
    private EditText editor;

    private String noteFilter;
    private String oldText;

    private SensorManager mSensorManager;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;

    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta; // perform low-cut filter

            if(mAccel > 15) {
                deleteNote();
                Toast.makeText(getApplicationContext(), R.string.note_deleted, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        editor = (EditText) findViewById(R.id.editText);

        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(NotesProvider.CONTENT_ITEM_TYPE);

        //Checks to see if a new note is being opened or an existing note. If existing, queries the database and retrieves the correct note
        if(uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_note));
        } else {
            action = Intent.ACTION_EDIT;
            noteFilter = NotesDBHelper.NOTE_ID + "=" + uri.getLastPathSegment();

            Cursor cursor = getContentResolver().query(uri, NotesDBHelper.ALL_COLUMNS, noteFilter, null, null);
            cursor.moveToFirst();
            oldText = cursor.getString(cursor.getColumnIndex(NotesDBHelper.NOTE_TEXT));
            editor.setText(oldText);
            editor.requestFocus();
        }

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

    //Adds delete icon to the toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(action.equals(Intent.ACTION_EDIT)) {
            getMenuInflater().inflate(R.menu.note_editor_menu, menu);
        }
        return true;
    }

    //Adds delete functionality to the delete icon and 'back' arrow
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case android.R.id.home:
                finishEditing();
                break;

            case R.id.action_delete:
                deleteNote();
                break;

            case R.id.action_add_picture:
                //function to add picture;
                break;
        }

        return true;
    }

    //Function that deletes a specific note from the database using functions from the content provider, NotesProvider
    private void deleteNote() {
        getContentResolver().delete(NotesProvider.CONTENT_URI, noteFilter, null);
        Toast.makeText(this, R.string.note_deleted, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    //Function that does several things. If user starts a new note but closes without entering anything, it does nothing with the note.
    //If user opens a new note, types something and closes, it inserts and saves the note.
    //If user opens an existing note and deletes all the text, the note is deleted.
    //If user opens an existing note and the text is modified, the updated note is pushed into the database.
    private void finishEditing() {
        String newText = editor.getText().toString().trim();

        switch (action) {
            case Intent.ACTION_INSERT:
                //Nothing happens if user does nothing with new note
                if(newText.length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    insertNote(newText); //New note is pushed into database
                }
                break;

            case Intent.ACTION_EDIT:
                //If existing note now has no text, note is deleted
                if(newText.length() == 0) {
                    deleteNote();
                } else if(oldText.equals(newText)) {
                    setResult(RESULT_CANCELED); //If existing note is completely unchanged, nothing happens
                } else {
                    updateNote(newText); //Note is updated and pushed into the database if the text has been changed
                }
        }

        finish();
    }

    //Pushes updated text into content provider NotesProvider to update note in the database
    private void updateNote(String noteText) {
        ContentValues values = new ContentValues();
        values.put(NotesDBHelper.NOTE_TEXT, noteText);
        getContentResolver().update(NotesProvider.CONTENT_URI, values, noteFilter, null);
        Toast.makeText(this, R.string.note_updated, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    //Pushes new note through content provider NotesProvider to insert a new note in the database
    private void insertNote(String noteText) {
        ContentValues values = new ContentValues();
        values.put(NotesDBHelper.NOTE_TEXT, noteText);
        getContentResolver().insert(NotesProvider.CONTENT_URI, values);
        setResult(RESULT_OK);
    }

    @Override
    public void onBackPressed() {
        finishEditing();
    }
}
