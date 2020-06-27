package com.rusefi.dfu.commands;

import com.rusefi.dfu.DfuCommmand;
import com.rusefi.dfu.usb4java.USBDfuConnection;

import java.nio.ByteBuffer;

public class DfuCommandAbort {
    public static void execute(USBDfuConnection session) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(0);
        session.sendData(DfuCommmand.ABORT, (short) 0, buffer);
    }
}
