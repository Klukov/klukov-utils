package org.klukov.utils.combinatorics.choose;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.TreeMap;
import org.klukov.utils.java.BigDecimalUtils;

class ChoosableCommons<T> {

    TreeMap<BigDecimal, ChoosableWrapper<T>> generateProbabilityCoefficientMap(
            Collection<ChoosableWrapper<T>> objects) {
        var result = new TreeMap<BigDecimal, ChoosableWrapper<T>>();
        var sumHelper = BigDecimal.ZERO;
        for (var object : objects) {
            var probability = object.probabilityCoefficient();
            var nextStageProbability = sumHelper.add(probability);
            result.put(nextStageProbability, object);
            sumHelper = nextStageProbability;
        }
        return result;
    }

    ChoosableWrapper<T> getElementByRandom(
            TreeMap<BigDecimal, ChoosableWrapper<T>> probabilityCoefficients, BigDecimal random) {
        var highestProbabilityCoefficient = probabilityCoefficients.lastEntry();
        if (BigDecimalUtils.areEqual(highestProbabilityCoefficient.getKey(), random)) {
            return highestProbabilityCoefficient.getValue();
        }
        return probabilityCoefficients.higherEntry(random).getValue();
    }

    BigDecimal calculateProbabilitySum(Collection<ChoosableWrapper<T>> objects) {
        return objects.stream()
                .map(ChoosableWrapper::probabilityCoefficient)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
