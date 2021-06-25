package com.xavierjonesco.keepnotes;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.content.ActivityNotFoundException;
import android.view.MenuItem;
import android.speech.RecognizerIntent;
import java.util.Locale;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MaterialSearchBar.OnSearchActionListener {
    //TODO Add new app icon + identity.
    //TODO Add haptics
    Toolbar toolbar;
    private MaterialSearchBar searchBar;
    RecyclerView recyclerview;
    List<Note> notes;
    Adapter adapter;
    SearchView look;
    private final int REQ_CODE = 100;
    MenuItem back;
    MenuItem can;
    //Handles Swipes n Stuff
    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            NoteDatabase db = new NoteDatabase(getApplicationContext());
            TrashDatabase tb = new TrashDatabase(getApplicationContext());
            Note note = notes.get(viewHolder.getAdapterPosition());
            db.deleteNote(note.getId());
            tb.addNote(note);
            notes.remove(viewHolder.getAdapterPosition());
            if (notes.size() != 0) {
                adapter.notifyDataSetChanged();
            } else {
                recyclerview.setVisibility(recyclerview.GONE);
                TextView t = findViewById(R.id.noNotes);
                t.setVisibility(t.VISIBLE);
                t.setTextAlignment(t.TEXT_ALIGNMENT_CENTER);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle("All Notes");
        setSupportActionBar(toolbar);
        NoteDatabase db = new NoteDatabase(this);
        notes = db.getAllNotes();

        recyclerview = findViewById(R.id.listOfNotes);

        //Handles Search
        searchBar = findViewById(R.id.searchBar);
        searchBar.setHint("Search for your notes.");
        searchBar.setSpeechMode(true);
       // searchBar.setRoundedSearchBarEnabled(true);
        searchBar.setOnSearchActionListener(this);
        searchBar.setVisibility(View.GONE);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(searchBar.getText());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (notes.size() != 0) {
            recyclerview.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            adapter = new Adapter(this, notes);
            recyclerview.setAdapter(adapter);
        } else {
            recyclerview.setVisibility(recyclerview.GONE);
            TextView t = findViewById(R.id.noNotes);
            t.setVisibility(t.VISIBLE);
            t.setTextAlignment(t.TEXT_ALIGNMENT_CENTER);
        }
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerview);
        FloatingActionButton fab = findViewById(R.id.fabDelete);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddNote.class);
                startActivity(i);
            }
        });
        /**
        look = findViewById(R.id.searchView);
        look.setImeOptions(EditorInfo.IME_ACTION_DONE);
        look.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                look.setVisibility(View.GONE);
                return false;
            }
        });
        look.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        **/

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Hello", "Its doing it");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Hello", "Its doing it 2");
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_menu, menu);

        TrashDatabase trash = new TrashDatabase(this);
        List<Note> bin = trash.getAllNotes();
        if (bin.isEmpty()) {
            invalidateOptionsMenu();
            MenuItem trashItem = menu.findItem(R.id.menu_item_trash);
            trashItem.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.menu_item_trash) {
            Intent intent = new Intent(this, TrashActivity.class);
            startActivity(intent);
        }
        if (id == R.id.search) {
            searchBar.setVisibility(View.VISIBLE);
            searchBar.openSearch();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        if(!(searchBar.isSearchOpened())){
            searchBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {

    }

    @Override
    public void onButtonClicked(int buttonCode) {
        switch (buttonCode) {
            case MaterialSearchBar.BUTTON_SPEECH:
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Need to speak");
                try {
                    startActivityForResult(intent, REQ_CODE);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(), "Sorry your device not supported", Toast.LENGTH_SHORT).show();
                }
                break;
            case MaterialSearchBar.BUTTON_BACK:
            case MaterialSearchBar.BUTTON_NAVIGATION:
                searchBar.setVisibility(View.GONE);
                searchBar.closeSearch();

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    searchBar.setText(result.get(0).toString());
                    searchBar.openSearch();
                }
                break;
            }
        }
    }
}


