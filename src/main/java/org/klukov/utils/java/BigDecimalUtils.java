package org.klukov.utils.java;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class BigDecimalUtils {

    public static boolean isBigger(BigDecimal a, BigDecimal b) {
        validateInput(a, b);
        return a.compareTo(b) > 0;
    }

    public static boolean isBiggerOrEqual(BigDecimal a, BigDecimal b) {
        return !isSmaller(a, b);
    }

    public static boolean isSmaller(BigDecimal a, BigDecimal b) {
        validateInput(a, b);
        return a.compareTo(b) < 0;
    }

    public static boolean isSmallerOrEqual(BigDecimal a, BigDecimal b) {
        return !isBigger(a, b);
    }

    public static boolean areEqual(BigDecimal a, BigDecimal b) {
        validateInput(a, b);
        return a.compareTo(b) == 0;
    }

    private static void validateInput(BigDecimal a, BigDecimal b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("Cannot compare null big decimals");
        }
    }
}
