package org.klukov.utils.graphs.validation;

import java.util.Collection;

public interface ValidatableGraphNodeInput<ID> {

    ID getId();

    Collection<ID> getRelatedIds();
}
