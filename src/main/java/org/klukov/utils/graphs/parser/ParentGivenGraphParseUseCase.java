package org.klukov.utils.graphs.parser;

import org.klukov.utils.graphs.common.GraphProcessingException;

public interface ParentGivenGraphParseUseCase<ID, T extends ParentGivenGraphNodeInput<ID, T>> {

    ParentGivenGraphParserResult<ID, T> parseGraphCollection(
            ParentGivenGraphParseInput<ID, T> parentGivenGraphParseInput
    ) throws GraphProcessingException;
}
