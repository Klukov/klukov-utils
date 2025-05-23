package org.klukov.utils.structures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;
import lombok.NonNull;

/**
 * A thread-safe data structure that maps keys to lists of elements. Optimized for high-frequency
 * add and remove operations. Uses ConcurrentLinkedQueue internally for better performance in
 * concurrent environments.
 */
public class ConcurrentAddRemoveListInMap<K, E> {

    private final Map<K, Collection<E>> storage = new ConcurrentHashMap<>();
    private final Supplier<? extends Collection<E>> collectionSupplier;

    public ConcurrentAddRemoveListInMap() {
        collectionSupplier = ConcurrentLinkedQueue::new;
    }

    public ConcurrentAddRemoveListInMap(Supplier<? extends Collection<E>> collectionSupplier) {
        this.collectionSupplier = collectionSupplier;
    }

    /**
     * Returns a list of elements associated with the given key. If the key is not present, it
     * returns an empty list. The returned list is a copy to ensure thread safety.
     *
     * @param key the key whose associated elements are to be returned
     * @return a list containing the elements associated with the key
     */
    public @NonNull List<E> getElements(@NonNull K key) {
        var collection = storage.get(key);
        if (collection == null) {
            return Collections.emptyList();
        }
        // Return a copy of the queue to ensure thread safety
        return new ArrayList<>(collection);
    }

    /**
     * Adds the specified value to the list associated with the specified key. If the key is not
     * present, creates a new list.
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
