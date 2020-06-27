package com.rusefi.dfu;

import com.rusefi.dfu.commands.DfuCommandGetStatus;
import com.rusefi.dfu.commands.DfuSeCommandSetAddress;
import com.rusefi.dfu.usb4java.DfuDeviceLocator;
import com.rusefi.dfu.usb4java.LogUtil;
import com.rusefi.dfu.usb4java.USBDfuConnection;
import cz.jaybee.intelhex.IntelHexException;
import org.apache.commons.logging.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

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

        for (int offset = 0; offset < device.getFlashRange().getTotalLength(); offset += device.getTransferSize()) {
            System.out.println("Handing offset " + offset);
            DfuSeCommandSetAddress.execute(device, device.getFlashRange().getBaseAddress() + offset);
            waitStatus(device);

            ByteBuffer buffer = ByteBuffer.allocateDirect(device.getTransferSize());
            buffer.put(image.getImage(), offset, device.getTransferSize());
            device.sendData(DfuCommmand.DNLOAD, (short) 2, buffer);
            waitStatus(device);
        }


        log.info("STM32 DFU " + device);
    }

    private static void waitStatus(USBDfuConnection device) {
        DfuCommandGetStatus.State state = DfuCommandGetStatus.read(device);
        System.out.println(" state " + state);
        while (state == DfuCommandGetStatus.State.DFU_DOWNLOAD_BUSY) {
            sleep(106);
            state = DfuCommandGetStatus.read(device);
            System.out.println(" state " + state);
        }
    }

    private static void sleep(int millis) {
        System.out.println("Sleep " + millis);
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
}
