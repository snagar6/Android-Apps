package com.intel.yamba;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TemplatesActivity extends ListActivity {
	
	private String[] templates;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_templates);
		
		/*String[] templates = new String[] {
				"I'm late", "I am bored to death", "No, I dont love you"
		};*/
		
		templates = getResources().getStringArray(R.array.templates);
				
		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, templates);
		
		setListAdapter(adapter);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		
		String tweet = templates[position];
		Intent intent = new Intent();
		intent.putExtra("template", tweet);
		setResult(RESULT_OK, intent);
		// Getting back to the previous activity from this activity ...
		finish();
		
		super.onListItemClick(l, v, position, id);
	}
}
