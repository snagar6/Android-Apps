package com.intel.yamba;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
			
		switch (item.getItemId()) {
		
		case R.id.item_preferences:
			Intent intent5 = new Intent(this, SettingsActivity.class);
			startActivity(intent5);
			break;
			
		case R.id.item_refresh:
			Intent intent4 = new Intent(this, FetchService.class);
			startService(intent4);
			break;
		
		case R.id.item_new:
			Intent intent = new Intent(this, StatusActivity.class);
			startActivity(intent);
			break;
			
		case R.id.item_web:
			Intent intent1 = new Intent(Intent.ACTION_VIEW);
			intent1.setData(Uri.parse("http://yamba.marakana.com"));
			startActivity(intent1);
			break;
			
		case R.id.item_SMS:
			Intent intent2 = new Intent(Intent.ACTION_VIEW);
			intent2.setData(Uri.parse("sms:4088386979?body=cake bhekha????"));
			startActivity(intent2);
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
}
