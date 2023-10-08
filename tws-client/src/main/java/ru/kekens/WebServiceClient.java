package ru.kekens;

import ws.Account;
import ws.AccountService;
import ws.AccountsRequest;
import ws.KeyValueParamsDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class WebServiceClient {
    public static void main(String[] args) throws MalformedURLException, ParseException {
        // Web app
        URL url = new URL("http://localhost:8080/tws-javaee/ws/account-service?wsdl");
        // Standalone
//        URL url = new URL("http://localhost:8081/AccountService?wsdl");
        AccountService accountService = new AccountService(url);

        // Request 0
        List<Account> accountList = accountService.getAccountWebServicePort().getAccounts(new AccountsRequest());

        System.out.println("Request 0 - All");
        for (Account account : accountList) {
            printAccountInfo(account);
        }

        // Request 1
        AccountsRequest request1 = new AccountsRequest();
        KeyValueParamsDto paramsDto1 = new KeyValueParamsDto();
        paramsDto1.setKey("category");
        paramsDto1.setValue("personal");
        paramsDto1.setCompareOperation("=");
        paramsDto1.setLogicOperation("AND");
        request1.getList().add(paramsDto1);
        accountList = accountService.getAccountWebServicePort().getAccounts(request1);

        System.out.println("\nRequest 1 - by category \"personal\". Found " + accountList.size() + " accounts");
        for (Account account : accountList) {
            printAccountInfo(account);
        }

        // Request 2
        AccountsRequest request2 = new AccountsRequest();
        KeyValueParamsDto paramsDto2 = new KeyValueParamsDto();
        paramsDto2.setKey("open_date");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        paramsDto2.setValue(format.parse("2022-01-01"));
        paramsDto2.setCompareOperation(">");
        paramsDto2.setLogicOperation("AND");
        request2.getList().add(paramsDto2);
        accountList = accountService.getAccountWebServicePort().getAccounts(request2);

        System.out.println("\nRequest 2 - by date > 2022-01-01. Found " + accountList.size() + " accounts");
        for (Account account : accountList) {
            printAccountInfo(account);
        }

        // Request 3
        AccountsRequest request3 = new AccountsRequest();
        request3.getList().add(paramsDto1);
        request3.getList().add(paramsDto2);
        accountList = accountService.getAccountWebServicePort().getAccounts(request3);

        System.out.println("\nRequest 3 - by category \"personal\" and date > 2022-01-01. Found " + accountList.size() + " accounts");
        for (Account account : accountList) {
            printAccountInfo(account);
        }

        // Request 4
        AccountsRequest request4 = new AccountsRequest();
        KeyValueParamsDto paramsDto3 = new KeyValueParamsDto();
        paramsDto3.setKey("label");
        paramsDto3.setValue("Test");
        paramsDto3.setCompareOperation("LIKE");
        paramsDto3.setLogicOperation("OR");
        request4.getList().add(paramsDto3);

        KeyValueParamsDto paramsDto4 = new KeyValueParamsDto();
        paramsDto4.setKey("amount");
        paramsDto4.setValue(BigDecimal.ZERO);
        paramsDto4.setCompareOperation("<");
        paramsDto4.setLogicOperation("OR");
        request4.getList().add(paramsDto4);
        accountList = accountService.getAccountWebServicePort().getAccounts(request4);

        System.out.println("\nRequest 4 - by label LIKE \"Test\" or amount < 0. Found " + accountList.size() + " accounts");
        for (Account account : accountList) {
            printAccountInfo(account);
        }

        // Request 5
        AccountsRequest request5 = new AccountsRequest();
        KeyValueParamsDto paramsDto5 = new KeyValueParamsDto();
        paramsDto5.setKey("code");
        paramsDto5.setValue("47");
        paramsDto5.setCompareOperation("LIKE");
        paramsDto5.setLogicOperation("AND");
        request5.getList().add(paramsDto5);

        KeyValueParamsDto paramsDto6 = new KeyValueParamsDto();
        paramsDto6.setKey("category");
        paramsDto6.setValue("personal");
        paramsDto6.setCompareOperation("=");
        paramsDto6.setLogicOperation("AND");
        request5.getList().add(paramsDto6);

        KeyValueParamsDto paramsDto7 = new KeyValueParamsDto();
        paramsDto7.setKey("amount");
        paramsDto7.setValue(BigDecimal.ZERO);
        paramsDto7.setCompareOperation(">");
        paramsDto7.setLogicOperation("AND");
        request5.getList().add(paramsDto7);

        KeyValueParamsDto paramsDto8 = new KeyValueParamsDto();
        paramsDto8.setKey("open_date");
        paramsDto8.setValue(format.parse("2020-04-05"));
        paramsDto8.setCompareOperation("=");
        paramsDto8.setLogicOperation("AND");
        request5.getList().add(paramsDto8);
        accountList = accountService.getAccountWebServicePort().getAccounts(request5);

        System.out.println("\nRequest 5 - by code LIKE \"47\" and category = \"personal\" and amount > 0 and " +
                "date = \"2020-04-05\". Found " + accountList.size() + " accounts");
        for (Account account : accountList) {
            printAccountInfo(account);
        }

    }

    private static void printAccountInfo(Account acc) {
        System.out.printf("Account %d: label - %s;\t code - %s;\t category - %s;\t amount - %.2f;\t openDate - %s\n",
                acc.getId(), acc.getLabel(), acc.getCode(), acc.getCategory(),
                acc.getAmount().setScale(2, RoundingMode.HALF_UP), acc.getOpenDate());
    }

}