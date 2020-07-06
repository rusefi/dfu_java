package com.rusefi.dfu;

public interface BinaryImage {
    byte[] getImage();

    default int getImageSize() {
        return getImage().length;
    }
}
