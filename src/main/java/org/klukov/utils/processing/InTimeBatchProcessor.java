package org.klukov.utils.processing;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class InTimeBatchProcessor {
    @NonNull private final Duration duration;
    @NonNull private final Supplier<Instant> currentTimeSupplier;
    private final String processNameMessage;

    public InTimeBatchProcessor(
            @NonNull InTimeBatchProcessorProperties inTimeBatchProcessorProperties) {
        this.duration = inTimeBatchProcessorProperties.duration();
        this.currentTimeSupplier = inTimeBatchProcessorProperties.currentTimeSupplier();
        this.processNameMessage =
                inTimeBatchProcessorProperties
                        .getProcessName()
                        .map(name -> "PROCESS NAME: " + name + " | ")
                        .orElse(null);
    }

    public long process(Callable<Long> processingUnit) {
        var finishDateTime = currentTimeSupplier.get().plus(duration);
        long recordsProcessed = 0;
        logDebug("Starting processing callable batch processor");
        while (true) {
            if (processingFinished(finishDateTime)) {
                logDebug("Finished processing callable batch processor - time limit exceeded");
                return recordsProcessed;
            }
            long recordsProcessedInUnit;
            try {
                recordsProcessedInUnit = processingUnit.call();
            } catch (Exception e) {
                logError("Callable batch processor threw an error", e);
                return recordsProcessed;
            }
            if (recordsProcessedInUnit < 1) {
                logDebug(
                        "Finished processing callable batch processor - no more records to process");
                return recordsProcessed;
            }
            recordsProcessed += recordsProcessedInUnit;
            logDebug(
                    "Processing callable batch processor - processed number of records: {}",
                    recordsProcessed);
        }
    }

    public <T, C extends Collection<T>> long process(
            Supplier<C> recordsProvider, Consumer<C> recordsConsumer) {
        var finishDateTime = currentTimeSupplier.get().plus(duration);
        long recordsProcessed = 0;
        logDebug("Starting processing supplier-consumer batch processor");
        while (true) {
            if (processingFinished(finishDateTime)) {
                logDebug(
                        "Finished processing supplier-consumer batch processor - time limit exceeded");
                return recordsProcessed;
            }
            var recordsToProcess = recordsProvider.get();
            if (recordsToProcess.isEmpty()) {
                logDebug(
                        "Finished processing supplier-consumer batch processor - no more records to process");
                return recordsProcessed;
            }
            recordsConsumer.accept(recordsToProcess);
            recordsProcessed += recordsToProcess.size();
            logDebug(
                    "Processing supplier-consumer batch processor - processed number of records: {}",
                    recordsProcessed);
        }
    }

    private void logError(String message, Exception exception) {
        if (processNameMessage != null) {
            log.error(processNameMessage + message, exception);
        } else {
            log.error(message);
        }
    }

    private void logDebug(String message) {
        if (processNameMessage != null) {
            log.debug("{}{}", processNameMessage, message);
        } else {
            log.debug(message);
        }
    }

    private void logDebug(String message, Object object) {
        if (processNameMessage != null) {
            log.debug(processNameMessage + message, object);
        } else {
            log.debug(message);
        }
    }

    private boolean processingFinished(Instant finishDateTime) {
        return !currentTimeSupplier.get().isBefore(finishDateTime);
    }
}
