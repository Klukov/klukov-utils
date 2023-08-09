package org.klukov.utils.graphs.validation;

import static org.klukov.utils.graphs.common.ProcessingErrorType.DUPLICATED_NODES;
import static org.klukov.utils.graphs.common.ProcessingErrorType.NULL_NODES;
import static org.klukov.utils.graphs.common.ProcessingErrorType.NULL_OR_EMPTY_GRAPH;
import static org.klukov.utils.graphs.common.ProcessingErrorType.NULL_START_ID;
import static org.klukov.utils.graphs.common.ProcessingErrorType.STAR_NODE_NOT_IN_GRAPH;

import java.util.Collection;
import java.util.stream.Collectors;
import org.klukov.utils.graphs.common.GraphProcessingException;

class GraphValidatorService<ID, T extends ValidatableGraphNodeInput<ID>>
        implements GraphValidator<ID, T> {

    public void validate(ValidatableGraphInput<ID, T> input) throws GraphProcessingException {
        var startNodeId = input.getStartNodeId();
        var parserInput = input.getGraphInput();
        if (startNodeId == null) {
            throw new GraphProcessingException(NULL_START_ID, "Start node id is null");
        }
        if (parserInput == null || parserInput.isEmpty()) {
            throw new GraphProcessingException(
                    NULL_OR_EMPTY_GRAPH, "Input collection with graph is null or empty");
        }
        if (anyNodeIsNullOrHasNullId(parserInput)) {
            throw new GraphProcessingException(
                    NULL_NODES, "At least one wrapped node is null or has id null");
        }
        var allNodesIds =
                parserInput.stream()
                        .map(ValidatableGraphNodeInput::getId)
                        .collect(Collectors.toSet());
        if (allNodesIds.size() != parserInput.size()) {
            throw new GraphProcessingException(DUPLICATED_NODES, "Nodes have duplicates");
        }
        if (!allNodesIds.contains(startNodeId)) {
            throw new GraphProcessingException(STAR_NODE_NOT_IN_GRAPH, "Lack of start node");
        }
    }

    private boolean anyNodeIsNullOrHasNullId(Collection<T> parserInput) {
        return parserInput.stream()
                .anyMatch(nodeWrapper -> nodeWrapper == null || nodeWrapper.getId() == null);
    }
}
