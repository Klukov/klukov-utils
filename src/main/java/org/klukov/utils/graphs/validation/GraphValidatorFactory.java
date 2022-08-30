package org.klukov.utils.graphs.validation;

import lombok.experimental.UtilityClass;

@UtilityClass
public class GraphValidatorFactory {

    public <ID, T extends ValidatableGraphNodeInput<ID>> GraphValidator<ID, T> graphValidator() {
        return new GraphValidatorService<>();
    }
}
