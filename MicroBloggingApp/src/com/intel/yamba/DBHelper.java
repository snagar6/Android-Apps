package com.intel.yamba;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	
	private static final String TAG = "DBHelper";

	public DBHelper(Context context) {
		super(context, StatusContract.DB_NAME, null, StatusContract.DB_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		Log.d(TAG, "DB Created Boss!!");
		
		db.execSQL(String.format("CREATE TABLE %s (%s int, %s text, %s text)", 
						StatusContract.TABLE_NAME,
						BaseColumns._ID,
						StatusContract.Columns.USERNAME,
						StatusContract.Columns.MESSAGE)
						);
						
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		if (newVersion == 2 && oldVersion == 1) {
			db.execSQL("ALTER TABLE XXXX");
		}

	}

}
;