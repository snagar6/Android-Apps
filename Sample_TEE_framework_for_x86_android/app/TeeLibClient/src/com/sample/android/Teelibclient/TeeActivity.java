package com.android.Teelibclient;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.sample.android.lib.tee.Tee;

public class TeeActivity extends Activity
  implements View.OnClickListener, Runnable {

  private TextView output;
  private Handler handler;
  private int handle;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    super.setContentView(R.layout.log);
    this.output = (TextView) super.findViewById(R.id.output);
    Button button = (Button) super.findViewById(R.id.button);
    button.setOnClickListener(this);
    this.handler = new Handler();
  }

  private void updateOutput() {

    int retVal = Tee.nativeFuncSendMessage (this.handle, 2000);
   
    this.output.setText(
      super.getString(R.string.tee_message, retVal, retVal));
  }

  @Override
  public void onResume() {
    super.onResume();
    this.handle = Tee.init();
    this.handler.post(this);
  }

  @Override
  public void onPause() {
    super.onPause();
    this.handler.removeCallbacks(this);
    Tee.close(this.handle);
  }

  public void onClick(View view) {
    this.updateOutput();
  }

  public void run() {
    // this.updateOutput();
    // this.handler.postDelayed(this, 1000);
  }
}

