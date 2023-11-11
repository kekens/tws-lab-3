package ru.kekens.exception;

import javax.xml.ws.WebFault;

@WebFault(faultBean = "ru.kekens.exception.ThrottlingFault")
public class ThrottlingException extends RuntimeException {

    private final ThrottlingFault fault;

    public ThrottlingException(String message, ThrottlingFault fault)
    {
        super(message);
        this.fault = fault;
    }

    public ThrottlingException(String message, ThrottlingFault fault, Throwable cause) {
        super(message, cause);
        this.fault = fault;
    }

    public ThrottlingFault getFaultInfo() {
        return fault;
    }

}
