package org.klukov.utils.graphs.parser

import org.klukov.utils.graphs.GraphProcessingException
import org.klukov.utils.graphs.ProcessingErrorType
import spock.lang.Specification
import spock.lang.Subject

class ParentGivenGraphParserTest extends Specification {

    @Subject
    ParentGivenGraphParser<String, ParentGivenGraphNodeInputTestImplInput> sub = new ParentGivenGraphParser<>()

    def "should throw exception if graph is null or empty"() {
        given:
        def startNodeId = 'START'

        when:
        sub.parseGraphCollection(new ParentGivenGraphParseInput(graphInput, startNodeId))

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
        sub.parseGraphCollection(new ParentGivenGraphParseInput(generateOrderedSimpleGraph(), null))

        then:
        def exception = thrown(GraphProcessingException.class)
        exception.processingErrorType == ProcessingErrorType.NULL_START_ID
    }

    def "should throw exception if graph does not contain start node id"() {
        given:
        def startNodeId = 'notMatchedId'

        when:
        sub.parseGraphCollection(new ParentGivenGraphParseInput(generateOrderedSimpleGraph(), startNodeId))

        then:
        def exception = thrown(GraphProcessingException.class)
        exception.processingErrorType == ProcessingErrorType.STAR_NODE_NOT_IN_GRAPH
    }

    def "should throw exception if any node is null or has id with null value"() {
        given:
        def startNodeId = 'START'

        when:
        sub.parseGraphCollection(new ParentGivenGraphParseInput(graphInput, startNodeId))

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
        def startNodeId = 'START'

        when:
        sub.parseGraphCollection(new ParentGivenGraphParseInput(generateGraphWithDuplicates(), startNodeId))

        then:
        def exception = thrown(GraphProcessingException.class)
        exception.processingErrorType == ProcessingErrorType.DUPLICATED_NODES
    }

    def "should parse single element graph"() {
        given:
        def graphInput = generateSingleElementGraph()

        when:
        def result = sub.parseGraphCollection(
                new ParentGivenGraphParseInput(graphInput, graphInput[0].id))

        then:
        def nodesMap = result.graphNodes
        nodesMap.size() == graphInput.size()
        assertGraphNode(nodesMap['START'], graphInput[0], PathType.MAIN, [], [])
    }

    def "should parse simple graph"() {
        given:
        def graphInput = generateOrderedSimpleGraph()

        when:
        def result = sub.parseGraphCollection(new ParentGivenGraphParseInput(graphInput, graphInput[4].id))

        then:
        def nodesMap = result.graphNodes
        nodesMap.size() == graphInput.size()
        nodesMap['START'].childNodes.isEmpty()
        assertGraphNode(nodesMap['START'], graphInput[4], PathType.MAIN, [nodesMap['004']], [])
        assertGraphNode(nodesMap['004'], graphInput[3], PathType.MAIN, [nodesMap['003']], [nodesMap['START']])
        assertGraphNode(nodesMap['003'], graphInput[2], PathType.MAIN, [nodesMap['002']], [nodesMap['004']])
        assertGraphNode(nodesMap['002'], graphInput[1], PathType.MAIN, [nodesMap['001']], [nodesMap['003']])
        assertGraphNode(nodesMap['001'], graphInput[0], PathType.MAIN, [], [nodesMap['002']])
    }

    def "should parse simple graph from unordered list"() {
        given:
        def graphInput = generateUnorderedSimpleGraph()

        when:
        def startNodeId = graphInput[3].id
        def result = sub.parseGraphCollection(
                new ParentGivenGraphParseInput(graphInput, startNodeId))

        then:
        def nodesMap = result.graphNodes
        nodesMap.size() == graphInput.size()
        nodesMap['START'].childNodes.isEmpty()
        assertGraphNode(nodesMap['START'], graphInput[3], PathType.MAIN, [nodesMap['004']], [])
        assertGraphNode(nodesMap['004'], graphInput[1], PathType.MAIN, [nodesMap['003']], [nodesMap['START']])
        assertGraphNode(nodesMap['003'], graphInput[0], PathType.MAIN, [nodesMap['002']], [nodesMap['004']])
        assertGraphNode(nodesMap['002'], graphInput[4], PathType.MAIN, [nodesMap['001']], [nodesMap['003']])
        assertGraphNode(nodesMap['001'], graphInput[2], PathType.MAIN, [], [nodesMap['002']])
    }

