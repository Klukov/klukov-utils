package org.klukov.utils.graphs

import org.klukov.utils.graphs.parser.ParentGivenNodeInput

class ParentGivenNodeInputTestImpl implements ParentGivenNodeInput<String, ParentGivenNodeInputTestImpl> {

    private String id
    private Collection<String> parentIds

    @Override
    String getId() {
        return id
    }

    @Override
    Collection<String> getParentIds() {
        return parentIds
    }

    @Override
    ParentGivenNodeInputTestImpl getObject() {
        return this
    }
}
