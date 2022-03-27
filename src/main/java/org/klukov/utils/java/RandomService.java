package org.klukov.utils.java;

import org.klukov.utils.combinatorics.choose.RandomFrictionQuery;

import java.math.BigDecimal;
import java.util.Random;

class RandomService implements RandomFrictionQuery {

    @Override
    public BigDecimal getRandomFraction() {
        return BigDecimal.valueOf(new Random().nextDouble());
    }
}
