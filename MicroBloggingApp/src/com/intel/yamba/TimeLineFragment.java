package com.intel.yamba;

import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

public class TimeLineFragment extends ListFragment {
	
	public static final String TAG = "TimeLineFragment";
	private SimpleCursorAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View myView = inflater.inflate(R.layout.fragment_timeline, container);
				
		return myView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		BroadcastReceiver receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d(TAG, "Request Received at BroadcastReceiver ");
				refreshData();
			}
			
		};
		
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, new IntentFilter("com.intel.yamba.new_data"));		
		refreshData();
		
		super.onActivityCreated(savedInstanceState);
	}

	private void refreshData() {
		DBHelper dbHelper = new DBHelper(getActivity());
		SQLiteDatabase db = dbHelper.getWritableDatabase();
				
		Cursor cursor = db.query(StatusContract.TABLE_NAME,
				new String[] { BaseColumns._ID, StatusContract.Columns.MESSAGE, StatusContract.Columns.USERNAME },
				null, null,
				null, null, BaseColumns._ID + " desc");

		Log.d(TAG, String.valueOf(cursor.getCount()));
		
		String[] from = new String[] { StatusContract.Columns.USERNAME, StatusContract.Columns.MESSAGE };		
		int[] idsTo = new int[] {android.R.id.text1, android.R.id.text2};		
		adapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_2, cursor, from, idsTo, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		setListAdapter(adapter);
	}
}
