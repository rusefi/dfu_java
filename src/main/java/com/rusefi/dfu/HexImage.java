package com.rusefi.dfu;

import cz.jaybee.intelhex.DataListener;
import cz.jaybee.intelhex.IntelHexException;
import cz.jaybee.intelhex.Parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class HexImage implements BinaryImage {
    private final byte[] image;

    public HexImage(byte[] image) {
        this.image = image;
    }

    static HexImage loadHexToBuffer(InputStream is, FlashRange flashRange) throws IntelHexException, IOException {
        Objects.requireNonNull(flashRange, "flashRange");
        byte[] image = new byte[flashRange.getTotalLength()];

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

                if (address < flashRange.getBaseAddress() || address + data.length > flashRange.getBaseAddress() + flashRange.getTotalLength())
                    throw new IllegalStateException(String.format("Image data out of range: %x@%x not within %s",
                            data.length,
                            address,
                            flashRange.toString()));
                System.arraycopy(data, 0, image, (int) (address - flashRange.getBaseAddress()), data.length);
            }

            @Override
            public void eof() {
                // do some action
            }
        });
        ihp.parse();

        int imageSize = maxOffset.get() - flashRange.getBaseAddress();
        return new HexImage(Arrays.copyOfRange(image, 0, imageSize));
    }

    @Override
    public byte[] getImage() {
        return image;
    }

    @Override
    public String toString() {
        return "HexImage{" +
                "image=" + image.length +
                '}';
    }
}
