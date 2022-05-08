package org.klukov.utils.graphs;

import org.klukov.utils.graphs.relation.RelatedIdsExtractor;

import java.util.Collection;

public interface ParentGivenNodeInput<ID, T> extends RelatedIdsExtractor<ID> {

    ID getId();

    Collection<ID> getParentIds();

    T getObject();

    default Collection<ID> getRelatedIds() {
        return this.getParentIds();
    }
}
