package org.klukov.utils.graphs.parser

import org.klukov.utils.graphs.GraphUtils
import org.klukov.utils.graphs.common.GraphProcessingException
import org.klukov.utils.graphs.common.ProcessingErrorType
import spock.lang.Specification

class ParentGivenGraphParserTest extends Specification {

    def "should throw exception if graph is null or empty"() {
        given:
        def startNodeId = 'START'

        when:
        GraphUtils.parseGraphCollection(new ParentGivenGraphParseInput(graphInput, startNodeId))

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
        GraphUtils.parseGraphCollection(new ParentGivenGraphParseInput(generateOrderedSimpleGraph(), null))

        then:
        def exception = thrown(GraphProcessingException.class)
        exception.processingErrorType == ProcessingErrorType.NULL_START_ID
    }

    def "should throw exception if graph does not contain start node id"() {
        given:
        def startNodeId = 'notMatchedId'

        when:
        GraphUtils.parseGraphCollection(new ParentGivenGraphParseInput(generateOrderedSimpleGraph(), startNodeId))

        then:
        def exception = thrown(GraphProcessingException.class)
        exception.processingErrorType == ProcessingErrorType.STAR_NODE_NOT_IN_GRAPH
    }

    def "should throw exception if any node is null or has id with null value"() {
        given:
        def startNodeId = 'START'

        when:
        GraphUtils.parseGraphCollection(new ParentGivenGraphParseInput(graphInput, startNodeId))

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
        GraphUtils.parseGraphCollection(new ParentGivenGraphParseInput(generateGraphWithDuplicates(), startNodeId))

        then:
        def exception = thrown(GraphProcessingException.class)
        exception.processingErrorType == ProcessingErrorType.DUPLICATED_NODES
    }

    def "should parse single element graph"() {
        given:
        def graphInput = generateSingleElementGraph()
        def startNodeId = graphInput[0].id

        when:
        ParentGivenGraphParserResult<String, ParentGivenGraphNodeInputTestImpl> result =
                GraphUtils.parseGraphCollection(new ParentGivenGraphParseInput(graphInput, startNodeId))

        then:
        def nodesMap = result.graphNodes
        nodesMap.size() == graphInput.size()
        assertGraphNode(nodesMap['START'], graphInput[0], PathType.MAIN, [], [])
    }

