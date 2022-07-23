package org.klukov.utils.graphs.parser

class ParentGivenGraphNodeTestImplInput implements ParentGivenGraphNodeInput<String, ParentGivenGraphNodeTestImplInput> {

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
    ParentGivenGraphNodeTestImplInput getObject() {
        return this
    }
}
