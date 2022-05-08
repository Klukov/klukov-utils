package org.klukov.utils.graphs;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ParentGivenGraphParser<T extends ParentGivenNodeInput<T>> {

    /**
     * Do not use parser for graphs with loops
     *
     * @param parserInput
     * @param startNodeId
     * @return
     */
    public GraphParserResult<T> parseGraphCollection(Collection<ParentGivenNodeInput<T>> parserInput, String startNodeId) throws GraphParserException {
        validateInput(parserInput, startNodeId);
        var edges = generateEdges(parserInput);
        var nodesMap = generateNodesMap(startNodeId, parserInput, edges);
        edges.forEach(edge -> connectNodes(edge, nodesMap));
        return GraphParserResult.<T>builder()
                .graphNodes(nodesMap)
                .build();
    }

    private void validateInput(Collection<ParentGivenNodeInput<T>> parserInput, String startNodeId) throws GraphParserException {
        if (startNodeId == null) {
            throw new GraphParserException("Start node id is null");
        }
        if (anyNodeIsNullOrHasNullId(parserInput)) {
            throw new GraphParserException("At least one wrapped node is null or has id null");
        }
        var allNodesIds = parserInput.stream().map(ParentGivenNodeInput::getId).collect(Collectors.toSet());
        if (allNodesIds.size() != parserInput.size()) {
            throw new GraphParserException("Node id duplicates");
        }
        if (!allNodesIds.contains(startNodeId)) {
            throw new GraphParserException("Lack of start node");
        }
    }

    private boolean anyNodeIsNullOrHasNullId(Collection<ParentGivenNodeInput<T>> parserInput) {
        return parserInput.stream().anyMatch(nodeWrapper -> nodeWrapper == null || nodeWrapper.getId() == null);
    }

    private void connectNodes(Edge edge, Map<String, GraphNode<T>> nodesMap) {
        var parent = nodesMap.get(edge.getParentId());
        var child = nodesMap.get(edge.getChildId());
        if (parent != null && child != null) {
            parent.addChild(child);
            child.addParent(parent);
        }
    }

    private Map<String, GraphNode<T>> generateNodesMap(String startNodeId, Collection<ParentGivenNodeInput<T>> parserInput, Set<Edge> edges) {
        var result = new HashMap<String, GraphNode<T>>();
        var element = convert(parserInput.stream().findFirst().get());
        result.put(element.getId(), element);
        return result;
    }

    private GraphNode<T> convert(ParentGivenNodeInput<T> input) {
        return GraphNode.<T>builder()
                .id(input.getId())
                .object(input.getObject())
                .startNodePathType(PathType.MAIN)
                .build();
    }

    private Set<Edge> generateEdges(Collection<ParentGivenNodeInput<T>> parserInput) {
        return parserInput.stream()
                .flatMap(nodeWrapper ->
                        nodeWrapper.getParentIds().stream()
                                .map(parentId -> new Edge(parentId, nodeWrapper.getId())))
                .collect(Collectors.toSet());
    }
}
