package org.klukov.utils.graphs.relation

import org.klukov.utils.graphs.common.GraphProcessingException
import org.klukov.utils.graphs.common.ProcessingErrorType
import org.klukov.utils.graphs.validation.GraphValidatorFactory
import spock.lang.Specification
import spock.lang.Subject

class DirectionalRelationIdsFinderTest extends Specification {

    @Subject
    DirectionalRelationIdsFinder<String, GraphNodeInputTestImpl> sub =
            GraphRelationFactory.directionalRelationIdsQuery(GraphValidatorFactory.graphValidator())

    def "should throw exception if start id is null"() {
        when:
        sub.findAllConnectedIds(
                new RelationIdsFinderInputTestImpl(
                        startNodeId: null,
                        graphInput: generateOrderedSimpleGraph(),
                ))

        then:
        def exception = thrown(GraphProcessingException.class)
        exception.processingErrorType == ProcessingErrorType.NULL_START_ID
    }

    def "should throw exception if graph elements collection is null or empty"() {
        when:
        sub.findAllConnectedIds(
                new RelationIdsFinderInputTestImpl(
                        startNodeId: 'START',
                        graphInput: graphInput as Collection<GraphNodeInputTestImpl>,
                ))

        then:
        def exception = thrown(GraphProcessingException.class)
        exception.processingErrorType == ProcessingErrorType.NULL_OR_EMPTY_GRAPH

        where:
        graphInput              || _
        null                    || _
        Collections.emptyList() || _
    }

    def "should throw exception if graph contains null nodes or null node ids"() {
        when:
        sub.findAllConnectedIds(
                new RelationIdsFinderInputTestImpl(
                        startNodeId: 'START',
                        graphInput: graphInput,
                ))

        then:
        def exception = thrown(GraphProcessingException.class)
        exception.processingErrorType == ProcessingErrorType.NULL_NODES

        where:
        graphInput                     || _
        generateGraphWithNullNodes()   || _
        generateGraphWithNullNodeIds() || _
    }

    def "should throw exception if graph elements do not contain start id"() {
        when:
        sub.findAllConnectedIds(
                new RelationIdsFinderInputTestImpl(
                        startNodeId: 'notMatchedId',
                        graphInput: generateOrderedSimpleGraph(),
                ))

        then:
        def exception = thrown(GraphProcessingException.class)
        exception.processingErrorType == ProcessingErrorType.STAR_NODE_NOT_IN_GRAPH
    }

    def "should throw exception if graph contains id duplicates"() {
        when:
        sub.findAllConnectedIds(
                new RelationIdsFinderInputTestImpl(
                        startNodeId: 'START',
                        graphInput: generateGraphWithDuplicates(),
                ))

        then:
        def exception = thrown(GraphProcessingException.class)
        exception.processingErrorType == ProcessingErrorType.DUPLICATED_NODES
    }

    def "should properly process list like graph"() {
        when:
        def result = sub.findAllConnectedIds(
                new RelationIdsFinderInputTestImpl(
                        startNodeId: 'START',
                        graphInput: givenGraph,
                ))

        then:
        result == givenGraph.collect { it -> it.id }.toSet()

        where:
        givenGraph                     || _
        generateSingleElementGraph()   || _
        generateOrderedSimpleGraph()   || _
        generateUnorderedSimpleGraph() || _
    }

    def "should properly process complex graphs"() {
        when:
        def result = sub.findAllConnectedIds(
                new RelationIdsFinderInputTestImpl(
                        startNodeId: 'START',
                        graphInput: givenGraph,
                ))
        def expectedNodes = givenGraph.findAll { it.id.startsWith("M") }.collect({ it.id }) + "START"

        then:
        result == expectedNodes.toSet()

        where:
        givenGraph                          || _
        generateComplexGraphWithoutCycles() || _
        generateSimpleGraphWithCycles()     || _
        generateComplexGraphWithCycles() || _
    }

    private static List<GraphNodeInputTestImpl> generateGraphWithNullNodes() {
        [
                new GraphNodeInputTestImpl(id: "001", relatedIds: ["UNKNOWN"]),
                new GraphNodeInputTestImpl(id: "002", relatedIds: ["001"]),
                null,
                new GraphNodeInputTestImpl(id: "003", relatedIds: ["002"]),
                new GraphNodeInputTestImpl(id: "004", relatedIds: ["003"]),
                new GraphNodeInputTestImpl(id: "START", relatedIds: ["004"]),
        ]
    }

