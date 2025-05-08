package org.klukov.utils.java

import spock.lang.Specification
import spock.lang.Subject

class ArrayListUtilsTest extends Specification {

    @Subject
    ArrayListUtils utils = new ArrayListUtils()

    def "test shallowCopyTo2DArrayList with integers"() {
        given:
        def input = [
                [1, 2, 3],
                [4, 5, 6],
                [7, 8, 9]
        ]

        when:
        def result = utils.shallowCopyTo2DArrayList(input)

        then:
        result.size() == input.size()
        result.eachWithIndex { row, i ->
            row == input[i]
        }
    }

    def "test shallowCopyTo2DArrayList with strings"() {
        given:
        def input = [
                ["a", "b", "c"],
                ["d", "e", "f"]
        ]

        when:
        def result = utils.shallowCopyTo2DArrayList(input)

        then:
        result.size() == input.size()
        result.eachWithIndex { row, i ->
            row == input[i]
        }
    }

    def "test shallowCopyTo2DArrayList with empty list"() {
        given:
        def input = []

        when:
        def result = utils.shallowCopyTo2DArrayList(input)

        then:
        result.isEmpty()
    }

    def "test shallowCopyTo2DArrayList with list of empty lists"() {
        given:
        def input = [[], []]

        when:
        def result = utils.shallowCopyTo2DArrayList(input)

        then:
        result.size() == 2
        result[0].isEmpty()
        result[1].isEmpty()
    }

    def "test shallowCopyTo3DArrayList with integers"() {
        given:
        def input = [
                [
                        [1, 2],
                        [3, 4]
                ],
                [
                        [5, 6],
                        [7, 8]
                ]
        ]

        when:
        def result = utils.shallowCopyTo3DArrayList(input)

        then:
        result.size() == input.size()
        result.eachWithIndex { twoDList, i ->
            twoDList.size() == input[i].size()
            twoDList.eachWithIndex { row, j ->
                row == input[i][j]
            }
        }
    }

    def "test copyTo3DArrayList with strings"() {
        given:
        def input = [
                [
                        ["a", "b"],
                        ["c", "d"]
                ],
                [
                        ["e", "f"],
                        ["g", "h"]
                ]
        ]

        when:
        def result = utils.shallowCopyTo3DArrayList(input)

        then:
        result.size() == input.size()
        result.eachWithIndex { twoDList, i ->
            twoDList.size() == input[i].size()
            twoDList.eachWithIndex { row, j ->
                row == input[i][j]
            }
        }
    }

    def "test copyTo3DArrayList with empty list"() {
        given:
        def input = []

        when:
        def result = utils.shallowCopyTo3DArrayList(input)

        then:
        result.isEmpty()
    }
}
