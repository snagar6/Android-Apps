/*
 * TeeServiceApp.java: This application launches the sample TEE Service instance (process) 
 * for the Tee Clients to communicate and send their reqests/messages. This is a persistant 
 * android app which would always be up and running on the device.
 */

package com.sample.android.teeservice;

import android.app.Application;
import android.os.ServiceManager;
import android.util.Log;
import com.sample.android.service.tee.ITeedevService;

/* This App does not have an GUI activity; Its role is to launch the Deepsafe service app instance only */
public class TeeServiceApp extends Application {

	private static final String TAG = "TeeServiceApp";
	private static final String REMOTE_SERVICE_NAME = ITeedevService.class.getName();
	private ITeeServiceImpl serviceImpl;

	public void onCreate() {

		super.onCreate();

		/* Instantiates the TeeService Implementation interface */
		this.serviceImpl = new ITeeServiceImpl(this);
		ServiceManager.addService(REMOTE_SERVICE_NAME, this.serviceImpl);
		Log.d(TAG, "Registered [" + serviceImpl.getClass().getName() + "] as [" + REMOTE_SERVICE_NAME + "]");
	}

	public void onTerminate() {
	
		/* Called upon force-kill; Since this is a persistant app */
		super.onTerminate();
		Log.d(TAG, "Terminated");
	}
}
