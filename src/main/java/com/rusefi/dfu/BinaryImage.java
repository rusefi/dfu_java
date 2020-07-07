package com.rusefi.dfu;

import java.io.FileOutputStream;
import java.io.IOException;

public interface BinaryImage {
    byte[] getImage();

    default int getImageSize() {
        return getImage().length;
    }

    default void saveToFile(String fileName) throws IOException {
        FileOutputStream fos = new FileOutputStream(fileName);
        fos.write(getImage(), 0, getImageSize());
        fos.close();
    }
}
