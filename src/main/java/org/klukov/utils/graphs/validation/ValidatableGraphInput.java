package org.klukov.utils.graphs.validation;

import java.util.Collection;

public interface ValidatableGraphInput<ID, T extends ValidatableGraphNodeInput<ID>> {

    ID getStartNodeId();

    Collection<T> getGraphInput();
}
