package org.klukov.utils.structures

import java.util.concurrent.CountDownLatch
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import org.awaitility.Awaitility
import spock.lang.Specification

class ConcurrentAddRemoveListInMapTest extends Specification {

    def "should add and retrieve elements"() {
        given:
        def map = new ConcurrentAddRemoveListInMap<String, Integer>()

        when:
        map.add("key1", 1)
        map.add("key1", 2)
        map.add("key2", 3)

        then:
        map.getElements("key1").sort() == [1, 2]
        map.getElements("key2") == [3]
        map.getElements("key3") == []
    }

    def "should remove elements"() {
        given:
        def map = new ConcurrentAddRemoveListInMap<String, Integer>()
        map.add("key1", 1)
        map.add("key1", 2)
        map.add("key2", 3)

        when:
        map.remove("key1", 1)

        then:
        map.getElements("key1") == [2]
        map.getElements("key2") == [3]

        when:
        map.remove("key1", 2)

        then:
        map.getElements("key1") == []

        when:
        map.remove("key2", 3)

        then:
        map.getElements("key2") == []

        and:
        map.storage.size() == 0
    }

    def "should handle concurrent element addition operations for the same key"() {
        given:
        def sub = new ConcurrentAddRemoveListInMap<String, Integer>()
        def numThreads = 16
        def numOperationsPerThread = 1000
        def expectedNumElements = numThreads * numOperationsPerThread
        def executor = Executors.newFixedThreadPool(numThreads)
        def latch = new CountDownLatch(numThreads)
        def syncBarrier = new CyclicBarrier(numThreads + 1)

        when:
        numThreads.times { threadId ->
            executor.submit {
                syncBarrier.await(5, TimeUnit.SECONDS)
                try {
                    numOperationsPerThread.times { i ->
                        sub.add("key", threadId * numOperationsPerThread + i)
                    }
                } finally {
                    latch.countDown()
                }
            }
        }
        syncBarrier.await(5, TimeUnit.SECONDS)
        latch.await(10, TimeUnit.SECONDS)

        then:
        def elements = sub.getElements("key")
        elements.size() == expectedNumElements
        elements.sort() == (0..<expectedNumElements)

        cleanup:
        executor.shutdown()
        executor.awaitTermination(5, TimeUnit.SECONDS)
    }

    def "should handle concurrent removal of different elements with the same key"() {
        given:
        def sub = new ConcurrentAddRemoveListInMap<String, Integer>()
        def numThreads = 16
        def numOperationsPerThread = 1000
        def numElements = numOperationsPerThread * numThreads
        def executor = Executors.newFixedThreadPool(numThreads)
        def latch = new CountDownLatch(numThreads)
        def syncBarrier = new CyclicBarrier(numThreads + 1)

        and:
        numElements.times { i ->
            sub.add("key", i)
        }

        when:
        numThreads.times { threadId ->
            executor.submit {
                syncBarrier.await(5, TimeUnit.SECONDS)
                try {
                    numOperationsPerThread.times { i ->
                        sub.remove("key", threadId * numOperationsPerThread + i)
                    }
                } finally {
                    latch.countDown()
                }
            }
        }
        syncBarrier.await(5, TimeUnit.SECONDS)
        latch.await(10, TimeUnit.SECONDS)

        then:
        sub.getElements("key").isEmpty()
        sub.storage.size() == 0

        cleanup:
        executor.shutdown()
        executor.awaitTermination(5, TimeUnit.SECONDS)
    }

    def "should handle concurrent removal of the same elements with the same key"() {
        given:
        def sub = new ConcurrentAddRemoveListInMap<String, Integer>()
        def numThreads = 16
        def numElements = 1000
        def executor = Executors.newFixedThreadPool(numThreads)
        def latch = new CountDownLatch(numThreads)
        def syncBarrier = new CyclicBarrier(numThreads + 1)

        and:
        numElements.times { i ->
            sub.add("key", i)
        }

        when:
        numThreads.times { threadId ->
            executor.submit {
                syncBarrier.await(5, TimeUnit.SECONDS)
                try {
                    numElements.times { i ->
                        sub.remove("key", i)
                    }
                } finally {
                    latch.countDown()
                }
            }
        }
        syncBarrier.await(5, TimeUnit.SECONDS)
        latch.await(10, TimeUnit.SECONDS)

        then:
        sub.getElements("key").isEmpty()
        sub.storage.size() == 0

        cleanup:
        executor.shutdown()
        executor.awaitTermination(5, TimeUnit.SECONDS)
    }

    def "should handle concurrent element addition operations for different keys"() {
        given:
        def sub = new ConcurrentAddRemoveListInMap<String, Integer>()
        def numThreads = 16
        def numOperationsPerThread = 1000
        def expectedNumElements = numOperationsPerThread
        def executor = Executors.newFixedThreadPool(numThreads)
        def latch = new CountDownLatch(numThreads)
        def syncBarrier = new CyclicBarrier(numThreads + 1)

        when:
        numThreads.times { threadId ->
            executor.submit {
                syncBarrier.await(5, TimeUnit.SECONDS)
                try {
                    def key = "key" + threadId
                    numOperationsPerThread.times { i ->
                        sub.add(key, i)
                    }
                } finally {
                    latch.countDown()
                }
            }
        }
        syncBarrier.await(5, TimeUnit.SECONDS)
        latch.await(10, TimeUnit.SECONDS)

        then:
        numThreads.times { threadId ->
            def key = "key" + threadId
            def elements = sub.getElements(key)
            assert elements.size() == expectedNumElements
            assert elements.sort() == (0..<expectedNumElements)
        }

        cleanup:
        executor.shutdown()
        executor.awaitTermination(5, TimeUnit.SECONDS)
    }

