package com.rusefi.dfu.commands;

import com.rusefi.dfu.DfuCommmand;
import com.rusefi.dfu.DfuConnection;
import com.rusefi.dfu.DfuSeCommand;
import com.rusefi.dfu.usb4java.LogUtil;
import com.rusefi.dfu.usb4java.USBDfuConnection;
import org.apache.commons.logging.Log;

import java.nio.ByteBuffer;

public class DfuSeCommandErasePage {
    private static final Log log = LogUtil.getLog(DfuSeCommandErasePage.class);

    public static void execute(USBDfuConnection session, int address) {
        log.info(String.format("SetAddress %x", address));
        ByteBuffer buffer = DfuSeCommandSetAddress.createSpecialCommandBuffer(DfuSeCommand.SE_ERASE_PAGE, address);
        session.sendData(DfuCommmand.DNLOAD, DfuSeCommand.W_SPECIAL, buffer);
        DfuConnection.waitStatus(session);
    }
}
