package org.klukov.utils.combinatorics.choose;

import java.math.BigDecimal;
import java.security.SecureRandom;

class RandomService implements RandomFrictionQuery {

    @Override
    public BigDecimal getRandomFraction() {
        return BigDecimal.valueOf(new SecureRandom().nextDouble());
    }
}
