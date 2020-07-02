package com.rusefi.dfu;

import com.rusefi.dfu.commands.DfuSeCommandErasePage;
import com.rusefi.dfu.commands.DfuSeCommandSetAddress;
import com.rusefi.dfu.usb4java.USBDfuConnection;

import java.nio.ByteBuffer;
import java.util.List;

public class DfuLogic {
    public static final short ST_VENDOR = 0x0483;
    public static final int ST_DFU_PRODUCT = 0xdf11;
    public static final int USB_CLASS_APP_SPECIFIC = 0xfe;
    public static final byte DFU_SUBCLASS = 0x01;
    public static final byte USB_DT_DFU = 0x21;
    public static final String FLASH_TAG = "Flash";

    static void uploadImage(USBDfuConnection device, HexImage image) {
        List<Integer> erasePages = image.getRange().pagesForSize(image.getTotalBytes());
        // todo: smarted start address logic
        int eraseAddress = 0x08000000;
        for (Integer erasePage : erasePages) {
            DfuSeCommandErasePage.execute(device, eraseAddress);
            eraseAddress += erasePage;
        }
        System.out.println(String.format("Erased up to %x", eraseAddress));

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
