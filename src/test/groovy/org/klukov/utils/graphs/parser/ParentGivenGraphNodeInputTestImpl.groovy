package org.klukov.utils.graphs.parser

class ParentGivenGraphNodeInputTestImpl implements ParentGivenGraphNodeInput<String, ParentGivenGraphNodeInputTestImpl> {

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
    ParentGivenGraphNodeInputTestImpl getObject() {
        return this
    }
}
