/*
 * com_sample_android_lib_Tee.cpp: Native interface of the TEE JNI library; The methods defined here would
 * be called by the TEE services layer of the TEE framework.
 */ 

#include <jni.h>
#include <tee_dev.h>
#include <hardware/hardware.h>
#include "JNIHelp.h"


/* Custom jni exception defined for the TEE JNI interface */
static void throwTeeException(JNIEnv *env, const char *msg) {

	jniThrowException(env, "com/sample/android/lib/tee/TeeException", msg);
}


/* Initialzing the TEE HAL module */
static jint native_init(JNIEnv *env, jclass clazz) {

	hw_module_t* module;
	
	/* Getting the handle to the TEE HAL (hardware abstraction layer) module */
	int err = hw_get_module(TEEDEV_HARDWARE_MODULE_ID, (hw_module_t const**)&module);
	if (err) {
		throwTeeException(env, "Failed to get module");
		return -1;
	}


	struct teedev_device_t *dev;

	/* Opening the TEE device */
	err = module->methods->open(module, 0, (struct hw_device_t **) &dev);
	if (err) {
		throwTeeException(env, "Failed to open device");
		return -1;
	}

	return (jint) dev;
}


/* Closing the reference to the TEE HAL module */
static void native_close(JNIEnv *env, jclass clazz, jint handle) {

	struct teedev_device_t *dev = (struct teedev_device_t *) handle;
	dev->common.close((struct hw_device_t *)dev);
}


/* JNI method for one of the TEE API - Send Message/Request  */
static jint nativeFuncSendMessage (JNIEnv *env, jclass clazz, jint handle, jint input) {

        struct teedev_device_t *dev = (struct teedev_device_t *) handle;

	/* Native call into the TEE HAL layer */
        int ret = dev->native_func_send_message(input);
        if (ret < 0) {
                throwTeeException(env, "TEEDEV: Failed to Call the native_func_send_message ");
        }

        return ret;
}


/* JNI method for one of the TEE API - Currently, a place holder/ dummy API */
static jint nativeFuncRegister (JNIEnv *env, jclass clazz, jint handle) {

        struct teedev_device_t *dev = (struct teedev_device_t *) handle;

	/* Native call into the TEE HAL layer */
        int ret = dev->native_func_register();
        if (ret < 0) {
                throwTeeException(env, "TEEDEV: Failed to Call the native_func_register ");
        }

        return ret;
}


/* JNI method for one of the TEE API - Currently, a place holder/ dummy API */
static jint nativeFuncUnregister (JNIEnv *env, jclass clazz, jint handle) {

        struct teedev_device_t *dev = (struct teedev_device_t *) handle;

	/* Native call into the TEE HAL layer */
        int ret = dev->native_func_unregister();
        if (ret < 0) {
                throwTeeException(env, "TEEDEV: Failed to Call the native_func_unregister ");
        }

        return ret;
}


/* List of TEE JNI methods */
static JNINativeMethod method_table[] = {

  { "init", "()I", (void *) native_init },
  { "close", "(I)V", (void *) native_close },
  { "nativeFuncRegister", "(I)I", (void *) nativeFuncRegister },
  { "nativeFuncUnregister", "(I)I", (void *) nativeFuncUnregister },
  { "nativeFuncSendMessage", "(II)I", (void *) nativeFuncSendMessage }, 
};


/* Registering all the TEE JNI methods */ 
extern "C" jint JNI_OnLoad(JavaVM* vm, void* reserved) {

	JNIEnv* env = NULL;

	if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) == JNI_OK) {
		if (jniRegisterNativeMethods(env, "com/sample/android/lib/tee/Tee",
		    method_table, NELEM(method_table)) == 0) {
			return JNI_VERSION_1_4;
		}
	}

	return JNI_ERR;
}

