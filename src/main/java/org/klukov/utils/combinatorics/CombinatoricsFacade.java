package org.klukov.utils.combinatorics;

import java.util.Collection;
import java.util.List;
import org.klukov.utils.combinatorics.choose.ChoosableWrapper;
import org.klukov.utils.combinatorics.choose.VariationWithRepetition;
import org.klukov.utils.combinatorics.choose.VariationWithoutRepetition;

public final class CombinatoricsFacade {

    private final RandomFrictionQuery randomFrictionQuery;

    public CombinatoricsFacade() {
        this.randomFrictionQuery = new RandomService();
    }

    public CombinatoricsFacade(RandomFrictionQuery randomFrictionQuery) {
        this.randomFrictionQuery = randomFrictionQuery;
    }

    public <T> List<T> chooseWithoutRepetition(Collection<ChoosableWrapper<T>> objects, int k) {
        return new VariationWithoutRepetition<T>(this.randomFrictionQuery).choose(objects, k);
    }

    public <T> List<T> chooseWithRepetition(Collection<ChoosableWrapper<T>> objects, int k) {
        return new VariationWithRepetition<T>(this.randomFrictionQuery).choose(objects, k);
    }
}
