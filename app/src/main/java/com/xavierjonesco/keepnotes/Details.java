package com.xavierjonesco.keepnotes;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class Details extends AppCompatActivity {
    TextView text;
    NoteDatabase db;
    TrashDatabase tb;
    Note note;
    Long id;
    String layout;
    List<Note> notes;
    List<Note> trash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //GEts intent that sends note id to show
        Intent i = getIntent();
        id = i.getLongExtra("ID", 0);
        db = new NoteDatabase(this);
        notes = db.getAllNotes();
        note = db.getNote(id);



        //sets toolbar title and  edit text contents
        text = findViewById(R.id.noteDesc);
        getSupportActionBar().setTitle(note.getTitle().trim());
        text.setText(note.getContent());
       // text.setMovementMethod(new ScrollingMovementMethod());
        text.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Edit.class);
                i.putExtra("ID", id);
                startActivity(i);
            }
        });
        /*
        text.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

            }
        });
        */

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.edit) {
            //send user to edit activity
            Intent i = new Intent(this, Edit.class);
            i.putExtra("ID", id);
            startActivity(i);
        }else if(item.getItemId() == R.id.delete){
            NoteDatabase db = new NoteDatabase(getApplicationContext());
            TrashDatabase tb = new TrashDatabase(getApplicationContext());
            db.deleteNote(note.getId());
            tb.addNote(note);
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}
