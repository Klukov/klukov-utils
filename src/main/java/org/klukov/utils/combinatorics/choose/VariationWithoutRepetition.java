package org.klukov.utils.combinatorics.choose;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class VariationWithoutRepetition<T> {

    private final InputValidator<T> inputValidator;
    private final ChoosableCommons<T> choosableCommons;
    private final RandomFrictionQuery randomFrictionQuery;

    public VariationWithoutRepetition(RandomFrictionQuery randomFrictionQuery) {
        this.inputValidator = new InputValidator<>();
        this.choosableCommons = new ChoosableCommons<>();
        this.randomFrictionQuery = randomFrictionQuery;
    }

    public List<T> choose(Collection<ChoosableWrapper<T>> objects, int k) {
        validateInput(objects, k);
        var clonedObjects = cloneObjects(objects);
        var wrappedResults = new LinkedList<ChoosableWrapper<T>>();
        IntStream.range(0, k).forEach(index -> {
            var probabilityCoefficientSum = choosableCommons.calculateProbabilitySum(clonedObjects);
            var normalizedProbabilityMap = choosableCommons.generateProbabilityCoefficientMap(clonedObjects);
            var random = randomFrictionQuery.getRandomFraction().multiply(probabilityCoefficientSum);
            var randomElement = choosableCommons.getElementByRandom(normalizedProbabilityMap, random);
            wrappedResults.add(randomElement);
            clonedObjects.remove(randomElement);
        });
        return wrappedResults.stream()
                .map(ChoosableWrapper::wrappedObject)
                .collect(Collectors.toList());
    }

    private void validateInput(Collection<ChoosableWrapper<T>> objects, int k) {
        inputValidator.validateChoosableObjects(objects);
        inputValidator.validateNumberOfObjectsToChoose(k);
        if (objects.size() < k) {
            throw new IllegalArgumentException("Number of objects to choose must be higher than 0");
        }
    }

    private Collection<ChoosableWrapper<T>> cloneObjects(Collection<ChoosableWrapper<T>> objects) {
        return objects.stream()
                .map(ChoosableWrapper::copy)
                .collect(Collectors.toList());
    }

}
