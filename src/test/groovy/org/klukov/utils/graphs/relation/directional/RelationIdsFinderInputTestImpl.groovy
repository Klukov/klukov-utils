package org.klukov.utils.graphs.relation.directional

import org.klukov.utils.graphs.relation.RelationIdsFinderInput

class RelationIdsFinderInputTestImpl implements RelationIdsFinderInput<String, GraphNodeInputTestImpl> {

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
