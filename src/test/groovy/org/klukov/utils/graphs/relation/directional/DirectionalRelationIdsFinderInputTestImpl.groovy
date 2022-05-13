package org.klukov.utils.graphs.relation.directional

class DirectionalRelationIdsFinderInputTestImpl implements DirectionalRelationIdsFinderInput<String, GraphNodeInputTestImpl> {

    private String startNodeId
    private Collection<GraphNodeInputTestImpl> graphInput

    @Override
    String getStartNodeId() {
        return startNodeId
    }

    @Override
    Collection<GraphNodeInputTestImpl> getGraphInput() {
        return graphInput
    }
}
