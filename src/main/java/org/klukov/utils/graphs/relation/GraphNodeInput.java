package org.klukov.utils.graphs.relation;

import java.util.Collection;
import org.klukov.utils.graphs.validation.ValidatableGraphNodeInput;

public interface GraphNodeInput<ID> extends ValidatableGraphNodeInput<ID> {

    ID getId();

    Collection<ID> getRelatedIds();
}
