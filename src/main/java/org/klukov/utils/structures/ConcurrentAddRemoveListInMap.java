package org.klukov.utils.structures;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import lombok.NonNull;

/**
 * A thread-safe data structure that maps keys to collections of elements with concurrent add and
 * remove operations.
 *
 * <p>This class provides a concurrent map where each key is associated with a collection of
 * elements. It supports thread-safe operations for adding and removing elements, as well as
 * retrieving snapshots of the collections. When a collection becomes empty, its key is
 * automatically removed from the map.
 *
 * <p>Two factory methods are provided with different concurrency characteristics:
 *
 * <ul>
 *   <li>{@link #highConcurrentListInMap()} - Optimized for high throughput with potential
 *       consistency trade-offs
 *   <li>{@link #consistentConcurrentListInMap()} - Guarantees complete consistency with potential
 *       performance trade-offs
 * </ul>
 *
 * <p>The implementation ensures that retrieving elements always returns a snapshot of the
 * collection, which prevents {@code ConcurrentModificationException} when iterating over the
 * returned list while concurrent modifications are happening.
 *
 * @param <K> the type of keys maintained by this map
 * @param <E> the type of elements in the collections
 */
public class ConcurrentAddRemoveListInMap<K, E> {

    private final Map<K, Collection<E>> storage = new ConcurrentHashMap<>();
    private final Supplier<? extends Collection<E>> collectionSupplier;

    public ConcurrentAddRemoveListInMap(Supplier<? extends Collection<E>> collectionSupplier) {
        this.collectionSupplier = collectionSupplier;
    }

    /**
     * Creates a ConcurrentAddRemoveListInMap optimized for high concurrent throughput.
     *
     * <p>This implementation uses ConcurrentLinkedQueue as the underlying collection.
     *
     * <p>Note: There is a trade-off with consistency. During concurrent operations, it's possible
     * that when calling getCopyOfElements(K key) while two deletions occur, the result may
     * represent a state that never actually existed in the collection. This happens because the
     * first element might be removed from the internal collection but copied during iteration,
     * while the second element might be deleted before being copied to the result list.
     *
     * @param <K> the type of keys maintained by this map
     * @param <E> the type of elements in the lists
     * @return a new ConcurrentAddRemoveListInMap instance optimized for high concurrency
     */
    public static <K, E> ConcurrentAddRemoveListInMap<K, E> highConcurrentListInMap() {
        return new ConcurrentAddRemoveListInMap<>(ConcurrentLinkedQueue::new);
    }

    /**
     * Creates a ConcurrentAddRemoveListInMap that guarantees 100% consistency in concurrent
     * operations.
     *
     * <p>This implementation uses CopyOnWriteArrayList as the underlying collection.
     *
     * <p>Note: Long lists for the same map key can cause increased processing times for add/remove
     * operations. This implementation should only be used when complete consistency is necessary.
     * Unlike the {@link #highConcurrentListInMap()}, this implementation does not suffer from the
     * consistency issues where getCopyOfElements might return a state that never existed.
     *
     * @param <K> the type of keys maintained by this map
     * @param <E> the type of elements in the lists
     * @return a new ConcurrentAddRemoveListInMap instance with guaranteed consistency
     */
    public static <K, E> ConcurrentAddRemoveListInMap<K, E> consistentConcurrentListInMap() {
        return new ConcurrentAddRemoveListInMap<>(CopyOnWriteArrayList::new);
    }

    /**
     * Returns immutable list of elements associated with the given key. If the key is not present,
     * it returns an empty list. The returned list is a copy to ensure thread safety.
     *
     * @param key the key whose associated elements are to be returned
     * @return immutable list containing the elements associated with the key
     */
    public @NonNull List<E> getCopyOfElements(@NonNull K key) {
        var collection = storage.get(key);
        if (collection == null) {
            return Collections.emptyList();
        }
        // Return an immutable copy of the collection to ensure thread safety
        return List.copyOf(collection);
    }

    /**
     * Adds the specified value to the list associated with the specified key. If the key is not
     * present, create a new list.
     *
     * @param key the key with which the specified value is to be associated
     * @param value the value to be added to the list associated with the key
     */
    public void add(@NonNull K key, @NonNull E value) {
        storage.compute(
                key,
                (k, collection) -> {
                    if (collection == null) {
                        collection = collectionSupplier.get();
                    }
                    collection.add(value);
                    return collection;
                });
    }

    /**
     * Removes the specified value from the list associated with the specified key. If the list
     * becomes empty after removal, the key is removed from the map.
     *
     * @param key the key with which the specified value is associated
     * @param value the value to be removed from the list associated with the key
     */
    public void remove(@NonNull K key, @NonNull E value) {
        storage.computeIfPresent(
                key,
                (k, collection) -> {
                    collection.remove(value);
                    return collection.isEmpty() ? null : collection;
                });
    }
}
