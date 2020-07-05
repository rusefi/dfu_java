package com.rusefi.dfu.usb4java;

import com.rusefi.dfu.DfuCommmand;
import com.rusefi.dfu.DfuConnection;
import com.rusefi.dfu.FlashRange;
import org.usb4java.DeviceHandle;
import org.usb4java.LibUsb;

import java.nio.ByteBuffer;
import java.util.Objects;

public class USBDfuConnection implements DfuConnection {
    private final DeviceHandle deviceHandle;
    private final byte interfaceNumber;
    private final int transferSize;
    private final FlashRange flashRange;

    public USBDfuConnection(DeviceHandle deviceHandle, byte interfaceNumber, int transferSize, FlashRange flashRange) {
        if (transferSize == 0)
            throw new IllegalArgumentException("transfer size not detected");
        Objects.requireNonNull(flashRange, "flashRange");
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
    public int receiveData(DfuCommmand command, short wValue, ByteBuffer data) {
        return transfer(command, wValue, data, LibUsb.ENDPOINT_IN);
    }

    @Override
    public int sendData(DfuCommmand command, short wValue, ByteBuffer data) {
        return transfer(command, wValue, data, LibUsb.ENDPOINT_OUT);
    }

    @Override
    public ByteBuffer allocateBuffer(int capacity) {
        // usb4java requires direct buffer
        return ByteBuffer.allocateDirect(capacity);
    }

    private int transfer(DfuCommmand command, short wValue, ByteBuffer data, byte mode) {
        return LibUsb.controlTransfer(
                deviceHandle,
                (byte) (mode | LibUsb.REQUEST_TYPE_CLASS | LibUsb.RECIPIENT_INTERFACE),
                command.getValue(),
                wValue,
                interfaceNumber,
                data,
                DFU_TIMEOUT);
    }
}
