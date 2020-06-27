package com.rusefi.dfu.commands;

import com.rusefi.dfu.DfuCommmand;
import com.rusefi.dfu.DfuSeCommand;
import com.rusefi.dfu.usb4java.LogUtil;
import com.rusefi.dfu.usb4java.USBDfuConnection;
import org.apache.commons.logging.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DfuSeCommandSetAddress {
    private static final Log log = LogUtil.getLog(DfuSeCommandSetAddress.class);

    public static void execute(USBDfuConnection session, int address) {
        log.info(String.format("SetAddress %x", address));
        ByteBuffer buffer = createSpecialCommandBuffer(DfuSeCommand.SE_SET_ADDRESS, address);
        buffer.putInt(address);
        session.sendData(DfuCommmand.DNLOAD, DfuSeCommand.W_SPECIAL, buffer);
    }

    protected static ByteBuffer createSpecialCommandBuffer(byte command, int address) {
        ByteBuffer buffer = createBuffer(5);
        buffer.put(command);
//        buffer.rewind();
//        byte[] t = new byte[4];
//        buffer.get(t);
        buffer.putInt(address);
        return buffer;
    }

    protected static ByteBuffer createBuffer(int capacity) {
        return ByteBuffer.allocateDirect(capacity).order(ByteOrder.LITTLE_ENDIAN);
    }
}
