package org.klukov.utils.combinatorics.choose;

import java.util.Collection;
import java.util.Objects;

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
    }
}
