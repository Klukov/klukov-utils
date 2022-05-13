package org.klukov.utils.graphs.relation;

import org.klukov.utils.graphs.validation.ValidatableGraphNodeInput;

import java.util.Collection;

public interface GraphNodeInput<ID> extends ValidatableGraphNodeInput<ID> {

    ID getId();

    Collection<ID> getRelatedIds();
}
