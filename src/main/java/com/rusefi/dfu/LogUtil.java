package com.rusefi.dfu;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LogUtil {
    // todo: one day I would love to learn how to enable trace level with commons logging + java logging

    static Log getLog(Class<?> clazz) {
        return LogFactory.getLog(clazz);
    }
}
