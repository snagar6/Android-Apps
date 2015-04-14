/*
 * ITeedevListener.aidl: AIDL-definition of a TEE client-side listener
 */

package com.sample.android.service.tee;

/**
 * Listener
 *
 * {@hide}
 */

oneway interface ITeedevListener {
	
	/* A provisional callback, which will enable the TEE clients to subscribe (via the manager) 
	with the service and receive notifications based on the occurance of particular events */

	/* Currently, not used for the TEE Demo */ 
	void onSendTeeRequest (int result);
}
