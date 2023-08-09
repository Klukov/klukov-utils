package org.klukov.utils.graphs.relation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.klukov.utils.graphs.common.GraphProcessingException;
import org.klukov.utils.graphs.validation.GraphValidator;

@Slf4j
@AllArgsConstructor
class DirectionalRelationIdsFinder<ID, T extends GraphNodeInput<ID>>
        implements DirectionalRelationIdsQuery<ID, T> {

    private final GraphValidator<ID, T> graphValidator;

    @Override
    public Set<ID> findAllConnectedIds(RelationIdsFinderInput<ID, T> input)
            throws GraphProcessingException {
        validateInput(input);
        var graphElementsMap = getGraphElementsMap(input.getGraphInput());
        var result = new HashSet<ID>();
        var queue = new LinkedList<T>();
        queue.add(graphElementsMap.get(input.getStartNodeId()));
        while (!queue.isEmpty()) {
            var element = queue.poll();
            result.add(element.getId());
            queue.addAll(getNextGraphElements(element.getRelatedIds(), graphElementsMap, result));
        }
        return result;
    }

    private Collection<T> getNextGraphElements(
            Collection<ID> relatedIds, Map<ID, T> graphElementsMap, HashSet<ID> result) {
        return Optional.ofNullable(relatedIds).orElse(Collections.emptyList()).stream()
                .filter(id -> !result.contains(id))
                .map(graphElementsMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void validateInput(RelationIdsFinderInput<ID, T> input)
            throws GraphProcessingException {
        graphValidator.validate(input);
    }

    private Map<ID, T> getGraphElementsMap(Collection<T> graphElements) {
        return graphElements.stream()
                .collect(Collectors.toMap(GraphNodeInput::getId, graphElement -> graphElement));
    }
}
