package org.klukov.utils.graphs.parser;

import lombok.extern.slf4j.Slf4j;
import org.klukov.utils.graphs.GraphProcessingException;
import org.klukov.utils.graphs.relation.bidirectional.BidirectionalRelationIdsFinder;
import org.klukov.utils.graphs.relation.directional.DirectionalRelationIdsFinder;
import org.klukov.utils.graphs.validation.GraphValidator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class ParentGivenGraphParser<ID, T extends ParentGivenGraphNodeInputInput<ID, T>> {

    private final DirectionalRelationIdsFinder<ID, T> directionalRelationIdsFinder = new DirectionalRelationIdsFinder<>();
    private final BidirectionalRelationIdsFinder<ID, T> bidirectionalRelationIdsFinder = new BidirectionalRelationIdsFinder<>();
    private final GraphValidator<ID, T> graphValidator = new GraphValidator<>();

    /**
     * Parent ids could reference to not existing nodes
     *
     * @throws GraphProcessingException
     */
    public ParentGivenGraphParserResult<ID, T> parseGraphCollection(ParentGivenGraphParseInput<ID, T> parentGivenGraphParseInput) throws GraphProcessingException {
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

    private void validate(ParentGivenGraphParseInput<ID, T> parentGivenGraphParseInput) throws GraphProcessingException {
        graphValidator.validate(parentGivenGraphParseInput);
    }

    private void connectNodes(GraphParserEdge<ID> graphParserEdge, Map<ID, GraphNode<ID, T>> nodesMap) {
        var parent = nodesMap.get(graphParserEdge.getParentId());
        var child = nodesMap.get(graphParserEdge.getChildId());
        if (parent != null && child != null) {
            parent.addChild(child);
            child.addParent(parent);
        }
    }

    private Map<ID, GraphNode<ID, T>> generateNodesMap(
            ParentGivenGraphParseInput<ID, T> parentGivenGraphParseInput,
            Set<GraphParserEdge<ID>> graphParserEdges
    ) throws GraphProcessingException {
        var mainNodeIds = findAllMainNodeIds(parentGivenGraphParseInput);
        log.info("Found main node ids: {}", mainNodeIds);
        var connectedNodeIds = findAllConnectedNodeIds(
                parentGivenGraphParseInput.getStartNodeId(),
                parentGivenGraphParseInput.getGraphInput(),
                graphParserEdges);
        log.info("Found connected commits ids: {}", connectedNodeIds);
        return parentGivenGraphParseInput.getGraphInput().stream()
                .map(nodeInput -> convertToResponseNode(nodeInput, mainNodeIds, connectedNodeIds))
                .collect(Collectors.toMap(GraphNode::getId, node -> node));
    }

    private GraphNode<ID, T> convertToResponseNode(ParentGivenGraphNodeInputInput<ID, T> nodeInput, Set<ID> mainNodeIds, Set<ID> connectedNodeIds) {
        var pathType = determinePathType(nodeInput.getId(), mainNodeIds, connectedNodeIds);
        return GraphNode.<ID, T>builder()
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

    private Set<ID> findAllMainNodeIds(ParentGivenGraphParseInput<ID, T> parentGivenGraphParseInput) throws GraphProcessingException {
        return directionalRelationIdsFinder.findAllConnectedIds(parentGivenGraphParseInput);
    }

    private Set<ID> findAllConnectedNodeIds(ID startNodeId, Collection<T> parserInput, Set<GraphParserEdge<ID>> graphParserEdges) {
        return new HashSet<>();
    }

    private Set<GraphParserEdge<ID>> generateEdges(Collection<T> parserInput) {
        return parserInput.stream()
                .flatMap(nodeWrapper ->
                        nodeWrapper.getParentIds().stream()
                                .map(parentId -> new GraphParserEdge<>(parentId, nodeWrapper.getId())))
                .collect(Collectors.toSet());
    }
}
