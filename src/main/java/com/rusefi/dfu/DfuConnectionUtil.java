package com.rusefi.dfu;

import com.rusefi.dfu.commands.DfuCommandGetStatus;

public class DfuConnectionUtil {
    public static void waitStatus(DfuConnection device) {
        DfuCommandGetStatus.State state = DfuCommandGetStatus.read(device);
        System.out.println(" state " + state);
        while (state == DfuCommandGetStatus.State.DFU_DOWNLOAD_BUSY || state == DfuCommandGetStatus.State.DFU_ERROR) {
            sleep(106);
            state = DfuCommandGetStatus.read(device);
            System.out.println(" state " + state);
        }
    }

    public static void sleep(int millis) {
        System.out.println("Sleep " + millis);
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
}
