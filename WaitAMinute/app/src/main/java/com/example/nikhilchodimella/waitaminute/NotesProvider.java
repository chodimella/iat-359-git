package com.example.nikhilchodimella.waitaminute;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class NotesProvider extends ContentProvider {

    //Content provider class that updates, deletes, queries and establishes the database of notes

    private static final String AUTHORITY = "com.example.nikhilchodimella.waitaminute.notesprovider";
    private static final String BASE_PATH = "notes";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    // Constant to identify the requested operation
    private static final int NOTES = 1;
    private static final int NOTES_ID = 2;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final String CONTENT_ITEM_TYPE = "Note";

    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, NOTES);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", NOTES_ID);
    }

    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {

        NotesDBHelper helper = new NotesDBHelper(getContext());
        database = helper.getWritableDatabase();
        return true;
    }

    //Retrieves String parameter selection from the database
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (uriMatcher.match(uri) == NOTES_ID) {
            selection = NotesDBHelper.NOTE_ID + "=" + uri.getLastPathSegment();
        }

        return database.query(NotesDBHelper.TABLE_NOTES, NotesDBHelper.ALL_COLUMNS, selection, null, null, null, NotesDBHelper.NOTE_CREATED + " DESC");
    }

    //Does nothing here
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    //Inserts new note in to the database
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = database.insert(NotesDBHelper.TABLE_NOTES, null, values);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    //Deletes String parameter selection from the database
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return database.delete(NotesDBHelper.TABLE_NOTES, selection, selectionArgs);
    }

    //Updates String parameter selection in the database
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return database.update(NotesDBHelper.TABLE_NOTES, values, selection, selectionArgs);
    }
}
