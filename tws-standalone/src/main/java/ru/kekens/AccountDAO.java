package ru.kekens;

import org.apache.commons.lang3.StringUtils;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static ru.kekens.Constants.*;

/**
 * Класс, содержащий метод для выборки данных из базы данных, а также упаковки этих данных в объекты класса Account.
 */
public class AccountDAO {

    private static final String SELECT_ACCOUNT_QUERY = "SELECT * FROM account WHERE 1=1";
    private static final String INSERT_ACCOUNT_QUERY = "INSERT INTO account(label, code, category, amount, open_date) VALUES(?,?,?,?,?)";
    private static final String UPDATE_ACCOUNT_QUERY = "UPDATE account SET";
    private static final String DELETE_ACCOUNT_QUERY = "DELETE FROM account";

    /**
     * Метод для поиска всех счетов
     * @return список всех счетов
     */
    public List<Account> getAccounts() {
        return executeQuery(SELECT_ACCOUNT_QUERY);
    }

    public Account getAccountById(Long id) {
        // Формируем запрос
        StringBuilder query = new StringBuilder(SELECT_ACCOUNT_QUERY);
        query.append(" AND id = ").append(id);

        Account result = null;
        // Ищем счет
        try (Connection connection = ConnectionUtil.getConnection()){
            CallableStatement stmt = connection.prepareCall(query.toString());
            List<Account> listResult = handleResultSet(stmt.executeQuery());
            result = !listResult.isEmpty() ? listResult.get(0) : null;
        } catch (SQLException ex) {
            Logger.getLogger(AccountDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Возвращаем счет
        return result;
    }

    /**
     * Метод для поиска счетов по параметрам
     * @param params параметры для поиска счетов
     * @return список счетов
     */
    public List<Account> getAccountsByParams(List<KeyValueParamsDto> params) {
        // Проверка параметров на пустоту
        if (params == null || params.isEmpty()) {
            return getAccounts();
        }

        // Сортируем мапу по логической операции
        params = params
                .stream()
                .sorted(Comparator.comparing(entry -> !entry.getLogicOperation().equals("AND")))
                .collect(Collectors.toList());

        // Формируем запрос
        StringBuilder query = new StringBuilder(SELECT_ACCOUNT_QUERY);
        for (int i = 0; i < params.size(); i++) {
            KeyValueParamsDto entry = params.get(i);
            if (i == 0) {
                query.append(String.format(" AND (%s %s ?", entry.getKey(),
                        entry.getCompareOperation()));
            } else {
                query.append(String.format(" %s %s %s ?", entry.getLogicOperation(), entry.getKey(),
                        entry.getCompareOperation()));
            }
        }
        query.append(")");

        // Test
        System.out.println(query);

        return executeQueryWithParams(query.toString(), params);
    }

    /**
     * Метод для создания нового счета
     * @param params параметры для создания счета
     * @return идентификатор нового счета
     */
    public Long insertAccount(List<KeyValueParamsDto> params) {
        // Формируем запрос
        long id = -1L;
        params.sort(Comparator.comparing(param -> FIELD_ORDER.indexOf(param.getKey())));
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement stmt = getExecuteUpdateWithParamsStatement(connection, INSERT_ACCOUNT_QUERY, params, Statement.RETURN_GENERATED_KEYS);
            if (stmt != null) {
                stmt.executeUpdate();
                // Получаем идентификатора
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    id = rs.getLong(1);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(AccountDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return id;
    }

    /**
     * Метод для обновления счета по его идентификатору и новым параметрам
     * @param id идентификатор счета
     * @param params параметры для обновления
     * @return результат операции
     */
    public boolean updateAccount(Long id, List<KeyValueParamsDto> params) {
        // Формируем запрос
        StringBuilder query = new StringBuilder(UPDATE_ACCOUNT_QUERY);
        for (int i = 0; i <  params.size(); i++) {
            KeyValueParamsDto entry = params.get(i);
            if (i == params.size() - 1) {
                query.append(String.format(" %s = ?", entry.getKey()));
            } else {
                query.append(String.format(" %s = ?,", entry.getKey()));
            }
        }
        query.append(" WHERE id = ").append(id);

        // Test
        System.out.println(query);

        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement stmt = getExecuteUpdateWithParamsStatement(connection, query.toString(), params);
            if (stmt != null) {
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException ex) {
            Logger.getLogger(AccountDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    /**
     * Метод для удаления всех счетов
     * @return результат операции
     */
    public boolean deleteAccounts() {
        // Формируем запрос
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement stmt = getExecuteUpdateStatement(connection, DELETE_ACCOUNT_QUERY);
            if (stmt != null) {
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException ex) {
            Logger.getLogger(AccountDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    /**
     * Метод для удаления счета по идентификатору
     * @param id идентификатор счета
     * @return результат операции
     */
    public boolean deleteAccount(Long id) {
        // Формируем запрос
        StringBuilder query = new StringBuilder(DELETE_ACCOUNT_QUERY);
        query.append(" WHERE id = ").append(id);
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement stmt = getExecuteUpdateStatement(connection, query.toString());
            if (stmt != null) {
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException ex) {
            Logger.getLogger(AccountDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    /**
     * Метод для вызова SQL-запроса к базе данных по поиску счетов
     * @return список счетов
     */
    private List<Account> executeQuery(String query) {
        List<Account> accounts = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection()){
            Statement stmt = connection.createStatement();
            accounts.addAll(handleResultSet(stmt.executeQuery(query)));
        } catch (SQLException ex) {
            Logger.getLogger(AccountDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return accounts;
    }

    /**
     * Метод для вызова SQL-запроса к базе данных по поиску счетов с параметрами
     * @return список счетов
     */
    private List<Account> executeQueryWithParams(String query, List<KeyValueParamsDto> params) {
        List<Account> accounts = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection()){
            PreparedStatement stmt = connection.prepareStatement(query);

            // Устанавливаем параметры
            int i = 1;
            for (KeyValueParamsDto param : params) {
                Object value = param.getValue();

                // Проверяем и устанавливаем значение
                setStatementParam(stmt, i++, value, StringUtils.isNotEmpty(param.getCompareOperation())
                        && param.getCompareOperation().equalsIgnoreCase("LIKE"));
            }

            accounts.addAll(handleResultSet(stmt.executeQuery()));
        } catch (SQLException ex) {
            Logger.getLogger(AccountDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return accounts;
    }

    /**
     * Метод для получения SQL-запроса вида DML к базе данных с параметрами для создания, измененя или удаления счетов
     * @return список счетов
     */
    private PreparedStatement getExecuteUpdateStatement(Connection connection, String query) throws SQLException {
        return connection.prepareStatement(query);
    }

    /**
     * Метод для получения SQL-запроса вида DML к базе данных с параметрами для создания, измененя или удаления счетов
     * @return список счетов
     */
    private PreparedStatement getExecuteUpdateWithParamsStatement(Connection connection, String query, List<KeyValueParamsDto> params) throws SQLException {
        return getExecuteUpdateWithParamsStatement(connection, query, params, null);
    }

    /**
     * Метод для получения SQL-запроса вида DML к базе данных с параметрами для создания, измененя или удаления счетов
     * @return список счетов
     */
    private PreparedStatement getExecuteUpdateWithParamsStatement(Connection connection, String query, List<KeyValueParamsDto> params, Integer statementConst) throws SQLException {
        PreparedStatement stmt;
        if (statementConst == null) {
            stmt = connection.prepareStatement(query);
        } else {
            stmt = connection.prepareStatement(query, statementConst);
        }

        // Устанавливаем параметры
        int i = 1;
        for (KeyValueParamsDto param : params) {
            Object value = param.getValue();

            // Проверяем и устанавливаем значение
            setStatementParam(stmt, i++, value, StringUtils.isNotEmpty(param.getCompareOperation())
                    && param.getCompareOperation().equalsIgnoreCase("LIKE"));
        }

        return stmt;
    }

    /**
     * Установка параметра в запрос
     * @param stmt SQL-утверждение
     * @param index индекс параметра
     * @param value значение
     * @throws SQLException sql-ошибка
     */
    private void setStatementParam(PreparedStatement stmt, int index, Object value, boolean isLike) throws SQLException {
        // Проверяем значение
        if (value instanceof String) {
            String valueStr = isLike ?
                    "%" + value + "%" : (String) value;
            stmt.setString(index, valueStr);
        } else if (value instanceof Long) {
            stmt.setLong(index, (Long) value);
        } else if (value instanceof Integer) {
            stmt.setInt(index, (Integer) value);
        } else if (value instanceof BigDecimal) {
            stmt.setBigDecimal(index, (BigDecimal) value);
        } else if (value instanceof XMLGregorianCalendar) {
            stmt.setDate(index, new java.sql.Date(((XMLGregorianCalendar) value).toGregorianCalendar().getTime().getTime()));
        } else {
            log("Не поддерживается тип данных параметра " + value.getClass().getName());
            System.out.println("Не поддерживается тип данных параметра " + value.getClass().getName());
        }
    }

    /**
     * Обработка результата запроса
     * @param rs результат запрос
     * @return список счетов
     * @throws SQLException sql-ошибка
     */
    private List<Account> handleResultSet(ResultSet rs) throws SQLException {
        List<Account> accounts = new ArrayList<>();

        while (rs.next()) {
            // Получение параметров счета
            Long id = rs.getLong("id");
            String label = rs.getString("label");
            String code = rs.getString("code");
            String category = rs.getString("category");
            BigDecimal amount = rs.getBigDecimal("amount");
            Date date = rs.getDate("open_date");

            // Создаем и кладем в список
            Account account = new Account(id, label, code, category, amount, date);
            accounts.add(account);
        }

        return accounts;
    }

    /**
     * Логирование ошибки
     * @param text текст
     */
    private void log(String text) {
        Logger.getLogger(AccountDAO.class.getName()).log(Level.SEVERE, text);
    }
    
}
