package com.acme.util;

import java.math.RoundingMode;
import java.text.NumberFormat;

public class ApiUtil {

    public static Float formatGrade(Float grade, int digits, RoundingMode mode) {
        if (grade == null) {
            return null;
        }
        NumberFormat formatter = NumberFormat.getInstance();
        formatter.setMaximumFractionDigits(digits);
        formatter.setRoundingMode(mode);
        return new Float(formatter.format(grade));
    }

}
