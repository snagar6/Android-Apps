package com.MyNewAppNotifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.content.pm.PackageManager;

public class MainActivity extends Activity {

	private BroadcastReceiver mReceiver;
	public Socket socket;
	public String data1;
	public String pkgName;

	public static final int SERVERPORT = 5900;
	public static final String SERVER_IP = "10.0.2.2";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// new Thread(new ClientThread()).start();
	}

	protected void onResume() {       
		super.onResume();		
		
		IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
        
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            	String actionStr = intent.getAction();
            	
            	if (Intent.ACTION_PACKAGE_ADDED.equals(actionStr)) {
            		
            		Uri data = intent.getData();
            	    pkgName = data.getEncodedSchemeSpecificPart();
            	    data1 = null;
            	    
            	    PackageManager pm = context.getPackageManager();
            	    ApplicationInfo app;
					try {
						app = pm.getApplicationInfo(pkgName, 0);
						data1 = app.sourceDir;
					} catch (NameNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}            	    
            	                  
            	    Log.w("++++++++++++++++++++  MY_APP_INSTALLATION_NOTIFIER_LOG_1 +++++++++++++++++++", pkgName);
            	    Log.w("++++++++++++++++++++  MY_APP_INSTALLATION_NOTIFIER_LOG_2 +++++++++++++++++++", data1);
            	    
            	    try {
            	     
            	    new Thread(new ClientThread()).start();
            	    
            	    /*
            	    PrintWriter out = new PrintWriter(new BufferedWriter(
            					new OutputStreamWriter(socket.getOutputStream())), true);
           			Log.w("++++++++++++++++++++  MY_APP_INSTALLATION_NOTIFIER_LOG_3 +++++++++++++++++++", pkgName);
            		out.println(data1);
            		Log.w("++++++++++++++++++++  MY_APP_INSTALLATION_NOTIFIER_LOG_4 +++++++++++++++++++", data1);
            		
            		BufferedReader in = new BufferedReader(new InputStreamReader (socket.getInputStream()));
            		String line = null;
            		if ((line = in.readLine()) != null) {
            			Log.w("++++++++++++++++++++  MY_APP_INSTALLATION_NOTIFIER_LOG_5 +++++++++++++++++++", line);            			
            		}
            		
            		socket.close();
            	    
            		} catch (UnknownHostException e) {
            			e.printStackTrace();
            		} catch (IOException e) {
            			e.printStackTrace();
            		} 
            		*/
            	    } catch (Exception e) {
            			e.printStackTrace();
            		}
                }
            }
        };
        //registering our receiver
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addDataScheme("package");        
        this.registerReceiver(mReceiver, intentFilter);
    }
   
	class ClientThread implements Runnable {

		@Override
		public void run() {

			try {
				InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
				socket = new Socket(serverAddr, SERVERPORT);
				
				PrintWriter out = new PrintWriter(new BufferedWriter(
    					new OutputStreamWriter(socket.getOutputStream())), true);
   			Log.w("++++++++++++++++++++  MY_APP_INSTALLATION_NOTIFIER_LOG_3 +++++++++++++++++++", pkgName);
    		out.println(data1);
    		Log.w("++++++++++++++++++++  MY_APP_INSTALLATION_NOTIFIER_LOG_4 +++++++++++++++++++", data1);
    		
    		BufferedReader in = new BufferedReader(new InputStreamReader (socket.getInputStream()));
    		String line = null;
    		if ((line = in.readLine()) != null) {
    			Log.w("++++++++++++++++++++  MY_APP_INSTALLATION_NOTIFIER_LOG_5 +++++++++++++++++++", line);            			
    		}
    		
    		socket.close();

			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}

	}

}
