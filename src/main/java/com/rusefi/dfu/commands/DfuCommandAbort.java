package com.rusefi.dfu.commands;

import com.rusefi.dfu.DfuCommmand;
import com.rusefi.dfu.DfuConnection;

import java.nio.ByteBuffer;

public class DfuCommandAbort {
    public static void execute(DfuConnection connection) {
        ByteBuffer buffer = connection.allocateBuffer(0);
        connection.sendData(DfuCommmand.ABORT, (short) 0, buffer);
    }
}
