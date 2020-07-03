package com.rusefi.dfu;

import java.nio.ByteBuffer;

public interface DfuConnection {
    int SECOND = 1000;
    int DFU_TIMEOUT = 10 * DfuConnection.SECOND;

    FlashRange getFlashRange();

    int getTransferSize();

    int receiveData(DfuCommmand command, short wValue, ByteBuffer data);

    int sendData(DfuCommmand command, short wValue, ByteBuffer data);
}
