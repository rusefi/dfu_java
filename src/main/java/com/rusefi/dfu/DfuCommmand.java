package com.rusefi.dfu;

public enum DfuCommmand {
    DETACH(0),
    DNLOAD(1),
    UPLOAD(2),
    GETSTATUS(3),
    CLRSTATUS(4),
    GETSTATE(5),
    ABORT(6);

    private final byte value;

    DfuCommmand(int value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return this.value;
    }
}
