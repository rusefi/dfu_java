package com.rusefi.dfu;

public class DfuReaderSandbox {
    public static void main(String[] args) {
        DfuImage dfuImage = new DfuImage();
        dfuImage.read("rusefi.dfu");
    }
}
