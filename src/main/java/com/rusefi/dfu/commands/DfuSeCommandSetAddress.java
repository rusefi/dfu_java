package com.rusefi.dfu.commands;

import com.rusefi.dfu.DfuCommmand;
import com.rusefi.dfu.usb4java.USBDfuConnection;

import java.nio.ByteBuffer;

public class DfuSeCommandSetAddress {
    public static void execute(USBDfuConnection session, int address) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(4);
        buffer.putInt(address);
        session.sendData(DfuCommmand.CLRSTATUS, (short) 0, buffer);
    }
}
