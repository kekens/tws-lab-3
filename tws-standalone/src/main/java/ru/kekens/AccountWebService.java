package ru.kekens;

import org.apache.commons.lang3.StringUtils;
import ru.kekens.exception.AccountServiceException;
import ru.kekens.exception.AccountServiceFault;

import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.util.List;

import static ru.kekens.Constants.*;

/**
 * Класс-реализация веб-сервиса
 */
@WebService(serviceName = "AccountService")
@HandlerChain(file = "handler-chain.xml")
public class AccountWebService {

    private AccountDAO dao;

    @WebMethod(operationName = "getAccountById")
    public Account getAccountById(Long id) throws AccountServiceException {
        String faultInfo = "Ошибка при поиске счета по ID = " + id;
        // Проверка id на null
        if (id == null) {
            throw new AccountServiceException("Идентификатор счета не задан", new AccountServiceFault(faultInfo));
        }

        // Проверка валидного значения ID
        if (id < 0) {
            throw new AccountServiceException("Переданный идентификатор должен быть больше 0", new AccountServiceFault(faultInfo));
        }
        Account result =  getAccountDAO().getAccountById(id);
        // Проверяем результат на пустоту
        if (result == null) {
            throw new AccountServiceException("Не найден счет с ID = " + id, new AccountServiceFault(faultInfo));
        }

        return result;
    }

    @WebMethod(operationName = "getAccounts")
    public List<Account> getAccounts(AccountsRequest accountsRequest) throws AccountServiceException {
        String faultInfo = "Ошибка при поиске счетов по параметрам";
        // Проверяем параметры
        List<KeyValueParamsDto> params = accountsRequest.getList();
        if (params != null) {
            for (KeyValueParamsDto entry : params) {
                // Проверяем значение
                ValidationResult validationResult = checkValueParams(entry, true);
                if (!validationResult.getSuccess()) {
                    throw new AccountServiceException(validationResult.getErrorMessage(), new AccountServiceFault(faultInfo));
                }
            }
        }
        return getAccountDAO().getAccountsByParams(params);
    }

    @WebMethod(operationName = "insertAccount")
    public Long insertAccount(AccountsRequest accountsRequest) throws AccountServiceException {
        String faultInfo = "Ошибка при вставке счета";
        // Проверяем параметры
        List<KeyValueParamsDto> params = accountsRequest.getList();
        if (params != null) {
            for (KeyValueParamsDto entry : params) {
                // Проверяем значение
                ValidationResult validationResult = checkValueParams(entry);
                if (!validationResult.getSuccess()) {
                    throw new AccountServiceException(validationResult.getErrorMessage(), new AccountServiceFault(faultInfo));
                }
            }
        }
        // Проверяем число переданных параметров
        if (params == null || params.size() != FIELD_ORDER.size()) {
            int size = params == null ? 0 : params.size();
            throw new AccountServiceException("Для вставки счета передано недостаточное количество параметров - " +
                    size + " вместо " + FIELD_ORDER.size(), new AccountServiceFault(faultInfo));
        }

        return getAccountDAO().insertAccount(accountsRequest.getList());
    }

    @WebMethod(operationName = "updateAccount")
    public Boolean updateAccount(Long id, AccountsRequest accountsRequest) throws AccountServiceException {
        String faultInfo = "Ошибка при обновлении счета с ID = " + id;
        // Проверяем существует ли счет
        try {
            getAccountById(id);
        } catch (AccountServiceException e) {
            throw new AccountServiceException(e.getMessage(), new AccountServiceFault(faultInfo));
        }
        // Проверка параметров на пустоту
        List<KeyValueParamsDto> params = accountsRequest != null ? accountsRequest.getList() : null;
        if (params == null || params.isEmpty()) {
            throw new AccountServiceException("Для обновления счета не передано ни одного параметра",
                    new AccountServiceFault(faultInfo));
        }

        // Проверка параметров
        for (KeyValueParamsDto entry : params) {
            // Проверяем значение
            ValidationResult validationResult = checkValueParams(entry);
            if (!validationResult.getSuccess()) {
                throw new AccountServiceException(validationResult.getErrorMessage(), new AccountServiceFault(faultInfo));
            }
        }

        // Проверяем существует ли счет
        getAccountById(id);

        return getAccountDAO().updateAccount(id, accountsRequest.getList());
    }

