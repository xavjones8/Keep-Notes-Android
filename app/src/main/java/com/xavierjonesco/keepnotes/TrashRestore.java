package com.xavierjonesco.keepnotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class TrashRestore extends AppCompatActivity {

    TextView text;
    NoteDatabase db;
    TrashDatabase tb;
    Note note;
    Long id;
    String layout;
    List<Note> notes;
    List<Note> trash;
    MenuItem restore;
    MenuItem edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash_restore);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //GEts intent that sends note id to show
        Intent i = getIntent();
        id = i.getLongExtra("ID", 0);
        tb = new TrashDatabase(this);
        db = new NoteDatabase(this);
        trash = tb.getAllNotes();
        note = tb.getNote(id);
            //sets toolbar title and  edit text contents
        text = findViewById(R.id.noteDesc);
        getSupportActionBar().setTitle(note.getTitle().trim());
        text.setText(note.getContent());
        text.setMovementMethod(new ScrollingMovementMethod());
        text.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent i = new Intent(getApplicationContext(), Edit.class);
                i.putExtra("ID", id);
                startActivity(i);
                return true;
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.restore_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_restore) {
            //Adds note to the Main database
            db.addNote(note);
            tb.deleteNote(note.getId());
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }else if(item.getItemId() == R.id.delete){
            tb.deleteNote(note.getId());
            Intent i = new Intent(this, TrashActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
       // startActivity(new Intent(getApplicationContext(), MainActivity.class));
        super.onBackPressed();
    }
}