package org.klukov.utils.processing

import groovyjarjarantlr4.v4.runtime.misc.NotNull
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import org.awaitility.Awaitility
import org.awaitility.core.ConditionFactory
import spock.lang.Specification

class ConcurrentProcessorTest extends Specification {

    def "should run single process"() {
        given:
        def subject = new ConcurrentProcessor<Long>()
        def incrementor = new AtomicInteger(0)

        when:
        subject.process(1L, () -> incrementor.incrementAndGet())

        then:
        incrementor.get() == 1
        subject.LOCK_MAP.size() == 0
    }

    def "should run multiple processes with different ids"() {
        given:
        def subject = new ConcurrentProcessor<Integer>()
        def initCounter = new AtomicInteger(0)
        def finishCounter = new AtomicInteger(0)
        def numberOfThreads = 32
        def cyclicBarrier = new CyclicBarrier(numberOfThreads + 1)

        when:
        def executor = Executors.newFixedThreadPool(numberOfThreads)

        (1..numberOfThreads).each { i ->
            executor.submit {
                subject.process(i, blockingProcess(initCounter, cyclicBarrier, finishCounter)) }}

        simpleAwait().until(() -> initCounter.get() == numberOfThreads)
        assert finishCounter.get() == 0
        cyclicBarrier.await()

        then:
        simpleAwait().until(() -> subject.LOCK_MAP.size() == 0)
        assert numberOfThreads == finishCounter.get()

        cleanup:
        executor.shutdown()
    }

    def "should block two processes with the same id"() {
        given:
        def subject = new ConcurrentProcessor<Integer>()
        def initCounter = new AtomicInteger(0)
        def finishCounter = new AtomicInteger(0)
        def processId = 999
        def firstCB = new CyclicBarrier(2)
        def secondCB = new CyclicBarrier(2)

        when:
        def executor = Executors.newFixedThreadPool(2)

        // First task
        executor.submit {
            subject.process(processId, blockingProcess(initCounter, firstCB, finishCounter))
        }
        simpleAwait().until(() -> insideFirstTask(initCounter, finishCounter))
        assert subject.LOCK_MAP[processId].get() == 1

        // Second task
        executor.submit {
            subject.process(processId, blockingProcess(initCounter, secondCB, finishCounter))
        }
        simpleAwait().until(() -> subject.LOCK_MAP[processId].get() == 2)
        assert insideFirstTask(initCounter, finishCounter)

        // release the first task
        firstCB.await()
        simpleAwait().until(() -> insideSecondTask(initCounter, finishCounter))
        assert subject.LOCK_MAP[processId].get() == 1

        // release the second task
        secondCB.await()

        then:
        simpleAwait().until(() -> bothTasksFinished(initCounter, finishCounter))
        subject.LOCK_MAP.size() == 0

        cleanup:
        executor.shutdown()
    }

    private static Runnable blockingProcess(
            @NotNull AtomicInteger initCounter,
            @NotNull CyclicBarrier cyclicBarrier,
            @NotNull AtomicInteger finishCounter) {
        () -> {
            initCounter.incrementAndGet()
            cyclicBarrier.await(5, TimeUnit.SECONDS)
            finishCounter.incrementAndGet()
        }
    }

    private static boolean insideFirstTask(AtomicInteger initCounter, AtomicInteger finishCounter) {
        initCounter.get() == 1 && finishCounter.get() == 0
    }

    private static boolean insideSecondTask(AtomicInteger initCounter, AtomicInteger finishCounter) {
        initCounter.get() == 2 && finishCounter.get() == 1
    }

    private static boolean bothTasksFinished(AtomicInteger initCounter, AtomicInteger finishCounter) {
        initCounter.get() == 2 && finishCounter.get() == 2
    }

    private static ConditionFactory simpleAwait(int seconds = 1) {
        Awaitility.await()
                .atMost(seconds, TimeUnit.SECONDS)
                .pollDelay(1, TimeUnit.MILLISECONDS)
                .pollInterval(1, TimeUnit.MILLISECONDS)
    }
}
