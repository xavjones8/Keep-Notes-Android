package com.xavierjonesco.keepnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.List;
import java.util.Locale;

public class TrashActivity extends AppCompatActivity implements MaterialSearchBar.OnSearchActionListener {
    Toolbar toolbar;
    RecyclerView recyclerview;
    List<Note> trash;
    TrashAdapter adapter;
    private MaterialSearchBar searchBar;
    private final int REQ_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);

        //Sets up AppBar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle("Trash");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Sets up search bar
        searchBar = findViewById(R.id.searchBar);
        searchBar.setHint("Search for your notes.");
        searchBar.setSpeechMode(true);
        searchBar.setRoundedSearchBarEnabled(true);
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
        //Sets Adapter
        recyclerview = findViewById(R.id.listOfNotes);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerview);
        TrashDatabase tb = new TrashDatabase(this);
        trash = tb.getAllNotes();
        if(trash.size()!=0){
            recyclerview.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
            adapter = new TrashAdapter(this,trash);
            recyclerview.setAdapter(adapter);
        }else{
            recyclerview.setVisibility(recyclerview.GONE);
            TextView t = findViewById(R.id.noNotes);
            t.setVisibility(t.VISIBLE);
            t.setTextAlignment(t.TEXT_ALIGNMENT_CENTER);
        }

    }

    @Override
    public void onBackPressed() { super.onBackPressed(); }
    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            TrashDatabase tb = new TrashDatabase(getApplicationContext());
            Note note = trash.get(viewHolder.getAdapterPosition());
            tb.deleteNote(note.getId());
            trash.remove(viewHolder.getAdapterPosition());
            if(trash.size() != 0) {
                adapter.notifyDataSetChanged();
            }else{
                recyclerview.setVisibility(recyclerview.GONE);
                TextView t = findViewById(R.id.noNotes);
                t.setVisibility(t.VISIBLE);
                t.setTextAlignment(t.TEXT_ALIGNMENT_CENTER);
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.add_menu,menu);
        invalidateOptionsMenu();
        MenuItem trash = menu.findItem(R.id.menu_item_trash);
        trash.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.search){
            searchBar.setVisibility(View.VISIBLE);
            searchBar.openSearch();
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        if(!(searchBar.isSearchOpened())){
            searchBar.setVisibility(View.GONE);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
}