package org.klukov.utils.combinatorics.choose;

import java.math.BigDecimal;

//@Getter
public record ChoosableWrapper<T>(BigDecimal probabilityCoefficient, T wrappedObject) {

    public static <T> ChoosableWrapper<T> of(BigDecimal probabilityCoefficient, T wrappedObject) {
        return new ChoosableWrapper<>(probabilityCoefficient, wrappedObject);
    }

    public ChoosableWrapper<T> copy() {
        return new ChoosableWrapper<>(probabilityCoefficient, wrappedObject);
    }

}
