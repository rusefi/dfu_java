package com.rusefi.dfu.commands;

import com.rusefi.dfu.DfuCommmand;
import com.rusefi.dfu.DfuConnection;
import com.rusefi.dfu.DfuConnectionUtil;
import com.rusefi.dfu.DfuLogic;

import java.nio.ByteBuffer;

public class DfuCommandGetStatus {
    private static final int PACKET_SIZE = 6;

    public static State read(DfuLogic.Logger logger, DfuConnection session) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(PACKET_SIZE);
        int count = session.receiveData(DfuCommmand.GETSTATUS, (short) 0, buffer);
        if (count != PACKET_SIZE)
            return State.DFU_ERROR;
        buffer.rewind();
        Status status = Status.valueOf(buffer.get()); // status
        int timeout = buffer.get();
        timeout = timeout | (buffer.get() << 8);
        timeout = timeout | (buffer.get() << 8);
        System.out.println("During " + status + " timeout=" + timeout);
        byte state = buffer.get();
        DfuConnectionUtil.sleep(logger, timeout);
        return State.valueOf(state);
    }

    public enum Status {
        OK(0x00),
        ERROR_TARGET(0x01),
        ERROR_FILE(0x02),
        ERROR_WRITE(0x03),
        ERROR_ERASE(0x04),
        ERROR_CHECK_ERASED(0x05),
        ERROR_PROG(0x06),
        ERROR_VERIFY(0x07),
        ERROR_ADDRESS(0x08),
        ERROR_NOTDONE(0x09),
        ERROR_FIRMWARE(0x0a),
        ERROR_VENDOR(0x0b),
        ERROR_USBR(0x0c),
        ERROR_POR(0x0d),
        ERROR_UNKNOWN(0x0e),
        ERROR_STALLEDPKT(0x0f);

        private final byte value;

        Status(int value) {
            this.value = (byte) value;
        }

        public static Status valueOf(byte value) {
            for (Status s : Status.values()) {
                if (s.value == value) {
                    return s;
                }
            }
            return null;
        }
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
