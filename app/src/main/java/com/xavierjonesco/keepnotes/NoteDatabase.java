package com.xavierjonesco.keepnotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class NoteDatabase extends SQLiteOpenHelper {
    // declare require values
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "SimpleDB";
    private static final String TABLE_NAME = "SimpleTable";

    public NoteDatabase(Context context){
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
    public void onCreate(SQLiteDatabase db) {
        String createDb = "CREATE TABLE "+TABLE_NAME+" ("+
                KEY_ID+" INTEGER PRIMARY KEY,"+
                KEY_TITLE+" TEXT,"+
                KEY_CONTENT+" TEXT,"+
                KEY_DATE+" TEXT,"+
                KEY_TIME+" TEXT"
                +" )";
        db.execSQL(createDb);
    }

    // upgrade db if older version exists
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion >= newVersion)
            return;

        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public long addNote(Note note){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(KEY_TITLE,note.getTitle());
        v.put(KEY_CONTENT,note.getContent());
        v.put(KEY_DATE,note.getDate());
        v.put(KEY_TIME,note.getTime());

        // inserting data into db
        long ID = db.insert(TABLE_NAME,null,v);
        return  ID;
    }

    public Note getNote(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] query = new String[]{KEY_ID, KEY_TITLE, KEY_CONTENT, KEY_DATE, KEY_TIME};
        Cursor cursor = db.query(TABLE_NAME, query, KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
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
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);
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

    public long editNote(Note note){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues c = new ContentValues();
        c.put(KEY_TITLE,note.getTitle());
        c.put(KEY_CONTENT,note.getContent());
        c.put(KEY_DATE,note.getDate());
        c.put(KEY_TIME,note.getTime());
        long ID = db.insert(TABLE_NAME,null,c);
        db.delete(TABLE_NAME,KEY_ID+"=?",new String[]{String.valueOf(note.getId())});
        //db.update(TABLE_NAME,c,KEY_ID+"=?",new String[]{String.valueOf(note.getId())})
        return ID;
    }



    void deleteNote(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,KEY_ID+"=?",new String[]{String.valueOf(id)});
        db.close();
    }
    public long getSize(){
        SQLiteDatabase db = this.getReadableDatabase();

        return db.getPageSize();

    }
    public void exportDB(){
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source=null;
        FileChannel destination=null;
        String currentDBPath = "/data/"+ "com.xavierjonesco.keepnotes" +"/databases/"+DATABASE_NAME ;
        String backupDBPath = TABLE_NAME;
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
           // Toast.makeText(this, "Your Database is Exported !!", Toast.LENGTH_LONG).show();

            
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    public void importDB(){
        String dir=Environment.getExternalStorageDirectory().getAbsolutePath();
        File sd = new File(dir);
        File data = Environment.getDataDirectory();
        FileChannel source = null;
        FileChannel destination = null;
        String backupDBPath = "/data/com.xavierjonesco.keepnotes/databases/SimpleDB.db";
        String currentDBPath = "SimpleDB.db";
        File currentDB = new File(sd, currentDBPath);
        File backupDB = new File(data, backupDBPath);

        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
          //  Toast.makeText(this, "Your Database is Imported !!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