    private static List<GraphNodeInputTestImpl> generateGraphWithNullNodeIds() {
        [
                new GraphNodeInputTestImpl(id: "001", relatedIds: ["UNKNOWN"]),
                new GraphNodeInputTestImpl(id: "002", relatedIds: ["001"]),
                new GraphNodeInputTestImpl(id: "003", relatedIds: ["002"]),
                new GraphNodeInputTestImpl(id: "004", relatedIds: ["003"]),
                new GraphNodeInputTestImpl(id: "START", relatedIds: ["004"]),
                new GraphNodeInputTestImpl(id: null, relatedIds: ["004"]),
        ]
    }

    private static List<GraphNodeInputTestImpl> generateGraphWithDuplicates() {
        [
                new GraphNodeInputTestImpl(id: "001", relatedIds: ["UNKNOWN"]),
                new GraphNodeInputTestImpl(id: "002", relatedIds: ["001"]),
                new GraphNodeInputTestImpl(id: "003", relatedIds: ["002"]),
                new GraphNodeInputTestImpl(id: "004", relatedIds: ["003"]),
                new GraphNodeInputTestImpl(id: "START", relatedIds: ["004"]),
                new GraphNodeInputTestImpl(id: "003", relatedIds: ["002", "003"]),
        ]
    }

    private static List<GraphNodeInputTestImpl> generateSingleElementGraph() {
        [
                new GraphNodeInputTestImpl(id: "START", relatedIds: [],)
        ]
    }

    private static List<GraphNodeInputTestImpl> generateOrderedSimpleGraph() {
        [
                new GraphNodeInputTestImpl(id: "001", relatedIds: ["UNKNOWN"]),
                new GraphNodeInputTestImpl(id: "002", relatedIds: ["001"]),
                new GraphNodeInputTestImpl(id: "003", relatedIds: ["002"]),
                new GraphNodeInputTestImpl(id: "004", relatedIds: ["003"]),
                new GraphNodeInputTestImpl(id: "START", relatedIds: ["004"]),
        ]
    }

    private static List<GraphNodeInputTestImpl> generateUnorderedSimpleGraph() {
        [
                new GraphNodeInputTestImpl(id: "003", relatedIds: ["002"]),
                new GraphNodeInputTestImpl(id: "004", relatedIds: ["003"]),
                new GraphNodeInputTestImpl(id: "001", relatedIds: ["UNKNOWN"]),
                new GraphNodeInputTestImpl(id: "START", relatedIds: ["004"]),
                new GraphNodeInputTestImpl(id: "002", relatedIds: ["001"]),
        ]
    }

    private static List<GraphNodeInputTestImpl> generateComplexGraphWithoutCycles() {
        [
                new GraphNodeInputTestImpl(id: "M01", relatedIds: ["UNKNOWN"]),
                new GraphNodeInputTestImpl(id: "M02", relatedIds: ["M01"]),
                new GraphNodeInputTestImpl(id: "M03", relatedIds: ["M02"]),
                new GraphNodeInputTestImpl(id: "C030", relatedIds: ["M03"]),
                new GraphNodeInputTestImpl(id: "C031", relatedIds: ["M03"]),
                new GraphNodeInputTestImpl(id: "C032", relatedIds: ["C031"]),
                new GraphNodeInputTestImpl(id: "M04", relatedIds: ["M03"]),
                new GraphNodeInputTestImpl(id: "M05", relatedIds: ["M04"]),
                new GraphNodeInputTestImpl(id: "M06", relatedIds: ["M05", "M07"]),
                new GraphNodeInputTestImpl(id: "M07", relatedIds: []),
                new GraphNodeInputTestImpl(id: "C070", relatedIds: ["M07"]),
                new GraphNodeInputTestImpl(id: "M08", relatedIds: ["M05"]),
                new GraphNodeInputTestImpl(id: "M09", relatedIds: ["M08"]),
                new GraphNodeInputTestImpl(id: "C090", relatedIds: ["M09"]),
                new GraphNodeInputTestImpl(id: "M10", relatedIds: ["M06", "M09"]),
                new GraphNodeInputTestImpl(id: "START", relatedIds: ["M10"]),
                new GraphNodeInputTestImpl(id: "C100", relatedIds: ["M10"]),
                new GraphNodeInputTestImpl(id: "C101", relatedIds: ["C100"]),
                new GraphNodeInputTestImpl(id: "C102", relatedIds: ["C100", "C101"]),
                new GraphNodeInputTestImpl(id: "AS00", relatedIds: ["START"]),
                new GraphNodeInputTestImpl(id: "AS01", relatedIds: ["START"]),
                new GraphNodeInputTestImpl(id: "AS02", relatedIds: ["AS01"]),
                new GraphNodeInputTestImpl(id: "OUTER01", relatedIds: ["OUTER-UNKNOWN"]),
                new GraphNodeInputTestImpl(id: "OUTER02", relatedIds: ["OUTER01"]),
                new GraphNodeInputTestImpl(id: "OUTER03", relatedIds: ["OUTER02"]),
        ]
    }

