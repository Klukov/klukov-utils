package org.klukov.utils.graphs.relation.directional;

import lombok.extern.slf4j.Slf4j;
import org.klukov.utils.graphs.relation.RelatedIdsExtractor;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class DirectionalRelationIdsFinder<ID, T extends RelatedIdsExtractor<ID>> {

    public Set<ID> findAllConnectedIds(ID startId, Collection<T> graphElements) {
        validateInput(startId, graphElements);
        var graphElementsMap = getGraphElementsMap(graphElements);
        preProcessValidation(startId, graphElements, graphElementsMap);
        var result = new HashSet<ID>();
        var queue = new LinkedList<T>();
        queue.add(graphElementsMap.get(startId));
        while (!queue.isEmpty()) {
            var element = queue.poll();
            result.add(element.getId());
            queue.addAll(getNextGraphElements(element.getRelatedIds(), graphElementsMap, result));
        }
        return result;
    }

    private Collection<T> getNextGraphElements(Collection<ID> relatedIds, Map<ID, T> graphElementsMap, HashSet<ID> result) {
        return Optional.ofNullable(relatedIds)
                .orElse(Collections.emptyList())
                .stream()
                .filter(id -> !result.contains(id))
                .map(graphElementsMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void preProcessValidation(ID startId, Collection<T> graphElements, Map<ID, T> graphElementsMap) {
        if (graphElementsMap.size() != graphElements.size()) {
            throw new RuntimeException("TO DO");
        }
        if (!graphElementsMap.containsKey(startId)) {
            throw new RuntimeException("TO DO");
        }
    }

    private void validateInput(ID startId, Collection<T> graphElements) {
        if (startId == null || graphElements == null || graphElements.size() == 0) {
            throw new RuntimeException("TO DO");
        }
        if (anyNullObjectOrNullId(graphElements)) {
            throw new RuntimeException("TO DO");
        }
        if (graphElementsDoesNotContainStartElement(startId, graphElements)) {
            throw new RuntimeException("TODO");
        }
    }

    private boolean graphElementsDoesNotContainStartElement(ID startId, Collection<T> graphElements) {
        return graphElements.stream().noneMatch(element -> element.getId().equals(startId));
    }

    private boolean anyNullObjectOrNullId(Collection<T> graphElements) {
        return graphElements.stream().anyMatch(this::validateSingleElement);
    }

    private boolean validateSingleElement(T element) {
        return element == null || element.getId() == null || element.getRelatedIds() == null;
    }

    private Map<ID, T> getGraphElementsMap(Collection<T> graphElements) {
        return graphElements.stream()
                .collect(Collectors.toMap(
                        RelatedIdsExtractor::getId,
                        graphElement -> graphElement
                ));
    }
}
