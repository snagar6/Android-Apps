/*
 * ITeedevService.aidl: AIDL interface to define the TEE service.
 */

package com.sample.android.service.tee;

import com.sample.android.service.tee.ITeedevListener;

/**
 * System-private API for talking to the TeedevService.
 *
 * {@hide}
 */

interface ITeedevService {

	/* TEE API wrapper method, can be invoked by a TEE Client app */
	int sendTeeRequest (int inputValue);

	/* Methods exposed to TEE client for them to register/un-register with TEE service */
	void register(in ITeedevListener listener);
	void unregister(in ITeedevListener listener);
}
