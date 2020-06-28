package com.rusefi.dfu;

import com.rusefi.dfu.commands.DfuSeCommandErasePage;
import com.rusefi.dfu.commands.DfuSeCommandSetAddress;
import com.rusefi.dfu.usb4java.USBDfuConnection;

import java.nio.ByteBuffer;

public class DfuLogic {
    public static final short ST_VENDOR = 0x0483;
    public static final short ST_DFU_PRODUCT = (short) 0xdf11;
    public static final byte USB_CLASS_APP_SPECIFIC = (byte) 0xfe;
    public static final byte DFU_SUBCLASS = 0x01;
    public static final byte USB_DT_DFU = 0x21;

    static void uploadImage(USBDfuConnection device, HexImage image) {
        // todo: smarter erase handling!
        DfuSeCommandErasePage.execute(device, 0x08000000);
        DfuSeCommandErasePage.execute(device, 0x08004000);
        DfuSeCommandErasePage.execute(device, 0x08008000);
        DfuSeCommandErasePage.execute(device, 0x0800C000);
        DfuSeCommandErasePage.execute(device, 0x08010000);
        DfuSeCommandErasePage.execute(device, 0x08020000);
        DfuSeCommandErasePage.execute(device, 0x08040000);

        for (int offset = 0; offset < image.getMaxOffset() - image.getRange().getBaseAddress(); offset += device.getTransferSize()) {
            DfuSeCommandSetAddress.execute(device, device.getFlashRange().getBaseAddress() + offset);
            DfuConnection.waitStatus(device);

            ByteBuffer buffer = ByteBuffer.allocateDirect(device.getTransferSize());
            buffer.put(image.getImage(), offset, device.getTransferSize());
            device.sendData(DfuCommmand.DNLOAD, DfuSeCommand.W_DNLOAD, buffer);
            // AN3156 USB DFU protocol used in the STM32 bootloader
            // "The Write memory operation is effectively executed only when a DFU_GETSTATUS request is issued by the host. "
            DfuConnection.waitStatus(device);
        }
    }
}
