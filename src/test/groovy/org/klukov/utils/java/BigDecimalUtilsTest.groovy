package org.klukov.utils.java

import spock.lang.Specification

class BigDecimalUtilsTest extends Specification {

    def "should return correct result for isBigger method"() {
        when:
        def result = BigDecimalUtils.isBigger(a, b)

        then:
        result == expectedResult

        where:
        a                        | b                        || expectedResult
        BigDecimal.ONE           | BigDecimal.ZERO          || true
        BigDecimal.ONE           | new BigDecimal("1.0000") || false
        new BigDecimal("1.0000") | BigDecimal.ONE           || false
        BigDecimal.ZERO          | BigDecimal.ONE           || false
    }

    def "should throw exception during validation of method isBigger"() {
        when:
        BigDecimalUtils.isBigger(a, b)

        then:
        thrown(IllegalArgumentException.class)

        where:
        a              | b               || _
        null           | BigDecimal.ZERO || _
        BigDecimal.ONE | null            || _
        null           | null            || _
    }

    def "should return correct result for isBiggerOrEqual method"() {
        when:
        def result = BigDecimalUtils.isBiggerOrEqual(a, b)

        then:
        result == expectedResult

        where:
        a                        | b                        || expectedResult
        BigDecimal.ONE           | BigDecimal.ZERO          || true
        BigDecimal.ONE           | new BigDecimal("1.0000") || true
        new BigDecimal("1.0000") | BigDecimal.ONE           || true
        BigDecimal.ZERO          | BigDecimal.ONE           || false
    }

    def "should throw exception during validation of method isBiggerOrEqual"() {
        when:
        BigDecimalUtils.isBiggerOrEqual(a, b)

        then:
        thrown(IllegalArgumentException.class)

        where:
        a              | b               || _
        null           | BigDecimal.ZERO || _
        BigDecimal.ONE | null            || _
        null           | null            || _
    }

    def "should return correct result for isSmaller method"() {
        when:
        def result = BigDecimalUtils.isSmaller(a, b)

        then:
        result == expectedResult

        where:
        a                        | b                        || expectedResult
        BigDecimal.ONE           | BigDecimal.ZERO          || false
        BigDecimal.ONE           | new BigDecimal("1.0000") || false
        new BigDecimal("1.0000") | BigDecimal.ONE           || false
        BigDecimal.ZERO          | BigDecimal.ONE           || true
    }

    def "should throw exception during validation of method isSmaller"() {
        when:
        BigDecimalUtils.isSmaller(a, b)

        then:
        thrown(IllegalArgumentException.class)

        where:
        a              | b               || _
        null           | BigDecimal.ZERO || _
        BigDecimal.ONE | null            || _
        null           | null            || _
    }

    def "should return correct result for isSmallerOrEqual method"() {
        when:
        def result = BigDecimalUtils.isSmallerOrEqual(a, b)

        then:
        result == expectedResult

        where:
        a                        | b                        || expectedResult
        BigDecimal.ONE           | BigDecimal.ZERO          || false
        BigDecimal.ONE           | new BigDecimal("1.0000") || true
        new BigDecimal("1.0000") | BigDecimal.ONE           || true
        BigDecimal.ZERO          | BigDecimal.ONE           || true
    }

    def "should throw exception during validation of method isSmallerOrEqual"() {
        when:
        BigDecimalUtils.isSmallerOrEqual(a, b)

        then:
        thrown(IllegalArgumentException.class)

        where:
        a              | b               || _
        null           | BigDecimal.ZERO || _
        BigDecimal.ONE | null            || _
        null           | null            || _
    }

    def "should return correct result for areEqual method"() {
        when:
        def result = BigDecimalUtils.areEqual(a, b)

        then:
        result == expectedResult

        where:
        a                        | b                        || expectedResult
        BigDecimal.ONE           | BigDecimal.ZERO          || false
        BigDecimal.ONE           | new BigDecimal("1.0000") || true
        new BigDecimal("1.0000") | BigDecimal.ONE           || true
        BigDecimal.ZERO          | BigDecimal.ONE           || false
    }

    def "should throw exception during validation of method areEqual"() {
        when:
        BigDecimalUtils.areEqual(a, b)

        then:
        thrown(IllegalArgumentException.class)

        where:
        a              | b               || _
        null           | BigDecimal.ZERO || _
        BigDecimal.ONE | null            || _
        null           | null            || _
    }

}
