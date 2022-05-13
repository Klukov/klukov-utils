package org.klukov.utils.graphs.parser;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.klukov.utils.graphs.relation.directional.DirectionalRelationIdsFinderInput;
import org.klukov.utils.graphs.validation.ValidatableGraphInput;

import java.util.Collection;

@Getter
@Builder
@ToString
public final class ParentGivenGraphParseInput<ID, T extends ParentGivenGraphNodeInputInput<ID, T>> implements DirectionalRelationIdsFinderInput<ID, T>, ValidatableGraphInput<ID, T> {
    private final Collection<T> graphInput;
    private final ID startNodeId;

    public ParentGivenGraphParseInput(Collection<T> graphInput, ID startNodeId) {
        this.graphInput = graphInput;
        this.startNodeId = startNodeId;
    }
}
