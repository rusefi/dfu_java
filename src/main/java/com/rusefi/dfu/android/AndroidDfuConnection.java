package com.rusefi.dfu.android;

import android.hardware.usb.UsbDeviceConnection;
import com.rusefi.dfu.DfuCommmand;
import com.rusefi.dfu.DfuConnection;
import com.rusefi.dfu.FlashRange;

import java.nio.ByteBuffer;

public class AndroidDfuConnection implements DfuConnection {
    private final UsbDeviceConnection usbDeviceConnection;
    private final FlashRange flashRange;

    public AndroidDfuConnection(UsbDeviceConnection usbDeviceConnection, FlashRange flashRange) {
        this.usbDeviceConnection = usbDeviceConnection;
        this.flashRange = flashRange;
    }

    @Override
    public FlashRange getFlashRange() {
        return flashRange;
    }

    @Override
    public int getTransferSize() {
        return 0;
    }

    @Override
    public int receiveData(DfuCommmand command, short wValue, ByteBuffer data) {
        //return usbDeviceConnection.controlTransfer();
        return 0;
    }

    @Override
    public int sendData(DfuCommmand command, short wValue, ByteBuffer data) {
        return 0;
    }
}
