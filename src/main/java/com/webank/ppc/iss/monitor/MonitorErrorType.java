/** Copyright (C) @2014-2022 Webank */
package com.webank.ppc.iss.monitor;

public enum MonitorErrorType {
    /** Success monitor error type. */
    SUCCESS(0),
    /** Sys error monitor error type. */
    SYS_ERROR(1),
    /** Biz error monitor error type. */
    BIZ_ERROR(2);

    private final int code;

    MonitorErrorType(int code) {
        this.code = code;
    }

    /**
     * Code int.
     *
     * @return the int
     */
    public int code() {
        return this.code;
    }
}
