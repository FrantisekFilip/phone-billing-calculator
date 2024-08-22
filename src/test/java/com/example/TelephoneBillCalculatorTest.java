package com.example;

import com.example.services.calculator.TelephoneBillCalculatorImpl;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TelephoneBillCalculatorTest {

    @Test
    public void testCalculate() {
        TelephoneBillCalculatorImpl calculator = new TelephoneBillCalculatorImpl();
        String phoneLog = "420774577453,13-01-2020 18:10:15,13-01-2020 18:12:57\n" +
                "420776562353,18-01-2020 08:59:20,18-01-2020 09:10:00\n" +
                "420774577453,13-01-2020 16:05:00,13-01-2020 16:07:30\n" +
                "420776562353,19-01-2020 15:59:00,19-01-2020 16:05:00";


        BigDecimal result = calculator.calculate(phoneLog);
        assertEquals(BigDecimal.valueOf(3.0), result);
    }
}
