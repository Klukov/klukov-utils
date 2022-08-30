package org.klukov.utils.graphs.validation;

import org.klukov.utils.graphs.common.GraphProcessingException;

public interface GraphValidator<ID, T extends ValidatableGraphNodeInput<ID>> {

    void validate(ValidatableGraphInput<ID, T> input) throws GraphProcessingException;
}
