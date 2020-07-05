package com.rusefi.dfu.commands;

import com.rusefi.dfu.DfuCommmand;
import com.rusefi.dfu.DfuConnection;
import com.rusefi.dfu.DfuLogic;
import com.rusefi.dfu.DfuSeCommand;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DfuSeCommandSetAddress {
    public static void execute(DfuLogic.Logger logger, DfuConnection session, int address) {
        logger.info(String.format("SetAddress %x", address));
        ByteBuffer buffer = createSpecialCommandBuffer(DfuSeCommand.SE_SET_ADDRESS, address);
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
        return ByteBuffer.allocate(capacity).order(ByteOrder.LITTLE_ENDIAN);
    }
}
