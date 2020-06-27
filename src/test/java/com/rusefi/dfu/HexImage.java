package com.rusefi.dfu;

import cz.jaybee.intelhex.DataListener;
import cz.jaybee.intelhex.IntelHexException;
import cz.jaybee.intelhex.Parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

public class HexImage extends AtomicInteger {
    private final byte[] image;
    private final int totalBytes;

    public HexImage(byte[] image, int totalBytes) {
        this.image = image;
        this.totalBytes = totalBytes;
    }

    static HexImage loadHexToBuffer(InputStream is, FlashRange range) throws IntelHexException, IOException {
        byte[] image = new byte[range.getTotalLength()];

        // create IntelHexParserObject
        Parser ihp = new Parser(is);

        AtomicInteger totalBytesReceived = new AtomicInteger();
        // register parser listener
        ihp.setDataListener(new DataListener() {
            @Override
            public void data(long address, byte[] data) {
//                System.out.printf("Address %x size %x\n", address, data.length);
                totalBytesReceived.addAndGet(data.length);

                if (address < range.getBaseAddress() || address + data.length > range.getBaseAddress() + range.getTotalLength())
                    throw new IllegalStateException(String.format("Image data out of range: %x@%x not withiin %s",
                            data.length,
                            address,
                            range.toString()));
                System.arraycopy(data, 0, image, (int) (address - range.getBaseAddress()), data.length);
            }

            @Override
            public void eof() {
                // do some action
            }
        });
        ihp.parse();

        return new HexImage(image, totalBytesReceived.get());
    }

    public byte[] getImage() {
        return image;
    }

    public int getTotalBytes() {
        return totalBytes;
    }
}
