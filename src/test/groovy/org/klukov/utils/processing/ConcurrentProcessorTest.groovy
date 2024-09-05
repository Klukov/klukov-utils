package org.klukov.utils.processing

import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import spock.lang.Specification

class ConcurrentProcessorTest extends Specification {

    def "should run single process"() {
        given:
        def sub = new ConcurrentProcessor<Long>()
        def incrementor = new AtomicInteger(0)

        when:
        sub.process(1L, () -> incrementor.incrementAndGet())

        then:
        incrementor.get() == 1
    }

    def "should run multiple processed with different ids"() {
        given:
        def processor = new ConcurrentProcessor<Integer>()
        def counter = new AtomicInteger(0)
        def numberOfThreads = 8
        def latch = new CountDownLatch(numberOfThreads)

        when:
        def executor = Executors.newFixedThreadPool(numberOfThreads)

        (1..numberOfThreads).each { i ->
            executor.submit {
                processor.process(i, {
                    counter.incrementAndGet()
                    latch.countDown()
                } as Runnable)
            }
        }

        latch.await(2, TimeUnit.SECONDS)

        then:
        counter.get() == numberOfThreads

        cleanup:
        executor.shutdown()
    }

    def "should block multiple processes with the same id"() {
        given:
        def processor = new ConcurrentProcessor<Integer>()
        def counter = new AtomicInteger(0)
        def processId = 999
        def firstLatch = new CountDownLatch(1) // to ensure the first task starts before the second
        def secondLatch = new CountDownLatch(1) // second latch to block first task

        when:
        def executor = Executors.newFixedThreadPool(2)

        // First task
        executor.submit {
            processor.process(processId, {
                counter.incrementAndGet()
                firstLatch.countDown()
                secondLatch.await(3, TimeUnit.SECONDS)
            } as Runnable)
        }

        // Ensure the first task has started and acquired the lock
        firstLatch.await(1, TimeUnit.SECONDS)
        assert counter.get() == 1

        // Second task with the same ID
        executor.submit {
            processor.process(processId, {
                counter.incrementAndGet()
            } as Runnable)
        }

        Thread.sleep(500) // wait to ensure that second task is blocked
        assert counter.get() == 1

        // release first task
        secondLatch.countDown()
        Thread.sleep(500) // wait to ensure that second task is released and processed

        then:
        counter.get() == 2

        cleanup:
        executor.shutdown()
    }
}
