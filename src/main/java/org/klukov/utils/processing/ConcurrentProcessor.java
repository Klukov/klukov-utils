package org.klukov.utils.processing;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConcurrentProcessor<ID> {

    private final ConcurrentHashMap<ID, AtomicInteger> LOCK_MAP = new ConcurrentHashMap<>();

    public void process(ID id, Runnable runnable) {
        log.info("Incoming request to process runnable with id = {}", id);
        var lock = LOCK_MAP.compute(id, (key, value) -> createOrIncrement(value));
        log.info("Acquired lock for id = {}", id);
        synchronized (lock) {
            log.info("Processing runnable with id = {}", id);
            try {
                runnable.run();
            } finally {
                log.info("Finished processing runnable with id = {}", id);
                LOCK_MAP.computeIfPresent(id, (key, value) -> decrement(value).orElse(null));
            }
        }
        log.info("Lock released with id = {}", id);
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
