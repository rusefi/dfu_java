package com.rusefi.dfu;

import com.rusefi.dfu.usb4java.DfuDeviceLocator;
import com.rusefi.dfu.usb4java.LogUtil;
import com.rusefi.dfu.usb4java.USBDfuConnection;
import cz.jaybee.intelhex.IntelHexException;
import org.apache.commons.logging.Log;

import java.io.FileInputStream;
import java.io.IOException;

public class Sandbox {
    private static final Log log = LogUtil.getLog(Sandbox.class);

    public static void main(String[] args) throws IOException, IntelHexException {
        log.info("Hello sandbox");

        USBDfuConnection device = DfuDeviceLocator.findDevice();
        if (device == null) {
            System.err.println("No DFU devices found");
            return;
        }

        HexImage image = HexImage.loadHexToBuffer(new FileInputStream("rusefi.hex"), device.getFlashRange());


        log.info("STM32 DFU " + device);
    }
}
