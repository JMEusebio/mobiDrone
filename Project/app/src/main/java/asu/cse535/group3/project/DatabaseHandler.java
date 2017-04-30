package asu.cse535.group3.project;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "contactsManager";

    // Contacts table name
    private static final String TABLE_CONTACTS = "users";

    // Contacts Table Columns names
    private static final String KEY_NAME = "name";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "_password";
    private static final String KEY_FEEDBACK = "feedback";
    private static final String KEY_LOCATION = "location";

    // Queue for location history
    LimitedSizeQueue history = new LimitedSizeQueue(4);

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = getWritableDatabase();

       //truncateUsers();
        onCreate(db);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_CONTACTS + "(ID INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " TEXT," + KEY_USERNAME + " TEXT," + KEY_PASSWORD + " TEXT," + KEY_FEEDBACK + " TEXT," + KEY_LOCATION + " TEXT" +  ")";

        db.execSQL(CREATE_CONTACTS_TABLE);

        //initLocationHistory();
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);

        // Create tables again
        onCreate(db);

    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */


    public boolean addUser(String name, String username, String password) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_USERNAME, username);
        values.put(KEY_PASSWORD, password);
        values.put(KEY_LOCATION, "N/A,N/A,N/A,N/A,N/A");


        if(!usernameExists(username))
        {
            db.insert(TABLE_CONTACTS, null, values);
            printUsers();
            db.close();
            return true;
        }

        return false;
    }

    public void addFeedback(String user, String message)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("update " + TABLE_CONTACTS + " set " + KEY_FEEDBACK + "='" + message + "' where username = '" + user + "'");
        db.close();

        printUsers();

    }

    public void addLocation(String user, String location)
    {
        refillHistory(user);
        history.add(location);

        String locations = android.text.TextUtils.join(",", history);

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("update " + TABLE_CONTACTS + " set " + KEY_LOCATION + "='" + locations + "' where username = '" + user + "'");
        db.close();

        printUsers();
    }

    public void refillHistory(String user)
    {
        String locations = getLocations(user);
        String[] locationsArray = locations.split(",");

        history.clear();
        history.add(locationsArray[0]);
        history.add(locationsArray[1]);
        history.add(locationsArray[2]);
        history.add(locationsArray[3]);
        history.add(locationsArray[4]);
    }

    public boolean usernameExists(String username)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select username from "+ TABLE_CONTACTS + " where username = '" + username + "'",null);

        if (res.getCount() == 0)
            return false;
        return true;
    }

    public boolean isLoginCorrect(String username, String password)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        if (usernameExists(username))
        {
            Cursor res = db.rawQuery("select " + KEY_PASSWORD + " from " + TABLE_CONTACTS + " where username = '" + username + "'",null);
            res.moveToFirst();
            if (password.equals(res.getString(res.getColumnIndex(KEY_PASSWORD))))
            {
                return true;
            }
        }
        return false;
    }

    public String getLocations(String user)
    {
        String locations = "";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_CONTACTS + " where username = '" + user + "'",null);


        if (res != null) {
            res.moveToFirst();
            locations = res.getString(5);
        }

        return locations;
    }


    public void printUsers()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+ TABLE_CONTACTS, null);

        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            buffer.append("ID :"+ res.getString(0)+"\n");
            buffer.append("Name :"+ res.getString(1)+"\n");
            buffer.append("Username :"+ res.getString(2)+"\n");
            buffer.append("Password :"+ res.getString(3)+"\n");
            buffer.append("Feedback :"+ res.getString(4)+"\n");
            buffer.append("Location :"+ res.getString(5)+"\n\n");
        }

        Log.d("printUsers", buffer.toString());
    }
}
