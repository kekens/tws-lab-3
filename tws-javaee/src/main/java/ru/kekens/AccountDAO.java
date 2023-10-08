package ru.kekens;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Date;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Класс, содержащий метод для выборки данных из базы данных, а также упаковки этих данных в объекты класса Account.
 */
public class AccountDAO {

    private static final String ACCOUNT_QUERY = "SELECT * FROM ACCOUNT WHERE 1=1";

    private static final Set<String> COMPARE_OPERATIONS_SET = Set.of("=", ">", "<", "LIKE");
    private static final Set<String> LOGIC_OPERATIONS_SET = Set.of("AND", "OR");

    private static final String DEFAULT_COMPARE_OPERATION = "=";
    private static final String DEFAULT_LOGIC_OPERATION = "AND";

    @Resource(lookup = "java:comp/env/jdbc/ifmo-tws")
    private DataSource dataSource;

    /**
     * Метод для поиска всех счетов
     * @return список всех счетов
     */
    public List<Account> getAccounts() {
        return executeQuery(ACCOUNT_QUERY);
    }

    /**
     * Метод для поиска счетов по параметрам
     * @return список счетов
     */
    public List<Account> getAccountsByParams(List<KeyValueParamsDto> params) {
        // Проверка параметров на пустоту
        if (params == null || params.isEmpty()) {
            return getAccounts();
        }

        // Проверяем параметры
        List<KeyValueParamsDto> resultParams = new ArrayList<>();
        for (KeyValueParamsDto entry : params) {
            // Проверяем значение
            if (checkValueParams(entry)) {
                resultParams.add(entry);
            }
        }

        // Сортируем мапу по логической операции
        resultParams = resultParams
                .stream()
                .sorted(Comparator.comparing(entry -> !entry.getLogicOperation().equals("AND")))
                .collect(Collectors.toList());

        // Формируем запрос
        StringBuilder query = new StringBuilder(ACCOUNT_QUERY);
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
     * Метод для вызова SQL-запроса к базе данных по поиску счетов
     * @return список счетов
     */
    private List<Account> executeQuery(String query) {
        List<Account> accounts = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()){
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
        try (Connection connection = dataSource.getConnection()){
            CallableStatement stmt = connection.prepareCall(query);

            // Устанавливаем параметры
            int i = 1;
            for (KeyValueParamsDto param : params) {
                Object value = param.getValue();

                // Проверяем значение
                if (value instanceof String) {
                    String valueStr = param.getCompareOperation().equalsIgnoreCase("LIKE") ?
                            "%" + value + "%" : (String) value;
                    stmt.setString(i++, valueStr);
                } else if (value instanceof Long) {
                    stmt.setLong(i++, (Long) value);
                } else if (value instanceof Integer) {
                    stmt.setInt(i++, (Integer) value);
                } else if (value instanceof BigDecimal) {
                    stmt.setBigDecimal(i++, (BigDecimal) value);
                } else if (value instanceof XMLGregorianCalendar) {
                    stmt.setDate(i++, new java.sql.Date(((XMLGregorianCalendar) value).toGregorianCalendar().getTime().getTime()));
                } else {
                    log("Не поддерживается тип данных параметра " + value.getClass().getName());
                    System.out.println("Не поддерживается тип данных параметра " + value.getClass().getName());
                }
            }

            accounts.addAll(handleResultSet(stmt.executeQuery()));
        } catch (SQLException ex) {
            Logger.getLogger(AccountDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return accounts;
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
        boolean isValid = true;
        String key = keyValueParamsDto.getKey();
        // Проверка операции сравнения
        String compareOperation = keyValueParamsDto.getCompareOperation();
        if (StringUtils.isEmpty(compareOperation)) {
            compareOperation = DEFAULT_COMPARE_OPERATION;
        }

        if (!COMPARE_OPERATIONS_SET.contains(compareOperation.toUpperCase())) {
            log("Не поддерживается операция сравнения \"" + compareOperation + "\". Параметр " + key + " не будет учтен");
            isValid = false;
        }

        if (compareOperation.toUpperCase().equals("LIKE") && !(keyValueParamsDto.getValue() instanceof String)) {
            log("Не поддерживается операция сравнения \"LIKE\" для нестроковых значений. Параметр " + key + " не будет учтен");
            isValid = false;
        }

        // Проверка логической операции
        String logicOperation = keyValueParamsDto.getLogicOperation();
        if (StringUtils.isEmpty(logicOperation)) {
            logicOperation = DEFAULT_LOGIC_OPERATION;
        }

        if (!LOGIC_OPERATIONS_SET.contains(logicOperation.toUpperCase())) {
            log("Не поддерживается логическая операция \"" + logicOperation + "\". Параметр " + key + " не будет учтен");
            isValid = false;
        }

        return isValid;
    }

    /**
     * Логирование ошибки
     * @param text текст
     */
    private void log(String text) {
        Logger.getLogger(AccountDAO.class.getName()).log(Level.SEVERE, text);
    }
    
}
