package ru.kekens;

/**
 * Объект для передачи параметров значения поля сущности
 */
//@XmlRootElement
//@XmlType(name = "ValueParamsDto")
public class KeyValueParamsDto {

    /**
     * Ключ
     */
    private String key;

    /**
     * Значение
     */
    private Object value;

    /**
     * Операция сравнения (>,<,=, LIKE и т.д.)
     */
    private String compareOperation;

    /**
     * Логическая операция (AND, OR)
     */
    private String logicOperation;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    //    @XmlElement
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

//    @XmlElement
    public String getCompareOperation() {
        return compareOperation;
    }

    public void setCompareOperation(String compareOperation) {
        this.compareOperation = compareOperation;
    }

//    @XmlElement
    public String getLogicOperation() {
        return logicOperation;
    }

    public void setLogicOperation(String logicOperation) {
        this.logicOperation = logicOperation;
    }

}
