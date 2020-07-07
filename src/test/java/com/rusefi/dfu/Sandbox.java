package com.rusefi.dfu;

import com.rusefi.dfu.usb4java.DfuDeviceLocator;
import com.rusefi.dfu.usb4java.USBDfuConnection;
import cz.jaybee.intelhex.IntelHexException;
import org.apache.commons.logging.Log;

import java.io.IOException;

public class Sandbox {
    private static final Log log = LogUtil.getLog(Sandbox.class);

    public static void main(String[] args) throws IOException, IntelHexException {
        log.info("Hello sandbox");

        DfuLogic.Logger logger = DfuLogic.Logger.CONSOLE;
        USBDfuConnection device = DfuDeviceLocator.findDevice(logger);
        if (device == null) {
            System.err.println("No DFU devices found");
            return;
        }

        //BinaryImage image = HexImage.loadHexToBuffer(new FileInputStream("rusefi.hex"), device.getFlashRange());

        BinaryImage image = new DfuImage().read("rusefi.dfu");
        DfuLogic.uploadImage(logger, device, image, device.getFlashRange());

        log.info("STM32 DFU " + device);
    }
}
