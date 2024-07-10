package org.klukov.utils.processing;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public final class InTimeBatchProcessor {
    @NonNull private final Duration duration;
    @NonNull private final Supplier<Instant> currentTimeSupplier;

    public InTimeBatchProcessor(@NonNull Duration duration) {
        this(duration, Instant::now);
    }

    public long process(Callable<Long> processingUnit) {
        var finishDateTime = currentTimeSupplier.get().plus(duration);
        long recordsProcessed = 0;
        log.debug("Starting processing callable batch processor");
        while (true) {
            if (processingFinished(finishDateTime)) {
                log.debug("Finished processing callable batch processor - time limit exceeded");
                return recordsProcessed;
            }
            long recordsProcessedInUnit;
            try {
                recordsProcessedInUnit = processingUnit.call();
            } catch (Exception e) {
                return recordsProcessed;
            }
            if (recordsProcessedInUnit < 1) {
                log.debug(
                        "Finished processing callable batch processor - no more records to process");
                return recordsProcessed;
            }
            recordsProcessed += recordsProcessedInUnit;
            log.debug(
                    "Processing callable batch processor - processed number of records: {}",
                    recordsProcessed);
        }
    }

    public <T, C extends Collection<T>> long process(
            Supplier<C> recordsProvider, Consumer<C> recordsConsumer) {
        var finishDateTime = currentTimeSupplier.get().plus(duration);
        long recordsProcessed = 0;
        log.debug("Starting processing supplier-consumer batch processor");
        while (true) {
            if (processingFinished(finishDateTime)) {
                log.debug(
                        "Finished processing supplier-consumer batch processor - time limit exceeded");
                return recordsProcessed;
            }
            var recordsToProcess = recordsProvider.get();
            if (recordsToProcess.isEmpty()) {
                log.debug(
                        "Finished processing supplier-consumer batch processor - no more records to process");
                return recordsProcessed;
            }
            recordsConsumer.accept(recordsToProcess);
            recordsProcessed += recordsToProcess.size();
            log.debug(
                    "Processing supplier-consumer batch processor - processed number of records: {}",
                    recordsProcessed);
        }
    }

    private boolean processingFinished(Instant finishDateTime) {
        return !currentTimeSupplier.get().isBefore(finishDateTime);
    }
}
