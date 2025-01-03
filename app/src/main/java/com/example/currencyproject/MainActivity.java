package com.example.currencyproject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.currencyproject.api.ApiClient;
import com.example.currencyproject.api.NbpApi;
import com.example.currencyproject.model.ExchangeRate;
import com.example.currencyproject.model.Table;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerFrom, spinnerTo;
    private EditText inputAmount;
    private TextView resultText;
    private Button convertButton;
    private List<ExchangeRate> exchangeRates = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicjalizacja widoków
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        inputAmount = findViewById(R.id.inputAmount);
        resultText = findViewById(R.id.resultText);
        convertButton = findViewById(R.id.convertButton);

        // Pobranie kursów walut
        fetchExchangeRates();

        // Obsługa przycisku "Przelicz"
        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateConversion();
            }
        });
    }

    private void fetchExchangeRates() {
        NbpApi api = ApiClient.getApiService();
        api.getExchangeRates().enqueue(new Callback<List<Table>>() {
            @Override
            public void onResponse(Call<List<Table>> call, Response<List<Table>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Pobranie listy kursów walut
                    exchangeRates = response.body().get(0).getRates();
                    setupSpinners();
                } else {
                    Toast.makeText(MainActivity.this, "Nie udało się pobrać kursów walut", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Table>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Błąd połączenia: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API Error", "Failed to fetch data", t);
            }
        });
    }

    private void setupSpinners() {
        // Tworzenie listy kodów walut
        List<String> currencyCodes = new ArrayList<>();
        for (ExchangeRate rate : exchangeRates) {
            currencyCodes.add(rate.getCode());
        }

        // Ustawienie adapterów dla spinnerów
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencyCodes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);
    }

    private void calculateConversion() {
        // Pobranie wartości z pól
        String fromCurrency = spinnerFrom.getSelectedItem().toString();
        String toCurrency = spinnerTo.getSelectedItem().toString();
        String inputAmountText = inputAmount.getText().toString();

        if (inputAmountText.isEmpty()) {
            Toast.makeText(this, "Wprowadź kwotę", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(inputAmountText);

        // Znalezienie kursów walut
        double fromRate = findRateByCode(fromCurrency);
        double toRate = findRateByCode(toCurrency);

        if (fromRate == 0 || toRate == 0) {
            Toast.makeText(this, "Nie można znaleźć kursu waluty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obliczenie wyniku
        double result = (amount / fromRate) * toRate;
        resultText.setText(String.format("%.2f %s", result, toCurrency));
    }

    private double findRateByCode(String code) {
        for (ExchangeRate rate : exchangeRates) {
            if (rate.getCode().equals(code)) {
                return rate.getMid();
            }
        }
        return 0;
    }
}
