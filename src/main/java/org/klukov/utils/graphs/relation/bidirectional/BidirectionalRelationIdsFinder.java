package org.klukov.utils.graphs.relation.bidirectional;

import org.klukov.utils.graphs.GraphProcessingException;
import org.klukov.utils.graphs.relation.GraphNodeInput;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class BidirectionalRelationIdsFinder<ID, T extends GraphNodeInput<ID>> {

    public Set<ID> findAllConnectedIds(ID startId, Collection<T> graphElements) throws GraphProcessingException {
        return new HashSet<>();
    }
}
