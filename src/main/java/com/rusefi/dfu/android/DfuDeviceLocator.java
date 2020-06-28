package com.rusefi.dfu.android;

import android.hardware.usb.*;
import com.rusefi.dfu.DfuLogic;
import com.rusefi.dfu.LogUtil;
import org.apache.commons.logging.Log;

public class DfuDeviceLocator {
    private static final Log log = LogUtil.getLog(DfuDeviceLocator.class);

    private static UsbDevice findDevice(UsbManager usbManager) {
        for (final UsbDevice usbDevice : usbManager.getDeviceList().values()) {
            if (usbDevice.getVendorId() == DfuLogic.ST_VENDOR && usbDevice.getProductId() == DfuLogic.ST_DFU_PRODUCT) {
                return usbDevice;
            }
        }
        return null;
    }


    public short xxx(UsbManager usbManager) {
        UsbDevice dfuDevice = findDevice(usbManager);

        for (int interfaceIndex = 0; interfaceIndex < dfuDevice.getInterfaceCount(); interfaceIndex++) {
            UsbInterface usbInterface = dfuDevice.getInterface(interfaceIndex);


        }

        for (int interfaceIndex = 0; interfaceIndex < dfuDevice.getInterfaceCount(); interfaceIndex++) {
            UsbInterface usbInterface = dfuDevice.getInterface(interfaceIndex);
            if (usbInterface.getInterfaceClass() == DfuLogic.USB_CLASS_APP_SPECIFIC &&
                    usbInterface.getInterfaceSubclass() == DfuLogic.DFU_SUBCLASS) {
                log.debug(String.format("Found DFU interface: " + usbInterface));


                UsbDeviceConnection connection = usbManager.openDevice(dfuDevice);


            }


        }

        return 0;
    }

}
