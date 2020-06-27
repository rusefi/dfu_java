package com.rusefi.dfu;

public enum DfuCommmand {
    DETACH(0),
    DNLOAD(1),
    UPLOAD(2),
    GETSTATUS(3),
    CLRSTATUS(4),
    GETSTATE(5),
    ABORT(6),

    /**
     * http://dfu-util.sourceforge.net/dfuse.html
     */
    SE_SET_ADDRESS(0x21);

    private final byte value;

    DfuCommmand(int value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return this.value;
    }
}
