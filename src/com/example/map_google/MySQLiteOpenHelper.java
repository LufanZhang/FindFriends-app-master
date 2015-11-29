package com.example.map_google;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteOpenHelper extends SQLiteOpenHelper{
	public final static int VERSION = 1;
	public final static String TABLE_NAME = "location_ht";
	public final static String ID = "id";
	public final static String LOCATION = "location";
	public static final String DATABASE_NAME = "myLocation.db";
	public static final String T = "Ctime";
	public MySQLiteOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, VERSION); 
	} 
	@Override
	//this funtion will be called when the database is created in the first time
	public void onCreate(SQLiteDatabase db) { 
		System.out.println("create table----------->");
		String str_sql = "create table if not exists " + TABLE_NAME + "("
							+ ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
							+ T + " test,"
							+ LOCATION + " text );";
		db.execSQL(str_sql);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		System.out.println("update");
		Log.v("location", "onUpgrade");
	} 

}
