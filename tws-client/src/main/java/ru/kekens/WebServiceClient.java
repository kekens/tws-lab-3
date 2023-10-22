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
//        URL url = new URL("http://localhost:8080/tws-javaee/ws/account-service?wsdl");
        // Standalone
        URL url = new URL("http://localhost:8081/AccountService?wsdl");
        AccountService accountService = new AccountService(url);

        getAccounts(accountService);

        // INSERT ACCOUNTS
        System.out.println("\n------ START INSERT ACCOUNTS ------ ");

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        // Request 1
        AccountsRequest requestIns1 = new AccountsRequest();
        KeyValueParamsDto paramsInsDto1 = new KeyValueParamsDto();
        paramsInsDto1.setKey("label");
        paramsInsDto1.setValue("New account 1");
        KeyValueParamsDto paramsInsDto2 = new KeyValueParamsDto();
        paramsInsDto2.setKey("code");
        paramsInsDto2.setValue("30303");
        KeyValueParamsDto paramsInsDto3 = new KeyValueParamsDto();
        paramsInsDto3.setKey("category");
        paramsInsDto3.setValue("derivative");
        KeyValueParamsDto paramsInsDto4 = new KeyValueParamsDto();
        paramsInsDto4.setKey("amount");
        paramsInsDto4.setValue(BigDecimal.TEN);
        KeyValueParamsDto paramsInsDto5 = new KeyValueParamsDto();
        paramsInsDto5.setKey("open_date");
        paramsInsDto5.setValue(format.parse("2021-04-04"));
        requestIns1.getList().addAll(List.of(paramsInsDto1, paramsInsDto2, paramsInsDto3, paramsInsDto4, paramsInsDto5));
        Long id = accountService.getAccountWebServicePort().insertAccount(requestIns1);

        System.out.println("\nRequest 1 - INSERT (New account 1, 30303, derivative, 10.00, 2021-04-04)\n" +
                "New id: " + id);

        // Request 2
        AccountsRequest requestIns2 = new AccountsRequest();
        KeyValueParamsDto paramsInsDto6 = new KeyValueParamsDto();
        paramsInsDto6.setKey("label");
        paramsInsDto6.setValue("New account 2");
        KeyValueParamsDto paramsInsDto7 = new KeyValueParamsDto();
        paramsInsDto7.setKey("code");
        paramsInsDto7.setValue("63696");
        KeyValueParamsDto paramsInsDto8 = new KeyValueParamsDto();
        paramsInsDto8.setKey("category");
        paramsInsDto8.setValue("fictious");
        KeyValueParamsDto paramsInsDto9 = new KeyValueParamsDto();
        paramsInsDto9.setKey("amount");
        paramsInsDto9.setValue(new BigDecimal("111.999"));
        KeyValueParamsDto paramsInsDto10 = new KeyValueParamsDto();
        paramsInsDto10.setKey("open_date");
        paramsInsDto10.setValue(format.parse("2023-09-01"));
        requestIns2.getList().addAll(List.of(paramsInsDto6, paramsInsDto7, paramsInsDto8, paramsInsDto9, paramsInsDto10));
        Long id2 = accountService.getAccountWebServicePort().insertAccount(requestIns2);

        System.out.println("\nRequest 2 - INSERT (New account 2, 63696, fictious, 111.999, 2023-09-01)\n" +
                "New id: " + id2);

        // Request 3
        AccountsRequest requestIns3 = new AccountsRequest();
        KeyValueParamsDto paramsInsDto11 = new KeyValueParamsDto();
        paramsInsDto11.setKey("label");
        paramsInsDto11.setValue("New account 3");
        requestIns3.getList().add(paramsInsDto11);
        Long id3 = accountService.getAccountWebServicePort().insertAccount(requestIns3);

        System.out.println("\nRequest 2 - INSERT (New account 3)\n" +
                "New id: " + id3);

        // Get all
        List<Account> accountList = accountService.getAccountWebServicePort().getAccounts(new AccountsRequest());
        System.out.println("\nAll accounts");
        for (Account account : accountList) {
            printAccountInfo(account);
        }
        System.out.println("------ END INSERT ACCOUNTS ------ ");

        // UPDATE ACCOUNTS
        System.out.println("\n------ START UPDATE ACCOUNTS ------ ");
        System.out.println("Request 1 - UPDATE account SET category=personal, amount=-1000, label = New account 1 after update\n" +
                "WHERE id = " + id);
        // Request 1 - Update new account 1
        AccountsRequest requestUpd1 = new AccountsRequest();
        KeyValueParamsDto paramsUpdDto1 = new KeyValueParamsDto();
        paramsUpdDto1.setKey("category");
        paramsUpdDto1.setValue("personal");
        KeyValueParamsDto paramsUpdDto2 = new KeyValueParamsDto();
        paramsUpdDto2.setKey("amount");
        paramsUpdDto2.setValue(new BigDecimal("-1000"));
        KeyValueParamsDto paramsUpdDto3 = new KeyValueParamsDto();
        paramsUpdDto3.setKey("label");
        paramsUpdDto3.setValue("New account 1 after update");
        requestUpd1.getList().addAll(List.of(paramsUpdDto1, paramsUpdDto2,paramsUpdDto3));
        accountService.getAccountWebServicePort().updateAccount(id, requestUpd1);

        // Request 2 - Update new account 2
        System.out.println("Request 2 - UPDATE account SET category=personal, amount=-3000, open_date = 2014-09-01\n" +
                "WHERE id = " + id2 + "\n");
        AccountsRequest requestUpd2 = new AccountsRequest();
        KeyValueParamsDto paramsUpdDto4 = new KeyValueParamsDto();
        paramsUpdDto4.setKey("category");
        paramsUpdDto4.setValue("personal");
        KeyValueParamsDto paramsUpdDto5 = new KeyValueParamsDto();
        paramsUpdDto5.setKey("amount");
        paramsUpdDto5.setValue(new BigDecimal("-3000"));
        KeyValueParamsDto paramsUpdDto6 = new KeyValueParamsDto();
        paramsUpdDto6.setKey("open_date");
        paramsUpdDto6.setValue(format.parse("2014-09-01"));
        requestUpd2.getList().addAll(List.of(paramsUpdDto4, paramsUpdDto5, paramsUpdDto6));
        accountService.getAccountWebServicePort().updateAccount(id2, requestUpd2);

        // Get accounts 1 and 2
        // Request 4
        AccountsRequest requestGetNewAccounts = new AccountsRequest();
        KeyValueParamsDto paramsDtoNew1 = new KeyValueParamsDto();
        paramsDtoNew1.setKey("id");
        paramsDtoNew1.setValue(id);
        paramsDtoNew1.setCompareOperation("=");
        paramsDtoNew1.setLogicOperation("OR");

        KeyValueParamsDto paramsDtoNew2 = new KeyValueParamsDto();
        paramsDtoNew2.setKey("id");
        paramsDtoNew2.setValue(id2);
        paramsDtoNew2.setCompareOperation("=");
        paramsDtoNew2.setLogicOperation("OR");
        requestGetNewAccounts.getList().addAll(List.of(paramsDtoNew1, paramsDtoNew2));
        accountList = accountService.getAccountWebServicePort().getAccounts(requestGetNewAccounts);
        for (Account account : accountList) {
            printAccountInfo(account);
        }
        System.out.println("------ END UPDATE ACCOUNTS ------ ");

        // UPDATE ACCOUNTS
        System.out.println("\n------ START DELETE ACCOUNTS ------ ");
        System.out.println("Request 1 - DELETE FROM account" +
                "WHERE id = " + id);
        // Request 1 - Delete new account 1
        accountService.getAccountWebServicePort().deleteAccount(id);

        // Request 2 - Delete new account 2
        System.out.println("Request 2 - DELETE FROM account" +
                "WHERE id = " + id2);
        accountService.getAccountWebServicePort().deleteAccount(id2);

        // Get all
        accountList = accountService.getAccountWebServicePort().getAccounts(new AccountsRequest());
        System.out.println("\nAll accounts");
        for (Account account : accountList) {
            printAccountInfo(account);
        }

        // Request 3 - Delete all accounts
        System.out.println("\nRequest 3 - DELETE FROM account");
        accountService.getAccountWebServicePort().deleteAccounts();

        accountList = accountService.getAccountWebServicePort().getAccounts(new AccountsRequest());
        System.out.println("\nAll accounts");
        for (Account account : accountList) {
            printAccountInfo(account);
        }
        System.out.println("------ END DELETE ACCOUNTS ------ ");

    }

    private static void printAccountInfo(Account acc) {
        System.out.printf("Account %d: label - %s;\t code - %s;\t category - %s;\t amount - %.2f;\t openDate - %s\n",
                acc.getId(), acc.getLabel(), acc.getCode(), acc.getCategory(),
                acc.getAmount().setScale(2, RoundingMode.HALF_UP), acc.getOpenDate());
    }


    private static void getAccounts(AccountService accountService) throws ParseException {
        // Request 0
        List<Account> accountList = accountService.getAccountWebServicePort().getAccounts(new AccountsRequest());

        System.out.println("------ START GET ACCOUNTS ------ ");
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
        System.out.println("------ END GET ACCOUNTS ------ ");
    }

    private void updateAccounts(AccountService accountService) {

    }

    private void deleteAccounts(AccountService accountService) {

    }

}