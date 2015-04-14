/*
 * ITeeServiceImpl.java: This is the actual implementation of the TEE service which is built 
 * over the TEE service stubs and interfaces.
 */

package com.sample.android.teeservice;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import com.sample.android.service.tee.ITeedevListener;
import com.sample.android.service.tee.ITeedevService;
import com.sample.android.lib.tee.Tee;
import com.sample.android.lib.tee.TeeException;


class ITeeServiceImpl extends ITeedevService.Stub {

	private static final String TAG = "ITeeServiceImpl";
	private static final boolean DEBUG = false;
	private final Map<IBinder, ListenerTracker> listeners = new HashMap<IBinder, ListenerTracker>();
	private final Context context;
	private TeeServiceThread teeServiceThread;
	private int nativeHandle;


	ITeeServiceImpl(Context context) {

		this.context = context;

		/* Getting a handle to communicate with the TEE native layer/HAL module */
		this.nativeHandle = Tee.init();
	}


	protected void finalize() throws Throwable {

		/* Destroying the TEE native layer/HAL module handle */
		Tee.close(this.nativeHandle);

		super.finalize();
	}


	public int sendTeeRequest(int inputValue) {

		/* Checking for the permissions "TEE REQUEST" which the TEE client needs to have */
		this.context.enforceCallingOrSelfPermission(Manifest.permission.TEE_REQUEST, "TEE Allowed");

		/* Making the corresponding JNI native call of the TEE framework's Send Message API */
		return Tee.nativeFuncSendMessage (this.nativeHandle, inputValue);
	}


	/* This can be called at the time when a TEE Client registers with the TEE service.
        This is currently not being used to do anything in specific. However, this is more
        of a provision made for any future use */
            	
	public void register(ITeedevListener listener) throws RemoteException {

		if (listener != null) {
			IBinder binder = listener.asBinder();

			/* A TEE Client can be put onto the queue of listeners (clients) who could be 
			possibly waiting for the TEE service to notify them upon the occurance of a
			particular event */
 
			synchronized(this.listeners) {
				if (this.listeners.containsKey(binder)) {
					Slog.w(TAG, "Ignoring duplicate listener registration attempt: " + binder);
				} 
				else {
					ListenerTracker listenerTracker = new ListenerTracker(listener);
					binder.linkToDeath(listenerTracker, 0);
					this.listeners.put(binder, listenerTracker);
          
					/* Optionally, a thread can be created to service the client threads */ 
					if (this.teeServiceThread == null) {
						this.teeServiceThread = new TeeServiceThread();
						this.teeServiceThread.start();
					}
				}
			}
    		}
  	}


	/* This can be called at the time when a TEE Client un-registers with the TEE service.
        This is currently not being used to do anything in specific. This is complementary
	to the register method above. */

	public void unregister(ITeedevListener listener) {

		if (listener != null) {
			IBinder binder = listener.asBinder();

			/* Removing the TEE Client from the queue of listeners */ 
			synchronized(this.listeners) {
				ListenerTracker listenerTracker = this.listeners.remove(binder);
				if (listenerTracker == null) {
					Slog.w(TAG, "Ignoring unregistered listener unregistration: " + binder);
				} 
				else {
					binder.unlinkToDeath(listenerTracker, 0);
			
					/* Interrupting the service thread and killing it */
					if (this.teeServiceThread != null && this.listeners.isEmpty()) {
						this.teeServiceThread.interrupt();
						this.teeServiceThread = null;
					}
				}
			}
		}
	}


	/* The TEE client threads will have to be notified when a the service process dies. 
        The death recipient interface is hence implemented to ensure that all the client
	threads un-register themselves from the TEE service */

	private final class ListenerTracker implements IBinder.DeathRecipient {

		private final ITeedevListener listener;

		public ListenerTracker(ITeedevListener listener) {
			this.listener = listener;
		}

		public ITeedevListener getListener() {
			return this.listener;
		}

		public void binderDied() {
			ITeeServiceImpl.this.unregister(this.listener);
		}
	}


	/*  A sample service Thread implementation - Currently, Not used */
	private final class TeeServiceThread extends Thread {

		public void run() {

			/*
			while(!Thread.interrupted()) {

				try {
					int result = ITeeServiceImpl.this.sendTeeRequest(threadInputValue);

					synchronized(ITeeServiceImpl.this.listeners) {

						for(ListenerTracker listenerTracker:ITeeServiceImpl.this.listeners.values()) {
							try {
								listenerTracker.getListener().onSendTeeRequest(result);
							} 
							catch (RemoteException e) {
								ITeeServiceImpl.this.unregister(listenerTracker.getListener());
							}
						}
					}
       		 		} 
				catch (TeeException e) {
					try {
						Thread.sleep(2000);
					} 
					catch (InterruptedException e2) {
						break;
      		  	 	 	}
       				 }
			}
			*/
		}
	}

}

