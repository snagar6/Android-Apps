/*
 * TeedevManager.java: Implementation of the Teedev Manager which is an interface between the TEE clients 
 * and the TEE service module.
 */ 

package com.sample.android.service.tee;

import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.util.Slog; 
import java.util.HashSet;
import java.util.Set;


public class TeedevManager {

	private static final String TAG = "TeedevManager";
	private static final String REMOTE_SERVICE_NAME = ITeedevService.class.getName();
	private static final boolean DEBUG = false; 

	private final Set<TeedevListener> listeners = new HashSet<TeedevListener>();

	/* Implementation of ITeedevListener, which will handle the notifications from the remote ITeedevService */
	private final ITeedevListener listener = new ITeedevListener.Stub() {  

		/* This is a provisional functionality, currently not being used in the TEE Demo */

		/* This method will be invoked on the binder thread, so we cannot just call the client’s 
		TeedevListener. Instead, we send the notification to the looper thread via the handler */
		public void onSendTeeRequest (final int result) {       

			if (DEBUG) Slog.d(TAG, "onSendTeeRequest: " + result);
			Message message = TeedevManager.this.handler.obtainMessage();
			message.arg1 = result;
			TeedevManager.this.handler.sendMessage(message);
		}
	};


	/* This is the handle onto the looper thread’s message queue. The association with the looper is 
	implicit, because this handler object is instantiated by the looper thread */
	private final Handler handler = new Handler() {  

		/* This method will be invoked by the looper thread in response to the message sent to us by 
	        the listener */
		@Override
		public void handleMessage(Message message) { 

			int result = message.arg1;
			if (DEBUG) Slog.d(TAG, "Notifying local listeners: " + result);

			/* The handler is guaranteed to run "single-threaded", but the client may register/unregister 
			listeners concurrently, so we should to protect the shared set of listeners */

			synchronized(TeedevManager.this.listeners) {
				for (TeedevListener TeedevListener : TeedevManager.this.listeners) {
					if (DEBUG) Slog.d(TAG, "Notifying local listener [" + TeedevListener
					+ "] of more used data: " + result);
	
					/* Notifying the client’s TeedevListener on the looper thread */
					TeedevListener.onSendTeeRequest (result);
				}
			}
		}
	};

	
	private final ITeedevService service;

	public static TeedevManager getInstance() {
		return new TeedevManager();
	}

	private TeedevManager() {

		Log.d(TAG, "Connecting to ITeedevService by name [" + REMOTE_SERVICE_NAME + "]");
		this.service = ITeedevService.Stub.asInterface(
		ServiceManager.getService(REMOTE_SERVICE_NAME));

		if (this.service == null) {
			throw new IllegalStateException("Failed to find ITeedevService by name [" + REMOTE_SERVICE_NAME + "]");
		}
	}


	/* TEE API wrapper method, which should invoked by a TEE Client app in order to place a TEE request*/	
	public int sendTeeRequest(int inputValue) {

		try {
			if (DEBUG) Slog.d(TAG, "Sending a TEE Request.");

			/* Actual Call directed to the TEE service module */
			return this.service.sendTeeRequest(inputValue); 
		} 
		catch (Exception e) {
			throw new RuntimeException("Failed to send TEE Request", e);
		}
	}

	/* Method exposed to the TEE clients for them to register with TEE service */
	public void register(TeedevListener listener) {

		if (listener != null) {
			synchronized(this.listeners) {
				if (this.listeners.contains(listener)) {
					Log.w(TAG, "Already registered: " + listener);
				} 
				else {
					try {
						boolean registerRemote = this.listeners.isEmpty();
						if (DEBUG) Log.d(TAG, "Registering local listener.");
						this.listeners.add(listener);
						if (registerRemote) {
							if (DEBUG) Log.d(TAG, "Registering remote listener.");
							this.service.register(this.listener);
						}
					} 
					catch (RemoteException e) {
						throw new RuntimeException("Failed to register " + listener, e);
					}
				}
			}
		}
	}

	/* Method exposed to the TEE clients for them to un-register with TEE service */
	public void unregister(TeedevListener listener) {

		if (listener != null) {
			synchronized(this.listeners) {
				if (!this.listeners.contains(listener)) {
					Log.w(TAG, "Not registered: " + listener);
				} 
				else {
					if (DEBUG) Log.d(TAG, "Unregistering local listener.");
					this.listeners.remove(listener);
				}

				if (this.listeners.isEmpty()) {
					try {
						if (DEBUG) Log.d(TAG, "Unregistering remote listener.");
						this.service.unregister(this.listener);
					} 
					catch (RemoteException e) {
						throw new RuntimeException("Failed to unregister " + listener, e);
					}
				}
			}
		}
  	}

}
