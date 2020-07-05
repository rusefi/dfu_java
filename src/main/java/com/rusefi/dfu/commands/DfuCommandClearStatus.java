package com.rusefi.dfu.commands;

import com.rusefi.dfu.DfuCommmand;
import com.rusefi.dfu.DfuConnection;

import java.nio.ByteBuffer;

public class DfuCommandClearStatus {
    public static void execute(DfuConnection session) {
        ByteBuffer buffer = ByteBuffer.allocate(0);
        session.sendData(DfuCommmand.CLRSTATUS, (short) 0, buffer);
    }
}
