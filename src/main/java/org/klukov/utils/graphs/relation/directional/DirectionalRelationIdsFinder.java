package org.klukov.utils.graphs.relation.directional;

import lombok.extern.slf4j.Slf4j;
import org.klukov.utils.graphs.GraphProcessingException;
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

import static org.klukov.utils.graphs.ProcessingErrorType.DUPLICATED_NODES;
import static org.klukov.utils.graphs.ProcessingErrorType.NULL_NODES;
import static org.klukov.utils.graphs.ProcessingErrorType.NULL_OR_EMPTY_GRAPH;
import static org.klukov.utils.graphs.ProcessingErrorType.NULL_START_ID;
import static org.klukov.utils.graphs.ProcessingErrorType.STAR_NODE_NOT_IN_GRAPH;

@Slf4j
public class DirectionalRelationIdsFinder<ID, T extends RelatedIdsExtractor<ID>> {

    public Set<ID> findAllConnectedIds(ID startId, Collection<T> graphElements) throws GraphProcessingException {
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

    private void preProcessValidation(ID startId, Collection<T> graphElements, Map<ID, T> graphElementsMap) throws GraphProcessingException {
        if (graphElementsMap.size() != graphElements.size()) {
            throw new GraphProcessingException(DUPLICATED_NODES, "Nodes have duplicates");
        }
        if (!graphElementsMap.containsKey(startId)) {
            throw new GraphProcessingException(STAR_NODE_NOT_IN_GRAPH, "Lack of start node");
        }
    }

    private void validateInput(ID startId, Collection<T> graphElements) throws GraphProcessingException {
        if (startId == null) {
            throw new GraphProcessingException(NULL_START_ID, "Start node id is null");
        }
        if (graphElements == null || graphElements.size() == 0) {
            throw new GraphProcessingException(NULL_OR_EMPTY_GRAPH, "Input collection with graph is null or empty");
        }
        if (anyNullObjectOrNullId(graphElements)) {
            throw new GraphProcessingException(NULL_NODES, "At least one wrapped node is null or has id null");
        }
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
