package com.example.map_google;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ListView_location extends Activity{
	private SQLiteDatabase mDb;  
    private SQLiteDatabaseDao dao;  
    public final static String TABLE_NAME = "location_ht";
    public static final String DATABASE_NAME = "myLocation.db";
    public final static String T = "Ctime";
	public final static String LOCATION = "location";
	public final static String ID = "id";
	private File file = new File("/sdcard/findFriends/my_location_history.db");
	
    ArrayList<HashMap<String, Object>> listData;  
    SimpleAdapter listItemAdapter;  
  
    /** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.list_location);  
        dao = new SQLiteDatabaseDao();  
  
        ListView list = (ListView) findViewById(R.id.list_location);  
        listItemAdapter = new SimpleAdapter(ListView_location.this,  
                listData,  
                R.layout.item,  
                new String[] { "location", "location_time" },  
                new int[] { R.id.location_ls, R.id.location_time });  
        list.setAdapter(listItemAdapter);  
        list.setOnCreateContextMenuListener(listviewLongPress);  
    }  
  
  //basic database operation class
    class SQLiteDatabaseDao {  
  //put all of data in database in listData which will be the resource of listView
        public SQLiteDatabaseDao() {  
            mDb = SQLiteDatabase.openOrCreateDatabase(file, null);  
            createTable(mDb, TABLE_NAME);  
            Cursor c = mDb.rawQuery("select * from " + TABLE_NAME, null); 
            int columnsSize = c.getColumnCount();  
            listData = new ArrayList<HashMap<String, Object>>();  
            while (c.moveToNext()) {  
                HashMap<String, Object> map = new HashMap<String, Object>();  
                for (int i = 0; i < columnsSize; i++) {   
                    map.put("location", c.getString(2));  
                    map.put("location_time", c.getString(1)); 
                    map.put(ID, c.getString(0));
                }  
                listData.add(map);  
            }  
        }  
  
 // create a database  
        public void createTable(SQLiteDatabase mDb, String table) {  
            try {  
                mDb.execSQL("create table if not exists " + table + 
                		"("+ ID + " INTEGER PRIMARY KEY,"  
                			+ T + " text,"
                		   + LOCATION + " text );");  
            } catch (SQLException e) {  
                Toast.makeText(getApplicationContext(), "fail to create the database",  
                        Toast.LENGTH_LONG).show();  
            }  
        }  
 //delete one of the rows 
        public boolean delete(SQLiteDatabase mDb, String table, int id) {  
            String whereClause = "id=?";  
            String[] whereArgs = new String[] { String.valueOf(id) };  
            try {  
                mDb.delete(table, whereClause, whereArgs);  
            } catch (SQLException e) {  
                Toast.makeText(getApplicationContext(), "fail to delete!",  
                        Toast.LENGTH_LONG).show();  
                return false;  
            }  
            return true;  
        }  
    }  
    // when click one of the list long time  
    OnCreateContextMenuListener listviewLongPress = new OnCreateContextMenuListener() {  
        @Override  
        public void onCreateContextMenu(ContextMenu menu, View v,  
                ContextMenuInfo menuInfo) {  
            // TODO Auto-generated method stub  
            final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;  
            new AlertDialog.Builder(ListView_location.this)  
                    .setTitle("delete this location history")  
                    .setIcon(android.R.drawable.ic_dialog_info)  
                    .setMessage("are you sure about delete")  
                    .setPositiveButton("yes",  
                            new DialogInterface.OnClickListener() {  
                                public void onClick(  
                                        DialogInterface dialoginterface, int i) {  
                                    int mListPos = info.position; 
                                    HashMap<String, Object> map = listData  
                                            .get(mListPos);  
                                    int id = Integer.valueOf((map.get(ID)  
                                            .toString())); 
                                    if (dao.delete(mDb, TABLE_NAME, id)) {  
                                        listData.remove(mListPos);  
                                        listItemAdapter.notifyDataSetChanged();  
                                    }  
                                }  
                            })  
                    .setNegativeButton("no",  
                            new DialogInterface.OnClickListener() {  
                                public void onClick(  
                                        DialogInterface dialoginterface, int i) {  
                                }  
                            }).show();  
        }  
    };  
  
    @Override  
    public void finish() {  
        // TODO Auto-generated method stub  
        super.finish();  
        mDb.close();  
    }   
}