    private static List<GraphNodeInputTestImpl> generateSimpleGraphWithCycles() {
        [
                new GraphNodeInputTestImpl(id: "M01", relatedIds: ["UNKNOWN"]),
                new GraphNodeInputTestImpl(id: "M02", relatedIds: ["M01", "M05"]),
                new GraphNodeInputTestImpl(id: "M03", relatedIds: ["M02"]),
                new GraphNodeInputTestImpl(id: "M04", relatedIds: ["M03"]),
                new GraphNodeInputTestImpl(id: "M05", relatedIds: ["M04"]),
                new GraphNodeInputTestImpl(id: "START", relatedIds: ["M05"]),
        ]
    }

    private static List<GraphNodeInputTestImpl> generateComplexGraphWithCycles() {
        [
                new GraphNodeInputTestImpl(id: "M01", relatedIds: ["UNKNOWN"]),
                new GraphNodeInputTestImpl(id: "M02", relatedIds: ["M01"]),
                new GraphNodeInputTestImpl(id: "M03", relatedIds: ["M02"]),
                new GraphNodeInputTestImpl(id: "C030", relatedIds: ["M03"]),
                new GraphNodeInputTestImpl(id: "C031", relatedIds: ["M03"]),
                new GraphNodeInputTestImpl(id: "C032", relatedIds: ["C031"]),
                new GraphNodeInputTestImpl(id: "M04", relatedIds: ["M03", "M08"]),
                new GraphNodeInputTestImpl(id: "M05", relatedIds: ["M04"]),
                new GraphNodeInputTestImpl(id: "M06", relatedIds: ["M05", "M07"]),
                new GraphNodeInputTestImpl(id: "M07", relatedIds: []),
                new GraphNodeInputTestImpl(id: "C070", relatedIds: ["M07"]),
                new GraphNodeInputTestImpl(id: "M08", relatedIds: ["M09"]),
                new GraphNodeInputTestImpl(id: "M09", relatedIds: ["M10"]),
                new GraphNodeInputTestImpl(id: "C090", relatedIds: ["M09"]),
                new GraphNodeInputTestImpl(id: "M10", relatedIds: ["M06"]),
                new GraphNodeInputTestImpl(id: "START", relatedIds: ["M10"]),
                new GraphNodeInputTestImpl(id: "C100", relatedIds: ["M10", "C102"]),
                new GraphNodeInputTestImpl(id: "C101", relatedIds: ["C100"]),
                new GraphNodeInputTestImpl(id: "C102", relatedIds: ["C101"]),
                new GraphNodeInputTestImpl(id: "AS00", relatedIds: ["START"]),
                new GraphNodeInputTestImpl(id: "AS01", relatedIds: ["START"]),
                new GraphNodeInputTestImpl(id: "AS02", relatedIds: ["AS01"]),
                new GraphNodeInputTestImpl(id: "OUTER00", relatedIds: ["OUTER-UNKNOWN"]),
                new GraphNodeInputTestImpl(id: "OUTER01", relatedIds: ["OUTER00"]),
                new GraphNodeInputTestImpl(id: "OUTER02", relatedIds: ["OUTER01"]),
        ]
    }
}
