package org.klukov.utils.combinatorics.choose;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public record ChoosableWrapper<T>(BigDecimal probabilityCoefficient, T wrappedObject) {
    public static <T> ChoosableWrapper<T> of(BigDecimal probabilityCoefficient, T wrappedObject) {
        return new ChoosableWrapper<T>(probabilityCoefficient, wrappedObject);
    }

}
