package com.rusefi.dfu;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DfuSeFlashDescriptorTest {
    @Test
    public void testParseStm32f407() {
        FlashRange range = DfuSeFlashDescriptor.parse("@Internal Flash  /0x08000000/04*016Kg,01*064Kg,07*128Kg");
        assertEquals(0x8000000, range.getBaseAddress());
        assertEquals(0x100000, range.getTotalLength());

        assertEquals(1, range.pagesForSize(1).size());
        assertEquals(1, range.pagesForSize(16 * 1024).size());

        assertEquals(2, range.pagesForSize(17 * 1024).size());
    }
}
