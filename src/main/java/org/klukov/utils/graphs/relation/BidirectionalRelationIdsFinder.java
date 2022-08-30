package org.klukov.utils.graphs.relation;

import org.klukov.utils.graphs.common.GraphEdge;

import java.util.Collection;
import java.util.Set;

class BidirectionalRelationIdsFinder<ID> implements BidirectionalRelationIdsQuery<ID> {

    public <E extends GraphEdge<ID>> Set<ID> findAllConnectedIds(ID startId, Collection<E> graphEdges) {
        var solver = new BidirectionalRelationSolver<>(graphEdges);
        return solver.getRelatedIds(startId);
    }
}
