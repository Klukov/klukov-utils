package org.klukov.utils.combinatorics.choose;

import org.klukov.utils.java.BigDecimalUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class VariationWithRepetition<T> {

    private final InputValidator<T> inputValidator;
    private final RandomFrictionQuery randomFrictionQuery;

    public VariationWithRepetition(RandomFrictionQuery randomFrictionQuery) {
        this.inputValidator = new InputValidator<>();
        this.randomFrictionQuery = randomFrictionQuery;
    }

    public List<T> choose(Collection<ChoosableWrapper<T>> objects, int k) {
        validateInput(objects, k);
        var probabilitySum = calculateProbabilitySum(objects);
        var probabilityCoefficients = generateProbabilityCoefficientMap(objects);
        return IntStream.rangeClosed(1, k)
                .mapToObj(e -> randomFrictionQuery.getRandomFraction())
                .map(random -> random.multiply(probabilitySum))
                .map(random -> getElementByRandom(probabilityCoefficients, random))
                .collect(Collectors.toList());
    }

    private T getElementByRandom(TreeMap<BigDecimal, T> probabilityCoefficients, BigDecimal random) {
        var highestProbabilityCoefficient = probabilityCoefficients.lastEntry();
        if (BigDecimalUtils.areEqual(highestProbabilityCoefficient.getKey(), random)) {
            return highestProbabilityCoefficient.getValue();
        }
        return probabilityCoefficients.higherEntry(random).getValue();
    }

    private TreeMap<BigDecimal, T> generateProbabilityCoefficientMap(Collection<ChoosableWrapper<T>> objects) {
        var result = new TreeMap<BigDecimal, T>();
        var sumHelper = BigDecimal.ZERO;
        for (var object : objects) {
            var probability = object.probabilityCoefficient();
            var nextStageProbability = sumHelper.add(probability);
            result.put(nextStageProbability, object.wrappedObject());
            sumHelper = nextStageProbability;
        }
        return result;
    }

    private BigDecimal calculateProbabilitySum(Collection<ChoosableWrapper<T>> objects) {
        return objects.stream()
                .map(ChoosableWrapper::probabilityCoefficient)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validateInput(Collection<ChoosableWrapper<T>> objects, int k) {
        inputValidator.validateChoosableObjects(objects);
        inputValidator.validateNumberOfObjectsToChoose(k);
    }
}
