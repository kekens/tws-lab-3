package ru.kekens.exception;

public class ThrottlingFault {

    private static final String DEFAULT_MESSAGE = "Превышено максимальное количество выполняемых запросов";

    protected String message;

    public ThrottlingFault() {
        this.message = DEFAULT_MESSAGE;
    }

    public ThrottlingFault(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

}
