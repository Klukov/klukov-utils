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
        sub.parseGraphCollection(graphInput as Collection<ParentGivenNodeInput<String, ParentGivenNodeInputTestImpl>>, startNodeId)

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
        given:
        def graphInput = generateOrderedSimpleGraph()

        when:
        def result = sub.parseGraphCollection(graphInput, graphInput[4].id)

        then:
        result.graphNodes.size() == graphInput.size()
        result.graphNodes['005'].childNodes.isEmpty()
        assertGraphNode(result.graphNodes['005'], graphInput[4], PathType.MAIN, 1, 0)
        assertGraphNode(result.graphNodes['004'], graphInput[3], PathType.MAIN, 1, 1)
        assertGraphNode(result.graphNodes['003'], graphInput[2], PathType.MAIN, 1, 1)
        assertGraphNode(result.graphNodes['002'], graphInput[1], PathType.MAIN, 1, 1)
        assertGraphNode(result.graphNodes['001'], graphInput[0], PathType.MAIN, 0, 1)
    }

    def "should parse simple graph from unordered list"() {
        def graphInput = generateUnorderedSimpleGraph()

        when:
        def startNodeId = graphInput[3].id
        def result = sub.parseGraphCollection(graphInput, startNodeId)

        then:
        result.graphNodes.size() == graphInput.size()
        result.graphNodes['005'].childNodes.isEmpty()
        assertGraphNode(result.graphNodes['005'], graphInput[3], PathType.MAIN, 1, 0)
        assertGraphNode(result.graphNodes['004'], graphInput[1], PathType.MAIN, 1, 1)
        assertGraphNode(result.graphNodes['003'], graphInput[0], PathType.MAIN, 1, 1)
        assertGraphNode(result.graphNodes['002'], graphInput[4], PathType.MAIN, 1, 1)
        assertGraphNode(result.graphNodes['001'], graphInput[2], PathType.MAIN, 0, 1)
    }

    def "should parse complex graph"() {
        given:
        def graphInput = null

        when:
        sub.parseGraphCollection(graphInput, '000')

        then:
        noExceptionThrown()
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

    private void assertGraphNode(
            GraphNode<String, ParentGivenNodeInputTestImpl> result,
            ParentGivenNodeInputTestImpl input,
            PathType expectedPathType,
            int expectedNumberOfParents,
            int expectedNumberOfChildren
    ) {
        assert result.id == input.id
        assert result.object == input.object
        assert result.startNodePathType == expectedPathType
        assert result.parentNodes.size() == expectedNumberOfParents
        assert result.childNodes.size() == expectedNumberOfChildren
        // validation constrain is fact that parentId could be outside graph
        input.parentIds.containsAll(result.parentNodes.collect { it -> it.id })
    }
}
