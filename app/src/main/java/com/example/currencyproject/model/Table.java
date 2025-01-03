package com.example.currencyproject.model;

import java.util.List;

public class Table {
    private String table;
    private String no;
    private String effectiveDate;
    private List<ExchangeRate> rates;

    public String getTable() {
        return table;
    }

    public String getNo() {
        return no;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public List<ExchangeRate> getRates() {
        return rates;
    }
}
