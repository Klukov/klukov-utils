package org.klukov.utils.graphs.relation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.klukov.utils.graphs.common.GraphEdge;

class BidirectionalRelationSolver<ID> {

    private final Map<Integer, Set<ID>> edgeBlocks;
    private final Map<ID, Integer> idToBlockMap;
    private Integer blockIterator = 0;

    public <E extends GraphEdge<ID>> BidirectionalRelationSolver(Collection<E> edges) {
        this.edgeBlocks = new HashMap<>();
        this.idToBlockMap = new HashMap<>();
        edges.forEach(this::processEdge);
    }

    private void processEdge(GraphEdge<ID> edge) {
        var parent = edge.getParentId();
        var child = edge.getChildId();
        var parentBlockNumber = idToBlockMap.get(parent);
        var childBlockNumber = idToBlockMap.get(child);
        if (parentBlockNumber == null && childBlockNumber == null) {
            createNewBlock(parent, child);
        } else if (parentBlockNumber == null) {
            addIdToBlock(parent, childBlockNumber);
        } else if (childBlockNumber == null) {
            addIdToBlock(child, parentBlockNumber);
        } else {
            mergeBlocks(parentBlockNumber, childBlockNumber);
        }
    }

    private void createNewBlock(ID parent, ID child) {
        var blockNumber = blockIterator++;
        edgeBlocks.put(blockNumber, new HashSet<>(List.of(parent, child)));
        idToBlockMap.put(parent, blockNumber);
        idToBlockMap.put(child, blockNumber);
    }

    private void addIdToBlock(ID newId, Integer blockNumber) {
        edgeBlocks.get(blockNumber).add(newId);
        idToBlockMap.put(newId, blockNumber);
    }

    private void mergeBlocks(Integer parentBlockNumber, Integer childBlockNumber) {
        var parentBlockIds = edgeBlocks.get(parentBlockNumber);
        var childBlockIds = edgeBlocks.get(childBlockNumber);
        if (parentBlockIds.size() < childBlockIds.size()) {
            childBlockIds.addAll(parentBlockIds);
            parentBlockIds.forEach(id -> idToBlockMap.put(id, childBlockNumber));
        } else {
            parentBlockIds.addAll(childBlockIds);
            childBlockIds.forEach(id -> idToBlockMap.put(id, parentBlockNumber));
        }
    }

    public Set<ID> getRelatedIds(ID startId) {
        return Optional.ofNullable(startId)
                .map(idToBlockMap::get)
                .map(edgeBlocks::get)
                .orElse(Collections.emptySet());
    }
}
