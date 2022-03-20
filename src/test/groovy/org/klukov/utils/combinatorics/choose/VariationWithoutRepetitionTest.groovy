package org.klukov.utils.combinatorics.choose

import spock.lang.Specification

class VariationWithoutRepetitionTest extends Specification {

    VariationWithoutRepetition<String> sub = new VariationWithoutRepetition<>()

    def "should throw exception when input is invalid"() {
        when:
        sub.choose(objects as Collection<ChoosableWrapper<String>>, k)

        then:
        thrown(IllegalArgumentException.class)

        where:
        objects                                         | k                                                          || _
        ChoosableDataGenerator.sampleStringChoosables() | -1                                                         || _
        ChoosableDataGenerator.sampleStringChoosables() | 0                                                          || _
        ChoosableDataGenerator.sampleStringChoosables() | ChoosableDataGenerator.sampleStringChoosables().size() + 1 || _
        null                                            | 2                                                          || _
        Collections.emptyList()                         | 2                                                          || _
    }
}
