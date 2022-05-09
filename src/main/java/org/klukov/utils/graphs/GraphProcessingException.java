package org.klukov.utils.graphs;

import lombok.Getter;

@Getter
public class GraphProcessingException extends Exception {

    private final ProcessingErrorType processingErrorType;

    public GraphProcessingException(String message) {
        this(ProcessingErrorType.NOT_SPECIFIED, message);
    }

    public GraphProcessingException(ProcessingErrorType processingErrorType) {
        super(processingErrorType.name());
        this.processingErrorType = processingErrorType;
    }

    public GraphProcessingException(ProcessingErrorType processingErrorType, String message) {
        super(getMessage(processingErrorType, message));
        this.processingErrorType = processingErrorType;
    }

    private static String getMessage(ProcessingErrorType processingErrorType, String message) {
        return processingErrorType + " - " + message;
    }
}
