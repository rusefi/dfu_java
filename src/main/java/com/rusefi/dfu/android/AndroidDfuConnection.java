package com.rusefi.dfu.android;

import android.hardware.usb.UsbDeviceConnection;
import com.rusefi.dfu.DfuCommmand;
import com.rusefi.dfu.DfuConnection;
import com.rusefi.dfu.FlashRange;

import java.nio.ByteBuffer;

import static android.hardware.usb.UsbConstants.USB_DIR_IN;
import static android.hardware.usb.UsbConstants.USB_DIR_OUT;

public class AndroidDfuConnection implements DfuConnection {
    private final UsbDeviceConnection usbDeviceConnection;
    private final int interfaceNumber;
    private final int transferSize;
    private final FlashRange flashRange;

    private static final byte REQUEST_TYPE_CLASS = 32;
    private static final byte RECIPIENT_INTERFACE = 0x01;

    public AndroidDfuConnection(UsbDeviceConnection usbDeviceConnection, int interfaceNumber, int transferSize, FlashRange flashRange) {
        this.usbDeviceConnection = usbDeviceConnection;
        this.interfaceNumber = interfaceNumber;
        this.transferSize = transferSize;
        this.flashRange = flashRange;
    }

    @Override
    public FlashRange getFlashRange() {
        return flashRange;
    }

    @Override
    public int getTransferSize() {
        return transferSize;
    }

    @Override
    public int receiveData(DfuCommmand command, short wValue, ByteBuffer data) {
        return transfer(usbDeviceConnection, USB_DIR_IN, command.getValue(), wValue, data);
    }

    @Override
    public int sendData(DfuCommmand command, short wValue, ByteBuffer data) {
        return transfer(usbDeviceConnection, USB_DIR_OUT, command.getValue(), wValue, data);
    }

    @Override
    public ByteBuffer allocateBuffer(int capacity) {
        // on Android direct buffer comes with arrayOffset which is just too much trouble
        return ByteBuffer.allocate(capacity);
    }

    private int transfer(UsbDeviceConnection connection, int direction, int request, short wValue, ByteBuffer byteBuffer) {
        if (!byteBuffer.hasArray() || byteBuffer.arrayOffset() != 0)
            throw new IllegalArgumentException("Need a simpler ByteArray");
        return connection.controlTransfer(REQUEST_TYPE_CLASS | RECIPIENT_INTERFACE | direction, request,
                wValue, interfaceNumber, byteBuffer.array(), byteBuffer.limit(), DFU_TIMEOUT);
    }
}
