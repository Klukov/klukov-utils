package org.klukov.utils.combinatorics.choose;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class VariationWithRepetition<T> {

    private final InputValidator<T> inputValidator;

    public VariationWithRepetition() {
        this.inputValidator = new InputValidator<>();
    }

    public List<T> choose(Collection<ChoosableWrapper<T>> objects, int k) {
        validateInput(objects, k);
        return Collections.emptyList();
    }

    private void validateInput(Collection<ChoosableWrapper<T>> objects, int k) {
        inputValidator.validateChoosableObjects(objects);
        inputValidator.validateNumberOfObjectsToChoose(k);
    }
}
