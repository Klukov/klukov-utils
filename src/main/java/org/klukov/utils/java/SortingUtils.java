package org.klukov.utils.java;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import lombok.NonNull;

/**
 * Utility for sorting collections based on a specified key order.
 *
 * <p>Elements are sorted according to the order of the provided keys as determined by the {@code
 * keyExtractor}. If multiple elements share the same key, the {@code sameKeyComparator} is used to
 * sort them; otherwise, their original order is preserved. Any {@code null} elements are placed at
 * the end of the sorted list.
 */
public class SortingUtils {

    public static <E, K> List<E> sort(
            @NonNull List<E> elements,
            @NonNull List<K> keys,
            @NonNull Function<E, K> keyExtractor) {
        return sort(elements, keys, keyExtractor, null);
    }

    public static <E, K> List<E> sort(
            @NonNull List<E> elements,
            @NonNull List<K> keys,
            @NonNull Function<E, K> keyExtractor,
            Comparator<E> sameKeyComparator) {
        if (elements.isEmpty()) {
            return elements;
        }
        var orderMap = createOrderMap(keys);
        var comparator =
                Comparator.<E>comparingInt(
                        e -> orderMap.getOrDefault(keyExtractor.apply(e), Integer.MAX_VALUE));
        if (sameKeyComparator != null) {
            comparator = comparator.thenComparing(sameKeyComparator);
        }
        return elements.stream().sorted(Comparator.nullsLast(comparator)).toList();
    }

    private static <K> Map<K, Integer> createOrderMap(List<K> list) {
        var orderMap = new LinkedHashMap<K, Integer>();
        var index = new AtomicInteger(0);
        list.forEach(e -> orderMap.compute(e, (k, v) -> v == null ? index.getAndIncrement() : v));
        return orderMap;
    }
}
