package com.github.ronlamb.bplustree;

import java.util.*;

public class BPTreeMap<K extends Comparable<K>,V> implements Map<K,V> {
    BPTree<K,V> map;

    public BPTreeMap() {
        map = new BPTree<K,V>();
    }

    public BPTreeMap(int branchFactor) {
        map = new BPTree<>(branchFactor);
    }

    public BPTreeMap(int branchFactor, double density) {
        map = new BPTree<>(branchFactor, density);
    }
    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return map.search((K) key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        LeafNode<K,V> curr = map.firstLeaf;
        while (curr != null) {
            for (KeyValue<K,V> rec : curr.records) {
                if (rec.value.equals(value)) {
                    return true;
                }
            }
            curr = (LeafNode<K,V>) curr.rightNode;
        }
        return false;
    }

    @Override
    public V get(Object key) {
        return map.search((K) key);
    }

    @Override
    public V put(K key, V value) {
        V oldval = map.search(key);
        map.insert(key,value);;
        return oldval;
    }

    @Override
    public V remove(Object key) {
        V oldval = map.search((K) key);
        if (oldval != null) {
            map.delete((K) key);
        }
        return oldval;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        Set<? extends Entry<? extends K, ? extends V>> entrySet = m.entrySet();
        for (Entry<? extends K, ? extends V> entry : entrySet ) {
            map.insert(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        Set<K> set = new HashSet<>();
        for (Map.Entry<K,V> entry : entrySet()) {
            set.add(entry.getKey());
        }
        return set;
    }

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K,V>> rval = new BPTreeSet<>(map);
        return rval;
    }
}
