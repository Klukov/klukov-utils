package org.klukov.utils.graphs.parser

class ParentGivenGraphNodeInputTestImplInput implements ParentGivenGraphNodeInputInput<String, ParentGivenGraphNodeInputTestImplInput> {

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
    ParentGivenGraphNodeInputTestImplInput getObject() {
        return this
    }
}
