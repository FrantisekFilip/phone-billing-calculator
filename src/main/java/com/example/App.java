package com.example;

import com.example.services.CsvReader;
import com.example.services.calculator.TelephoneBillCalculator;
import com.example.services.calculator.TelephoneBillCalculatorImpl;

import java.io.IOException;
import java.math.BigDecimal;

public class App {

    public static void main(String[] args) {
        CsvReader csvReader = new CsvReader();
        TelephoneBillCalculator calculator = new TelephoneBillCalculatorImpl();

        try {
            String filePath = "phone-log.csv";
            String csvContent = csvReader.readCsvAsString(filePath);

            BigDecimal result = calculator.calculate(csvContent);
            System.out.println("Total amount to pay: " + result);
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }
    }
}
