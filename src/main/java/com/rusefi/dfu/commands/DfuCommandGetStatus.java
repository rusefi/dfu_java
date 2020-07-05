package com.rusefi.dfu.commands;

import com.rusefi.dfu.DfuCommmand;
import com.rusefi.dfu.DfuConnection;

import java.nio.ByteBuffer;

public class DfuCommandGetStatus {
    private static final int PACKET_SIZE = 6;

    public static State read(DfuConnection session) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(PACKET_SIZE);
        int count = session.receiveData(DfuCommmand.GETSTATUS, (short) 0, buffer);
        if (count == 0)
            return State.DFU_ERROR;
        if (count != PACKET_SIZE)
            throw new IllegalStateException("Got " + count);
        buffer.rewind();
        buffer.get(); // status
        buffer.get(); // timeout
        buffer.get(); // timeout
        buffer.get(); // timeout
        byte state = buffer.get();
        return State.valueOf(state);
    }

    public enum State {
        APP_IDLE(0x00),
        APP_DETACH(0x01),
        DFU_IDLE(0x02),
        DFU_DOWNLOAD_SYNC(0x03),
        DFU_DOWNLOAD_BUSY(0x04),
        DFU_DOWNLOAD_IDLE(0x05),
        DFU_MANIFEST_SYNC(0x06),
        DFU_MANIFEST(0x07),
        DFU_MANIFEST_WAIT_RESET(0x08),
        DFU_UPLOAD_IDLE(0x09),
        DFU_ERROR(0x0a);

        private final byte value;

        State(int value) {
            this.value = (byte) value;
        }

        public static State valueOf(byte value) {
            for (State state : State.values()) {
                if (state.value == value) {
                    return state;
                }
            }
            return null;
        }
    }
}
