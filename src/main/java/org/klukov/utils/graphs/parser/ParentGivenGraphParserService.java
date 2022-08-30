package org.klukov.utils.graphs.parser;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.klukov.utils.graphs.common.GraphEdge;
import org.klukov.utils.graphs.common.GraphProcessingException;
import org.klukov.utils.graphs.relation.BidirectionalRelationIdsQuery;
import org.klukov.utils.graphs.relation.DirectionalRelationIdsQuery;
import org.klukov.utils.graphs.validation.GraphValidator;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
class ParentGivenGraphParserService<ID, T extends ParentGivenGraphNodeInput<ID, T>>
        implements ParentGivenGraphParseUseCase<ID, T> {

    private final DirectionalRelationIdsQuery<ID, T> directionalRelationIdsFinder;
    private final BidirectionalRelationIdsQuery<ID> bidirectionalRelationIdsFinder;
    private final GraphValidator<ID, T> graphValidator;

    /**
     * Parent ids could reference to not existing nodes
     *
     * @throws GraphProcessingException
     */
    @Override
    public ParentGivenGraphParserResult<ID, T> parseGraphCollection(
            ParentGivenGraphParseInput<ID, T> parentGivenGraphParseInput
    ) throws GraphProcessingException {
        log.info("Starting validation of input: {}", parentGivenGraphParseInput);
        validate(parentGivenGraphParseInput);
        log.info("Validation finished. Starting generating edges");
        var edges = generateEdges(parentGivenGraphParseInput.getGraphInput());
        log.info("Generated edges: {}", edges);
        var nodesMap = generateNodesMap(parentGivenGraphParseInput, edges);
        log.info("Generated nodes: {}", nodesMap);
        edges.forEach(graphParserEdge -> connectNodes(graphParserEdge, nodesMap));
        log.info("All edges are connected");
        return ParentGivenGraphParserResult.<ID, T>builder()
                .graphNodes(nodesMap)
                .build();
    }

    private void validate(
            ParentGivenGraphParseInput<ID, T> parentGivenGraphParseInput
    ) throws GraphProcessingException {
        graphValidator.validate(parentGivenGraphParseInput);
    }

    private void connectNodes(GraphEdge<ID> graphEdge, Map<ID, ParentGivenGraphNodeResult<ID, T>> nodesMap) {
        var parent = nodesMap.get(graphEdge.getParentId());
        var child = nodesMap.get(graphEdge.getChildId());
        if (parent != null && child != null) {
            parent.addChild(child);
            child.addParent(parent);
        }
    }

    private Map<ID, ParentGivenGraphNodeResult<ID, T>> generateNodesMap(
            ParentGivenGraphParseInput<ID, T> parentGivenGraphParseInput,
            Set<GraphParserEdge<ID>> graphParserEdges
    ) throws GraphProcessingException {
        var mainNodeIds = findAllMainNodeIds(parentGivenGraphParseInput);
        log.info("Found main node ids: {}", mainNodeIds);
        var connectedNodeIds = findAllConnectedNodeIds(
                parentGivenGraphParseInput.getStartNodeId(),
                graphParserEdges);
        log.info("Found connected commits ids: {}", connectedNodeIds);
        return parentGivenGraphParseInput.getGraphInput().stream()
                .map(nodeInput -> convertToResponseNode(nodeInput, mainNodeIds, connectedNodeIds))
                .collect(Collectors.toMap(ParentGivenGraphNodeResult::getId, node -> node));
    }

    private ParentGivenGraphNodeResult<ID, T> convertToResponseNode(
            ParentGivenGraphNodeInput<ID, T> nodeInput, Set<ID> mainNodeIds, Set<ID> connectedNodeIds
    ) {
        var pathType = determinePathType(nodeInput.getId(), mainNodeIds, connectedNodeIds);
        return ParentGivenGraphNodeResult.<ID, T>builder()
                .id(nodeInput.getId())
                .object(nodeInput.getObject())
                .startNodePathType(pathType)
                .build();
    }

    private PathType determinePathType(ID nodeId, Set<ID> mainNodeIds, Set<ID> connectedNodeIds) {
        if (mainNodeIds.contains(nodeId)) {
            return PathType.MAIN;
        } else if (connectedNodeIds.contains(nodeId)) {
            return PathType.CONNECTED;
        }
        return PathType.OUTER;
    }

    private Set<ID> findAllMainNodeIds(
            ParentGivenGraphParseInput<ID, T> parentGivenGraphParseInput
    ) throws GraphProcessingException {
        return directionalRelationIdsFinder.findAllConnectedIds(parentGivenGraphParseInput);
    }

    private Set<ID> findAllConnectedNodeIds(ID startNodeId, Set<GraphParserEdge<ID>> graphParserEdges) {
        return bidirectionalRelationIdsFinder.findAllConnectedIds(startNodeId, graphParserEdges);
    }

    private Set<GraphParserEdge<ID>> generateEdges(Collection<T> parserInput) {
        return parserInput.stream()
                .flatMap(nodeWrapper ->
                        nodeWrapper.getParentIds().stream()
                                .map(parentId -> new GraphParserEdge<>(parentId, nodeWrapper.getId())))
                .collect(Collectors.toSet());
    }
}
