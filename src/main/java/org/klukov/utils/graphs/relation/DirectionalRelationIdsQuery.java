package org.klukov.utils.graphs.relation;

import org.klukov.utils.graphs.common.GraphProcessingException;

import java.util.Set;

public interface DirectionalRelationIdsQuery<ID, T extends GraphNodeInput<ID>> {

    Set<ID> findAllConnectedIds(RelationIdsFinderInput<ID, T> input) throws GraphProcessingException;
}
