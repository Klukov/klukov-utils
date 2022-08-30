package org.klukov.utils.graphs.parser;


import lombok.experimental.UtilityClass;
import org.klukov.utils.graphs.relation.BidirectionalRelationIdsQuery;
import org.klukov.utils.graphs.relation.DirectionalRelationIdsQuery;
import org.klukov.utils.graphs.validation.GraphValidator;

@UtilityClass
public class ParentGivenGraphFactory {

    public <ID, T extends ParentGivenGraphNodeInput<ID, T>> ParentGivenGraphParseUseCase<ID, T> parentGivenGraphParser(
            DirectionalRelationIdsQuery<ID, T> directionalRelationIdsFinder,
            BidirectionalRelationIdsQuery<ID> bidirectionalRelationIdsFinder,
            GraphValidator<ID, T> graphValidator
    ) {
        return new ParentGivenGraphParserService<>(
                directionalRelationIdsFinder, bidirectionalRelationIdsFinder, graphValidator);
    }
}
