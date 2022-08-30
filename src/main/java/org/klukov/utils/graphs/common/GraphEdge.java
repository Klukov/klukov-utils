package org.klukov.utils.graphs.common;

public interface GraphEdge<ID> {
    ID getParentId();

    ID getChildId();
}
