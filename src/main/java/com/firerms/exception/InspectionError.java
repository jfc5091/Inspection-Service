package com.firerms.exception;

import java.time.ZonedDateTime;

public class InspectionError {

    private final String message;
    private final int status;
    private final ZonedDateTime timeStamp;

    public InspectionError(String message, int status, ZonedDateTime timeStamp) {
        this.message = message;
        this.status = status;
        this.timeStamp = timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public ZonedDateTime getTimeStamp() {
        return timeStamp;
    }
}
