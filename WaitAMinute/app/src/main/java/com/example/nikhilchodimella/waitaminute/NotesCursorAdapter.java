package com.example.nikhilchodimella.waitaminute;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class NotesCursorAdapter extends CursorAdapter{

    //Custom cursor adapter to ensure that text is properly truncated when displayed as a list

    public NotesCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.note_list_item, parent, false);
    }

    //Checks line length of the note and adjust it accordingly as a preview if too long
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String noteText = cursor.getString(cursor.getColumnIndex(NotesDBHelper.NOTE_TEXT));

        int pos = noteText.indexOf(10);
        if(pos != -1) {
            noteText = noteText.substring(0, pos) + "...";
        }

        TextView tv = (TextView) view.findViewById(R.id.tvNote);
        tv.setText(noteText);
    }
}
