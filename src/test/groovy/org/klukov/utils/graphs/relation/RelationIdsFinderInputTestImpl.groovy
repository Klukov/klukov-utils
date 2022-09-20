package org.klukov.utils.graphs.relation

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

    @Override
    String toString() {
        return "RelationIdsFinderInputTestImpl{" +
                "startNodeId='" + startNodeId + '\'' +
                ", graphInput=" + graphInput +
                '}'
    }
}
