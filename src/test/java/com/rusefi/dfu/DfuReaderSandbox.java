package com.rusefi.dfu;

import java.io.IOException;

public class DfuReaderSandbox {
    public static void main(String[] args) throws IOException {
        DfuImage dfuImage = new DfuImage();
        dfuImage.read("rusefi.dfu");
    }
}
