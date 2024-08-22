package com.example.services;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CsvReader {

    public String readCsvAsString(String filePath) throws IOException {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }

        return sb.toString();
    }
}
