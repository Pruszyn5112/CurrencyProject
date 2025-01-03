package com.example.currencyproject.api;

import com.example.currencyproject.model.Table;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface NbpApi {
    @GET("exchangerates/tables/A/")
    Call<List<Table>> getExchangeRates();
}