    def "should parse complex graph without cycles"() {
        given:
        def graphInput = generateComplexGraphWithoutCycles()

        when:
        def result = sub.parseGraphCollection(
                new ParentGivenGraphParseInput(graphInput, 'START'))

        then:
        def nodesMap = result.graphNodes
        nodesMap.size() == graphInput.size()
        assertGraphNode(nodesMap['M01'], graphInput[0], PathType.MAIN, [], [nodesMap['M02']])
        assertGraphNode(nodesMap['M02'], graphInput[1], PathType.MAIN, [nodesMap['M01']], [nodesMap['M03']])
        assertGraphNode(nodesMap['M03'], graphInput[2], PathType.MAIN, [nodesMap['M02']], [nodesMap['M04'], nodesMap['C030'], nodesMap['C031']])
        assertGraphNode(nodesMap['C030'], graphInput[3], PathType.CONNECTED, [nodesMap['M03']], [])
        assertGraphNode(nodesMap['C031'], graphInput[4], PathType.CONNECTED, [nodesMap['M03']], [nodesMap['C032']])
        assertGraphNode(nodesMap['C032'], graphInput[5], PathType.CONNECTED, [nodesMap['C031']], [])
        assertGraphNode(nodesMap['M04'], graphInput[6], PathType.MAIN, [nodesMap['M03']], [nodesMap['M05']])
        assertGraphNode(nodesMap['M05'], graphInput[7], PathType.MAIN, [nodesMap['M04']], [nodesMap['M06'], nodesMap['M08']])
        assertGraphNode(nodesMap['M06'], graphInput[8], PathType.MAIN, [nodesMap['M05'], nodesMap['M07']], [nodesMap['M10']])
        assertGraphNode(nodesMap['M07'], graphInput[9], PathType.MAIN, [], [nodesMap['M06'], nodesMap['C070']])
        assertGraphNode(nodesMap['C070'], graphInput[10], PathType.CONNECTED, [nodesMap['M07']], [])
        assertGraphNode(nodesMap['M08'], graphInput[11], PathType.MAIN, [nodesMap['M05']], [nodesMap['M09']])
        assertGraphNode(nodesMap['M09'], graphInput[12], PathType.MAIN, [nodesMap['M08']], [nodesMap['C090'], nodesMap['M10']])
        assertGraphNode(nodesMap['C090'], graphInput[13], PathType.CONNECTED, [nodesMap['M09']], [])
        assertGraphNode(nodesMap['M10'], graphInput[14], PathType.MAIN, [nodesMap['M06'], nodesMap['M09']], [nodesMap['START'], nodesMap['C100']])
        assertGraphNode(nodesMap['START'], graphInput[15], PathType.MAIN, [nodesMap['M10']], [nodesMap['AS00'], nodesMap['AS01']])
        assertGraphNode(nodesMap['C100'], graphInput[16], PathType.CONNECTED, [nodesMap['M10']], [nodesMap['C101'], nodesMap['C102']])
        assertGraphNode(nodesMap['C101'], graphInput[17], PathType.CONNECTED, [nodesMap['C100']], [nodesMap['C102']])
        assertGraphNode(nodesMap['C102'], graphInput[18], PathType.CONNECTED, [nodesMap['C100'], nodesMap['C101']], [])
        assertGraphNode(nodesMap['AS00'], graphInput[19], PathType.CONNECTED, [nodesMap['START']], [])
        assertGraphNode(nodesMap['AS01'], graphInput[20], PathType.CONNECTED, [nodesMap['START']], [nodesMap['AS02']])
        assertGraphNode(nodesMap['AS02'], graphInput[21], PathType.CONNECTED, [nodesMap['AS01']], [])
        assertGraphNode(nodesMap['OUTER00'], graphInput[22], PathType.OUTER, [], [nodesMap['OUTER01']])
        assertGraphNode(nodesMap['OUTER01'], graphInput[23], PathType.OUTER, [nodesMap['OUTER00']], [nodesMap['OUTER02']])
        assertGraphNode(nodesMap['OUTER02'], graphInput[24], PathType.OUTER, [nodesMap['OUTER01']], [])
    }

    def "should parse complex graph with cycles"() {
        given:
        def graphInput = generateComplexGraphWithCycles()

        when:
        sub.parseGraphCollection(new ParentGivenGraphParseInput(graphInput, 'START'))

        then:
        noExceptionThrown()
    }

