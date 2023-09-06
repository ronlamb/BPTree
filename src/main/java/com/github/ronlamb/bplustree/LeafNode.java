package com.github.ronlamb.bplustree;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.Key;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class LeafNode<K extends Comparable<K>, V> extends Node<K,V> {
	private static final Logger log = LogManager.getLogger(LeafNode.class);

	ArrayList<KeyValue<K,V>>records;
	BPTConfig config;

	public LeafNode(BPTConfig config, KeyValue<K,V> record) {
		this.config = config;
		records = new ArrayList<KeyValue<K,V>>();
		records.add(record);
	}

	public LeafNode(BPTConfig config, ArrayList<KeyValue<K,V>> records, InternalNode<K,V> parent) {
		this.config = config;
		this.records = records;
		this.parent = parent;
	}

	public String leafsString() {
		String rval = "{ ";
		boolean first = true;
		for (KeyValue<K,V> record : records) {
			if (!first) {
				rval += ", ";
			}
			first = false;
			rval += record;
		}
		return rval + " }";
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
		LeafNode<K,V> first;
		for ( first =getFirstNode() , i = 0 ; first != null; first = (LeafNode<K, V>) first.rightNode, i++) {
			System.out.println("Leafs[" + i + "] = " + leafsString());
		}

		return null;
	}

	public int binarySearch(K key) {
		KeyValue<K,V> temp = KeyValue.tempKeyValue.get();
		temp.key = key;

		Comparator<KeyValue<K,V>> c = new Comparator<KeyValue<K,V>>() {
			@Override
			public int compare(KeyValue<K, V> o1, KeyValue<K, V> o2) {
				return o1.compareTo(o2);
			}
		};

		// Keep a thread local variable so that it isn't recreated each time.
		return Collections.binarySearch(records, temp, c);
	}

	public boolean insert(KeyValue<K, V> record) {
		// Do a binary search to find location
		int index = binarySearch(record.key);
		if (index < 0) {
			index = -(index+1);
			if (index == -1) {
				index = 0;
			}
		}

		//log.debug("record: {}, insert at index = {}, records: {}", record, index, records);
		if (index == records.size()) {
			records.add(record);
		} else {
			records.add(index,record);
		}

		//log.debug("new records = {}, record: {}", records, records.get(index));
		return records.size() > config.branchFactor;
	}

	int middle() {
		return (int) Math.ceil((config.branchFactor + 1) / 2.0);
	}

	public LeafNode<K, V> split() {
		int mid = middle();

		ArrayList<KeyValue<K,V>> rightRecs = new ArrayList<KeyValue<K,V>>(records.subList(mid, records.size()));
		records.subList(mid , records.size()).clear();
		LeafNode<K,V> right = new LeafNode<K,V>(config, rightRecs, parent);
		right.leftNode = this;
		right.rightNode = rightNode;
		rightNode = right;
		return right;
	}

    public String toString() {
    	String rval = "LeafNode: { ";
    	boolean first = true;
    	for (KeyValue<K,V> pair: records) {
    		if (!first) {
    			rval = rval + ", ";
    		}
    		first = false;
    		rval = rval + pair;
    	}
    	rval += "}";
    	return rval;
    }
}
