package org.klukov.utils.graphs.relation.bidirectional;

import org.klukov.utils.graphs.GraphEdge;

import java.util.Collection;
import java.util.Set;

public class BidirectionalRelationIdsFinder<ID> {

    public <E extends GraphEdge<ID>> Set<ID> findAllConnectedIds(ID startId, Collection<E> graphEdges) {
        var solver = new BidirectionalRelationSolver<ID>(graphEdges);
        return solver.getRelatedIds(startId);
    }
}
