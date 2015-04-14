/*
 * Main.java: A template java class representing the usage of the TEE JNI interface through java calls
 * This is just an example meant for testing only, not used by the final TEE framework.
 */

package com.sample.android.lib.tee;

/** @hide */

public class Main {

	public static void main (String[] args) {

		try {
			int handle = Tee.init();
			int input1 = 2000;
			int val1 = 0;

			try {
				val1 = Tee.nativeFuncSendMessage (handle, input1);
			} 
			finally {
				Tee.close(handle);
      			}

		} 

		catch (TeeException e) {
			System.err.println("Failed to do the TEE JNI Opertions");
			e.printStackTrace();
		}
	}
}
