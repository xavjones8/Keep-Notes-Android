package com.xavierjonesco.keepnotes;


import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;


import java.util.Calendar;

public class AddNote extends AppCompatActivity  {
    Toolbar toolbar;
    EditText noteTitle, noteDetails;
    Calendar c;
    String todaysDate;
    String currentTime;
    FrameLayout fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Note");
        fragment = findViewById(R.id.frag_container);
        noteDetails = findViewById(R.id.noteDetails);
        noteTitle = findViewById(R.id.noteTitle);
        Intent i = getIntent();
        try{
            String title = i.getStringExtra("title");
            String details = i.getStringExtra("message");
            noteTitle.setText(title);
            noteDetails.setText(details);
        }catch(NullPointerException e){

        }
        noteDetails.requestFocus();
        noteTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    getSupportActionBar().setTitle(s);
                }else{
                    getSupportActionBar().setTitle("New Note");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // set current date and time
        c = Calendar.getInstance();
        todaysDate =  (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.DAY_OF_MONTH)+ "/" +c.get(Calendar.YEAR);
        currentTime = pad(c.get(Calendar.HOUR)) + ":" + pad(c.get(Calendar.MINUTE));
    }

    private String pad(int time) {
        if (time < 10)
            return "0" + time;
        return String.valueOf(time);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save ) {
            Note note = new Note(noteTitle.getText().toString(), noteDetails.getText().toString(), todaysDate, currentTime);
            NoteDatabase sDB = new NoteDatabase(this);
            if(!note.getContent().trim().equals("")){
                if(!note.getTitle().trim().equals("")) {
                    sDB.addNote(note);
                }else{
                    Note note2 = new Note("Untitled" , noteDetails.getText().toString(), todaysDate, currentTime);
                    sDB.addNote(note2);
                }
                goToMain();
            }else{
                Toast.makeText(this,"You cannot create empty notes.",Toast.LENGTH_SHORT).show();
            }

        }
        if (item.getItemId() == R.id.delete) {
            onBackPressed();

        }

        return super.onOptionsItemSelected(item);
    }

    private void goToMain() {
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        goToMain();
    }


}

