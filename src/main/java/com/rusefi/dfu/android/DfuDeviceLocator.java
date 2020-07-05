package com.rusefi.dfu.android;

import android.hardware.usb.*;

import com.rusefi.dfu.DfuLogic;
import com.rusefi.dfu.DfuSeFlashDescriptor;
import com.rusefi.dfu.FlashRange;

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

    public Result openDfu(UsbManager usbManager, UsbDevice dfuDevice) {
        for (int interfaceIndex = 0; interfaceIndex < dfuDevice.getInterfaceCount(); interfaceIndex++) {
            UsbInterface usbInterface = dfuDevice.getInterface(interfaceIndex);
            String stringDescriptor = usbInterface.getName();
            if (usbInterface.getInterfaceClass() == DfuLogic.USB_CLASS_APP_SPECIFIC &&
                    usbInterface.getInterfaceSubclass() == DfuLogic.DFU_SUBCLASS &&
                    stringDescriptor.contains(DfuLogic.FLASH_TAG)) {
//                log.debug(String.format("Found DFU interface: " + usbInterface));

                FlashRange flashRange = DfuSeFlashDescriptor.parse(stringDescriptor);

                UsbDeviceConnection connection = usbManager.openDevice(dfuDevice);

                //UsbInterface intf = dfuDevice.getInterface(0);
                connection.claimInterface(usbInterface, true);

                byte[] rawDescs = connection.getRawDescriptors();
                // todo: need proper handling of this rawDesc area since I have no clue what's the format
                if (rawDescs.length != 72)
                    throw new IllegalStateException("Unexpected RawDescriptors length");
                if (rawDescs[64] != DfuLogic.USB_DT_DFU)
                    throw new IllegalStateException("Unexpected USB_DT_DFU");
                int transferSize = rawDescs[69] * 256 + rawDescs[68];

                return new Result(connection, interfaceIndex, flashRange, transferSize);
            }
        }
        return null;
    }

    public static class Result {
        private final UsbDeviceConnection connection;
        private final int interfaceIndex;
        private final FlashRange flashRange;
        private final int transferSize;

        public Result(UsbDeviceConnection connection, int interfaceIndex, FlashRange flashRange, int transferSize) {
            this.connection = connection;
            this.interfaceIndex = interfaceIndex;
            this.flashRange = flashRange;
            this.transferSize = transferSize;
        }

        public UsbDeviceConnection getConnection() {
            return connection;
        }

        public FlashRange getFlashRange() {
            return flashRange;
        }

        public int getInterfaceIndex() {
            return interfaceIndex;
        }

        public int getTransferSize() {
            return transferSize;
        }
    }
}
