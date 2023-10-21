package ru.kekens;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.List;

/**
 * Класс-реализация веб-сервиса
 */
@WebService(serviceName = "AccountService")
public class AccountWebService {

    private AccountDAO dao;

    @WebMethod(operationName = "getAccounts")
    public List<Account> getAccounts(AccountsRequest accountsRequest) {
        return getAccountDAO().getAccountsByParams(accountsRequest.getList());
    }

    @WebMethod(operationName = "insertAccount")
    public Long insertAccount(AccountsRequest accountsRequest) {
        return getAccountDAO().insertAccount(accountsRequest.getList());
    }

    @WebMethod(operationName = "updateAccount")
    public Boolean updateAccount(Long id, AccountsRequest accountsRequest) {
        return getAccountDAO().updateAccount(id, accountsRequest.getList());
    }

    @WebMethod(operationName = "deleteAccount")
    public Boolean deleteAccount(Long id) {
        return getAccountDAO().deleteAccount(id);
    }

    @WebMethod(operationName = "deleteAccounts")
    public Boolean deleteAccounts() {
        return getAccountDAO().deleteAccounts();
    }

    private AccountDAO getAccountDAO() {
        if (dao == null) {
            dao = new AccountDAO();
        }
        return dao;
    }

}
