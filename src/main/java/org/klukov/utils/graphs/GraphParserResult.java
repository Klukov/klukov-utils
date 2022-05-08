package org.klukov.utils.graphs;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@Builder
@RequiredArgsConstructor
public final class GraphParserResult<T> {
    private final Map<String, GraphNode<T>> graphNodes;
}
