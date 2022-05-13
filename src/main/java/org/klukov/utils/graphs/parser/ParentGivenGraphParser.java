package org.klukov.utils.graphs.parser;

import lombok.extern.slf4j.Slf4j;
import org.klukov.utils.graphs.GraphProcessingException;
import org.klukov.utils.graphs.relation.directional.DirectionalRelationIdsFinder;
import org.klukov.utils.graphs.validation.GraphValidator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class ParentGivenGraphParser<ID, T extends ParentGivenGraphNodeInputInput<ID, T>> {

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
        edges.forEach(edge -> connectNodes(edge, nodesMap));
        log.info("All edges are connected");
        return ParentGivenGraphParserResult.<ID, T>builder()
                .graphNodes(nodesMap)
                .build();
    }

    private void validate(ParentGivenGraphParseInput<ID, T> parentGivenGraphParseInput) throws GraphProcessingException {
        new GraphValidator<ID, T>().validate(parentGivenGraphParseInput);
    }

    private void connectNodes(Edge<ID> edge, Map<ID, GraphNode<ID, T>> nodesMap) {
        var parent = nodesMap.get(edge.getParentId());
        var child = nodesMap.get(edge.getChildId());
        if (parent != null && child != null) {
            parent.addChild(child);
            child.addParent(parent);
        }
    }

    private Map<ID, GraphNode<ID, T>> generateNodesMap(
            ParentGivenGraphParseInput<ID, T> parentGivenGraphParseInput,
            Set<Edge<ID>> edges
    ) throws GraphProcessingException {
        var mainNodeIds = findAllMainNodeIds(parentGivenGraphParseInput);
        log.info("Found main node ids: {}", mainNodeIds);
        var connectedNodeIds = findAllConnectedNodeIds(
                parentGivenGraphParseInput.getStartNodeId(),
                parentGivenGraphParseInput.getGraphInput(),
                edges);
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
        return new DirectionalRelationIdsFinder<ID, T>()
                .findAllConnectedIds(parentGivenGraphParseInput);
    }

    private Set<ID> findAllConnectedNodeIds(ID startNodeId, Collection<T> parserInput, Set<Edge<ID>> edges) {
        return new HashSet<>();
    }

    private Map<ID, ParentGivenGraphNodeInputInput<ID, T>> createNodeInputMap(Collection<ParentGivenGraphNodeInputInput<ID, T>> parserInput) {
        return parserInput.stream()
                .collect(Collectors.toMap(
                        ParentGivenGraphNodeInputInput::getId,
                        inputNode -> inputNode
                ));
    }

    private GraphNode<ID, T> convert(ParentGivenGraphNodeInputInput<ID, T> input) {
        return GraphNode.<ID, T>builder()
                .id(input.getId())
                .object(input.getObject())
                .startNodePathType(PathType.MAIN)
                .build();
    }

    private Set<Edge<ID>> generateEdges(Collection<T> parserInput) {
        return parserInput.stream()
                .flatMap(nodeWrapper ->
                        nodeWrapper.getParentIds().stream()
                                .map(parentId -> new Edge<>(parentId, nodeWrapper.getId())))
                .collect(Collectors.toSet());
    }
}
