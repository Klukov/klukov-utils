package org.klukov.utils.combinatorics.choose;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class VariationWithRepetition<T> {

    private final InputValidator<T> inputValidator;
    private final ChoosableCommons<T> choosableCommons;
    private final RandomFrictionQuery randomFrictionQuery;

    public VariationWithRepetition(RandomFrictionQuery randomFrictionQuery) {
        this.inputValidator = new InputValidator<>();
        this.choosableCommons = new ChoosableCommons<>();
        this.randomFrictionQuery = randomFrictionQuery;
    }

    public List<T> choose(Collection<ChoosableWrapper<T>> objects, int k) {
        validateInput(objects, k);
        var probabilitySum = choosableCommons.calculateProbabilitySum(objects);
        var probabilityCoefficients = choosableCommons.generateProbabilityCoefficientMap(objects);
        return IntStream.range(0, k)
                .mapToObj(e -> randomFrictionQuery.getRandomFraction())
                .map(randomNumber -> randomNumber.multiply(probabilitySum))
                .map(randomNumber -> choosableCommons.getElementByRandom(probabilityCoefficients, randomNumber))
                .map(ChoosableWrapper::wrappedObject)
                .collect(Collectors.toList());
    }

    private void validateInput(Collection<ChoosableWrapper<T>> objects, int k) {
        inputValidator.validateChoosableObjects(objects);
        inputValidator.validateNumberOfObjectsToChoose(k);
    }
}
