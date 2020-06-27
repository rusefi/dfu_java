package com.rusefi.dfu.usb4java;

import com.rusefi.dfu.DfuCommmand;
import com.rusefi.dfu.DfuConnection;
import com.rusefi.dfu.FlashRange;
import org.usb4java.DeviceHandle;
import org.usb4java.LibUsb;

import java.nio.ByteBuffer;

public class USBDfuConnection implements DfuConnection {
    private final DeviceHandle deviceHandle;
    private final byte interfaceNumber;
    private final int transferSize;
    private final FlashRange flashRange;

    public USBDfuConnection(DeviceHandle deviceHandle, byte interfaceNumber, int transferSize, FlashRange flashRange) {
        this.deviceHandle = deviceHandle;
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
    public int receiveData(DfuCommmand command, short value, ByteBuffer data) {
        return transfer(command, value, data, LibUsb.ENDPOINT_IN);
    }

    @Override
    public int sendData(DfuCommmand command, short value, ByteBuffer data) {
        return transfer(command, value, data, LibUsb.ENDPOINT_OUT);
    }

    private int transfer(DfuCommmand command, short value, ByteBuffer data, byte mode) {
        return LibUsb.controlTransfer(
                deviceHandle,
                (byte) (mode | LibUsb.REQUEST_TYPE_CLASS | LibUsb.RECIPIENT_INTERFACE),
                command.getValue(),
                value,
                interfaceNumber,
                data,
                DFU_TIMEOUT);
    }
}
