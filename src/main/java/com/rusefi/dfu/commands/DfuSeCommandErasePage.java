package com.rusefi.dfu.commands;

import com.rusefi.dfu.*;

import java.nio.ByteBuffer;

public class DfuSeCommandErasePage {
    public static void execute(DfuLogic.Logger logger, DfuConnection session, int address) {
        logger.info(String.format("SetAddress %x", address));
        ByteBuffer buffer = DfuSeCommandSetAddress.createSpecialCommandBuffer(DfuSeCommand.SE_ERASE_PAGE, address);
        session.sendData(DfuCommmand.DNLOAD, DfuSeCommand.W_SPECIAL, buffer);
        DfuConnectionUtil.waitStatus(logger, session);
    }
}
