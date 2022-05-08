package org.klukov.utils.graphs;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Builder
@RequiredArgsConstructor
public final class GraphParserResult<ID, T> {
    private final Map<ID, GraphNode<ID, T>> graphNodes;

    public Map<ID, GraphNode<ID, T>> getGraphNodes() {
        return Map.copyOf(graphNodes);
    }
}
