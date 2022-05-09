package org.klukov.utils.graphs.parser;

import lombok.extern.slf4j.Slf4j;
import org.klukov.utils.graphs.GraphProcessingException;
import org.klukov.utils.graphs.relation.directional.DirectionalRelationIdsFinder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class ParentGivenGraphParser<ID, T extends ParentGivenNodeInput<ID, T>> {

    public GraphParserResult<ID, T> parseGraphCollection(Collection<ParentGivenNodeInput<ID, T>> parserInput, ID startNodeId) throws GraphProcessingException {
        log.info("Starting validation of input: {}, {}", startNodeId, parserInput);
        validateInput(parserInput, startNodeId);
        log.info("Validation finished. Starting generating edges");
        var edges = generateEdges(parserInput);
        log.info("Generated edges: {}", edges);
        var nodesMap = generateNodesMap(startNodeId, parserInput, edges);
        log.info("Generated nodes: {}", nodesMap);
        edges.forEach(edge -> connectNodes(edge, nodesMap));
        log.info("All edges are connected");
        return GraphParserResult.<ID, T>builder()
                .graphNodes(nodesMap)
                .build();
    }

    private void validateInput(Collection<ParentGivenNodeInput<ID, T>> parserInput, ID startNodeId) throws GraphProcessingException {
        if (startNodeId == null) {
            throw new GraphProcessingException("Start node id is null");
        }
        if (anyNodeIsNullOrHasNullId(parserInput)) {
            throw new GraphProcessingException("At least one wrapped node is null or has id null");
        }
        var allNodesIds = parserInput.stream()
                .map(ParentGivenNodeInput::getId)
                .collect(Collectors.toSet());
        if (allNodesIds.size() != parserInput.size()) {
            throw new GraphProcessingException("Nodes have duplicates");
        }
        if (!allNodesIds.contains(startNodeId)) {
            throw new GraphProcessingException("Lack of start node");
        }
    }

    private boolean anyNodeIsNullOrHasNullId(Collection<ParentGivenNodeInput<ID, T>> parserInput) {
        return parserInput.stream().anyMatch(nodeWrapper -> nodeWrapper == null || nodeWrapper.getId() == null);
    }

    private void connectNodes(Edge<ID> edge, Map<ID, GraphNode<ID, T>> nodesMap) {
        var parent = nodesMap.get(edge.getParentId());
        var child = nodesMap.get(edge.getChildId());
        if (parent != null && child != null) {
            parent.addChild(child);
            child.addParent(parent);
        }
    }

    private Map<ID, GraphNode<ID, T>> generateNodesMap(ID startNodeId, Collection<ParentGivenNodeInput<ID, T>> parserInput, Set<Edge<ID>> edges) {
        var mainNodeIds = findAllMainNodeIds(startNodeId, parserInput);
        log.debug("Found main node ids: {}", mainNodeIds);
        var connectedNodeIds = findAllConnectedNodeIds(startNodeId, parserInput, edges);
        log.debug("Found connected commits ids: {}", connectedNodeIds);
        return parserInput.stream()
                .map(nodeInput -> convertToResponseNode(nodeInput, mainNodeIds, connectedNodeIds))
                .collect(Collectors.toMap(GraphNode::getId, node -> node));
    }

    private GraphNode<ID, T> convertToResponseNode(ParentGivenNodeInput<ID, T> nodeInput, Set<ID> mainNodeIds, Set<ID> connectedNodeIds) {
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

    private Set<ID> findAllMainNodeIds(ID startNodeId, Collection<ParentGivenNodeInput<ID, T>> parserInput) {
        return new DirectionalRelationIdsFinder<ID, ParentGivenNodeInput<ID, T>>()
                .findAllConnectedIds(startNodeId, parserInput);
    }

    private Set<ID> findAllConnectedNodeIds(ID startNodeId, Collection<ParentGivenNodeInput<ID, T>> parserInput, Set<Edge<ID>> edges) {
        return new HashSet<>();
    }

    private Map<ID, ParentGivenNodeInput<ID, T>> createNodeInputMap(Collection<ParentGivenNodeInput<ID, T>> parserInput) {
        return parserInput.stream()
                .collect(Collectors.toMap(
                        ParentGivenNodeInput::getId,
                        inputNode -> inputNode
                ));
    }

    private GraphNode<ID, T> convert(ParentGivenNodeInput<ID, T> input) {
        return GraphNode.<ID, T>builder()
                .id(input.getId())
                .object(input.getObject())
                .startNodePathType(PathType.MAIN)
                .build();
    }

    private Set<Edge<ID>> generateEdges(Collection<ParentGivenNodeInput<ID, T>> parserInput) {
        return parserInput.stream()
                .flatMap(nodeWrapper ->
                        nodeWrapper.getParentIds().stream()
                                .map(parentId -> new Edge<>(parentId, nodeWrapper.getId())))
                .collect(Collectors.toSet());
    }
}
