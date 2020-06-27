package com.rusefi.dfu.usb4java;

import com.rusefi.dfu.commands.DfuCommandClearStatus;
import com.rusefi.dfu.commands.DfuCommandGetStatus;
import org.apache.commons.logging.Log;
import org.usb4java.*;

public class DfuDeviceLocator {
    private static final short ST_VENDOR = 0x0483;
    private static final short ST_DFU_PRODUCT = (short) 0xdf11;

    private static final byte USB_CLASS_APP_SPECIFIC = (byte) 0xfe;
    private static final byte DFU_SUBCLASS = 0x01;

    private static final Log log = LogUtil.getLog(DfuDeviceLocator.class);

    public static Context openContext() {
        Context context = new Context();
        int result = LibUsb.init(context);
        if (result != LibUsb.SUCCESS)
            throw new LibUsbException("init", result);
        log.info("Welcome " + context);
        return context;
    }

    public static USBDfuConnection findDevice() {
        return findDevice(openContext(), ST_VENDOR, ST_DFU_PRODUCT);
    }

    private static USBDfuConnection findDevice(Context context, short vendorId, short productId) {
        // Read the USB device list
        DeviceList list = new DeviceList();
        int result = LibUsb.getDeviceList(context, list);
        if (result < 0)
            throw new LibUsbException("getDeviceList", result);
        log.info(list.getSize() + " device(s) found");

        try {
            // Iterate over all devices and scan for the right one
            for (Device device : list) {
                DeviceDescriptor descriptor = new DeviceDescriptor();
                result = LibUsb.getDeviceDescriptor(device, descriptor);
//                System.out.println("I see " + descriptor);
                if (result != LibUsb.SUCCESS)
                    throw new LibUsbException("getDeviceDescriptor", result);
                if (descriptor.idVendor() == vendorId && descriptor.idProduct() == productId) {
                    return findDfuInterface(device, descriptor);
                }
            }
        } finally {
            // Ensure the allocated device list is freed
            LibUsb.freeDeviceList(list, true);
        }

        // Device not found
        return null;
    }

    public static USBDfuConnection findDfuInterface(Device device, DeviceDescriptor deviceDescriptor) {
        byte numConfigurations = deviceDescriptor.bNumConfigurations();
        log.info(numConfigurations + " configuration(s)");
        for (int configurationIndex = 0; configurationIndex < numConfigurations; configurationIndex++) {
            ConfigDescriptor config = new ConfigDescriptor();
            int result = LibUsb.getConfigDescriptor(device, (byte) configurationIndex, config);
            if (result != LibUsb.SUCCESS) {
                throw new LibUsbException("getConfigDescriptor", result);
            }

            byte numInterfaces = config.bNumInterfaces();
            log.info(numInterfaces + " interface(s)");

            for (int interfaceIndex = 0; interfaceIndex < numInterfaces; interfaceIndex++) {
                Interface iface = config.iface()[interfaceIndex];

                for (int s = 0; s < iface.numAltsetting(); s++) {
                    InterfaceDescriptor setting = iface.altsetting()[s];
                    log.info("Set " + setting);
                    byte interfaceNumber = setting.bInterfaceNumber();
                    log.info(String.format("Setting %d: %x %x class %x, subclass %x, protocol: %x", s,
                            interfaceNumber,
                            setting.iInterface(),
                            setting.bInterfaceClass(), setting.bInterfaceSubClass(),
                            setting.bInterfaceProtocol()
                    ));

                    if (setting.bInterfaceClass() == USB_CLASS_APP_SPECIFIC &&
                            setting.bInterfaceSubClass() == DFU_SUBCLASS) {
                        log.debug(String.format("Found DFU interface: %d", interfaceNumber));

                        DeviceHandle deviceHandle = open(device);

                        result = LibUsb.claimInterface(deviceHandle, interfaceNumber);
                        if (result != LibUsb.SUCCESS) {
                            throw new LibUsbException("claimInterface", result);
                        }

                        USBDfuConnection session = new USBDfuConnection(deviceHandle, interfaceNumber);

                        DfuCommandGetStatus.State state = DfuCommandGetStatus.read(session);
                        log.info("DFU state: " + state);
                        switch (state) {
                            case DFU_IDLE:
                                // best status
                                break;
                            case DFU_ERROR:
                                DfuCommandClearStatus.execute(session);
                                break;
                            default:
                                throw new IllegalStateException("Unexpected state " + state);
                        }
                        state = DfuCommandGetStatus.read(session);
                        if (state != DfuCommandGetStatus.State.DFU_IDLE)
                            throw new IllegalStateException("Not idle");

                        return session;
                    }
                }
            }
        }
        return null;
    }

    private static DeviceHandle open(Device device) {
        DeviceHandle deviceHandle = new DeviceHandle();
        int result = LibUsb.open(device, deviceHandle);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException("open", result);
        }
        result = LibUsb.setConfiguration(deviceHandle, 1);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException("setConfiguration", result);
        }
        return deviceHandle;
    }

}
