package ru.kekens;

import java.util.List;
import java.util.Set;

public class Constants {

    public static final List<String> FIELD_ORDER = List.of("label", "code", "category", "amount", "open_date");

    public static final Set<String> COMPARE_OPERATIONS_SET = Set.of("=", ">", "<", "LIKE");
    public static final Set<String> LOGIC_OPERATIONS_SET = Set.of("AND", "OR");

    public static final String DEFAULT_COMPARE_OPERATION = "=";
    public static final String DEFAULT_LOGIC_OPERATION = "AND";
    
}
