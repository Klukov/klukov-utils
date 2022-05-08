package org.klukov.utils.graphs.relation;

import java.util.Collection;

public interface RelatedIdsExtractor<ID> {

    ID getId();

    Collection<ID> getRelatedIds();
}
