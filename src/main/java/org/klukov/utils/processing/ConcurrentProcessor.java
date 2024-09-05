package org.klukov.utils.processing;

import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConcurrentProcessor<ID> {

    private final ConcurrentHashMap<ID, Object> LOCK_MAP = new ConcurrentHashMap<>();

    public void process(ID id, Runnable runnable) {
        log.debug("Incoming request to process runnable with id = {}", id);
        var lock = LOCK_MAP.computeIfAbsent(id, key -> new Object());
        log.info("Acquired lock for id = {}", id);
        synchronized (lock) {
            log.info("Processing runnable with id = {}", id);
            runnable.run();
            log.info("Finished runnable with id = {}", id);
            LOCK_MAP.remove(id);
            log.debug("Lock object removed with id = {}", id);
        }
        log.debug("Lock released with id = {}", id);
    }
}
