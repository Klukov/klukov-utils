package org.klukov.utils.graphs.relation;

import lombok.experimental.UtilityClass;
import org.klukov.utils.graphs.validation.GraphValidator;

@UtilityClass
public class GraphRelationFactory {

    public <ID> BidirectionalRelationIdsQuery<ID> bidirectionalRelationIdsQuery() {
        return new BidirectionalRelationIdsFinder<>();
    }

    public <ID, T extends GraphNodeInput<ID>>
            DirectionalRelationIdsQuery<ID, T> directionalRelationIdsQuery(
                    GraphValidator<ID, T> graphValidator) {
        return new DirectionalRelationIdsFinder<>(graphValidator);
    }
}
