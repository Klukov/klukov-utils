package org.klukov.utils.graphs.parser;

import java.util.Collection;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.klukov.utils.graphs.relation.RelationIdsFinderInput;
import org.klukov.utils.graphs.validation.ValidatableGraphInput;

@Getter
@Builder
@ToString
public final class ParentGivenGraphParseInput<ID, T extends ParentGivenGraphNodeInput<ID, T>>
        implements RelationIdsFinderInput<ID, T>, ValidatableGraphInput<ID, T> {
    private final Collection<T> graphInput;
    private final ID startNodeId;

    public ParentGivenGraphParseInput(Collection<T> graphInput, ID startNodeId) {
        this.graphInput = graphInput;
        this.startNodeId = startNodeId;
    }
}
