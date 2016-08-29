package com.example.sarmedchaudhry.bartr20;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper implements Serializable {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "bartr.db";

    //USER TABLE AND ATTRIBUTES
    public static final String TABLE_USER = "users";
    public static final String COLUMN_USER_ID = "_id";
    public static final String COLUMN_USER_NAME = "_name";
    public static final String COLUMN_USER_EMAIL = "_email";
    public static final String COLUMN_USER_PASSWORD = "_password";

    //ITEM TABLE AND ATTRIBUTES
    public static final String TABLE_ITEM = "items";
    public static final String COLUMN_ITEM_ID = "_id";
    public static final String COLUMN_ITEM_NAME = "_name";
    public static final String COLUMN_ITEM_PRICE = "_price";
    public static final String COLUMN_ITEM_DESCRIPTION = "_description";
    public static final String COLUMN_ITEM_IMAGE = "_image";
    Context context;

    public DBHelper (Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //CREATE USER TABLE
        String user_query = "CREATE TABLE " + TABLE_USER + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                COLUMN_USER_NAME + " TEXT NOT NULL, " +
                COLUMN_USER_EMAIL + " TEXT NOT NULL UNIQUE, " +
                COLUMN_USER_PASSWORD + " TEXT NOT NULL" +
                ");";

        String item_query = "CREATE TABLE " + TABLE_ITEM + " (" +
                COLUMN_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                COLUMN_ITEM_NAME + " TEXT NOT NULL, " +
                COLUMN_ITEM_PRICE + " TEXT NOT NULL, " +
                COLUMN_ITEM_DESCRIPTION + " TEXT NOT NULL, " +
                COLUMN_ITEM_IMAGE + " BLOB NOT NULL" +
                ");";

        db.execSQL(user_query);
        db.execSQL(item_query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM);
        onCreate(db);
    }

    //ADD TO USER TABLE
    public void addUser(Users user){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.get_name());
        values.put(COLUMN_USER_EMAIL, user.get_email());
        values.put(COLUMN_USER_PASSWORD, user.get_password());

        db.insert(TABLE_USER, null, values);
        db.close();
    }

    //DELETE FROM USER TABLE
    public void deleteUser(String email){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_USER + " WHERE " + COLUMN_USER_EMAIL + " = \"" + email + "\";");
    }

    //CHECK IF USER AND PASSWORD IS IN THE DB
    public String validLogIn(String email, String password){
        SQLiteDatabase db = getWritableDatabase();
        String name = "";
        String query = "SELECT * FROM " + TABLE_USER + " WHERE " +
                COLUMN_USER_EMAIL + " = \'" + email + "\' AND " +
                COLUMN_USER_PASSWORD + " = \'" + password + "\';";

        Cursor c = db.rawQuery(query, null);

        if (c.moveToFirst()){
            if(c.getString(c.getColumnIndex(COLUMN_USER_NAME)) != null){
                name = (c.getString(c.getColumnIndex(COLUMN_USER_NAME)));
            }
            c.close();
        }
        db.close();

        return name;

    }

    //ADD TO ITEM TABLE
    public void addItem(Items item){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        Bitmap photo = item.getImage();
        if(photo == null){
            Drawable myDrawable = context.getResources().getDrawable(R.drawable.ic_no_image);
            photo = ((BitmapDrawable) myDrawable).getBitmap();
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] bArray = bos.toByteArray();
//System.out.println(item.getName());
        values.put(COLUMN_ITEM_NAME, item.getName());
        values.put(COLUMN_ITEM_PRICE, item.getPrice());
        values.put(COLUMN_ITEM_DESCRIPTION, item.getDescription());
        values.put(COLUMN_ITEM_IMAGE, bArray);

        db.insert(TABLE_ITEM, null, values);
        db.close();
    }

    //DELETE FROM ITEM TABLE
    public void deleteItem(String name){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_ITEM + " WHERE " + COLUMN_ITEM_NAME + " = \"" + name + "\";");
    }

   /* public void stupidSpelling(){
        SQLiteDatabase db = getWritableDatabase();

       // alter table anotherthing rename to anotherthing_1;
        //create table anotherthing (id number(10) not null, sometext varchar, primary key (id));
        //insert into anotherthing(id, sometext) select id, text from anotherthing_1;
        //drop table anotherthing_1;
        String query1 = "ALTER TABLE " + TABLE_USER + " RENAME TO temp;";
        String query2 = "CREATE TABLE " + TABLE_USER + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                COLUMN_USER_NAME + " TEXT NOT NULL, " +
                COLUMN_USER_EMAIL + " TEXT NOT NULL UNIQUE, " +
                COLUMN_USER_PASSWORD + " TEXT NOT NULL" +
                ");";
        String query3 = "DROP TABLE temp;";

        db.execSQL(query1);
        db.execSQL(query2);
        db.execSQL(query3);
    }*/

    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "mesage" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }


    }
}
