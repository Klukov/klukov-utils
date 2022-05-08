package org.klukov.utils.graphs;

import java.util.Collection;

public interface ParentGivenNodeInput<T> {
    String getId();

    Collection<String> getParentIds();

    T getObject();
}
