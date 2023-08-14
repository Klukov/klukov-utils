package org.klukov.utils.combinatorics.choose;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.klukov.utils.combinatorics.RandomFrictionQuery;

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
                .map(randomNumber -> getRandomElement(randomNumber, probabilityCoefficients))
                .map(ChoosableWrapper::wrappedObject)
                .collect(Collectors.toList());
    }

    private ChoosableWrapper<T> getRandomElement(
            BigDecimal randomNumber,
            TreeMap<BigDecimal, ChoosableWrapper<T>> probabilityCoefficients) {
        return choosableCommons.getRandomElement(probabilityCoefficients, randomNumber);
    }

    private void validateInput(Collection<ChoosableWrapper<T>> objects, int k) {
        inputValidator.validateChoosableObjects(objects);
        inputValidator.validateNumberOfObjectsToChoose(k);
    }
}
