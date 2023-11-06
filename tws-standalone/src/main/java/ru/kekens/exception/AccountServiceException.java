package ru.kekens.exception;

import javax.xml.ws.WebFault;

@WebFault(faultBean = "ru.kekens.exception.AccountServiceFault")
public class AccountServiceException extends Exception {

    private final AccountServiceFault fault;

    public AccountServiceException(String message, AccountServiceFault fault)
    {
        super(message);
        this.fault = fault;
    }

    public AccountServiceException(String message, AccountServiceFault fault, Throwable cause) {
        super(message, cause);
        this.fault = fault;
    }

    public AccountServiceFault getFaultInfo() {
        return fault;
    }

}
