package com.example.nikhilchodimella.waitaminute;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class NoteView extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EDITOR_REQUEST_CODE = 420;
    private CursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_view);

        cursorAdapter = new NotesCursorAdapter(this, null, 0);

        ListView list = findViewById(android.R.id.list);
        list.setAdapter(cursorAdapter);

        //Listener that opens the correct note in the list
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NoteView.this, NoteEditor.class);
                Uri uri = Uri.parse(NotesProvider.CONTENT_URI + "/" + id);
                intent.putExtra(NotesProvider.CONTENT_ITEM_TYPE, uri);
                startActivityForResult(intent, EDITOR_REQUEST_CODE);
            }
        });

        getLoaderManager().initLoader(0, null, this);
    }

    //Mainly a debugging function to directly enter a note into the database
    private void insertNote(String noteText) {
        ContentValues values = new ContentValues();
        values.put(NotesDBHelper.NOTE_TEXT, noteText);
        Uri noteUri = getContentResolver().insert(NotesProvider.CONTENT_URI, values);
        Log.d("NoteView", "Inserted note " + noteUri.getLastPathSegment());
    }

    //Makes menu options visible
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_menu, menu);
        return true;
    }

    //Provides options to add sample notes and to delete ALL notes
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_create_sample:
                insertSampleData();
                break;

            case R.id.action_delete_all:
                deleteAllNotes();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    //Opens a dialog box that asks user again if they want to delete all notes. Completely clears the database
    private void deleteAllNotes() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            //Insert Data management code here
                            getContentResolver().delete(NotesProvider.CONTENT_URI, null, null);
                            restartLoader();

                            Toast.makeText(NoteView.this,
                                    getString(R.string.all_deleted),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
    }

    //Test function for debugging reasons
    private void insertSampleData() {
        insertNote("Simple note");
        insertNote("Multi-line\nnote");
        insertNote("Very long note with a lot of text that exceeds the width of the screen");
        restartLoader();
    }

    //Method that tells the loader to re read the database after any changes are made
    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    //Loads up the database when the app first starts
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, NotesProvider.CONTENT_URI, null, null, null, null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    //Callback method when the database is empty
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    //Function that opens new activity for a new note
    public void openEditorForNewNote(View view) {
        Intent intent = new Intent(this, NoteEditor.class);
        startActivityForResult(intent, EDITOR_REQUEST_CODE);
    }

    //If any changes are made in NoteEditor activity, restartLoader is called to refresh the database and list of notes
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDITOR_REQUEST_CODE && resultCode == RESULT_OK) {
            restartLoader();
        }
    }
}
