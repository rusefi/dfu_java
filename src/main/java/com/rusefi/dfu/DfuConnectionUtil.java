package com.rusefi.dfu;

import com.rusefi.dfu.commands.DfuCommandGetStatus;

public class DfuConnectionUtil {
    public static void waitStatus(DfuLogic.Logger logger, DfuConnection device) {
        DfuCommandGetStatus.State state = DfuCommandGetStatus.read(logger, device);
        System.out.println(" state " + state);
        while (state == DfuCommandGetStatus.State.DFU_DOWNLOAD_BUSY || state == DfuCommandGetStatus.State.DFU_ERROR) {
            state = DfuCommandGetStatus.read(logger, device);
            System.out.println(" state " + state);
        }
    }

    public static void sleep(DfuLogic.Logger logger, int millis) {
        if (millis == 0)
            return;
        logger.info("Sleep " + millis);
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
}
