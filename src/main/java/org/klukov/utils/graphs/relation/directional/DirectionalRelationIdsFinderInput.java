package org.klukov.utils.graphs.relation.directional;

import org.klukov.utils.graphs.relation.GraphNodeInput;
import org.klukov.utils.graphs.validation.ValidatableGraphInput;

import java.util.Collection;

public interface DirectionalRelationIdsFinderInput<ID, T extends GraphNodeInput<ID>> extends ValidatableGraphInput<ID, T> {

    ID getStartNodeId();

    Collection<T> getGraphInput();
}
