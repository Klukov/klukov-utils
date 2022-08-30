package org.klukov.utils.graphs.parser;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Builder
@RequiredArgsConstructor
public final class ParentGivenGraphParserResult<ID, T> {
    private final Map<ID, ParentGivenGraphNodeResult<ID, T>> graphNodes;

    public Map<ID, ParentGivenGraphNodeResult<ID, T>> getGraphNodes() {
        return Map.copyOf(graphNodes);
    }
}
