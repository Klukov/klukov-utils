package org.klukov.utils.graphs.relation;

import org.klukov.utils.graphs.validation.ValidatableGraphInput;

import java.util.Collection;

public interface RelationIdsFinderInput<ID, T extends GraphNodeInput<ID>> extends ValidatableGraphInput<ID, T> {

    ID getStartNodeId();

    Collection<T> getGraphInput();
}
