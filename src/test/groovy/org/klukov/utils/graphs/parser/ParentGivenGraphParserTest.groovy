package org.klukov.utils.graphs.parser

import org.klukov.utils.graphs.GraphProcessingException
import org.klukov.utils.graphs.ProcessingErrorType
import spock.lang.Specification
import spock.lang.Subject

class ParentGivenGraphParserTest extends Specification {

    @Subject
    ParentGivenGraphParser<String, ParentGivenNodeInputTestImpl> sub = new ParentGivenGraphParser<>()

    def "should throw exception if graph is null or empty"() {
        given:
        def startNodeId = '001'

        when:
        sub.parseGraphCollection(graphInput, startNodeId)

        then:
        def exception = thrown(GraphProcessingException.class)
        exception.processingErrorType == ProcessingErrorType.NULL_OR_EMPTY_GRAPH

        where:
        graphInput              || _
        null                    || _
        Collections.emptyList() || _
    }

    def "should throw exception if start element is null"() {
        when:
        sub.parseGraphCollection(generateOrderedSimpleGraph(), null)

        then:
        def exception = thrown(GraphProcessingException.class)
        exception.processingErrorType == ProcessingErrorType.NULL_START_ID
    }

    def "should throw exception if graph does not contain start node id"() {
        given:
        def startNodeId = 'notMatchedId'

        when:
        sub.parseGraphCollection(generateOrderedSimpleGraph(), startNodeId)

        then:
        def exception = thrown(GraphProcessingException.class)
        exception.processingErrorType == ProcessingErrorType.STAR_NODE_NOT_IN_GRAPH
    }

    def "should throw exception if any node is null or has id with null value"() {
        given:
        def startNodeId = '001'

        when:
        sub.parseGraphCollection(graphInput, startNodeId)

        then:
        def exception = thrown(GraphProcessingException.class)
        exception.processingErrorType == ProcessingErrorType.NULL_NODES

        where:
        graphInput                     || _
        generateGraphWithNullNodes()   || _
        generateGraphWithNullNodeIds() || _
    }

    def "should throw exception if graph contains ids duplicates"() {
        given:
        def startNodeId = '001'

        when:
        sub.parseGraphCollection(generateGraphWithDuplicates(), startNodeId)

        then:
        def exception = thrown(GraphProcessingException.class)
        exception.processingErrorType == ProcessingErrorType.DUPLICATED_NODES
    }

    def "should parse single element graph"() {
        given:
        def graphInput = generateSingleElementGraph()

        when:
        def result = sub.parseGraphCollection(graphInput, graphInput[0].id)

        then:
        result.graphNodes.size() == graphInput.size()
        result.graphNodes['001'].id == '001'
        result.graphNodes['001'].getObject() == graphInput[0]
        result.graphNodes['001'].startNodePathType == PathType.MAIN
        result.graphNodes['001'].parentNodes.isEmpty()
        result.graphNodes['001'].childNodes.isEmpty()
    }

    def "should parse simple graph"() {
        def graphInput = generateOrderedSimpleGraph()

        when:
        def startNodeId = graphInput[0].id
        def result = sub.parseGraphCollection(graphInput, graphInput[0].id)

        then:
        result.graphNodes.size() == graphInput.size()
        result.graphNodes['001'].id == '001'
        result.graphNodes['001'].getObject() == graphInput[0]
        result.graphNodes['001'].startNodePathType == PathType.MAIN
        result.graphNodes['001'].parentNodes.isEmpty()
        result.graphNodes['001'].childNodes.isEmpty()
    }

    def "should parse simple graph from unordered list"() {
        def graphInput = generateOrderedSimpleGraph()

        when:
        def startNodeId = graphInput[2].id
        def result = sub.parseGraphCollection(graphInput, startNodeId)

        then:
        result.graphNodes.size() == graphInput.size()
        result.graphNodes['001'].id == graphInput[0].id
        result.graphNodes['001'].getObject() == graphInput[0]
        result.graphNodes['001'].startNodePathType == PathType.MAIN
        result.graphNodes['001'].parentNodes.isEmpty()
        result.graphNodes['001'].childNodes.isEmpty()
    }

