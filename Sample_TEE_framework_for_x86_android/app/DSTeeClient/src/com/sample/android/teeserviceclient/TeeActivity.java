/* 
 * TeeActivity.java: This contains the definition of the Main activity of 
 * the DSTeeClient Application
 */

package com.sample.android.teeserviceclient;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import com.sample.android.service.tee.TeedevManager;
import com.sample.android.service.tee.TeedevListener;

public class TeeActivity extends Activity implements TeedevListener {

	private TextView output;
	private EditText input;

	private final TeedevManager teedevManager = TeedevManager.getInstance();

	public void onCreate(Bundle savedInstanceState) {

	super.onCreate(savedInstanceState);
	super.setContentView(R.layout.log);

		findViewById(R.id.button_1).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				int inputVal1 = 0;
				int outVal1 = 0;
				String str = "";

				/* Creating a Editing field for keying in the Input integer value */
				EditText editText1 = (EditText)findViewById(R.id.input_1);

				/* If the user doesnt key in a input value, then input is assumed to be Zero */
				str = editText1.getText().toString();	
				if (str.length() == 0) {
					inputVal1 = 0;
				}
				else {
					inputVal1 = Integer.parseInt(str);
				}

				/* Creating a TextView to display the end result */
				TextView text1 = (TextView)findViewById(R.id.output_1);

				/* Making a call to the DSTeeService; Sending a request message */
				outVal1 = teedevManager.sendTeeRequest(inputVal1);

				/* Displaying the end result */
				text1.setText(getString(R.string.tee_message, outVal1)); 
			}
		});     
	}

	@Override
	public void onResume() {

		super.onResume();
		/* Registering back with the DSTeeService upon resume */
		this.teedevManager.register(this);    
	}

	@Override
	public void onPause() {

		super.onPause();
		/* De-Registering with the DSTeeService upon pause */
		this.teedevManager.unregister(this);
	}

	/* Provision for a callback which can be triggered from the DSTeeService upon an event */
	@Override
	public void onSendTeeRequest (int result) {

		// this.updateOutput(result);
	}

}
