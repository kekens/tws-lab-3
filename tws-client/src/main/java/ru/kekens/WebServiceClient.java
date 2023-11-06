package ru.kekens;

import ws.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Key;
import java.sql.SQLOutput;
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
        System.out.println("\nRequest 1 - INSERT (New account 1, 30303, derivative, 10.00, 2021-04-04)\n");
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
        Long id = insertAccount(accountService, requestIns1);

        System.out.println("New id: " + id);

        // Request 2
        System.out.println("\nRequest 2 - INSERT (New account 2, 63696, fictious, 111.999, 2023-09-01)\n");
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
        Long id2 = insertAccount(accountService, requestIns2);

        System.out.println("New id: " + id2);

        // Request 3
        System.out.println("\nRequest 3 - INSERT not all params\n");
        AccountsRequest requestIns3 = new AccountsRequest();
        KeyValueParamsDto paramsInsDto11 = new KeyValueParamsDto();
        paramsInsDto11.setKey("label");
        paramsInsDto11.setValue("New account 3");
        requestIns3.getList().add(paramsInsDto11);
        Long id3 = insertAccount(accountService, requestIns3);

        // Request 4
        System.out.println("\nRequest 4 - INSERT wrong param amount5\n");
        AccountsRequest requestIns4 = new AccountsRequest();
        KeyValueParamsDto paramsInsDto12 = new KeyValueParamsDto();
        paramsInsDto12.setKey("label");
        paramsInsDto12.setValue("New account 2");
        KeyValueParamsDto paramsInsDto13 = new KeyValueParamsDto();
        paramsInsDto13.setKey("code");
        paramsInsDto13.setValue("63696");
        KeyValueParamsDto paramsInsDto14 = new KeyValueParamsDto();
        paramsInsDto14.setKey("category");
        paramsInsDto14.setValue("fictious");
        KeyValueParamsDto paramsInsDto15 = new KeyValueParamsDto();
        paramsInsDto15.setKey("amount");
        paramsInsDto15.setValue(format.parse("2023-09-01"));
        KeyValueParamsDto paramsInsDto16 = new KeyValueParamsDto();
        paramsInsDto16.setKey("open_date");
        paramsInsDto16.setValue(format.parse("2023-09-01"));
        requestIns4.getList().addAll(List.of(paramsInsDto12, paramsInsDto13, paramsInsDto14, paramsInsDto15, paramsInsDto16));
        Long id4 = insertAccount(accountService, requestIns4);

        // Get all
        List<Account> accountList = getAccounts(accountService, new AccountsRequest());
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
        updateAccount(accountService, id, requestUpd1);

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
        updateAccount(accountService, id2, requestUpd2);

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
        accountList = getAccounts(accountService, requestGetNewAccounts);
        for (Account account : accountList) {
            printAccountInfo(account);
        }

        // Request 3 - Update account null
        System.out.println("\nRequest 3 - Update account null");
        AccountsRequest requestUpd3 = new AccountsRequest();
        updateAccount(accountService, null, null);

        // Request 4 - Update account empty
        System.out.println("Request 4 - Update account empty");
        updateAccount(accountService, id2, null);

        // Request 5 - Update not existed account
        System.out.println("Request 5 - Update account empty");
        updateAccount(accountService, 100100L, null);
        System.out.println("------ END UPDATE ACCOUNTS ------ ");

        // DELETE ACCOUNTS
        System.out.println("\n------ START DELETE ACCOUNTS ------ ");
        System.out.println("Request 1 - DELETE FROM account " +
                "WHERE id = " + id);
        // Request 1 - Delete new account 1
        deleteAccount(accountService, id);

        // Request 2 - Delete new account 2
        System.out.println("Request 2 - DELETE FROM account " +
                "WHERE id = " + id2);
        deleteAccount(accountService, id2);

        // Request 3 - Delete account by null
        System.out.println("Request 3 - DELETE FROM account " +
                "WHERE id = null");
        deleteAccount(accountService, null);

        // Request 4 - Delete account with negative id
        System.out.println("Request 4 - DELETE FROM account " +
                "WHERE id = -1");
        deleteAccount(accountService, -1L);

        // Get all
        accountList = getAccounts(accountService, new AccountsRequest());
        System.out.println("\nAll accounts");
        for (Account account : accountList) {
            printAccountInfo(account);
        }

        // Request 3 - Delete all accounts
