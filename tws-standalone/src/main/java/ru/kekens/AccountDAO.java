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

/**
 * Класс, содержащий метод для выборки данных из базы данных, а также упаковки этих данных в объекты класса Account.
 */
public class AccountDAO {

    private static final String SELECT_ACCOUNT_QUERY = "SELECT * FROM account WHERE 1=1";
    private static final String INSERT_ACCOUNT_QUERY = "INSERT INTO account(label, code, category, amount, open_date) VALUES(?,?,?,?,?)";
    private static final String UPDATE_ACCOUNT_QUERY = "UPDATE TABLE account SET";
    private static final String DELETE_ACCOUNT_QUERY = "DELETE FROM account";
    private static final List<String> FIELD_ORDER = List.of("label", "code", "category", "amount", "open_date");

    private static final Set<String> COMPARE_OPERATIONS_SET = Set.of("=", ">", "<", "LIKE");
    private static final Set<String> LOGIC_OPERATIONS_SET = Set.of("AND", "OR");

    private static final String DEFAULT_COMPARE_OPERATION = "=";
    private static final String DEFAULT_LOGIC_OPERATION = "AND";

    /**
     * Метод для поиска всех счетов
     * @return список всех счетов
     */
    public List<Account> getAccounts() {
        return executeQuery(SELECT_ACCOUNT_QUERY);
    }

    /**
     * Метод для поиска счетов по параметрам
     * @param params параметры для поиска счетов
     * @return список счетов
     */
    public List<Account> getAccountsByParams(List<KeyValueParamsDto> params) {
        // Проверяем параметры
        List<KeyValueParamsDto> resultParams = new ArrayList<>();
        if (params != null) {
            for (KeyValueParamsDto entry : params) {
                // Проверяем значение
                if (checkValueParams(entry, true)) {
                    resultParams.add(entry);
                }
            }
        }

        // Проверка параметров на пустоту
        if (resultParams.isEmpty()) {
            return getAccounts();
        }

        // Сортируем мапу по логической операции
        resultParams = resultParams
                .stream()
                .sorted(Comparator.comparing(entry -> !entry.getLogicOperation().equals("AND")))
                .collect(Collectors.toList());

        // Формируем запрос
        StringBuilder query = new StringBuilder(SELECT_ACCOUNT_QUERY);
        for (int i = 0; i < resultParams.size(); i++) {
            KeyValueParamsDto entry = resultParams.get(i);
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

        return executeQueryWithParams(query.toString(), resultParams);
    }

    /**
     * Метод для создания нового счета
     * @param params параметры для создания счета
     * @return идентификатор нового счета
     */
    public Long insertAccount(List<KeyValueParamsDto> params) {
        // Проверяем параметры
        List<KeyValueParamsDto> resultParams = new ArrayList<>();
        if (params != null) {
            for (KeyValueParamsDto entry : params) {
                // Проверяем значение
                if (checkValueParams(entry)) {
                    resultParams.add(entry);
                }
            }
        }

        // Проверяем число переданных параметров
        if (resultParams.size() != FIELD_ORDER.size()) {
            log("Для вставки счета передано недостаточное количество параметров - " +
                    resultParams.size() + " вместо " + FIELD_ORDER.size());
            return -1L;
        }

        // Формируем запрос
        long id = -1L;
        resultParams.sort(Comparator.comparing(param -> FIELD_ORDER.indexOf(param.getKey())));
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement stmt = getExecuteUpdateWithParamsStatement(connection, INSERT_ACCOUNT_QUERY, resultParams);
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
        // Проверка параметров на пустоту
        if (params == null || params.isEmpty()) {
            log("Для обновления счета не передано ни одного параметра");
            return false;
        }

        // Проверяем параметры
        List<KeyValueParamsDto> resultParams = new ArrayList<>();
        for (KeyValueParamsDto entry : params) {
            // Проверяем значение
            if (checkValueParams(entry)) {
                resultParams.add(entry);
            }
        }

        // Формируем запрос
        StringBuilder query = new StringBuilder(UPDATE_ACCOUNT_QUERY);
        for (int i = 0; i < resultParams.size(); i++) {
            KeyValueParamsDto entry = resultParams.get(i);
            if (i == resultParams.size() - 1) {
                query.append(String.format(" %s = ?,", entry.getKey()));
            } else {
                query.append(String.format(" %s = ?", entry.getKey()));
            }
        }
        query.append(" WHERE id = ").append(id);

        // Test
        System.out.println(query);

        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement stmt = getExecuteUpdateWithParamsStatement(connection, query.toString(), resultParams);
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
        PreparedStatement stmt = connection.prepareStatement(query);

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
     * Проверка объекта параметра для запроса
     * @param keyValueParamsDto объект параметра для запроса
     * @return признак да/нет (валидный/невалидный)
     */
    private Boolean checkValueParams(KeyValueParamsDto keyValueParamsDto) {
        return checkValueParams(keyValueParamsDto, false);
    }

    /**
     * Проверка объекта параметра для запроса
     * @param keyValueParamsDto объект параметра для запроса
     * @param checkOperation признак проверки операции
     * @return признак да/нет (валидный/невалидный)
     */
    private Boolean checkValueParams(KeyValueParamsDto keyValueParamsDto, boolean checkOperation) {
        String key = keyValueParamsDto.getKey();
        // Проверям поле
        if (!FIELD_ORDER.contains(key)) {
            log("У сущности Account не существует поля " + key);
            return false;
        }
        // Проверяем операции
        if (checkOperation) {
            // Проверка операции сравнения
            String compareOperation = keyValueParamsDto.getCompareOperation();
            if (StringUtils.isEmpty(compareOperation)) {
                compareOperation = DEFAULT_COMPARE_OPERATION;
            }

            if (!COMPARE_OPERATIONS_SET.contains(compareOperation.toUpperCase())) {
                log("Не поддерживается операция сравнения \"" + compareOperation + "\". Параметр " + key + " не будет учтен");
                return false;
            }

            if (compareOperation.toUpperCase().equals("LIKE") && !(keyValueParamsDto.getValue() instanceof String)) {
                log("Не поддерживается операция сравнения \"LIKE\" для нестроковых значений. Параметр " + key + " не будет учтен");
                return false;
            }

            // Проверка логической операции
            String logicOperation = keyValueParamsDto.getLogicOperation();
            if (StringUtils.isEmpty(logicOperation)) {
                logicOperation = DEFAULT_LOGIC_OPERATION;
            }

            if (!LOGIC_OPERATIONS_SET.contains(logicOperation.toUpperCase())) {
                log("Не поддерживается логическая операция \"" + logicOperation + "\". Параметр " + key + " не будет учтен");
                return false;
            }
        }

        return true;
    }

    /**
     * Логирование ошибки
     * @param text текст
     */
    private void log(String text) {
        Logger.getLogger(AccountDAO.class.getName()).log(Level.SEVERE, text);
    }
    
}
