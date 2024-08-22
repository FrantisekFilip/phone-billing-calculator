package com.example.services.calculator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TelephoneBillCalculatorImpl implements TelephoneBillCalculator {

    private static final BigDecimal DAY_RATE = BigDecimal.valueOf(1.0); // 1 Kč za minutu
    private static final BigDecimal NIGHT_RATE = BigDecimal.valueOf(0.50); // 0,50 Kč za minutu
    private static final BigDecimal EXTRA_RATE = BigDecimal.valueOf(0.20); // Extra sazba pro hovory nad 5 minut
    private static final int FREE_MINUTES = 5;

    @Override
    public BigDecimal calculate(String phoneLog) {
        String[] records = phoneLog.split("\n");
        Map<String, BigDecimal> phoneCharges = new HashMap<>();
        Map<String, Integer> callCounts = new HashMap<>();

        for (String record : records) {
            String[] fields = record.split(",");
            String phoneNumber = fields[0];
            LocalDateTime startTime = parseDateTime(fields[1]);
            LocalDateTime endTime = parseDateTime(fields[2]);

            BigDecimal charge = calculateCharge(startTime, endTime);

            phoneCharges.put(phoneNumber, phoneCharges.getOrDefault(phoneNumber, BigDecimal.ZERO).add(charge));
            callCounts.put(phoneNumber, callCounts.getOrDefault(phoneNumber, 0) + 1);
        }

        String mostFrequentNumber = findMostFrequentNumber(callCounts);


        if (mostFrequentNumber != null) {
            phoneCharges.put(mostFrequentNumber, BigDecimal.ZERO);
        }

        return phoneCharges.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private LocalDateTime parseDateTime(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return LocalDateTime.parse(dateTime, formatter);
    }

    private BigDecimal calculateCharge(LocalDateTime startTime, LocalDateTime endTime) {
        List<LocalDateTime> startOfMinutes = getStartOfEachMinute(startTime, endTime);

        BigDecimal totalCharge = BigDecimal.ZERO;
        int minutesCounted = 0;

        for (LocalDateTime minuteStart : startOfMinutes) {
            // Calculate the charge for this minute
            BigDecimal minuteCharge = calculateMinuteCharge(minuteStart);

            // Apply extra charge if needed
            if (minutesCounted >= FREE_MINUTES) {
                minuteCharge = EXTRA_RATE; // Apply reduced rate for extra minutes
            }

            totalCharge = totalCharge.add(minuteCharge);
            minutesCounted++;
        }

        return totalCharge;
    }

    private List<LocalDateTime> getStartOfEachMinute(LocalDateTime startTime, LocalDateTime endTime) {
        List<LocalDateTime> startOfMinutes = new ArrayList<>();
        LocalDateTime currentMinute = startTime;

        while (currentMinute.isBefore(endTime)) {
            startOfMinutes.add(currentMinute);
            currentMinute = currentMinute.plusMinutes(1);
        }

        if (endTime.isEqual(currentMinute)) {
            startOfMinutes.add(currentMinute);
        }

        return startOfMinutes;
    }

    private BigDecimal calculateMinuteCharge(LocalDateTime startOfMinute) {
        LocalDateTime eightAM = LocalDateTime.of(startOfMinute.toLocalDate(), LocalTime.of(8, 0));
        LocalDateTime fourPM = LocalDateTime.of(startOfMinute.toLocalDate(), LocalTime.of(16, 0));

        if (startOfMinute.isAfter(eightAM) && startOfMinute.isBefore(fourPM)) {
            return DAY_RATE;
        } else {
            return NIGHT_RATE;
        }
    }


    private String findMostFrequentNumber(Map<String, Integer> callCounts) {
        return callCounts.entrySet().stream()
                .max((entry1, entry2) -> {
                    int compareCount = Integer.compare(entry1.getValue(), entry2.getValue());
                    if (compareCount == 0) {
                        // Pokud je počet hovorů stejný, porovnej telefonní čísla numericky
                        return entry1.getKey().compareTo(entry2.getKey());
                    } else {
                        return compareCount;
                    }
                })
                .map(Map.Entry::getKey)
                .orElse(null);
    }
}
