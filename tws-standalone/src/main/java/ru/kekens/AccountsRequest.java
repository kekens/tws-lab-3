package ru.kekens;


import java.util.ArrayList;

/**
 * Обертка для запроса по счетам
 */
public class AccountsRequest {

    private ArrayList<KeyValueParamsDto> list;

    public AccountsRequest() {
        this.list = new ArrayList<>();
    }

    public AccountsRequest(ArrayList<KeyValueParamsDto> list) {
        this.list = list;
    }

    public ArrayList<KeyValueParamsDto> getList() {
        return list;
    }

    public void setList(ArrayList<KeyValueParamsDto> list) {
        this.list = list;
    }
}