    def "should handle concurrent element removal operations for different keys"() {
        given:
        def sub = new ConcurrentAddRemoveListInMap<String, Integer>()
        def numThreads = 16
        def numOperationsPerThread = 1000
        def executor = Executors.newFixedThreadPool(numThreads)
        def latch = new CountDownLatch(numThreads)
        def syncBarrier = new CyclicBarrier(numThreads + 1)

        and:
        numThreads.times { threadId ->
            def key = "key" + threadId
            numOperationsPerThread.times { i ->
                sub.add(key, i)
            }
        }

        when:
        numThreads.times { threadId ->
            executor.submit {
                syncBarrier.await(5, TimeUnit.SECONDS)
                try {
                    def key = "key" + threadId
                    numOperationsPerThread.times { i ->
                        sub.remove(key, i)
                    }
                } finally {
                    latch.countDown()
                }
            }
        }
        syncBarrier.await(5, TimeUnit.SECONDS)
        latch.await(10, TimeUnit.SECONDS)

        then:
        numThreads.times { threadId ->
            def key = "key" + threadId
            assert sub.getElements(key).isEmpty()
        }
        sub.storage.size() == 0

        cleanup:
        executor.shutdown()
        executor.awaitTermination(5, TimeUnit.SECONDS)
    }

    def "should handle concurrent addition and removal of different elements with the same key"() {
        given:
        def sub = new ConcurrentAddRemoveListInMap<String, Integer>()
        def numIterations = 1000
        def executor = Executors.newFixedThreadPool(2)
        def key = "key"

        when:
        numIterations.times { iteration ->
            sub.add(key, 1)
            def syncBarrier = new CyclicBarrier(3)
            def latch = new CountDownLatch(2)

            // Thread 1: Add element 2
            executor.submit {
                try {
                    syncBarrier.await(5, TimeUnit.SECONDS)
                    sub.add(key, 2)
                } finally {
                    latch.countDown()
                }
            }

            // Thread 2: Remove element 1
            executor.submit {
                try {
                    syncBarrier.await(5, TimeUnit.SECONDS)
                    sub.remove(key, 1)
                } finally {
                    latch.countDown()
                }
            }

            syncBarrier.await(5, TimeUnit.SECONDS)
            latch.await(5, TimeUnit.SECONDS)

            // Verify the result: "key": [2]
            def elements = sub.getElements(key)
            assert elements == [2]

            // Clean up for the next iteration
            sub.remove(key, 2)
        }

        then:
        sub.getElements(key).isEmpty()
        sub.storage.size() == 0

        cleanup:
        executor.shutdown()
        executor.awaitTermination(5, TimeUnit.SECONDS)
    }

    def "should handle concurrent adding and removing elements for the same key"() {
        // adding even numbers, removing odd numbers
        given:
        def sub = new ConcurrentAddRemoveListInMap<String, Integer>()
        def executor = Executors.newFixedThreadPool(2)
        def key = "key"
        def numElements = 10000

        when:
        numElements.times { i ->
            sub.add(key, 2 * i + 1)
        }

        def syncBarrier = new CyclicBarrier(3)
        def latch = new CountDownLatch(2)

        // Thread 1: Add even numbers
        executor.submit {
            try {
                syncBarrier.await(5, TimeUnit.SECONDS)
                numElements.times { i ->
                    sub.add(key, 2 * i)
                }
            } finally {
                latch.countDown()
            }
        }

        // Thread 2: Remove odd numbers
        executor.submit {
            try {
                syncBarrier.await(5, TimeUnit.SECONDS)
                numElements.times { i ->
                    sub.remove(key, 2 * i + 1)
                }
            } finally {
                latch.countDown()
            }
        }

        syncBarrier.await(5, TimeUnit.SECONDS)
        latch.await(5, TimeUnit.SECONDS)

        then:
        def elements = sub.getElements(key)
        elements.size() == numElements
        elements.every { it % 2 == 0 }
        elements.sort() == (0..<numElements).collect { 2 * it }

        cleanup:
        executor.shutdown()
        executor.awaitTermination(5, TimeUnit.SECONDS)
    }

    def "should ensure removal during returned list iteration does not cause errors like ConcurrentModificationException"() {
        given:
        def sub = new ConcurrentAddRemoveListInMap<String, Integer>()
        def numElements = 1_000_000
        def key = "key"
        def syncBarrier = new CyclicBarrier(2)

        numElements.times { i ->
            sub.add(key, i)
        }

        when:
        def executor = Executors.newSingleThreadExecutor()

        executor.submit {
            syncBarrier.await(5, TimeUnit.SECONDS)
            numElements.times { i ->
                sub.remove(key, i)
            }
        }

        def elements = sub.getElements(key)
        syncBarrier.await(5, TimeUnit.SECONDS)

        // Iterate through the elements while they're being removed from the map
        def count = 0
        elements.each { element ->
            count++
        }

        then:
        count == numElements
        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .pollDelay(1, TimeUnit.MILLISECONDS)
                .pollInterval(1, TimeUnit.MILLISECONDS)
                .until(() -> sub.getElements(key).isEmpty())
        sub.storage.size() == 0

        cleanup:
        executor.shutdown()
        executor.awaitTermination(5, TimeUnit.SECONDS)
    }

}
