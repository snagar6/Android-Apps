LOCAL_PATH := $(call my-dir)
 
include $(CLEAR_VARS)
 
# Here we give our module name and source file(s)
LOCAL_MODULE    := ndkmalware
LOCAL_SRC_FILES := ndkmalware.c
 
include $(BUILD_SHARED_LIBRARY)