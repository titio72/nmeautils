package com.aboni.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ScanThrough<I, K, A> {

    private final KeyExtractor<I, K> extractor;
    private final AggregateCreator<K, A> creator;
    private final AggregateIncrement<I, K, A> increment;

    public ScanThrough(KeyExtractor<I, K> keyExtractor, AggregateCreator<K, A> creator, AggregateIncrement<I, K, A> increment) {
        this.creator = creator;
        this.increment = increment;
        this.extractor = keyExtractor;
        this.theMap = new HashMap<>();
    }


    public interface KeyExtractor<I, K> {
        K getKey(I item);
    }

    public interface AggregateCreator<K, A> {
        A createAggregate(K key);
    }

    public interface AggregateIncrement<I, K, A> {
        A incrementAggregate(I item, K key, A aggregate);
    }

    private final Map<K, A> theMap;

    public void processItem(I item) {
        if (item!=null) {
            K key = extractor.getKey(item);
            A aggregate = theMap.getOrDefault(key, creator.createAggregate(key));
            A aggregateNew = increment.incrementAggregate(item, key, aggregate);
            theMap.put(key, aggregateNew);
        }
    }

    public void process(Collection<I> collection) {
        for (I item: collection) {
            processItem(item);
        }
    }

    public Map<K, A> getResults() {
        return new HashMap<>(theMap);
    }
}
