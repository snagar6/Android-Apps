package com.intel.yamba;

import java.util.List;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClient.Status;
import com.marakana.android.yamba.clientlib.YambaClientException;

import android.app.IntentService;
import android.app.Service;
import android.content.ContentValues;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.provider.BaseColumns;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class FetchService extends IntentService {

	public static final String TAG = FetchService.class.getName();
	private LocalBroadcastManager broadcastManager;
	
	public FetchService() {
		super("FetchService");
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		YambaClient client = new YambaClient("student", "password");
		
		DBHelper helper = new DBHelper(this);
		SQLiteDatabase db = helper.getWritableDatabase();
		
		try {
			List<Status> list = client.getTimeline(20);
			
			for (Status currentStatus : list) {
				
				Log.d(TAG, currentStatus.getMessage());
				
				ContentValues values = new ContentValues();
				values.put(BaseColumns._ID, currentStatus.getId());
				values.put(StatusContract.Columns.USERNAME, currentStatus.getUser());
				values.put(StatusContract.Columns.MESSAGE, currentStatus.getMessage());
				
				db.insertWithOnConflict(StatusContract.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
			}			
						
		} catch (YambaClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void onCreate() {
		
		Log.d(TAG, "Service onCreate");
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		broadcastManager = LocalBroadcastManager.getInstance(this);
				
		Log.d(TAG, "Service onStart");
		
		// New data is available
		Intent broadcastIntent = new Intent("com.intel.yamba.new_data");
		broadcastManager.sendBroadcast(broadcastIntent);
					
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {

		Log.d(TAG, "Service onDestroyed!");
		super.onDestroy();
	}
	
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
