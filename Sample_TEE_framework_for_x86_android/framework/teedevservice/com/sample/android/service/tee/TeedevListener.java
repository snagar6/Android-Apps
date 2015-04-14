/*
 * TeedevListener.java: A Java interface which hides complexity of Binder/threading from the developers
 * who intend to develop TEE Client applications.
 */

package com.sample.android.service.tee;

public interface TeedevListener {

	/* Invoked on the main/looper/UI thread */

	/* A provisional callback, which will enable the TEE clients to subscribe (via the manager)
        with the service and receive notifications based on the occurance of particular events */

	public void onSendTeeRequest (int result);
}

