package org.klukov.utils.combinatorics.choose

import spock.lang.Specification

class VariationWithoutRepetitionTest extends Specification {

    RandomFrictionQuery randomFrictionQuery = Mock()

    VariationWithoutRepetition<String> sub = new VariationWithoutRepetition<>(randomFrictionQuery)

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

    def "should return proper number of elements"() {
        def choosables = ChoosableDataGenerator.sampleStringChoosables()
        randomFrictionQuery.getRandomFraction() >> 0.8

        when:
        def result = sub.choose(choosables, k)

        then:
        result.size() == expectedObjects

        where:
        k || expectedObjects
        1 || 1
        2 || 2
        3 || 3
        4 || 4
    }

    def "should return all elements if k is equal to collection size"() {
        given:
        def choosables = ChoosableDataGenerator.sampleStringChoosables()
        randomFrictionQuery.getRandomFraction() >>> [0.8, 0.8, 0.8, 0.8]

        when:
        def result = sub.choose(choosables, choosables.size())

        then:
        result.size() == choosables.size()
        result[0] == choosables[3].wrappedObject
        result[1] == choosables[2].wrappedObject
        result[2] == choosables[1].wrappedObject
        result[3] == choosables[0].wrappedObject
    }

    def "should return single element when k is equal 1"() {
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
        0.8    || 3
        0.45   || 2
        0.2    || 1
        0.05   || 0
    }
}
