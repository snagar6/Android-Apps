package com.intel.yamba;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClientException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.CursorJoiner.Result;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StatusActivity extends Activity 
	implements OnClickListener, LocationListener {
	
	private static final int MAX_CHARS = 40;
	private EditText myMessage;
	private Location lastlocation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		setContentView(R.layout.activity_status);
		
		LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		String provider = manager.getBestProvider(criteria, true);
		
		manager.requestLocationUpdates(provider, 1000, 50, this);
		
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		Log.d("lifecycle", "onCreate");
		
		final Button buttonSend = (Button) findViewById(R.id.button_send);
		buttonSend.setOnClickListener(this);
		
		final Button buttonClear = (Button) findViewById(R.id.button_clear);
		buttonClear.setOnClickListener(this);
		
		myMessage = (EditText) this.findViewById(R.id.message_text);
		myMessage.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				String newMessageText = s.toString();
				int length = newMessageText.length();
				
				TextView charCount = (TextView) findViewById(R.id.charCount);
				charCount.setText(String.valueOf(140-length)+" char(s) left");
				if (length > MAX_CHARS) {
					myMessage.setTextColor(Color.RED);
					charCount.setTextColor(Color.RED);
					buttonSend.setEnabled(false);
				}
				else {
					myMessage.setTextColor(Color.BLACK);
					charCount.setTextColor(Color.GREEN);
					buttonSend.setEnabled(true);
				}
			}
		});
		
		TextView label123 = (TextView) findViewById(R.id.textView1);
		label123.setText("My message to the world");
		label123.setTextColor(Color.BLUE);
		
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onResume() { 
		// TODO Auto-generated method stub
		Log.d("lifecycle", "onResume");
		super.onResume();
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		Log.d("lifecycle", "onStart");
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		Log.d("lifecycle", "onStop");
		super.onStop();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		Log.d("lifecycle", "onPause");
		super.onPause();
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		Log.d("lifecycle", "onRestart");
		super.onRestart();
	}

	@Override
	public void onClick(View whichButton) {
		// TODO Auto-generated method stub
		
		myMessage = (EditText) this.findViewById(R.id.message_text);
		
		PostTask task = new PostTask();
		task.execute(myMessage.getText().toString());

	}
	

private class PostTask extends AsyncTask<String, Void, String> {

	private ProgressDialog dialog;
	
	@Override
	protected void onPreExecute() {
		
		dialog = ProgressDialog.show(StatusActivity.this, "Posting....","PLease, wait");
		super.onPreExecute();
	}
	
	@Override
	protected String doInBackground(String... params) {
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(StatusActivity.this);
		String username = prefs.getString("username",  "student");
		String password = prefs.getString("password",  "password");
		
		YambaClient client = new YambaClient(username, password);
		
		try {
			
			if (lastlocation == null) {
				client.postStatus(params[0]);
			} else {
				client.postStatus(params[0], lastlocation.getLatitude(), lastlocation.getLongitude());
			}			
			return "Sent";
		} 
		catch (YambaClientException e) {			
			e.printStackTrace();
			return "Failed";
		}
		
	}
	
	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		
		if (result == "Sent")
			Toast.makeText(StatusActivity.this, "Post Submitted", Toast.LENGTH_LONG).show();
		else 
			Toast.makeText(StatusActivity.this, "Some issue", Toast.LENGTH_LONG).show();
		
		dialog.cancel();
		
		super.onPostExecute(result);
	}
	
	
}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	
		getMenuInflater().inflate(R.menu.menu_status, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	
		switch (item.getItemId()) {
		
		case R.id.itemMenuCannedResponses:
			Intent intent = new Intent(this, TemplatesActivity.class);
			startActivityForResult(intent, 999);
			break;
			
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == 999 && resultCode == RESULT_OK) {
			String template = data.getStringExtra("template");
			myMessage.setText(template);
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onLocationChanged(Location arg0) {
		lastlocation = arg0;
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
		
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
}

