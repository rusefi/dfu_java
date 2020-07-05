package com.rusefi.dfu.usb4java;

import com.rusefi.dfu.*;
import org.apache.commons.logging.Log;
import org.usb4java.*;

import java.nio.ByteBuffer;

public class DfuDeviceLocator {

    private static final Log log = LogUtil.getLog(DfuDeviceLocator.class);

    private static StringBuilder usbInfo = new StringBuilder();

    public static Context openContext() {
        Context context = new Context();
        int result = LibUsb.init(context);
        if (result != LibUsb.SUCCESS)
            throw new LibUsbException("init", result);
        log.info("Welcome " + context);
        return context;
    }

    public static USBDfuConnection findDevice(DfuLogic.Logger logger) {
        return findDevice(logger, openContext(), DfuLogic.ST_VENDOR, (short) DfuLogic.ST_DFU_PRODUCT);
    }

    private static USBDfuConnection findDevice(DfuLogic.Logger logger, Context context, short vendorId, short productId) {
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
                    return findDfuInterface(logger, device, descriptor);
                }
            }
        } finally {
            // Ensure the allocated device list is freed
            LibUsb.freeDeviceList(list, true);
        }

        // Device not found
        return null;
    }

    public static int swap16(int x) {
        return (((x & 0xff) << 8) | ((x >> 8) & 0xff));
    }

    public static USBDfuConnection findDfuInterface(DfuLogic.Logger logger, Device device, DeviceDescriptor deviceDescriptor) {
        byte numConfigurations = deviceDescriptor.bNumConfigurations();

        appendInfo(numConfigurations + " configuration(s)");

        DeviceHandle deviceHandle = open(device);
        int transferSize = 0;
        FlashRange flashRange = null;

        for (int configurationIndex = 0; configurationIndex < numConfigurations; configurationIndex++) {
            ConfigDescriptor config = new ConfigDescriptor();
            int result = LibUsb.getConfigDescriptor(device, (byte) configurationIndex, config);
            if (result != LibUsb.SUCCESS) {
                throw new LibUsbException("getConfigDescriptor", result);
            }

            appendInfo("Config: " + config);

            byte numInterfaces = config.bNumInterfaces();
            appendInfo(numInterfaces + " interface(s)");

            for (int interfaceIndex = 0; interfaceIndex < numInterfaces; interfaceIndex++) {
                Interface iface = config.iface()[interfaceIndex];

                for (int s = 0; s < iface.numAltsetting(); s++) {
                    InterfaceDescriptor setting = iface.altsetting()[s];
                    System.out.println("setting " + setting);

                    ByteBuffer extra = setting.extra();

                    if (extra.limit() > 2) {
                        int len = extra.get();
                        byte type = extra.get();
                        if (type == DfuLogic.USB_DT_DFU) {
                            System.out.println(len + " " + type);
                            extra.get(); // bmAttributes
                            extra.get(); // wDetachTimeOut
                            extra.get();
                            transferSize = swap16(extra.getShort());
                            System.out.println("transferSize " + transferSize);
                        }
                    }
                }
            }

            for (int interfaceIndex = 0; interfaceIndex < numInterfaces; interfaceIndex++) {
                Interface iface = config.iface()[interfaceIndex];

                for (int s = 0; s < iface.numAltsetting(); s++) {
                    InterfaceDescriptor setting = iface.altsetting()[s];
                    appendInfo("Interface #" + interfaceIndex + " setting #" + s + ":");

                    byte interfaceNumber = setting.bInterfaceNumber();
                    appendInfo(String.format("Setting %d: %x %x class %x, subclass %x, protocol: %x", s,
                            interfaceNumber,
                            setting.iInterface(),
                            setting.bInterfaceClass(),
                            setting.bInterfaceSubClass(),
                            setting.bInterfaceProtocol()
                    ));
                    String stringDescriptor = LibUsb.getStringDescriptor(deviceHandle, setting.iInterface());
                    appendInfo("Descriptor " + stringDescriptor);
                }
            }

            for (int interfaceIndex = 0; interfaceIndex < numInterfaces; interfaceIndex++) {
                Interface iface = config.iface()[interfaceIndex];

                for (int s = 0; s < iface.numAltsetting(); s++) {
                    InterfaceDescriptor setting = iface.altsetting()[s];

                    log.info("Settings " + setting);
                    byte interfaceNumber = setting.bInterfaceNumber();

                    if (setting.bInterfaceClass() == (byte) DfuLogic.USB_CLASS_APP_SPECIFIC &&
                            setting.bInterfaceSubClass() == DfuLogic.DFU_SUBCLASS) {
                        log.debug(String.format("Found DFU interface: %d", interfaceNumber));

                        String stringDescriptor = LibUsb.getStringDescriptor(deviceHandle, setting.iInterface());
                        log.info("StringDescriptor: " + stringDescriptor);
                        if (stringDescriptor.contains(DfuLogic.FLASH_TAG))
                            flashRange = DfuSeFlashDescriptor.parse(stringDescriptor);

                        result = LibUsb.claimInterface(deviceHandle, interfaceNumber);
                        if (result != LibUsb.SUCCESS) {
                            throw new LibUsbException("claimInterface", result);
                        }

                        USBDfuConnection session = new USBDfuConnection(deviceHandle, interfaceNumber, transferSize, flashRange);

                        DfuLogic.startup(logger, session);


                        System.out.printf("info:\n" + usbInfo);

                        return session;
                    }
                }
            }
        }
        return null;
    }

    private static void appendInfo(String message) {
        log.info(message);
        usbInfo.append(message).append("\n");
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
