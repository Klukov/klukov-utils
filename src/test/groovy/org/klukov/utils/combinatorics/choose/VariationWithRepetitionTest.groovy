package org.klukov.utils.combinatorics.choose

import spock.lang.Specification

class VariationWithRepetitionTest extends Specification {

    RandomFrictionQuery randomFrictionQuery = Mock()

    VariationWithRepetition<String> sub = new VariationWithRepetition<>(randomFrictionQuery)

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

    def "should return correct single element when k is equal 1"() {
        given:
        def choosables = ChoosableDataGenerator.sampleStringChoosables()
        randomFrictionQuery.getRandomFraction() >> random

        when:
        def result = sub.choose(choosables, 1)

        then:
        result.size() == 1
        result[0] == choosables[expectedElement].wrappedObject

        where:
        random || expectedElement
        1.0    || 4
        0.8    || 4
        0.7    || 4
        0.6    || 3
        0.5    || 3
        0.4    || 3
        0.3    || 2
        0.2    || 2
        0.1    || 1
        0.01   || 0
        0.0    || 0
    }

    def "should return proper number of elements"() {
        def choosables = ChoosableDataGenerator.sampleStringChoosables()
        randomFrictionQuery.getRandomFraction() >> 0.5

        when:
        def result = sub.choose(choosables, k)

        then:
        result.size() == expectedObjects
        result.each { it ->
            assert it == choosables[3].wrappedObject
        }

        where:
        k  || expectedObjects
        1  || 1
        2  || 2
        3  || 3
        4  || 4
        5  || 5
        6  || 6
        10 || 10
    }

    def "should return correct elements"() {
        given:
        def choosables = ChoosableDataGenerator.sampleStringChoosables()
        randomFrictionQuery.getRandomFraction() >>> random

        when:
        def result = sub.choose(choosables, k)

        then:
        result.size() == expectedObjects.size()
        (0..expectedObjects.size() - 1).each { index ->
            assert result[index] == choosables[expectedObjects[index]].wrappedObject
        }

        where:
        k | random                              || expectedObjects
        7 | [0.5, 0.5, 0.5, 0.1, 0.1, 0.5, 0.5] || [3, 3, 3, 1, 1, 3, 3]
        5 | [0.5, 0.5, 0.5, 0.5, 0.5]           || [3, 3, 3, 3, 3]
        4 | [0.5, 0.3, 0.1, 0.01]               || [3, 2, 1, 0]
        4 | [0.01, 0.3, 0.3, 0.01]              || [0, 2, 2, 0]
        3 | [0.01, 0.1, 0.3]                    || [0, 1, 2]
        2 | [0.3, 0.1]                          || [2, 1]
    }


}
