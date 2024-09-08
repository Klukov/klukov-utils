package org.klukov.utils.processing;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConcurrentProcessor<ID> {

    private final ConcurrentHashMap<ID, AtomicInteger> LOCK_MAP = new ConcurrentHashMap<>();

    public void process(ID id, Runnable runnable) {
        executeWithLock(
                id,
                () -> {
                    runnable.run();
                    return null;
                });
    }

    public <T> T process(ID id, Supplier<T> supplier) {
        return executeWithLock(id, supplier);
    }

    private <T> T executeWithLock(ID id, Supplier<T> supplier) {
        log.debug("Incoming request with id = {}", id);
        var lock = LOCK_MAP.compute(id, (key, value) -> createOrIncrement(value));
        log.debug("Acquired lock for id = {}", id);
        synchronized (lock) {
            log.debug("Processing id = {}", id);
            try {
                return supplier.get();
            } finally {
                log.debug("Finished processing with id = {}", id);
                LOCK_MAP.computeIfPresent(id, (key, value) -> decrement(value).orElse(null));
                log.debug("Lock removed with id = {}", id);
            }
        }
    }

    @NonNull private AtomicInteger createOrIncrement(AtomicInteger value) {
        return value == null ? new AtomicInteger(1) : increment(value);
    }

    @NonNull private AtomicInteger increment(@NonNull AtomicInteger value) {
        value.incrementAndGet();
        return value;
    }

    private Optional<AtomicInteger> decrement(@NonNull AtomicInteger value) {
        var current = value.decrementAndGet();
        if (current < 1) {
            return Optional.empty();
        }
        return Optional.of(value);
    }
}
