package org.klukov.utils.graphs;

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
public final class GraphNode<T> {
    @EqualsAndHashCode.Include
    private final String id;
    private final T object;
    private final PathType startNodePathType;
    private final List<GraphNode<T>> parentNodes = new ArrayList<>();
    private final List<GraphNode<T>> childNodes = new ArrayList<>();

    public void addChild(GraphNode<T> node) {
        childNodes.add(node);
    }

    public void addParent(GraphNode<T> node) {
        parentNodes.add(node);
    }
}
