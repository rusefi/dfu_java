package com.rusefi.dfu;

import com.rusefi.dfu.commands.DfuCommandGetStatus;

public class DfuConnectionUtil {
    public static void waitStatus(DfuLogic.Logger logger, DfuConnection device) {
        DfuCommandGetStatus.DeviceStatus state = DfuCommandGetStatus.read(logger, device);
        logger.info("First state " + state);
        long enter = System.currentTimeMillis();
        while (state.getState() == DfuCommandGetStatus.State.DFU_DOWNLOAD_BUSY || state.getState() == DfuCommandGetStatus.State.DFU_ERROR) {
            state = DfuCommandGetStatus.read(logger, device);
            logger.info("Loop state " + state);
            if (System.currentTimeMillis() - enter > 10 * DfuConnection.SECOND)
                throw new IllegalStateException("State does not look good");
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
