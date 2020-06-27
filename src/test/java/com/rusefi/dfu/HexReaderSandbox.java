package com.rusefi.dfu;

import cz.jaybee.intelhex.DataListener;
import cz.jaybee.intelhex.IntelHexException;
import cz.jaybee.intelhex.Parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class HexReaderSandbox {
    public static void main(String[] args) throws IOException, IntelHexException {
        InputStream is = new FileInputStream("sample.hex");

        // create IntelHexParserObject
        Parser ihp = new Parser(is);

        // register parser listener
        ihp.setDataListener(new DataListener() {
            @Override
            public void data(long address, byte[] data) {
                System.out.printf("Address %x size %x\n", address, data.length);
            }

            @Override
            public void eof() {
                // do some action
            }
        });
        ihp.parse();
    }
}
