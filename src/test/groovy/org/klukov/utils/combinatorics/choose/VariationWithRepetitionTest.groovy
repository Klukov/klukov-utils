package org.klukov.utils.combinatorics.choose

import spock.lang.Specification

class VariationWithRepetitionTest extends Specification {

    VariationWithRepetition<String> sub = new VariationWithRepetition<>()

    def "should throw exception when input is invalid"() {
        when:
        sub.choose(objects as Collection<ChoosableWrapper<String>>, k)

        then:
        thrown(IllegalArgumentException.class)

        where:
        objects                                         | k  || _
        ChoosableDataGenerator.sampleStringChoosables() | -1 || _
        ChoosableDataGenerator.sampleStringChoosables() | 0  || _
        null                                            | 2  || _
        Collections.emptyList()                         | 2  || _
    }


}
