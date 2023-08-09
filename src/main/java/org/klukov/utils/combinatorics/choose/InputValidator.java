package org.klukov.utils.combinatorics.choose;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Objects;
import org.klukov.utils.java.BigDecimalUtils;

class InputValidator<T> {

    void validateNumberOfObjectsToChoose(int k) {
        if (k < 1) {
            throw new IllegalArgumentException("Number of objects to choose must be higher than 0");
        }
    }

    void validateChoosableObjects(Collection<ChoosableWrapper<T>> objects) {
        if (Objects.isNull(objects) || objects.isEmpty()) {
            throw new IllegalArgumentException("Lack of objects to choose");
        }
        objects.forEach(
                object -> {
                    if (!isProbabilityCoefficientValid(object.probabilityCoefficient())) {
                        throw new IllegalArgumentException(
                                "At least one of probability coefficients is invalid");
                    }
                });
    }

    private boolean isProbabilityCoefficientValid(BigDecimal probabilityCoefficient) {
        if (probabilityCoefficient == null) return false;
        return BigDecimalUtils.isBigger(probabilityCoefficient, BigDecimal.ZERO);
    }
}
