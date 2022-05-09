package org.klukov.utils.graphs

import org.klukov.utils.graphs.parser.GraphNode
import org.klukov.utils.graphs.parser.ParentGivenGraphParser
import org.klukov.utils.graphs.parser.PathType
import spock.lang.Specification
import spock.lang.Subject

class ParentGivenGraphParserTest extends Specification {

    @Subject
    ParentGivenGraphParser<String, ParentGivenNodeInputTestImpl> sub = new ParentGivenGraphParser<>()

    def "should throw exception if graph is null or empty"() {

    }

    def "should throw exception if start element is null"() {

    }

    def "should throw exception if graph does not contain start node id"() {

    }

    def "should throw exception if any node is null or has id with null value"() {

    }

    def "should throw exception if graph contains ids duplicates"() {

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
        throw new RuntimeException("Not Implemented")
    }

    private void assertGraphNode(GraphNode<String, ParentGivenNodeInputTestImpl> graphNode) {

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
