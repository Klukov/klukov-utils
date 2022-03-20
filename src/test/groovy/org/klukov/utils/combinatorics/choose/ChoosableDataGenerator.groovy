package org.klukov.utils.combinatorics.choose

class ChoosableDataGenerator {

    static Collection<ChoosableWrapper<String>> sampleStringChoosables() {
        [
                ChoosableWrapper.of(1 as BigDecimal, "A"),
                ChoosableWrapper.of(2 as BigDecimal, "B"),
                ChoosableWrapper.of(3 as BigDecimal, "C"),
                ChoosableWrapper.of(4 as BigDecimal, "D"),
                ChoosableWrapper.of(5 as BigDecimal, "E"),
        ]
    }
}
