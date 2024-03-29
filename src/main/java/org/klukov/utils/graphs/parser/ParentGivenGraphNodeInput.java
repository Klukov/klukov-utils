package org.klukov.utils.graphs.parser;

import java.util.Collection;
import org.klukov.utils.graphs.relation.GraphNodeInput;
import org.klukov.utils.graphs.validation.ValidatableGraphNodeInput;

public interface ParentGivenGraphNodeInput<ID, T>
        extends GraphNodeInput<ID>, ValidatableGraphNodeInput<ID> {

    ID getId();

    Collection<ID> getParentIds();

    T getObject();

    default Collection<ID> getRelatedIds() {
        return this.getParentIds();
    }
}
