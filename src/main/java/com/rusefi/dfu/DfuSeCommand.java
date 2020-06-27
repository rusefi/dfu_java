package com.rusefi.dfu;

public class DfuSeCommand {
    public static final short W_SPECIAL = 0;
    public static final short W_DNLOAD = 2;
    /**
     * http://dfu-util.sourceforge.net/dfuse.html
     */
    public static byte SE_SET_ADDRESS = 0x21;
    public static byte SE_ERASE_PAGE = 0x41;


}
