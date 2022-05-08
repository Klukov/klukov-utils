package org.klukov.utils.graphs;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
class Edge {
    private final String parentId;
    private final String childId;
}
