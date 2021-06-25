package com.xavierjonesco.keepnotes;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;

public class Edit extends AppCompatActivity {
    Toolbar toolbar;
    EditText nTitle, nContent;
    Calendar c;
    String todaysDate;
    String currentTime;
    long nId;
    Note note;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        nId = i.getLongExtra("ID", 0);
        NoteDatabase db = new NoteDatabase(this);
        note = db.getNote(nId);
        getSupportActionBar().setTitle(note.getTitle());

        final String title = note.getTitle();
        String content = note.getContent();
        nTitle = findViewById(R.id.noteTitle);
        nContent = findViewById(R.id.noteDetails);
        nTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                getSupportActionBar().setTitle("Editing: " + note.getTitle());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    getSupportActionBar().setTitle("Editing: " + s);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        String noteTitle;
        String message;

        if (!title.equals("Untitled")) {
            nTitle.setText(title);
        } else {
            nTitle.setText("");
        }
        nContent.setText(content);
        try{

            message= i.getStringExtra("message");
            noteTitle= i.getStringExtra("title");
            if(!(message.equals(""))||!(noteTitle.equals(""))){
                nTitle.setText(noteTitle);
                nContent.setText(message);
            }

        }catch (NullPointerException e){

        }
        // set current date and time
        c = Calendar.getInstance();
        todaysDate = (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.YEAR);
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
        if (item.getItemId() == R.id.save) {
            Note note = new Note(nId, nTitle.getText().toString(), nContent.getText().toString(), todaysDate, currentTime);
            NoteDatabase sDB = new NoteDatabase(getApplicationContext());
            if (!note.getContent().trim().equals("")) {
                if (!note.getTitle().trim().equals("")) {
                    long id = sDB.editNote(note);
                    Intent i = new Intent(this, Details.class);
                    i.putExtra("ID", id);
                    startActivity(i);
                    // goToMain();

                } else {
                    Toast.makeText(this, "Please enter a title.", Toast.LENGTH_SHORT).show();

                }
            } else {
                Toast.makeText(this, "You cannot create empty notes.", Toast.LENGTH_SHORT).show();
            }


        } else if (item.getItemId() == R.id.delete) {
            NoteDatabase db = new NoteDatabase(this);
            db.deleteNote(note.getId());
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    private void goToMain() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        goToMain();
    }
}
