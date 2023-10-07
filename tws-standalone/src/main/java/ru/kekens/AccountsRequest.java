package ru.kekens;


import java.util.ArrayList;

/**
 * Обертка для запроса
 */
public class AccountsRequest {

    ArrayList<KeyValueParamsDto> list;

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