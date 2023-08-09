package org.klukov.utils.graphs.relation;

import java.util.Collection;
import java.util.Set;
import org.klukov.utils.graphs.common.GraphEdge;

public interface BidirectionalRelationIdsQuery<ID> {

    <E extends GraphEdge<ID>> Set<ID> findAllConnectedIds(ID startId, Collection<E> graphEdges);
}
