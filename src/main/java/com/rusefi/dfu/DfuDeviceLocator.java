package com.rusefi.dfu;

import org.apache.commons.logging.Log;
import org.usb4java.*;

public class DfuDeviceLocator {
    private static final short ST_VENDOR = 0x0483;
    private static final short ST_DFU_PRODUCT = (short) 0xdf11;

    private static final Log log = LogUtil.getLog(DfuDeviceLocator.class);

    public static Device findDevice() {
        return findDevice(openContext(), ST_VENDOR, ST_DFU_PRODUCT);
    }

    public static Device findDevice(Context context, short vendorId, short productId) {
        // Read the USB device list
        DeviceList list = new DeviceList();
        int result = LibUsb.getDeviceList(context, list);
        if (result < 0)
            throw new LibUsbException("Unable to get device list", result);
        log.info(list.getSize() + " device(s) found");

        try {
            // Iterate over all devices and scan for the right one
            for (Device device : list) {
                DeviceDescriptor descriptor = new DeviceDescriptor();
                result = LibUsb.getDeviceDescriptor(device, descriptor);
//                System.out.println("I see " + descriptor);
                if (result != LibUsb.SUCCESS)
                    throw new LibUsbException("Unable to read device descriptor", result);
                if (descriptor.idVendor() == vendorId && descriptor.idProduct() == productId)
                    return device;
            }
        } finally {
            // Ensure the allocated device list is freed
            LibUsb.freeDeviceList(list, true);
        }

        // Device not found
        return null;
    }

    public static Context openContext() {
        Context context = new Context();
        int result = LibUsb.init(context);
        if (result != LibUsb.SUCCESS)
            throw new LibUsbException("Unable to initialize libusb.", result);
        log.info("Welcome " + context);
        return context;
    }
}
