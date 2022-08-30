package org.klukov.utils.graphs.relation

class GraphNodeInputTestImpl implements GraphNodeInput<String> {

    private String id
    private List<String> relatedIds

    @Override
    String getId() {
        return id
    }

    @Override
    Collection<String> getRelatedIds() {
        return relatedIds
    }
}