//        System.out.println("\nRequest 3 - DELETE FROM account");
//        accountService.getAccountWebServicePort().deleteAccounts();

//        accountList = accountService.getAccountWebServicePort().getAccounts(new AccountsRequest());
//        System.out.println("\nAll accounts");
//        for (Account account : accountList) {
//            printAccountInfo(account);
//        }
        System.out.println("------ END DELETE ACCOUNTS ------ ");

    }

    private static void printAccountInfo(Account acc) {
        System.out.printf("Account %d: label - %s;\t code - %s;\t category - %s;\t amount - %.2f;\t openDate - %s\n",
                acc.getId(), acc.getLabel(), acc.getCode(), acc.getCategory(),
                acc.getAmount().setScale(2, RoundingMode.HALF_UP), acc.getOpenDate());
    }


    private static void getAccounts(AccountService accountService) throws ParseException {
        // Request 0
        List<Account> accountList = getAccounts(accountService, new AccountsRequest());

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
        accountList = getAccounts(accountService, request1);

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
        accountList = getAccounts(accountService, request2);

        System.out.println("\nRequest 2 - by date > 2022-01-01. Found " + accountList.size() + " accounts");
        for (Account account : accountList) {
            printAccountInfo(account);
        }

        // Request 3
        AccountsRequest request3 = new AccountsRequest();
        request3.getList().add(paramsDto1);
        request3.getList().add(paramsDto2);
        accountList = getAccounts(accountService, request3);

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
        accountList = getAccounts(accountService, request4);

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
        accountList = getAccounts(accountService, request5);

        System.out.println("\nRequest 5 - by code LIKE \"47\" and category = \"personal\" and amount > 0 and " +
                "date = \"2020-04-05\". Found " + accountList.size() + " accounts");
        for (Account account : accountList) {
            printAccountInfo(account);
        }

        // Request 6
        AccountsRequest request6 = new AccountsRequest();
        KeyValueParamsDto paramsDto9 = new KeyValueParamsDto();
        paramsDto9.setKey("code");
        paramsDto9.setValue(47L);
        paramsDto9.setCompareOperation("LIKE");
        paramsDto9.setLogicOperation("AND");
        request6.getList().add(paramsDto9);
        System.out.println("\nRequest 6 - by Long code");
        accountList = getAccounts(accountService, request6);

        // Request 7
        AccountsRequest request7 = new AccountsRequest();
        KeyValueParamsDto paramsDto10 = new KeyValueParamsDto();
        paramsDto10.setKey("code");
        paramsDto10.setValue(BigInteger.TEN);
        paramsDto10.setCompareOperation("LIKE");
        paramsDto10.setLogicOperation("AND");
        request7.getList().add(paramsDto10);
        System.out.println("\nRequest 7 - by BigInteger code");
        accountList = getAccounts(accountService, request7);

        // Request 8
        AccountsRequest request8 = new AccountsRequest();
        request8.getList().add(new KeyValueParamsDto());
        System.out.println("\nRequest 8 - by empty param");
        accountList = getAccounts(accountService, request8);
        System.out.println("------ END GET ACCOUNTS ------ ");
    }

    private static List<Account> getAccounts(AccountService accountService, AccountsRequest accountsRequest) {
        try {
            return accountService.getAccountWebServicePort().getAccounts(accountsRequest);
        } catch (AccountServiceException e) {
            System.out.println(e.getFaultInfo().getMessage() + ": " + e.getMessage());
        }

        return null;
    }

    private static Long insertAccount(AccountService accountService, AccountsRequest accountsRequest) {
        try {
            return accountService.getAccountWebServicePort().insertAccount(accountsRequest);
        } catch (AccountServiceException e) {
            System.out.println(e.getFaultInfo().getMessage() + ": " + e.getMessage());
        }

        return null;
    }

    private static void updateAccount(AccountService accountService, Long id, AccountsRequest accountsRequest) {
        try {
            accountService.getAccountWebServicePort().updateAccount(id, accountsRequest);
        } catch (AccountServiceException e) {
            System.out.println(e.getFaultInfo().getMessage() + ": " + e.getMessage());
        }
    }

    private static void deleteAccount(AccountService accountService, Long id) {
        try {
            accountService.getAccountWebServicePort().deleteAccount(id);
        } catch (AccountServiceException e) {
            System.out.println(e.getFaultInfo().getMessage() + ": " + e.getMessage());
        }
    }


    private void deleteAccounts(AccountService accountService) {

    }

}