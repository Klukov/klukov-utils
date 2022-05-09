package org.klukov.utils.graphs.parser;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Builder
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
@ToString
public final class GraphNode<ID, T> {

    @EqualsAndHashCode.Include
    private final ID id;

    private final T object;

    private final PathType startNodePathType;

    @ToString.Exclude
    private final Set<GraphNode<ID, T>> parentNodes = new HashSet<>();

    @ToString.Exclude
    private final Set<GraphNode<ID, T>> childNodes = new HashSet<>();

    void addChild(GraphNode<ID, T> node) {
        childNodes.add(node);
    }

    void addParent(GraphNode<ID, T> node) {
        parentNodes.add(node);
    }

    public Set<GraphNode<ID, T>> getParentNodes() {
        return Set.copyOf(parentNodes);
    }

    public Set<GraphNode<ID, T>> getChildNodes() {
        return Set.copyOf(childNodes);
    }
}
