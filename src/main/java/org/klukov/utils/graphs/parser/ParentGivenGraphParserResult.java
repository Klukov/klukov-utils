package org.klukov.utils.graphs.parser;

import java.util.Map;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
public final class ParentGivenGraphParserResult<ID, T> {
    private final Map<ID, ParentGivenGraphNodeResult<ID, T>> graphNodes;

    public Map<ID, ParentGivenGraphNodeResult<ID, T>> getGraphNodes() {
        return Map.copyOf(graphNodes);
    }
}
