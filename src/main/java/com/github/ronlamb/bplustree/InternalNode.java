package com.github.ronlamb.bplustree;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InternalNode<K extends Comparable<K>, V> extends Node<K,V> {
	@SuppressWarnings("unused")
	private static final Logger log = LogManager.getLogger(InternalNode.class);
	BPTConfig config;
	ArrayList<K> keys;
	ArrayList<Node<K,V>> children;

	public InternalNode(BPTConfig config, ArrayList<K> keys, ArrayList<Node<K,V>> children) {
		this.config = config;
		this.keys = keys;
		this.children = children;
		this.rightNode = null;
		this.leftNode = null;
	}

	public String keysString() {
		String rval = "[";
		boolean first = true;
		for (K key : keys) {
			if (! first ) {
				rval += ", ";
			}
			first = false;
			rval += key;
		}
		return rval + "]";
	}
	
	public InternalNode<K,V> getFirstNode() {
		InternalNode<K,V> first = this;
		
		while (first.leftNode != null) {
			first = (InternalNode<K,V>) first.leftNode;
		}
		return first;
	}
	
	public Node<K,V> dump(int level, int depth) {
		super.dump(level,depth);
		InternalNode<K,V> node;
		int i;

		for (i = 0, node = getFirstNode(); node != null ; node = (InternalNode<K, V>) node.rightNode, i++) {
			System.out.println("keys[" + i + "] = " + node.keysString());
		}

		return children.get(0);
	}
	
	public String toString() {
		return "InternalNode: { keys: " + keys + " , children: " + children + " }";
	}

	public boolean insert(KeyValue<K, V> record, Node<K,V> child) {
		return insert(record.key, child);
	}

	public boolean insert(K key, Node<K,V> child) {
		int i;
		log.debug("Insert Internal");
		log.debug("Key       {}", key);
		log.debug("Child:    {}", child);
		log.debug("keys:     {}", keys);
		log.debug("Children: {}", children);
		// TODO: Do a binary search if keys size > 5
		for (i = 0; i < keys.size(); i++) {
			if (key.compareTo(keys.get(i)) <= 0) {
				log.debug("ins loc:  {}", i);

				keys.add(i,key);
				child.leftNode = children.get(i);
				child.rightNode = children.get(i+1);
				child.rightNode.leftNode = child;
				children.add(i+1, child);
				return keys.size() > config.maxKeys;
			}
		}
		
		keys.add(key);
		Node<K,V> oldRight;
		if (i >= children.size()) {
			oldRight = children.get(i-1);
		} else {
			oldRight = children.get(i);
		}
		child.leftNode = oldRight;

		/* If not a leaf node then remove right node if at end */
		if (!(child instanceof LeafNode<K,V>)) {
			child.rightNode = null;
		}
		oldRight.rightNode = child;
		
		children.add(child);
		return keys.size() > config.maxKeys;
	}
	
	int middle() {
		return (int) Math.ceil((config.branchFactor + 1) / 2.0) - 1;
	}

	/*
	 * Split internal node size = 5
	 *
	 *  Initial:
	 *                        [ 7                  ,  21,                 31,               44                             71 ]
	 *            { 0:*, 3:* } , { 7:*, 18:*, 19:*} , { 21:*, 22:*, 25:* }, {  31:*, 33:* }, {44:*, 45:*, 49:*, 50:*, 66:*}   { 71:*, 73:*, 75:* }
	 *
	 *  Add 57
	 *  Now
	 *                        [ 7                  ,  21,                 31,               44                               71 ]
	 *            { 0:*, 3:* } , { 7:*, 18:*, 19:*} , { 21:*, 22:*, 25:* }, {  31:*, 33:* }, {44:*, 45:*, 49:*, 50:*, 57:*, 66:*}  { 71:*, 73:*, 75:* }
	 *                                                                                               ^^^---- Has oveflowed
	 *  Split Leaf:
	 *                        [ 7                  ,  21,                 31,               44                 (50)              71 ] << overflowed
	 *            { 0:*, 3:* } , { 7:*, 18:*, 19:*} , { 21:*, 22:*, 25:* }, {  31:*, 33:* }, {44:*, 45:*, 49:*} {50:*, 57:*, 66:*}  { 71:*, 73:*, 75:* }
	 *
	 *  We are at this point
	 * Split Inner
	 *                        [ 7                  ,  21,                 31       ]                     [       44                 (50)              71 ] << overflowed
	 *            { 0:*, 3:* } , { 7:*, 18:*, 19:*} , { 21:*, 22:*, 25:* },         {  31:*, 33:* },       {44:*, 45:*, 49:*} {50:*, 57:*, 66:*}  { 71:*, 73:*, 75:* }
	 *
	 * Move right node up
	 *                                                                                    [ 44 ]
	 *                        [ 7                  ,  21,                 31       ]                                   [  50                   71 ]
	 *            { 0:*, 3:* } , { 7:*, 18:*, 19:*} , { 21:*, 22:*, 25:* },         {  31:*, 33:* },   {44:*, 45:*, 49:*}    {50:*, 57:*, 66:*}  { 71:*, 73:*, 75:* }
	 *
	 */
	InternalNode<K,V> split() {
		int mid = middle();
		// Split Inner
		ArrayList<K> rightKeys = new ArrayList<K>(keys.subList(mid, keys.size()));

		keys.subList(mid, keys.size()).clear();
		ArrayList<Node<K,V>> rightChildren = new ArrayList<Node<K,V>>(children.subList(mid+1, children.size()));
		children.subList(mid+1, children.size()).clear();
		InternalNode<K,V> right = new InternalNode<K,V>(config, rightKeys, rightChildren);
		for (Node<K,V> child : rightChildren) {
			// update child parents to right note
			child.parent = right;
		}
		right.leftNode = this;
		right.rightNode = rightNode;
		rightNode= right;
		right.parent = parent;
		return right;
	}

	public int leafIndex(Node<K,V> leaf) {
		// Replace with Binary search if possible
		for (int i = 0; i < children.size(); i++) {
			if (children.get(i) == leaf) {
				return i;
			}
		}
		return -1;
	}

	public int keyIndex(K key) {
		return Collections.binarySearch(keys, key);
	}
}
