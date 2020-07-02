package com.rusefi.dfu;

import java.util.ArrayList;
import java.util.List;

public class FlashRange {
    private final int baseAddress;
    private final List<Integer> pages;
    private final int totalLength;

    public FlashRange(int baseAddress, List<Integer> pages) {
        this.baseAddress = baseAddress;
        this.pages = pages;
        int t = 0;
        for (Integer page : pages)
            t += page;
        this.totalLength = t;
    }

    public int getBaseAddress() {
        return baseAddress;
    }

    public int getTotalLength() {
        return totalLength;
    }

    public List<Integer> getPages() {
        return pages;
    }

    @Override
    public String toString() {
        return "FlashRange{" +
                "baseAddress=" + baseAddress +
                ", totalLength=" + totalLength +
                '}';
    }

    public List<Integer> pagesForSize(int size) {
        int total = 0;
        List<Integer> result = new ArrayList<>();
        for (Integer page : pages) {
            if (total < size) {
                result.add(page);
                total += page;
            }
        }
        return result;
    }

}
