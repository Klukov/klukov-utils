package org.klukov.utils.graphs.parser;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
@ToString
class Edge<ID> {
    private final ID parentId;
    private final ID childId;
}
