package org.klukov.utils.graphs.relation;

import java.util.Set;
import org.klukov.utils.graphs.common.GraphProcessingException;

public interface DirectionalRelationIdsQuery<ID, T extends GraphNodeInput<ID>> {

    Set<ID> findAllConnectedIds(RelationIdsFinderInput<ID, T> input)
            throws GraphProcessingException;
}
