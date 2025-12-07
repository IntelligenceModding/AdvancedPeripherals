package de.srendi.advancedperipherals.common.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;

    public LRUCache(final int capacity) {
        super(capacity * 2, 1, true); // Should never rehash
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
        return this.size() > this.capacity;
    }
}
