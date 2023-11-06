package ru.kekens;

import javax.xml.ws.Endpoint;

/**
 * Класс для запуска веб-сервиса
 */
public class App {
    public static void main(String[] args) {
        String url = "http://0.0.0.0:8081/AccountService";
//        System.setProperty("com.sun.xml.ws.fault.SOAPFaultBuilder.disableCaptureStackTrace", "false");
        Endpoint.publish(url, new AccountWebService());
    }
}