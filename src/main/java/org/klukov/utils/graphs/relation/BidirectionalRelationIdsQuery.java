package org.klukov.utils.graphs.relation;

import org.klukov.utils.graphs.common.GraphEdge;

import java.util.Collection;
import java.util.Set;

public interface BidirectionalRelationIdsQuery<ID> {

    <E extends GraphEdge<ID>> Set<ID> findAllConnectedIds(ID startId, Collection<E> graphEdges);
}
