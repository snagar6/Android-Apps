/*
 * Tee.java:  Exposing the TEE JNI methods via Java interface.
 */ 

package com.sample.android.lib.tee;

public class Tee {

	/* Proto-types for the TEE JNI methods */
	public native static int init() throws TeeException;
	public native static void close(int handle);
	public native static int nativeFuncRegister (int handle) throws TeeException;
	public native static int nativeFuncUnregister (int handle) throws TeeException;
	public native static int nativeFuncSendMessage (int handle, int input) throws TeeException;

	/* Loading the TEE JNI library */ 
	static {
		System.loadLibrary("teedev_jni");
	}
}

