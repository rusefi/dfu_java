package com.rusefi.dfu;

import com.rusefi.dfu.commands.DfuCommandGetStatus;
import com.rusefi.dfu.usb4java.USBDfuConnection;

import java.nio.ByteBuffer;

public interface DfuConnection {
    int SECOND = 1000;
    int DFU_TIMEOUT = 10 * DfuConnection.SECOND;

    static void waitStatus(USBDfuConnection device) {
        DfuCommandGetStatus.State state = DfuCommandGetStatus.read(device);
        System.out.println(" state " + state);
        while (state == DfuCommandGetStatus.State.DFU_DOWNLOAD_BUSY || state == DfuCommandGetStatus.State.DFU_ERROR) {
            sleep(106);
            state = DfuCommandGetStatus.read(device);
            System.out.println(" state " + state);
        }
    }

    static void sleep(int millis) {
        System.out.println("Sleep " + millis);
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    FlashRange getFlashRange();

    int getTransferSize();

    int receiveData(DfuCommmand command, short wValue, ByteBuffer data);

    int sendData(DfuCommmand command, short wValue, ByteBuffer data);
}