    def "should parse simple graph"() {
        given:
        def graphInput = generateOrderedSimpleGraph()
        def startNodeId = graphInput[4].id

        when:
        ParentGivenGraphParserResult<String, ParentGivenGraphNodeInputTestImpl> result =
                GraphUtils.parseGraphCollection(new ParentGivenGraphParseInput(graphInput, startNodeId))

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
        def startNodeId = graphInput[3].id

        when:
        ParentGivenGraphParserResult<String, ParentGivenGraphNodeInputTestImpl> result =
                GraphUtils.parseGraphCollection(new ParentGivenGraphParseInput(graphInput, startNodeId))

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
        ParentGivenGraphParserResult<String, ParentGivenGraphNodeInputTestImpl> result =
                GraphUtils.parseGraphCollection(new ParentGivenGraphParseInput(graphInput, 'START'))

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

    def "should parse simple graph with cycles"() {
        given:
        def graphInput = generateSimpleGraphWithCycles()

        when:
        ParentGivenGraphParserResult<String, ParentGivenGraphNodeInputTestImpl> result =
                GraphUtils.parseGraphCollection(new ParentGivenGraphParseInput(graphInput, 'START'))

        then:
        def nodesMap = result.graphNodes
        nodesMap.size() == graphInput.size()
        assertGraphNode(nodesMap['M01'], graphInput[0], PathType.MAIN, [], [nodesMap['M02']])
        assertGraphNode(nodesMap['M02'], graphInput[1], PathType.MAIN, [nodesMap['M01'], nodesMap['M05']], [nodesMap['M03']])
        assertGraphNode(nodesMap['M03'], graphInput[2], PathType.MAIN, [nodesMap['M02']], [nodesMap['M04']])
        assertGraphNode(nodesMap['M04'], graphInput[3], PathType.MAIN, [nodesMap['M03']], [nodesMap['M05']])
        assertGraphNode(nodesMap['M05'], graphInput[4], PathType.MAIN, [nodesMap['M04']], [nodesMap['M02'], nodesMap['START']])
        assertGraphNode(nodesMap['START'], graphInput[5], PathType.MAIN, [nodesMap['M05']], [])
    }

    def "should parse complex graph with cycles"() {
        given:
        def graphInput = generateComplexGraphWithCycles()

        when:
        ParentGivenGraphParserResult<String, ParentGivenGraphNodeInputTestImpl> result =
                GraphUtils.parseGraphCollection(new ParentGivenGraphParseInput(graphInput, 'START'))

        then:
        def nodesMap = result.graphNodes
        nodesMap.size() == graphInput.size()
        assertGraphNode(nodesMap['M01'], graphInput[0], PathType.MAIN, [], [nodesMap['M02']])
        assertGraphNode(nodesMap['M02'], graphInput[1], PathType.MAIN, [nodesMap['M01']], [nodesMap['M03']])
        assertGraphNode(nodesMap['M03'], graphInput[2], PathType.MAIN, [nodesMap['M02']], [nodesMap['M04'], nodesMap['C030'], nodesMap['C031']])
        assertGraphNode(nodesMap['C030'], graphInput[3], PathType.CONNECTED, [nodesMap['M03']], [])
        assertGraphNode(nodesMap['C031'], graphInput[4], PathType.CONNECTED, [nodesMap['M03']], [nodesMap['C032']])
        assertGraphNode(nodesMap['C032'], graphInput[5], PathType.CONNECTED, [nodesMap['C031']], [])
        assertGraphNode(nodesMap['M04'], graphInput[6], PathType.MAIN, [nodesMap['M03'], nodesMap['M08']], [nodesMap['M05']])
        assertGraphNode(nodesMap['M05'], graphInput[7], PathType.MAIN, [nodesMap['M04']], [nodesMap['M06']])
        assertGraphNode(nodesMap['M06'], graphInput[8], PathType.MAIN, [nodesMap['M05'], nodesMap['M07']], [nodesMap['M10']])
        assertGraphNode(nodesMap['M07'], graphInput[9], PathType.MAIN, [], [nodesMap['M06'], nodesMap['C070']])
        assertGraphNode(nodesMap['C070'], graphInput[10], PathType.CONNECTED, [nodesMap['M07']], [])
        assertGraphNode(nodesMap['M08'], graphInput[11], PathType.MAIN, [nodesMap['M09']], [nodesMap['M04']])
        assertGraphNode(nodesMap['M09'], graphInput[12], PathType.MAIN, [nodesMap['M10']], [nodesMap['C090'], nodesMap['M08']])
        assertGraphNode(nodesMap['C090'], graphInput[13], PathType.CONNECTED, [nodesMap['M09']], [])
        assertGraphNode(nodesMap['M10'], graphInput[14], PathType.MAIN, [nodesMap['M06']], [nodesMap['START'], nodesMap['C100'], nodesMap['M09']])
        assertGraphNode(nodesMap['START'], graphInput[15], PathType.MAIN, [nodesMap['M10']], [nodesMap['AS00'], nodesMap['AS01']])
        assertGraphNode(nodesMap['C100'], graphInput[16], PathType.CONNECTED, [nodesMap['M10'], nodesMap['C102']], [nodesMap['C101']])
        assertGraphNode(nodesMap['C101'], graphInput[17], PathType.CONNECTED, [nodesMap['C100']], [nodesMap['C102']])
        assertGraphNode(nodesMap['C102'], graphInput[18], PathType.CONNECTED, [nodesMap['C101']], [nodesMap['C100']])
        assertGraphNode(nodesMap['AS00'], graphInput[19], PathType.CONNECTED, [nodesMap['START']], [])
        assertGraphNode(nodesMap['AS01'], graphInput[20], PathType.CONNECTED, [nodesMap['START']], [nodesMap['AS02']])
        assertGraphNode(nodesMap['AS02'], graphInput[21], PathType.CONNECTED, [nodesMap['AS01']], [])
        assertGraphNode(nodesMap['OUTER00'], graphInput[22], PathType.OUTER, [], [nodesMap['OUTER01']])
        assertGraphNode(nodesMap['OUTER01'], graphInput[23], PathType.OUTER, [nodesMap['OUTER00']], [nodesMap['OUTER02']])
        assertGraphNode(nodesMap['OUTER02'], graphInput[24], PathType.OUTER, [nodesMap['OUTER01']], [])
    }

    private static void assertGraphNode(
            ParentGivenGraphNodeResult<String, ParentGivenGraphNodeInputTestImpl> result,
            ParentGivenGraphNodeInputTestImpl input,
            PathType expectedPathType,
            List<ParentGivenGraphNodeResult<String, ParentGivenGraphNodeInputTestImpl>> expectedParents,
            List<ParentGivenGraphNodeResult<String, ParentGivenGraphNodeInputTestImpl>> expectedChildren
    ) {
        assert result.id == input.id
        assert result.object == input.object
        assert result.startNodePathType == expectedPathType
        assert result.parentNodes.toSet() == expectedParents.toSet()
        assert result.childNodes.toSet() == expectedChildren.toSet()
        // validation constrain is fact that parentId could be outside graph
        assert input.parentIds.containsAll(result.parentNodes.collect { it -> it.id })
    }

    private static List<ParentGivenGraphNodeInputTestImpl> generateGraphWithNullNodes() {
        [
                new ParentGivenGraphNodeInputTestImpl(id: "001", parentIds: ["UNKNOWN"]),
                new ParentGivenGraphNodeInputTestImpl(id: "002", parentIds: ["001"]),
                null,
                new ParentGivenGraphNodeInputTestImpl(id: "003", parentIds: ["002"]),
                new ParentGivenGraphNodeInputTestImpl(id: "004", parentIds: ["003"]),
                new ParentGivenGraphNodeInputTestImpl(id: "START", parentIds: ["004"]),
        ]
    }

    private static List<ParentGivenGraphNodeInputTestImpl> generateGraphWithNullNodeIds() {
        [
                new ParentGivenGraphNodeInputTestImpl(id: "001", parentIds: ["UNKNOWN"]),
                new ParentGivenGraphNodeInputTestImpl(id: "002", parentIds: ["001"]),
                new ParentGivenGraphNodeInputTestImpl(id: "003", parentIds: ["002"]),
                new ParentGivenGraphNodeInputTestImpl(id: "004", parentIds: ["003"]),
                new ParentGivenGraphNodeInputTestImpl(id: "START", parentIds: ["004"]),
                new ParentGivenGraphNodeInputTestImpl(id: null, parentIds: ["004"]),
        ]
    }

    private static List<ParentGivenGraphNodeInputTestImpl> generateGraphWithDuplicates() {
        [
                new ParentGivenGraphNodeInputTestImpl(id: "001", parentIds: ["UNKNOWN"]),
                new ParentGivenGraphNodeInputTestImpl(id: "002", parentIds: ["001"]),
                new ParentGivenGraphNodeInputTestImpl(id: "003", parentIds: ["002"]),
                new ParentGivenGraphNodeInputTestImpl(id: "004", parentIds: ["003"]),
                new ParentGivenGraphNodeInputTestImpl(id: "START", parentIds: ["004"]),
                new ParentGivenGraphNodeInputTestImpl(id: "003", parentIds: ["002", "003"]),
        ]
    }

    private static List<ParentGivenGraphNodeInputTestImpl> generateSingleElementGraph() {
        [
                new ParentGivenGraphNodeInputTestImpl(id: "START", parentIds: [],)
        ]
    }

    private static List<ParentGivenGraphNodeInputTestImpl> generateOrderedSimpleGraph() {
        [
                new ParentGivenGraphNodeInputTestImpl(id: "001", parentIds: ["UNKNOWN"]),
                new ParentGivenGraphNodeInputTestImpl(id: "002", parentIds: ["001"]),
                new ParentGivenGraphNodeInputTestImpl(id: "003", parentIds: ["002"]),
                new ParentGivenGraphNodeInputTestImpl(id: "004", parentIds: ["003"]),
                new ParentGivenGraphNodeInputTestImpl(id: "START", parentIds: ["004"]),
        ]
    }

    private static List<ParentGivenGraphNodeInputTestImpl> generateUnorderedSimpleGraph() {
        [
                new ParentGivenGraphNodeInputTestImpl(id: "003", parentIds: ["002"]),
                new ParentGivenGraphNodeInputTestImpl(id: "004", parentIds: ["003"]),
                new ParentGivenGraphNodeInputTestImpl(id: "001", parentIds: ["UNKNOWN"]),
                new ParentGivenGraphNodeInputTestImpl(id: "START", parentIds: ["004"]),
                new ParentGivenGraphNodeInputTestImpl(id: "002", parentIds: ["001"]),
        ]
    }

    private static List<ParentGivenGraphNodeInputTestImpl> generateComplexGraphWithoutCycles() {
        [
                new ParentGivenGraphNodeInputTestImpl(id: "M01", parentIds: ["UNKNOWN"]),
                new ParentGivenGraphNodeInputTestImpl(id: "M02", parentIds: ["M01"]),
                new ParentGivenGraphNodeInputTestImpl(id: "M03", parentIds: ["M02"]),
                new ParentGivenGraphNodeInputTestImpl(id: "C030", parentIds: ["M03"]),
                new ParentGivenGraphNodeInputTestImpl(id: "C031", parentIds: ["M03"]),
                new ParentGivenGraphNodeInputTestImpl(id: "C032", parentIds: ["C031"]),
                new ParentGivenGraphNodeInputTestImpl(id: "M04", parentIds: ["M03"]),
                new ParentGivenGraphNodeInputTestImpl(id: "M05", parentIds: ["M04"]),
                new ParentGivenGraphNodeInputTestImpl(id: "M06", parentIds: ["M05", "M07"]),
                new ParentGivenGraphNodeInputTestImpl(id: "M07", parentIds: []),
                new ParentGivenGraphNodeInputTestImpl(id: "C070", parentIds: ["M07"]),
                new ParentGivenGraphNodeInputTestImpl(id: "M08", parentIds: ["M05"]),
                new ParentGivenGraphNodeInputTestImpl(id: "M09", parentIds: ["M08"]),
                new ParentGivenGraphNodeInputTestImpl(id: "C090", parentIds: ["M09"]),
                new ParentGivenGraphNodeInputTestImpl(id: "M10", parentIds: ["M06", "M09"]),
                new ParentGivenGraphNodeInputTestImpl(id: "START", parentIds: ["M10"]),
                new ParentGivenGraphNodeInputTestImpl(id: "C100", parentIds: ["M10"]),
                new ParentGivenGraphNodeInputTestImpl(id: "C101", parentIds: ["C100"]),
                new ParentGivenGraphNodeInputTestImpl(id: "C102", parentIds: ["C100", "C101"]),
                new ParentGivenGraphNodeInputTestImpl(id: "AS00", parentIds: ["START"]),
                new ParentGivenGraphNodeInputTestImpl(id: "AS01", parentIds: ["START"]),
                new ParentGivenGraphNodeInputTestImpl(id: "AS02", parentIds: ["AS01"]),
                new ParentGivenGraphNodeInputTestImpl(id: "OUTER00", parentIds: ["OUTER-UNKNOWN"]),
                new ParentGivenGraphNodeInputTestImpl(id: "OUTER01", parentIds: ["OUTER00"]),
                new ParentGivenGraphNodeInputTestImpl(id: "OUTER02", parentIds: ["OUTER01"]),
        ]
    }

    private static List<ParentGivenGraphNodeInputTestImpl> generateSimpleGraphWithCycles() {
        [
                new ParentGivenGraphNodeInputTestImpl(id: "M01", parentIds: ["UNKNOWN"]),
                new ParentGivenGraphNodeInputTestImpl(id: "M02", parentIds: ["M01", "M05"]),
                new ParentGivenGraphNodeInputTestImpl(id: "M03", parentIds: ["M02"]),
                new ParentGivenGraphNodeInputTestImpl(id: "M04", parentIds: ["M03"]),
                new ParentGivenGraphNodeInputTestImpl(id: "M05", parentIds: ["M04"]),
                new ParentGivenGraphNodeInputTestImpl(id: "START", parentIds: ["M05"]),
        ]
    }

    private static List<ParentGivenGraphNodeInputTestImpl> generateComplexGraphWithCycles() {
        [
                new ParentGivenGraphNodeInputTestImpl(id: "M01", parentIds: ["UNKNOWN"]),
                new ParentGivenGraphNodeInputTestImpl(id: "M02", parentIds: ["M01"]),
                new ParentGivenGraphNodeInputTestImpl(id: "M03", parentIds: ["M02"]),
                new ParentGivenGraphNodeInputTestImpl(id: "C030", parentIds: ["M03"]),
                new ParentGivenGraphNodeInputTestImpl(id: "C031", parentIds: ["M03"]),
                new ParentGivenGraphNodeInputTestImpl(id: "C032", parentIds: ["C031"]),
                new ParentGivenGraphNodeInputTestImpl(id: "M04", parentIds: ["M03", "M08"]),
                new ParentGivenGraphNodeInputTestImpl(id: "M05", parentIds: ["M04"]),
                new ParentGivenGraphNodeInputTestImpl(id: "M06", parentIds: ["M05", "M07"]),
                new ParentGivenGraphNodeInputTestImpl(id: "M07", parentIds: []),
                new ParentGivenGraphNodeInputTestImpl(id: "C070", parentIds: ["M07"]),
                new ParentGivenGraphNodeInputTestImpl(id: "M08", parentIds: ["M09"]),
                new ParentGivenGraphNodeInputTestImpl(id: "M09", parentIds: ["M10"]),
                new ParentGivenGraphNodeInputTestImpl(id: "C090", parentIds: ["M09"]),
                new ParentGivenGraphNodeInputTestImpl(id: "M10", parentIds: ["M06"]),
                new ParentGivenGraphNodeInputTestImpl(id: "START", parentIds: ["M10"]),
                new ParentGivenGraphNodeInputTestImpl(id: "C100", parentIds: ["M10", "C102"]),
                new ParentGivenGraphNodeInputTestImpl(id: "C101", parentIds: ["C100"]),
                new ParentGivenGraphNodeInputTestImpl(id: "C102", parentIds: ["C101"]),
                new ParentGivenGraphNodeInputTestImpl(id: "AS00", parentIds: ["START"]),
                new ParentGivenGraphNodeInputTestImpl(id: "AS01", parentIds: ["START"]),
                new ParentGivenGraphNodeInputTestImpl(id: "AS02", parentIds: ["AS01"]),
                new ParentGivenGraphNodeInputTestImpl(id: "OUTER00", parentIds: ["OUTER-UNKNOWN"]),
                new ParentGivenGraphNodeInputTestImpl(id: "OUTER01", parentIds: ["OUTER00"]),
                new ParentGivenGraphNodeInputTestImpl(id: "OUTER02", parentIds: ["OUTER01"]),
        ]
    }
}
