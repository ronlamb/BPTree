package com.github.ronlamb.bplustree;

import java.util.*;

public class BPTreeSet<K extends Comparable<K>,V> implements Set<Map.Entry<K, V>> {
    BPTree<K,V> map;

    public BPTreeSet(BPTree<K, V> map) {
        this.map = map;
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
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        return new BPTreeIterator<K,V>(map);
    }

    @Override
    public Object[] toArray() {
        Object arr[] = new Object[map.size()];

        Iterator<Map.Entry<K,V>> it = iterator();
        int i = 0;
        while (it.hasNext()) {
            arr[i++] = it.next();
        }

        return arr;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        List<T> arr = new ArrayList<T>();

        Iterator<Map.Entry<K,V>> it = iterator();
        int i = 0;
        while (it.hasNext()) {
            arr.add((T) it.next());
        }

        return arr.toArray(a);
    }

    @Override
    public boolean add(Map.Entry<K, V> kvEntry) {
        return false;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Map.Entry<K, V>> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {

    }
}
