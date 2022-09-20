package org.klukov.utils.graphs.relation


import org.klukov.utils.graphs.common.GraphEdge

class GraphEdgeTestImpl implements GraphEdge<String> {

    private String parentId
    private String childId


    @Override
    String getParentId() {
        return parentId
    }

    @Override
    String getChildId() {
        return childId
    }

    @Override
    String toString() {
        return "GraphEdgeTestImpl{" +
                "parentId='" + parentId + '\'' +
                ", childId='" + childId + '\'' +
                '}'
    }
}
