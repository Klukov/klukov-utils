package org.klukov.utils.graphs.parser;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
public final class GraphNode<ID, T> {
    @EqualsAndHashCode.Include
    private final ID id;
    private final T object;
    private final PathType startNodePathType;
    private final List<GraphNode<ID, T>> parentNodes = new ArrayList<>();
    private final List<GraphNode<ID, T>> childNodes = new ArrayList<>();

    void addChild(GraphNode<ID, T> node) {
        childNodes.add(node);
    }

    void addParent(GraphNode<ID, T> node) {
        parentNodes.add(node);
    }

    public List<GraphNode<ID, T>> getParentNodes() {
        return List.copyOf(parentNodes);
    }

    public List<GraphNode<ID, T>> getChildNodes() {
        return List.copyOf(childNodes);
    }
}
