package org.klukov.utils.graphs.parser;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.klukov.utils.graphs.GraphEdge;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
@ToString
class GraphParserEdge<ID> implements GraphEdge<ID> {
    private final ID parentId;
    private final ID childId;
}
