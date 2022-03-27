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
        randomFrictionQuery.getRandomFraction() >> 0.9

        when:
        def result = sub.choose(choosables, k)

        then:
        result.size() == expectedObjects
        def index = choosables.size() - 1
        result.each { it ->
            assert it == choosables[index].wrappedObject()
            index--
        }

        where:
        k || expectedObjects
        1 || 1
        2 || 2
        3 || 3
        4 || 4
        5 || 5
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
        k | random                         || expectedObjects
        5 | [0.8, 0.8, 0.8, 0.8, 0.8]      || [4, 3, 2, 1, 0]
        5 | [0.8, 0.8, 0.8, 0.8, 0.8]      || [4, 3, 2, 1, 0]
        5 | [0.05, 0.05, 0.05, 0.05, 0.05] || [0, 1, 2, 3, 4]
        4 | [0.5, 0.5, 0.2, 0.1]           || [3, 2, 1, 0]
        4 | [0.3, 0.8, 0.1, 0.8]           || [2, 4, 0, 3]
        3 | [0.01, 0.01, 0.01]             || [0, 1, 2]
        2 | [0.3, 0.1]                     || [2, 1]
    }
}