    @WebMethod(operationName = "deleteAccount")
    public Boolean deleteAccount(Long id) throws AccountServiceException {
        String faultInfo = "Ошибка при удалении счета с ID = " + id;
        // Проверка id на null
        if (id == null) {
            throw new AccountServiceException("Идентификатор счета не задан", new AccountServiceFault(faultInfo));
        }

        // Проверка валидного значения ID
        if (id < 0) {
            throw new AccountServiceException("Переданный идентификатор должен быть больше 0", new AccountServiceFault(faultInfo));
        }
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

    /**
     * Проверка объекта параметра для запроса
     * @param keyValueParamsDto объект параметра для запроса
     * @return признак да/нет (валидный/невалидный)
     */
    private ValidationResult checkValueParams(KeyValueParamsDto keyValueParamsDto) {
        return checkValueParams(keyValueParamsDto, false);
    }

    /**
     * Проверка объекта параметра для запроса
     * @param keyValueParamsDto объект параметра для запроса
     * @param checkOperation признак проверки операции
     * @return признак да/нет (валидный/невалидный)
     */
    private ValidationResult checkValueParams(KeyValueParamsDto keyValueParamsDto, boolean checkOperation) {
        String key = keyValueParamsDto.getKey();
        // Проверям поле
        if (key == null || key.isEmpty()) {
            return new ValidationResult(false, "Ключ параметра пустой");
        }
        if (!FIELD_ORDER.contains(key) && !key.equals("id")) {
            return new ValidationResult(false, "У сущности Account не существует поля \" + key");
        }
        // Проверяем тип данных
        Object value = keyValueParamsDto.getValue();
        if (!(value instanceof String) && !(value instanceof Long)
            && !(value instanceof Integer) && !(value instanceof BigDecimal)
            && !(value instanceof XMLGregorianCalendar))
        {
            return new ValidationResult(false,
                    "Не поддерживается тип данных параметра " + value.getClass().getName());
        }
        // Проверяем валидность параметра
        ValidationResult result = checkValue(key, value);
        if (!result.getSuccess()) {
            return result;
        }

        // Проверяем операции
        if (checkOperation) {
            // Проверка операции сравнения
            String compareOperation = keyValueParamsDto.getCompareOperation();
            if (StringUtils.isEmpty(compareOperation)) {
                compareOperation = DEFAULT_COMPARE_OPERATION;
            }

            if (!COMPARE_OPERATIONS_SET.contains(compareOperation.toUpperCase())) {
                return new ValidationResult(false, "Не поддерживается операция сравнения \""
                        + compareOperation + "\". Параметр - " + key);
            }

            if (compareOperation.toUpperCase().equals("LIKE") && !(keyValueParamsDto.getValue() instanceof String)) {
                return new ValidationResult(false, "Не поддерживается операция сравнения \"LIKE\" " +
                        "для нестроковых значений. Параметр - " + key);
            }

            // Проверка логической операции
            String logicOperation = keyValueParamsDto.getLogicOperation();
            if (StringUtils.isEmpty(logicOperation)) {
                logicOperation = DEFAULT_LOGIC_OPERATION;
            }

            if (!LOGIC_OPERATIONS_SET.contains(logicOperation.toUpperCase())) {
                return new ValidationResult(false, "Не поддерживается логическая операция \""
                        + logicOperation + "\". Параметр - " + key);
            }
        }

        return new ValidationResult();
    }

    private ValidationResult checkValue(String key, Object value) {
        boolean correct = true;
        switch (key) {
            case "label":
                correct = value instanceof String;
                break;
            case "code":
                correct = value instanceof String;
                break;
            case "category":
                correct = value instanceof String;
                break;
            case "amount":
                correct = value instanceof BigDecimal;
                break;
            case "open_date":
                correct = value instanceof XMLGregorianCalendar;
                break;
        }

        if (!correct) {
            return new ValidationResult(false, "Тип данных " + value.getClass().getName() +
                    " параметра " + key + " не соответствует требуемому");
        }
        return new ValidationResult();
    }

    /**
     * Внутренний класс для возврата результата проверок
     */
    private class ValidationResult {
        private Boolean success;
        private String errorMessage;

        public ValidationResult() {
            this.success = true;
        }

        public ValidationResult(Boolean success, String errorMessage) {
            this.success = success;
            this.errorMessage = errorMessage;
        }

        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

    }

}
