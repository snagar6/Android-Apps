LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := $(call all-java-files-under,src)
LOCAL_REQUIRED_MODULES := \
    com.sample.android.service.tee \
    com.sample.android.lib.tee
LOCAL_JAVA_LIBRARIES := \
    com.sample.android.service.tee \
    com.sample.android.lib.tee \
    core \
    framework
LOCAL_PACKAGE_NAME := DSTeeService
LOCAL_SDK_VERSION := current
LOCAL_PROGUARD_ENABLED := disabled
LOCAL_CERTIFICATE := platform
include $(BUILD_PACKAGE)
