package com.rusefi.dfu;

import cz.jaybee.intelhex.IntelHexException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * A test tool focused only on HEX file input
 */
public class HexReaderSandbox {
    public static void main(String[] args) throws IOException, IntelHexException {

        FlashRange range = new FlashRange(0x8000000, Arrays.asList(0x100000));

        HexImage image = HexImage.loadHexToBuffer(new FileInputStream("rusefi.hex"), range);

        System.out.println("Total received " + image);
    }
}
