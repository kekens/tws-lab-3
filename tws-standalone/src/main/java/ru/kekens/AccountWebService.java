package ru.kekens;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.List;

/**
 * Класс-реализация веб-сервиса
 */
@WebService(serviceName = "AccountService")
public class AccountWebService {

    @WebMethod(operationName = "getAccounts")
    public List<Account> getAccounts() {
        AccountDAO dao = new AccountDAO();
        return dao.getAccounts();
    }

//    @WebMethod(operationName = "getAccounts")
//    public List<Account> getAccounts() {
//        AccountDAO dao = new AccountDAO();
//        return dao.getAccounts();
//    }

}
