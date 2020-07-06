package com.rusefi.dfu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ST chips send their memory layout in USB description string
 * This class knows how to unpack those descriptions
 */
public class DfuSeFlashDescriptor {
    private static final String HEX_PREFIX = "0x";
    private static final String K_SUFFIX = "Kg";

    public static FlashRange parse(String string) {
        String topLevelSections[] = string.split("\\/");

        if (topLevelSections.length != 3) {
            throw new IllegalStateException("Three sections expected in " + topLevelSections);
        }
        System.out.println(Arrays.toString(topLevelSections));

        String baseAddressString = topLevelSections[1].trim();
        if (baseAddressString.startsWith(HEX_PREFIX))
            baseAddressString = baseAddressString.substring(HEX_PREFIX.length());

        int baseAddress = Integer.parseInt(baseAddressString, 16);
        System.out.printf("Base address %x\n", baseAddress);

        List<Integer> pages = parseRegions(topLevelSections[2].trim());

        return new FlashRange(baseAddress, pages);
    }

    private static List<Integer> parseRegions(String regions) {
        List<Integer> pages = new ArrayList<>();

        String[] sections = regions.split(",");
        for (String section : sections) {
            System.out.println("Region " + section);
            String parts[] = section.split("\\*");
            int count = Integer.parseInt(parts[0]);
            String pageSizeString = parts[1];
            if (!pageSizeString.endsWith(K_SUFFIX))
                throw new IllegalStateException(K_SUFFIX + " expected at the end of " + section);
            pageSizeString = pageSizeString.substring(0, pageSizeString.length() - K_SUFFIX.length());
            int pageSize = Integer.parseInt(pageSizeString);

            System.out.println("Count " + count + " size " + pageSize);

            for (int i = 0; i < count; i++)
                pages.add(1024 * pageSize);
        }
        return pages;
    }
}
