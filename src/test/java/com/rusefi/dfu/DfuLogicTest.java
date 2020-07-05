package com.rusefi.dfu;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class DfuLogicTest {
    @Test
    public void test() {
        FlashRange range = new FlashRange(0, Arrays.asList(20, 40, 80, 800));

        // todo: migrate to mockito?
        DfuConnection device = new DfuConnection() {
            @Override
            public FlashRange getFlashRange() {
                return range;
            }

            @Override
            public int getTransferSize() {
                return 100;
            }

            @Override
            public int receiveData(DfuCommmand command, short wValue, ByteBuffer data) {
                return data.limit();
            }

            @Override
            public int sendData(DfuCommmand command, short wValue, ByteBuffer data) {
                return data.limit();
            }
        };

        BinaryImage image = new BinaryImage() {
            @Override
            public byte[] getImage() {
                return new byte[150];
            }

            @Override
            public int getImageSize() {
                return getImage().length;
            }
        };




        DfuLogic.uploadImage(device, image, range);

    }
}
