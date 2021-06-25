package com.xavierjonesco.keepnotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.xavierjonesco.keepnotes.Note;

import java.util.ArrayList;
import java.util.List;

public class TrashDatabase extends SQLiteOpenHelper {
    // declare require values
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "SimpleTB2";
    private static final String TABLE_NAME = "SimpleTable2";

    public TrashDatabase(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    // declare table column names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_DATE = "date";
    private static final String KEY_TIME = "time";





    // creating tables
    @Override
    public void onCreate(SQLiteDatabase tb) {
        String createTb = "CREATE TABLE "+TABLE_NAME+" ("+
                KEY_ID+" INTEGER PRIMARY KEY,"+
                KEY_TITLE+" TEXT,"+
                KEY_CONTENT+" TEXT,"+
                KEY_DATE+" TEXT,"+
                KEY_TIME+" TEXT"
                +" )";
        tb.execSQL(createTb);
    }

    // upgrade db if older version exists
    @Override
    public void onUpgrade(SQLiteDatabase tb, int oldVersion, int newVersion) {
        if(oldVersion >= newVersion)
            return;

        tb.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(tb);
    }

    public long addNote(Note note){
        SQLiteDatabase tb = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(KEY_TITLE,note.getTitle());
        v.put(KEY_CONTENT,note.getContent());
        v.put(KEY_DATE,note.getDate());
        v.put(KEY_TIME,note.getTime());

        // inserting data into db
        long ID = tb.insert(TABLE_NAME,null,v);
        return  ID;
    }

    public Note getNote(long id){
        SQLiteDatabase tb = this.getWritableDatabase();
        String[] query = new String[] {KEY_ID,KEY_TITLE,KEY_CONTENT,KEY_DATE,KEY_TIME};
        Cursor cursor=  tb.query(TABLE_NAME,query,KEY_ID+"=?",new String[]{String.valueOf(id)},null,null,null,null);
        Note note =  new Note(1,"","","","");

        if (cursor != null)
            cursor.moveToFirst();
        if (cursor != null && cursor.moveToFirst()) {
            note = new Note(
                    Long.parseLong(cursor.getString(0)),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4));
        }
        return note;
    }

    public List<Note> getAllNotes(){
        List<Note> allNotes = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME+" ORDER BY "+KEY_ID+" DESC";
        SQLiteDatabase tb = this.getReadableDatabase();
        Cursor cursor = tb.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                Note note = new Note();
                note.setId(Long.parseLong(cursor.getString(0)));
                note.setTitle(cursor.getString(1));
                note.setContents(cursor.getString(2));
                note.setDate(cursor.getString(3));
                note.setTime(cursor.getString(4));
                allNotes.add(note);
            }while (cursor.moveToNext());
        }

        return allNotes;

    }

    public int editNote(Note note){
        SQLiteDatabase tb = this.getWritableDatabase();
        ContentValues c = new ContentValues();
        c.put(KEY_TITLE,note.getTitle());
        c.put(KEY_CONTENT,note.getContent());
        c.put(KEY_DATE,note.getDate());
        c.put(KEY_TIME,note.getTime());
        return tb.update(TABLE_NAME,c,KEY_ID+"=?",new String[]{String.valueOf(note.getId())});
    }



    void deleteNote(long id){
        SQLiteDatabase tb = this.getWritableDatabase();
        tb.delete(TABLE_NAME,KEY_ID+"=?",new String[]{String.valueOf(id)});
        tb.close();
    }
    public long getSize(){
        SQLiteDatabase tb = this.getReadableDatabase();

        return tb.getPageSize();

    }




}
