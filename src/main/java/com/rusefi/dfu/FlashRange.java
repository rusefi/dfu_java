package com.rusefi.dfu;

public class FlashRange {
    private final int baseAddress;
    private final int totalLength;

    public FlashRange(int baseAddress, int totalLength) {
        this.baseAddress = baseAddress;
        this.totalLength = totalLength;
    }

    public int getBaseAddress() {
        return baseAddress;
    }

    public int getTotalLength() {
        return totalLength;
    }
}
