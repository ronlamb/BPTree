package com.github.ronlamb.bplustree;

import java.util.Iterator;
import java.util.Map;

public class BPTreeIterator<K extends Comparable<K>, V> implements Iterator<Map.Entry<K, V>> {
    BPTree<K,V> map;
    LeafNode<K,V> currLeaf;
    int leafIndex;
    public BPTreeIterator(BPTree<K, V> map) {
        this.map = map;
        currLeaf = map.firstLeaf;
        leafIndex = 0;
    }

    @Override
    public boolean hasNext() {
        return currLeaf != null;
//        return false;
    }

    @Override
    public Map.Entry<K, V> next() {
        if (currLeaf != null) {
            Map.Entry<K,V> entry = currLeaf.records.get(leafIndex++);

            if (leafIndex == currLeaf.records.size()) {
                leafIndex = 0;
                currLeaf = (LeafNode<K,V>) currLeaf.rightNode;
            }
            return entry;
        }
        return null;
    }
}
