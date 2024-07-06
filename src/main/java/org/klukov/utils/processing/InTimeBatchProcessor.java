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

    public InTimeBatchProcessor(Duration duration) {
        this(duration, Instant::now);
    }

    public long process(Callable<Long> processingUnit) {
        var finishDateTime = currentTimeSupplier.get().plus(duration);
        long recordsProcessed = 0;
        while (true) {
            if (processingFinished(finishDateTime)) {
                return recordsProcessed;
            }
            long recordsProcessedInUnit;
            try {
                recordsProcessedInUnit = processingUnit.call();
            } catch (Exception e) {
                return recordsProcessed;
            }
            if (recordsProcessedInUnit > 0) {
                recordsProcessed += recordsProcessedInUnit;
            } else {
                return recordsProcessed;
            }
        }
    }

    public <T, C extends Collection<T>> long process(
            Supplier<C> recordsProvider, Consumer<C> recordsConsumer) {
        var finishDateTime = currentTimeSupplier.get().plus(duration);
        long recordsProcessed = 0;
        while (true) {
            if (processingFinished(finishDateTime)) {
                return recordsProcessed;
            }
            var recordsToProcess = recordsProvider.get();
            if (recordsToProcess.isEmpty()) {
                return recordsProcessed;
            }
            recordsConsumer.accept(recordsToProcess);
            recordsProcessed += recordsToProcess.size();
        }
    }

    private boolean processingFinished(Instant finishDateTime) {
        return !currentTimeSupplier.get().isBefore(finishDateTime);
    }
}