    def "should parse complex graph"() {

    }

    private void assertGraphNode(GraphNode<String, ParentGivenNodeInputTestImpl> graphNode) {

    }

    List<ParentGivenNodeInputTestImpl> generateGraphWithNullNodes() {
        [
                new ParentGivenNodeInputTestImpl(
                        id: "001",
                        parentIds: ["UNKNOWN"],
                ),
                new ParentGivenNodeInputTestImpl(
                        id: "002",
                        parentIds: ["001"],
                ),
                null,
                new ParentGivenNodeInputTestImpl(
                        id: "003",
                        parentIds: ["002"],
                ),
                new ParentGivenNodeInputTestImpl(
                        id: "004",
                        parentIds: ["003"],
                ),
                new ParentGivenNodeInputTestImpl(
                        id: "005",
                        parentIds: ["004"],
                ),
        ]
    }

    List<ParentGivenNodeInputTestImpl> generateGraphWithNullNodeIds() {
        [
                new ParentGivenNodeInputTestImpl(
                        id: "001",
                        parentIds: ["UNKNOWN"],
                ),
                new ParentGivenNodeInputTestImpl(
                        id: "002",
                        parentIds: ["001"],
                ),
                new ParentGivenNodeInputTestImpl(
                        id: "003",
                        parentIds: ["002"],
                ),
                new ParentGivenNodeInputTestImpl(
                        id: "004",
                        parentIds: ["003"],
                ),
                new ParentGivenNodeInputTestImpl(
                        id: "005",
                        parentIds: ["004"],
                ),
                new ParentGivenNodeInputTestImpl(
                        id: null,
                        parentIds: ["005"],
                ),
        ]
    }

    List<ParentGivenNodeInputTestImpl> generateGraphWithDuplicates() {
        [
                new ParentGivenNodeInputTestImpl(
                        id: "001",
                        parentIds: ["UNKNOWN"],
                ),
                new ParentGivenNodeInputTestImpl(
                        id: "002",
                        parentIds: ["001"],
                ),
                new ParentGivenNodeInputTestImpl(
                        id: "003",
                        parentIds: ["002"],
                ),
                new ParentGivenNodeInputTestImpl(
                        id: "004",
                        parentIds: ["003"],
                ),
                new ParentGivenNodeInputTestImpl(
                        id: "005",
                        parentIds: ["004"],
                ),
                new ParentGivenNodeInputTestImpl(
                        id: "003",
                        parentIds: ["002", "003"],
                ),
        ]
    }

    List<ParentGivenNodeInputTestImpl> generateSingleElementGraph() {
        [
                new ParentGivenNodeInputTestImpl(
                        id: "001",
                        parentIds: [],
                )
        ]
    }

    List<ParentGivenNodeInputTestImpl> generateOrderedSimpleGraph() {
        [
                new ParentGivenNodeInputTestImpl(
                        id: "001",
                        parentIds: ["UNKNOWN"],
                ),
                new ParentGivenNodeInputTestImpl(
                        id: "002",
                        parentIds: ["001"],
                ),
                new ParentGivenNodeInputTestImpl(
                        id: "003",
                        parentIds: ["002"],
                ),
                new ParentGivenNodeInputTestImpl(
                        id: "004",
                        parentIds: ["003"],
                ),
                new ParentGivenNodeInputTestImpl(
                        id: "005",
                        parentIds: ["004"],
                ),
        ]
    }

    List<ParentGivenNodeInputTestImpl> generateUnorderedSimpleGraph() {
        [
                new ParentGivenNodeInputTestImpl(
                        id: "003",
                        parentIds: ["002"],
                ),
                new ParentGivenNodeInputTestImpl(
                        id: "004",
                        parentIds: ["003"],
                ),
                new ParentGivenNodeInputTestImpl(
                        id: "001",
                        parentIds: ["UNKNOWN"],
                ),
                new ParentGivenNodeInputTestImpl(
                        id: "005",
                        parentIds: ["004"],
                ),
                new ParentGivenNodeInputTestImpl(
                        id: "002",
                        parentIds: ["001"],
                ),
        ]
    }

}
