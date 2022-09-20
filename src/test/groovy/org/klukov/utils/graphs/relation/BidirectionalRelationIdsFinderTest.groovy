package org.klukov.utils.graphs.relation

import org.klukov.utils.graphs.common.GraphEdge
import spock.lang.Specification
import spock.lang.Subject

class BidirectionalRelationIdsFinderTest extends Specification {

    @Subject
    BidirectionalRelationIdsQuery<String> sub = GraphRelationFactory.bidirectionalRelationIdsQuery()

    def "should return empty set when graph edges are empty or start node is node existing in edges"() {
        when:
        def result = sub.findAllConnectedIds(startId, graphEdges)

        then:
        result.isEmpty()

        where:
        startId     | graphEdges                                            || _
        "START"     | null                                                  || _
        "START"     | []                                                    || _
        "NON-EXIST" | getSimpleGraphEdges()                                 || _
        "NON-EXIST" | getSimpleGraphEdges() + getSelfConnectedEdge("START") || _
    }

    def "should properly process graph edges"() {
        given:
        def expectedResult = (graphEdges.collect({ it.childId }) + graphEdges.collect { it.parentId })
                .findAll { !it.startsWith("OUTER") }
                .toSet()

        when:
        def result = sub.findAllConnectedIds("START", graphEdges)

        then:
        result == expectedResult

        where:
        startId | graphEdges                                            || _
        "START" | getSimpleGraphEdges()                                 || _
        "START" | getSimpleGraphEdges() + getSelfConnectedEdge("START") || _
        "START" | getSimpleGraphEdges() + getSelfConnectedEdge("M02")   || _
        "START" | getComplexGraph()                                     || _
        "START" | getComplexGraph() + getSelfConnectedEdge("M09")       || _
        "START" | getComplexGraph() + getOuterEdges()                   || _
    }

    private static List<GraphEdge<String>> getComplexGraph() { // graph without cycles
        [
                new GraphEdgeTestImpl(parentId: "UNKNOWN", childId: "M01"),
                new GraphEdgeTestImpl(parentId: "M01", childId: "M02"),
                new GraphEdgeTestImpl(parentId: "M02", childId: "M03"),
                new GraphEdgeTestImpl(parentId: "M03", childId: "M04"),
                new GraphEdgeTestImpl(parentId: "M03", childId: "C030"),
                new GraphEdgeTestImpl(parentId: "M03", childId: "C031"),
                new GraphEdgeTestImpl(parentId: "C031", childId: "C032"),
                new GraphEdgeTestImpl(parentId: "M04", childId: "M05"),
                new GraphEdgeTestImpl(parentId: "M05", childId: "M06"),
                new GraphEdgeTestImpl(parentId: "M05", childId: "M08"),
                new GraphEdgeTestImpl(parentId: "M06", childId: "M10"),
                new GraphEdgeTestImpl(parentId: "M07", childId: "M06"),
                new GraphEdgeTestImpl(parentId: "M07", childId: "C070"),
                new GraphEdgeTestImpl(parentId: "M08", childId: "M09"),
                new GraphEdgeTestImpl(parentId: "M09", childId: "M10"),
                new GraphEdgeTestImpl(parentId: "M09", childId: "C090"),
                new GraphEdgeTestImpl(parentId: "M10", childId: "C100"),
                new GraphEdgeTestImpl(parentId: "M10", childId: "START"),
                new GraphEdgeTestImpl(parentId: "C100", childId: "C101"),
                new GraphEdgeTestImpl(parentId: "C100", childId: "C102"),
                new GraphEdgeTestImpl(parentId: "C101", childId: "C102"),
                new GraphEdgeTestImpl(parentId: "START", childId: "AS00"),
                new GraphEdgeTestImpl(parentId: "START", childId: "AS01"),
                new GraphEdgeTestImpl(parentId: "AS01", childId: "AS02"),
        ]
    }

    private static List<GraphEdge<String>> getOuterEdges() {
        [
                new GraphEdgeTestImpl(parentId: "OUTER-UNKNOWN", childId: "OUTER00"),
                new GraphEdgeTestImpl(parentId: "OUTER00", childId: "OUTER01"),
                new GraphEdgeTestImpl(parentId: "OUTER01", childId: "OUTER02"),
        ]
    }

    private static List<GraphEdge<String>> getSimpleGraphEdges() {
        [
                new GraphEdgeTestImpl(parentId: "UNKNOWN", childId: "M01"),
                new GraphEdgeTestImpl(parentId: "M01", childId: "M02"),
                new GraphEdgeTestImpl(parentId: "M02", childId: "M03"),
                new GraphEdgeTestImpl(parentId: "M03", childId: "START"),
        ]
    }

    private static GraphEdge<String> getSelfConnectedEdge(String id) {
        new GraphEdgeTestImpl(parentId: id, childId: id)
    }
}
