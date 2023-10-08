package ru.kekens;


import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс-реализация веб-сервиса
 */
@WebService(serviceName = "AccountService")
public class AccountWebService {

    private DataSource dataSource;

    @WebMethod(operationName = "getAccounts")
    public List<Account> getAccounts(AccountsRequest accountsRequest) {
        Connection connection = getConnection();
        AccountDAO dao = new AccountDAO(connection);
        List<Account> result = dao.getAccountsByParams(accountsRequest.getList());
        closeConnection(connection);
        return result;
    }

    private Connection getConnection() {
        if (dataSource == null) {
            try {
                InitialContext cxt = new InitialContext();
                dataSource = (DataSource) cxt.lookup("java:/comp/env/jdbc/ifmoTwsDb");

                if (dataSource == null) {
                    throw new RuntimeException("Data source not found!");
                }

            } catch (NamingException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());

        }
    }

    private void closeConnection(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(AccountWebService.class.getName()).log(Level.SEVERE, null,
                    ex);
        }
    }

}
