package ru.kekens;

import org.apache.commons.lang3.StringUtils;

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

    private static final String ACCOUNT_QUERY = "SELECT * FROM ACCOUNT WHERE 1=1";

    private static final Set<String> COMPARE_OPERATIONS_SET = Set.of("=", ">", "<", "LIKE");
    private static final Set<String> LOGIC_OPERATIONS_SET = Set.of("AND", "OR");

    private static final String DEFAULT_COMPARE_OPERATION = "=";
    private static final String DEFAULT_LOGIC_OPERATION = "AND";

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
    public List<Account> getAccountsByParams(Map<String, ValueParamsDto> params) {
        // Проверка параметров на пустоту
        if (params == null || params.isEmpty()) {
            return getAccounts();
        }

        // Проверяем параметры
        Map<String, ValueParamsDto> resultParams = new HashMap<>();
        for (Map.Entry<String, ValueParamsDto> entry : params.entrySet()) {
            // Проверяем значение
            String key = entry.getKey();
            ValueParamsDto valueParamsDto = entry.getValue();
            if (checkValueParams(key, valueParamsDto)) {
                resultParams.put(key, valueParamsDto);
            }
        }

        // Сортируем мапу по логической операции
        resultParams = resultParams.entrySet()
                .stream()
                .sorted(Comparator.comparing(entry -> !entry.getValue().getLogicOperation().equals("AND")))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, HashMap::new));

        // Формируем запрос
        StringBuilder query = new StringBuilder(ACCOUNT_QUERY);
        for (Map.Entry<String, ValueParamsDto> entry : resultParams.entrySet()) {
            ValueParamsDto valueParamsDto = entry.getValue();
            query.append(String.format(" %s %s %s :%s", valueParamsDto.getLogicOperation(), entry.getKey(),
                    valueParamsDto.getCompareOperation(),
                    entry.getKey()));
        }

        // Test
        System.out.println(query);

        return executeQueryWithParams(query.toString(), params);
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
    private List<Account> executeQueryWithParams(String query, Map<String, ValueParamsDto> params) {
        List<Account> accounts = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection()){
            CallableStatement stmt = connection.prepareCall(query);

            // Устанавливаем параметры
            for (Map.Entry<String, ValueParamsDto> param : params.entrySet()) {
                String key = param.getKey();
                ValueParamsDto valueParamsDto = param.getValue();
                Object value = valueParamsDto.getValue();

                // Проверяем значение
                if (value instanceof String) {
                    stmt.setString(key, (String) value);
                } else if (value instanceof Long) {
                    stmt.setLong(key, (Long) value);
                } else if (value instanceof Integer) {
                    stmt.setInt(key, (Integer) value);
                } else if (value instanceof BigDecimal) {
                    stmt.setBigDecimal(key, (BigDecimal) value);
                } else if (value instanceof Date) {
                    stmt.setDate(key, (java.sql.Date) value);
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
            Date date = rs.getDate("date");

            // Создаем и кладем в список
            Account account = new Account(id, label, code, category, amount, date);
            accounts.add(account);
        }

        return accounts;
    }

    /**
     * Проверка объекта параметра для запроса
     * @param valueParamsDto объект параметра для запроса
     * @return признак да/нет (валидный/невалидный)
     */
    private Boolean checkValueParams(String key, ValueParamsDto valueParamsDto) {
        boolean isValid = true;
        // Проверка операции сравнения
        String compareOperation = valueParamsDto.getCompareOperation();
        if (StringUtils.isEmpty(compareOperation)) {
            compareOperation = DEFAULT_COMPARE_OPERATION;
        }

        if (!COMPARE_OPERATIONS_SET.contains(compareOperation.toUpperCase())) {
            log("Не поддерживается операция сравнения \"" + compareOperation + "\". Параметр " + key + " не будет учтен");
            isValid = false;
        }

        if (compareOperation.toUpperCase().equals("LIKE") && !(valueParamsDto.getValue() instanceof String)) {
            log("Не поддерживается операция сравнения \"LIKE\" для нестроковых значений. Параметр " + key + " не будет учтен");
            isValid = false;
        }

        // Проверка логической операции
        String logicOperation = valueParamsDto.getLogicOperation();
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
