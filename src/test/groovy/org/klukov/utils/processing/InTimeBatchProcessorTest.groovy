package org.klukov.utils.processing

import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.Callable
import java.util.function.Consumer
import java.util.function.Supplier
import spock.lang.Specification

class InTimeBatchProcessorTest extends Specification {

    Callable<Long> callableMock = Mock()
    Supplier<List<String>> supplierMock = Mock()
    Consumer<List<String>> consumerMock = Mock()

    def "should stop processing callable records when no record processed in last call"() {
        given:
        def inTimeBatchProcessor = new InTimeBatchProcessor(Duration.ofMinutes(1), () -> nowInstant())

        when:
        def result = inTimeBatchProcessor.process(callableMock)

        then:
        expectedCalls * callableMock.call() >>> callableResponse
        result == expectedResult

        where:
        callableResponse     || expectedCalls || expectedResult
        [8L, 7L, 2L, 1L, 0L] || 5             || 18L
        [3L, 0L]             || 2             || 3L
        [0L]                 || 1             || 0L
    }

    def "should stop processing callable records when times up"() {
        given:
        Supplier<Instant> timeSupplier = Mock()
        timeSupplier.get() >>> timeSupplierResponse // first call to calculate finish dateTime

        when:
        def inTimeBatchProcessor = new InTimeBatchProcessor(Duration.ofHours(2), timeSupplier)
        def result = inTimeBatchProcessor.process(callableMock)

        then:
        expectedCalls * callableMock.call() >> 10L
        result == 10L * expectedCalls

        where:
        timeSupplierResponse                                                                           || expectedCalls
        nowRepeatsAndThen(1, nowZoned().plusDays(1).toInstant())                                       || 0
        nowRepeatsAndThen(2, nowZoned().plusDays(1).toInstant())                                       || 1
        nowRepeatsAndThen(2, nowZoned().plusHours(2).toInstant())                                      || 1
        nowRepeatsAndThen(2, nowZoned().minusDays(7).toInstant(), nowZoned().plusHours(2).toInstant()) || 2
    }

    def "should stop processing when callable throw an error"() {
        given:
        def inTimeBatchProcessor = new InTimeBatchProcessor(Duration.ofMinutes(1), () -> nowInstant())

        when:
        def result = inTimeBatchProcessor.process(callableMock)

        then:
        expectedCalls * callableMock.call(*_) >>> callableResponse
        result == expectedResult

        where:
        callableResponse                       || expectedCalls || expectedResult
        [9L, { throw new RuntimeException() }] || 2             || 9L
        [{ throw new RuntimeException() }]     || 1             || 0L
    }

    def "should process callable without batch processor time provider"() {
        given:
        def inTimeBatchProcessor = new InTimeBatchProcessor(Duration.ofMillis(1))
        callableMock.call() >> 10L

        when:
        def result = inTimeBatchProcessor.process(callableMock)

        then:
        result % 10 == 0
        result / 10 > 0
    }

    def "should stop processing supplier-consumer records when no records returned by supplier"() {
        given:
        supplierMock.get() >>> supplierResponse
        def inTimeBatchProcessor = new InTimeBatchProcessor(Duration.ofMinutes(1), () -> nowInstant())

        when:
        def result = inTimeBatchProcessor.process(supplierMock, consumerMock)

        then:
        expectedConsumerCalls * consumerMock.accept(*_)
        result == expectedReturn

        where:
        supplierResponse               || expectedConsumerCalls || expectedReturn
        oneElementSupplier()           || 1                     || 1
        fourElementsSupplier()         || 1                     || 4
        doubleSupplier()               || 2                     || 3
        doubleSupplierWithEmptyBreak() || 1                     || 2
    }

    def "should stop processing supplier-consumer records when times up"() {
        given:
        supplierMock.get() >> fourElementsSupplier()[0]
        Supplier<Instant> timeSupplier = Mock()
        timeSupplier.get() >>> timeSupplierResponse // first call to calculate finish dateTime

        when:
        def inTimeBatchProcessor = new InTimeBatchProcessor(Duration.ofHours(2), timeSupplier)
        def result = inTimeBatchProcessor.process(supplierMock, consumerMock)

        then:
        expectedConsumerCalls * consumerMock.accept(*_)
        result == 4 * expectedConsumerCalls

        where:
        timeSupplierResponse                                                                           || expectedConsumerCalls
        nowRepeatsAndThen(1, nowZoned().plusDays(1).toInstant())                                       || 0
        nowRepeatsAndThen(2, nowZoned().plusDays(1).toInstant())                                       || 1
        nowRepeatsAndThen(2, nowZoned().plusHours(2).toInstant())                                      || 1
        nowRepeatsAndThen(2, nowZoned().minusDays(7).toInstant(), nowZoned().plusHours(2).toInstant()) || 2
    }

    def "should process supplier-consumer records without batch processor time provider"() {
        given:
        def inTimeBatchProcessor = new InTimeBatchProcessor(Duration.ofMillis(10))
        def supplierResult = fourElementsSupplier()[0]
        supplierMock.get() >> supplierResult

        when:
        def result = inTimeBatchProcessor.process(supplierMock, consumerMock)

        then:
        result % supplierResult.size() == 0
        result / supplierResult.size() > 0
        println("RESULT: " + result)
    }

    private static List<Instant> nowRepeatsAndThen(int instantRepeats, Instant... last) {
        (1..instantRepeats).collect { nowInstant() } + last.toList()
    }

    private static List<List<String>> oneElementSupplier() {
        [["A"], []]
    }

    private static List<List<String>> fourElementsSupplier() {
        [["A", "B", "C", "D"], []]
    }

    private static List<List<String>> doubleSupplier() {
        [["A", "B"], ["C"], []]
    }

    private static List<List<String>> doubleSupplierWithEmptyBreak() {
        [["A", "B"], [], ["C"], []]
    }

    private static ZonedDateTime nowZoned() {
        nowLocal().atZone(ZoneId.of("Europe/Warsaw"))
    }

    private static Instant nowInstant() {
        nowZoned().toInstant()
    }

    private static LocalDateTime nowLocal() {
        LocalDateTime.of(2024, 1, 2, 3, 4, 5)
    }
}
