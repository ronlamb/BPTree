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

	public void insertKeyChild(int index, K key, Node<K,V> child) {
		// TODO: do linear insert if keys.size <= 5 or so

		if (index == keys.size()) {
			keys.add(key);
			children.add(child);
			/*
			 * Pointer child.rightNode is already set in the Node split routine.
			 * Therefore no need to copy when the last node. However, if the child
			 * is a LeafNode and is the last item in children then set rightnode to null
			 */
			if (!(child instanceof LeafNode<K, V>)) {
				child.rightNode = null;
			}
		} else {
			keys.add(index,key);
			children.add(index+1, child);
			child.rightNode.leftNode = child;
		}
		child.leftNode = children.get(index);
		child.leftNode.rightNode = child;

		for (int i = index+1; i < children.size(); i++) {
			children.get(i).parentIndex = i;
		}
	}
	public boolean insert(K key, Node<K,V> child) {
		int i;
		/*
		log.debug("Insert Internal");
		log.debug("Key       {}", key);
		log.debug("Child:    {}", child);
		log.debug("keys:     {}", keys);
		log.debug("Children: {}", children);
		 */
		// TODO: If size <= 6 do a sequential search
		int index = Collections.binarySearch(keys, key);
		if (index < 0) {
			index = -(index+1);
		}
		insertKeyChild(index, key, child);

		return keys.size() > config.maxKeys;
	}

	/**
	 * Linear version of insert
	 *
	 * Future use: Call when keys.size() <= 6
	 *
	 * @param key
	 * @param child
	 * @return
	 */
	public boolean insertLinear(K key, Node<K,V> child) {
		int i;
		/*
		log.debug("Insert Internal");
		log.debug("Key       {}", key);
		log.debug("Child:    {}", child);
		log.debug("keys:     {}", keys);
		log.debug("Children: {}", children);
		 */
		// TODO: Replace with binary search if keys.size() > 5
		boolean found = false;
		for (i = 0; i < keys.size(); i++) {
			if (key.compareTo(keys.get(i)) <= 0) {
				found = true;
				//log.debug("ins loc:  {}", i);
				children.get(i).parentIndex = i;
				keys.add(i,key);
				child.leftNode = children.get(i);
				child.rightNode = children.get(i+1);
				child.rightNode.leftNode = child;
				children.add(i+1, child);
			}
		}

		if (found) {
			for (; i < children.size(); i++) {
				children.get(i).parentIndex = i;
			}
			return keys.size() > config.maxKeys;
		}

		keys.add(key);
		Node<K,V> oldRight;
		child.parentIndex = i;
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
		right.parent = parent;
		rightNode=right;
		if (children.get(0) instanceof LeafNode<K,V>) {
			resetParentIndex(this);
			resetParentIndex(right);
		}
		return right;
	}

	private void resetParentIndex(InternalNode<K,V> right) {
		int i = 0;
		for (Node<K,V> child: right.children) {
			child.parentIndex = i++;
		}
	}

	public int findPrevKey(K key) {
		int index = Collections.binarySearch(keys, key);
		if (index < 0) {
			index = -(index+1);
			if (index == -1) {
				index = 0;
			}
		} else {
			return index+1;
		}
		return index;
		/*
		 * Note: May want to check size and if less than say 10 run the following comparison
		int i;
		for (i = 0; i < keys.size(); i++) {
			if (key.compareTo(keys.get(i)) < 0) {
				// pointer is less than the current key
				break;
			}
		}
		return i;
		 */

	}
	public int keyIndex(K key) {
		return Collections.binarySearch(keys, key);
	}
}
