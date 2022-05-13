package org.klukov.utils.graphs;

public interface GraphEdge<ID> {
    ID getParentId();

    ID getChildId();
}
