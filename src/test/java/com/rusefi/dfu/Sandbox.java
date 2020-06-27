package com.rusefi.dfu;

import org.apache.commons.logging.Log;
import org.usb4java.Device;

public class Sandbox {
    private static final Log log = LogUtil.getLog(Sandbox.class);

    public static void main(String[] args) {
        log.info("Hello sandbox");

        Device device = DfuDeviceLocator.findDevice();
        log.info("STM32 DFU " + device);
    }
}
