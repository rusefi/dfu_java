package com.rusefi.dfu.android;

import android.hardware.usb.*;

import com.rusefi.dfu.DfuLogic;
//import com.rusefi.dfu.LogUtil;
//import org.apache.commons.logging.Log;

public class DfuDeviceLocator {
//    private static final Log log = LogUtil.getLog(DfuDeviceLocator.class);


    public static UsbDevice findDevice(UsbManager usbManager) {
        for (final UsbDevice usbDevice : usbManager.getDeviceList().values()) {
            if (usbDevice.getVendorId() == DfuLogic.ST_VENDOR && usbDevice.getProductId() == DfuLogic.ST_DFU_PRODUCT) {
                return usbDevice;
            }
        }
        return null;
    }


    public short openDfu(UsbManager usbManager, UsbDevice dfuDevice) {

        for (int interfaceIndex = 0; interfaceIndex < dfuDevice.getInterfaceCount(); interfaceIndex++) {
            UsbInterface usbInterface = dfuDevice.getInterface(interfaceIndex);

        }

        for (int interfaceIndex = 0; interfaceIndex < dfuDevice.getInterfaceCount(); interfaceIndex++) {
            UsbInterface usbInterface = dfuDevice.getInterface(interfaceIndex);
            if (usbInterface.getInterfaceClass() == DfuLogic.USB_CLASS_APP_SPECIFIC &&
                    usbInterface.getInterfaceSubclass() == DfuLogic.DFU_SUBCLASS) {
//                log.debug(String.format("Found DFU interface: " + usbInterface));


                UsbDeviceConnection connection = usbManager.openDevice(dfuDevice);

                UsbInterface intf = dfuDevice.getInterface(0);
                connection.claimInterface(intf, true);

                byte[] rawDescs = connection.getRawDescriptors();
                // todo: need proper handling of this rawDesc area since I have no clue what's the format
                if (rawDescs.length != 72)
                    throw new IllegalStateException("Unexpected RawDescriptors length");
                if (rawDescs[64] != DfuLogic.USB_DT_DFU)
                    throw new IllegalStateException("Unexpected USB_DT_DFU");
                int transferSize = rawDescs[69] * 256 + rawDescs[68];

                System.out.println(rawDescs + " " + transferSize);

            }


        }

        return 0;
    }

}