    private static void assertGraphNode(
            GraphNode<String, ParentGivenGraphNodeInputTestImplInput> result,
            ParentGivenGraphNodeInputTestImplInput input,
            PathType expectedPathType,
            List<GraphNode<String, ParentGivenGraphNodeInputTestImplInput>> expectedParents,
            List<GraphNode<String, ParentGivenGraphNodeInputTestImplInput>> expectedChildren
    ) {
        assert result.id == input.id
        assert result.object == input.object
        assert result.startNodePathType == expectedPathType
        assert result.parentNodes.toSet() == expectedParents.toSet()
        assert result.childNodes.toSet() == expectedChildren.toSet()
        // validation constrain is fact that parentId could be outside graph
        assert input.parentIds.containsAll(result.parentNodes.collect { it -> it.id })
    }

    List<ParentGivenGraphNodeInputTestImplInput> generateGraphWithNullNodes() {
        [
                new ParentGivenGraphNodeInputTestImplInput(id: "001", parentIds: ["UNKNOWN"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "002", parentIds: ["001"]),
                null,
                new ParentGivenGraphNodeInputTestImplInput(id: "003", parentIds: ["002"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "004", parentIds: ["003"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "START", parentIds: ["004"]),
        ]
    }

    List<ParentGivenGraphNodeInputTestImplInput> generateGraphWithNullNodeIds() {
        [
                new ParentGivenGraphNodeInputTestImplInput(id: "001", parentIds: ["UNKNOWN"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "002", parentIds: ["001"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "003", parentIds: ["002"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "004", parentIds: ["003"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "START", parentIds: ["004"]),
                new ParentGivenGraphNodeInputTestImplInput(id: null, parentIds: ["004"]),
        ]
    }

    List<ParentGivenGraphNodeInputTestImplInput> generateGraphWithDuplicates() {
        [
                new ParentGivenGraphNodeInputTestImplInput(id: "001", parentIds: ["UNKNOWN"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "002", parentIds: ["001"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "003", parentIds: ["002"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "004", parentIds: ["003"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "START", parentIds: ["004"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "003", parentIds: ["002", "003"]),
        ]
    }

    List<ParentGivenGraphNodeInputTestImplInput> generateSingleElementGraph() {
        [
                new ParentGivenGraphNodeInputTestImplInput(id: "START", parentIds: [],)
        ]
    }

    List<ParentGivenGraphNodeInputTestImplInput> generateOrderedSimpleGraph() {
        [
                new ParentGivenGraphNodeInputTestImplInput(id: "001", parentIds: ["UNKNOWN"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "002", parentIds: ["001"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "003", parentIds: ["002"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "004", parentIds: ["003"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "START", parentIds: ["004"]),
        ]
    }

    List<ParentGivenGraphNodeInputTestImplInput> generateUnorderedSimpleGraph() {
        [
                new ParentGivenGraphNodeInputTestImplInput(id: "003", parentIds: ["002"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "004", parentIds: ["003"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "001", parentIds: ["UNKNOWN"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "START", parentIds: ["004"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "002", parentIds: ["001"]),
        ]
    }

    List<ParentGivenGraphNodeInputTestImplInput> generateComplexGraphWithoutCycles() {
        [
                new ParentGivenGraphNodeInputTestImplInput(id: "M01", parentIds: ["UNKNOWN"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "M02", parentIds: ["M01"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "M03", parentIds: ["M02"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "C030", parentIds: ["M03"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "C031", parentIds: ["M03"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "C032", parentIds: ["C031"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "M04", parentIds: ["M03"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "M05", parentIds: ["M04"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "M06", parentIds: ["M05", "M07"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "M07", parentIds: []),
                new ParentGivenGraphNodeInputTestImplInput(id: "C070", parentIds: ["M07"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "M08", parentIds: ["M05"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "M09", parentIds: ["M08"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "C090", parentIds: ["M09"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "M10", parentIds: ["M06", "M09"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "START", parentIds: ["M10"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "C100", parentIds: ["M10"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "C101", parentIds: ["C100"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "C102", parentIds: ["C100", "C101"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "AS00", parentIds: ["START"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "AS01", parentIds: ["START"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "AS02", parentIds: ["AS01"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "OUTER00", parentIds: ["OUTER-UNKNOWN"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "OUTER01", parentIds: ["OUTER00"]),
                new ParentGivenGraphNodeInputTestImplInput(id: "OUTER02", parentIds: ["OUTER01"]),
        ]
    }

    List<ParentGivenGraphNodeInputTestImplInput> generateComplexGraphWithCycles() {
        throw new RuntimeException("NOT IMPLEMENTED")
    }
}
