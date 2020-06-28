package com.rusefi.dfu;

import cz.jaybee.intelhex.DataListener;
import cz.jaybee.intelhex.IntelHexException;
import cz.jaybee.intelhex.Parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

public class HexImage extends AtomicInteger {
    private final byte[] image;
    private final FlashRange range;
    private final int totalBytes;
    private final int maxOffset;

    public HexImage(byte[] image, FlashRange range, int totalBytes, int maxOffset) {
        this.image = image;
        this.range = range;
        this.totalBytes = totalBytes;
        this.maxOffset = maxOffset;
    }

    static HexImage loadHexToBuffer(InputStream is, FlashRange range) throws IntelHexException, IOException {
        byte[] image = new byte[range.getTotalLength()];

        // create IntelHexParserObject
        Parser ihp = new Parser(is);

        AtomicInteger totalBytesReceived = new AtomicInteger();

        AtomicInteger maxOffset = new AtomicInteger();
        // register parser listener
        ihp.setDataListener(new DataListener() {
            @Override
            public void data(long address, byte[] data) {
//                System.out.printf("Address %x size %x\n", address, data.length);
                totalBytesReceived.addAndGet(data.length);

                maxOffset.set((int) Math.max(maxOffset.get(), address + data.length));

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

        return new HexImage(image, range, totalBytesReceived.get(), maxOffset.get());
    }

    public byte[] getImage() {
        return image;
    }

    public int getTotalBytes() {
        return totalBytes;
    }

    public int getMaxOffset() {
        return maxOffset;
    }

    public FlashRange getRange() {
        return range;
    }

    @Override
    public String toString() {
        return "HexImage{" +
                "image=" + image.length +
                ", range=" + range +
                ", totalBytes=" + totalBytes +
                ", maxOffset=" + maxOffset +
                '}';
    }
}
