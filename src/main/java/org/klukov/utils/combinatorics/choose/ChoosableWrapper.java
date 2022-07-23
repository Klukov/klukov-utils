package org.klukov.utils.combinatorics.choose;

import java.math.BigDecimal;

public record ChoosableWrapper<T>(BigDecimal probabilityCoefficient, T wrappedObject) {

    public static <T> ChoosableWrapper<T> of(BigDecimal probabilityCoefficient, T wrappedObject) {
        return new ChoosableWrapper<>(probabilityCoefficient, wrappedObject);
    }

    public static <T> ChoosableWrapper<T> of(T wrappedObject) {
        return new ChoosableWrapper<>(BigDecimal.ONE, wrappedObject);
    }

    public ChoosableWrapper<T> copy() {
        return new ChoosableWrapper<>(probabilityCoefficient, wrappedObject);
    }

}
