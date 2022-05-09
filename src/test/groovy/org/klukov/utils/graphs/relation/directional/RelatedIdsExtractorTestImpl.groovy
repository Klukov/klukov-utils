package org.klukov.utils.graphs.relation.directional

import org.klukov.utils.graphs.relation.RelatedIdsExtractor

class RelatedIdsExtractorTestImpl implements RelatedIdsExtractor<String> {

    private String id
    private List<String> parentIds

    @Override
    String getId() {
        return id
    }

    @Override
    Collection<String> getRelatedIds() {
        return parentIds
    }
}
