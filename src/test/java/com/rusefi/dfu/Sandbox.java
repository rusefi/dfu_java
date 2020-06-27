package com.rusefi.dfu;

import com.rusefi.dfu.usb4java.DfuDeviceLocator;
import com.rusefi.dfu.usb4java.LogUtil;
import com.rusefi.dfu.usb4java.USBDfuConnection;
import org.apache.commons.logging.Log;

public class Sandbox {
    private static final Log log = LogUtil.getLog(Sandbox.class);

    public static void main(String[] args) {
        log.info("Hello sandbox");

        USBDfuConnection device = DfuDeviceLocator.findDevice();

        log.info("STM32 DFU " + device);
    }
}
