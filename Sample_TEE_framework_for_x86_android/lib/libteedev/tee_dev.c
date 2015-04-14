/*
 * tee_dev.c - Hardware Abstraction layer/library; Communicates with the inter partition TEE device driver. 
 */


#include <tee_dev.h>
#include <fcntl.h>
#include <poll.h>
#include <errno.h>
#include <sys/ioctl.h>
#include <cutils/log.h>
#include <cutils/native_handle.h>

/* TEE Device Node */
#define TEE_DEV_NODE	"/dev/tee_comm_drv"


/* Opening the connection with the TEE inter-partition device driver */
int initiate_driver_conn() {
	
	int fd = 0;

	if ((fd = open (TEE_DEV_NODE, O_RDWR)) == -1) {
		LOGD("libteedev:  Error opening - %s  Error number - %d \n", TEE_DEV_NODE, errno);
		return -1;
	}

	return (fd);
}


/* Closing the connection to the TEE inter-partition device driver */
void terminate_driver_conn (int fd) {
	close(fd);
}
 

/* Native TEE API implementation - for sending a message/request accross the partition */
static int native_func_send_message(int input_message){
	
	int fd = 0;
        int ret = 0;
	int retval = 0;
	s_msg msg_put;
	s_msg msg_get;

	/* Initiating the connection with the TEE device node (TEE inter-partition device driver) */
        fd = initiate_driver_conn();
        if(fd < 0) {
		LOGD("libteedev: Error opening the driver - %s", TEE_DEV_NODE);	
		return -1;
        }

	/* Building the message payload */
	msg_put.data = input_message;
        msg_put.msg_size = (sizeof(int)*2);

	/* Sending the Message to the driver */	
	ret = ioctl(fd, PUT_MSG, (char*) &msg_put);
        if (ret < 0) {
		LOGD("libteedev: Error with PUT MSG  Ret - %d", ret);
		terminate_driver_conn(fd);
                return -1;
        }

	msg_get.msg_size = 0;
	msg_get.data = 0;

	/* Loop until a Valid data/msg is returned back from the TEE inter-partition Driver */
	while (msg_get.msg_size == 0) {

		/* Getting back a message from the driver */
		retval = ioctl(fd, GET_RESP_MSG, (char*) &msg_get);
        	if (retval < 0) {
               		LOGD("libteedev: Error with GET_RESP_MSG  Ret - %d", retval);
			terminate_driver_conn(fd);
                	return -1; 
        	}
	}

	/* Closing the connection with the TEE device node (TEE inter-partition device driver) */
        terminate_driver_conn(fd);

	return (msg_get.data);
}


/* Place holder for TEE APIs */
static int native_func_unregister() {
        return 1;
}


/* Place holder for TEE APIs */
static int native_func_register() {
        return 1;
}


/* De-allocate the TEE device node */ 
static int close_teedev(struct teedev_device_t* dev) {

	if (dev)
		free(dev);

	return 0;
}


/* Creation of the TEE HAL (hardware abstraction layer) interface by registering all the methods  */
static int open_teedev(const struct hw_module_t *module, char const *name, struct hw_device_t **device) {

	struct teedev_device_t *dev = malloc(sizeof(struct teedev_device_t));
	if (!dev) {
		return -ENOMEM;
	}

    	memset(dev, 0, sizeof(*dev));

    	dev->common.tag = HARDWARE_DEVICE_TAG;
	dev->common.version = 0;
	dev->common.module = (struct hw_module_t *)module;
	dev->common.close = (int (*)(struct hw_device_t *)) close_teedev;

	dev->native_func_register = native_func_register;
	dev->native_func_unregister = native_func_unregister;
	dev->native_func_send_message = native_func_send_message;
	
	*device = (struct hw_device_t *)dev;
  	
	return 0;
}


static struct hw_module_methods_t teedev_module_methods = {

	.open = open_teedev,
};


/* TEE HAL (hardware abstraction layer) module */
struct hw_module_t HAL_MODULE_INFO_SYM = {

	.tag = HARDWARE_MODULE_TAG,
	.version_major = 1,
	.version_minor = 0,
	.id = TEEDEV_HARDWARE_MODULE_ID,
	.name = "teedev module",
	.author = "sample tee team",
	.methods = &teedev_module_methods,
};
