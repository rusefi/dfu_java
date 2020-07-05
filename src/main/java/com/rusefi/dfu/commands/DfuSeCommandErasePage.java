package com.rusefi.dfu.commands;

import com.rusefi.dfu.*;

import java.nio.ByteBuffer;

public class DfuSeCommandErasePage {
    public static void execute(DfuLogic.Logger logger, DfuConnection connection, int address) {
        logger.info(String.format("SetAddress %x", address));
        ByteBuffer buffer = DfuSeCommandSetAddress.createSpecialCommandBuffer(connection, DfuSeCommand.SE_ERASE_PAGE, address);
        connection.sendData(DfuCommmand.DNLOAD, DfuSeCommand.W_SPECIAL, buffer);
        DfuConnectionUtil.waitStatus(logger, connection);
    }
}
