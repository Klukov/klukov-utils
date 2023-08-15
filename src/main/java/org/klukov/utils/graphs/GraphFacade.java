package org.klukov.utils.graphs;

import lombok.experimental.UtilityClass;
import org.klukov.utils.graphs.common.GraphProcessingException;
import org.klukov.utils.graphs.parser.ParentGivenGraphFactory;
import org.klukov.utils.graphs.parser.ParentGivenGraphNodeInput;
import org.klukov.utils.graphs.parser.ParentGivenGraphParseInput;
import org.klukov.utils.graphs.parser.ParentGivenGraphParseUseCase;
import org.klukov.utils.graphs.parser.ParentGivenGraphParserResult;
import org.klukov.utils.graphs.relation.BidirectionalRelationIdsQuery;
import org.klukov.utils.graphs.relation.DirectionalRelationIdsQuery;
import org.klukov.utils.graphs.relation.GraphRelationFactory;
import org.klukov.utils.graphs.validation.GraphValidator;
import org.klukov.utils.graphs.validation.GraphValidatorFactory;

@UtilityClass
public class GraphFacade {

    public <ID, T extends ParentGivenGraphNodeInput<ID, T>>
            ParentGivenGraphParserResult<ID, T> parseGraphCollection(
                    ParentGivenGraphParseInput<ID, T> parentGivenGraphParseInput)
                    throws GraphProcessingException {
        ParentGivenGraphParseUseCase<ID, T> parentGivenGraphParser =
                generateParentGivenGraphParser();
        return parentGivenGraphParser.parseGraphCollection(parentGivenGraphParseInput);
    }

    private <ID, T extends ParentGivenGraphNodeInput<ID, T>>
            ParentGivenGraphParseUseCase<ID, T> generateParentGivenGraphParser() {
        GraphValidator<ID, T> graphValidator = GraphValidatorFactory.graphValidator();
        DirectionalRelationIdsQuery<ID, T> directionalRelationIdsFinder =
                GraphRelationFactory.directionalRelationIdsQuery(graphValidator);
        BidirectionalRelationIdsQuery<ID> bidirectionalRelationIdsFinder =
                GraphRelationFactory.bidirectionalRelationIdsQuery();
        return ParentGivenGraphFactory.parentGivenGraphParser(
                directionalRelationIdsFinder, bidirectionalRelationIdsFinder, graphValidator);
    }
}
