package ru.kekens.exception;

public class AccountServiceFault {

    private static final String DEFAULT_MESSAGE = "Error in Account service";

    protected String message;

    public AccountServiceFault() {
        this.message = DEFAULT_MESSAGE;
    }

    public AccountServiceFault(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

}
