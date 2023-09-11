package com.github.ronlamb.bplustree;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class LeafNode<K extends Comparable<K>, V> extends Node<K,V> {
	@SuppressWarnings("unused")
	private static final Logger log = LogManager.getLogger(LeafNode.class);

	ArrayList<KeyValue<K,V>>records;
	BPTConfig config;

	public LeafNode(BPTConfig config, KeyValue<K,V> record) {
		this.config = config;
		records = new ArrayList<>(config.branchFactor);
		records.add(record);
	}

	public LeafNode(BPTConfig config, ArrayList<KeyValue<K,V>> records, InternalNode<K,V> parent) {
		this.config = config;
		this.records = records;
		this.parent = parent;
	}

	public String leafsString() {
		StringBuilder rval = new StringBuilder("{ ");
		boolean first = true;
		for (KeyValue<K,V> record : records) {
			if (!first) {
				rval.append(", ");
			}
			first = false;
			rval.append(record);
		}
		rval.append(" }");
		return rval.toString();
	}

	public LeafNode<K,V> getFirstNode() {
		LeafNode<K,V> first = this;

		while (first.leftNode != null) {
			first = (LeafNode<K,V>) first.leftNode;
		}
		return first;
	}

	public Node<K,V> dump(int level, int depth) {
		super.dump(level, depth);
		int i;
		LeafNode<K,V> record;
		for ( record = getFirstNode() , i = 0 ; record != null; record = (LeafNode<K, V>) record.rightNode, i++) {
			System.out.println("Leafs[" + i + "] = " + record.leafsString());
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public int binarySearch(K key) {
		KeyValue<K,V> temp = KeyValue.tempKeyValue.get();
		temp.key = key;

		Comparator<KeyValue<K,V>> c = new Comparator<>() {
            @Override
            public int compare(KeyValue<K, V> o1, KeyValue<K, V> o2) {
                return o1.compareTo(o2);
            }
        };

		// Keep a thread local variable so that it isn't recreated each time.
		return Collections.binarySearch(records, temp, c);
	}

	@SuppressWarnings("ConstantValue")
	public boolean insert(KeyValue<K, V> record) {
		// Do a binary search to find location
		int index = binarySearch(record.key);
		if (index < 0) {
			index = -(index+1);
		}

		//log.debug("record: {}, insert at index = {}, records: {}", record, index, records);
		if (index == records.size()) {
			records.add(record);
		} else {
			if (records.get(index).key == record.key && !config.allowDuplicates) {
				// Duplicate key so overwrite
				records.set(index, record);
			} else {
				records.add(index, record);
			}
		}

		//log.debug("new records = {}, record: {}", records, records.get(index));
		return records.size() > config.branchFactor;
	}

	public LeafNode<K, V> split() {
		ArrayList<KeyValue<K,V>> rightRecs = new ArrayList<>(records.subList(config.midKeys, records.size()));
		records.subList(config.midKeys , records.size()).clear();
		LeafNode<K,V> right = new LeafNode<>(config, rightRecs, parent);
		right.leftNode = this;
		right.rightNode = rightNode;
		rightNode = right;
		int index = parentIndex;
		LeafNode<K,V> remain = (LeafNode<K, V>) rightNode;
		while (remain != null && remain.parent == parent) {
			index++;
			remain.parentIndex = index;
			remain = (LeafNode<K, V>) remain.rightNode;
		}

		return right;
	}

    public String toString() {
    	StringBuilder rval = new StringBuilder("LeafNode: { ");
    	boolean first = true;
    	for (KeyValue<K,V> pair: records) {
    		if (!first) {
    			rval.append(", ");
    		}
    		first = false;
    		rval.append(pair);
    	}
    	rval.append("}");
    	return rval.toString();
    }
}
