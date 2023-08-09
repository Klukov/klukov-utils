package org.klukov.utils.graphs.relation;

import java.util.Collection;
import org.klukov.utils.graphs.validation.ValidatableGraphInput;

public interface RelationIdsFinderInput<ID, T extends GraphNodeInput<ID>>
        extends ValidatableGraphInput<ID, T> {

    ID getStartNodeId();

    Collection<T> getGraphInput();
}
