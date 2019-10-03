package com.postaplus.etrack;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//local database
public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
   public static final int DATABASE_VERSION = 1;

    // Database Name
    public static final String DATABASE_NAME = "Ops.db";

 //logindata table name
 public static final String TABLE_NAME1 = "logindata";
    //eventdata table name
    public static final String TABLE_NAME2 = "eventdata";

 // login Table Columns names
 private static final String USER_NAME = "Username";
 private static final String LOGIN_STATUS = "Loginstatus";

    //event table column names
    private static final String EVENT_CODE = "EventCode";
    private static final String EVENT_NAME ="EventName";
    

    public DatabaseHandler(Context context) {
	super(context, DATABASE_NAME, null, DATABASE_VERSION);
	
    }

   
    @Override
    public void onCreate(SQLiteDatabase db) {
     // Creating Tables for login
   /*  String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_NAME1 + "("
             + USER_NAME + " TEXT," + LOGIN_STATUS + " INTEGER" +")";
     db.execSQL(CREATE_LOGIN_TABLE);
*/

     //event_table
	String CREATE_EVENT_TABLE = "CREATE TABLE " + TABLE_NAME2 + "("
			+ EVENT_CODE + " TEXT," + EVENT_NAME + " TEXT"+")";
	db.execSQL(CREATE_EVENT_TABLE);


    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
     //   db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME1);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME2);


        // Create tables again
        onCreate(db);
	
    }
   

  

    
    }
