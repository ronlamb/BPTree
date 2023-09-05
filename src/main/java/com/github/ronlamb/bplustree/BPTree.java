package com.github.ronlamb.bplustree;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Based on descriptions o B+ Tree at
//https://en.wikipedia.org/wiki/B%2B_tree

/**
 * Main BPTree class
 *
 * @param <K>
 * @param <V>
 */
public class BPTree<K extends Comparable<K>,V> {
    private static final Logger log = LogManager.getLogger(BPTree.class);
	private static final double minDensity = 50.0;
	private static final double maxDensity = 90.0;

	BPTConfig config;

    LeafNode<K,V> firstLeaf;
    InternalNode<K,V> root;
    int depth;
	int minLeafSize;

	/**
	 * Constructor: BPTree(int branchFactor)
	 *
	 * Constructs new BPTree object with the given branching factor and a default density of 50%.
	 *
	 * @param branchFactor Branching factor for the tree
	 */
	public BPTree(int branchFactor) {
		initialize(branchFactor, 50.0);
	}

	/**
	 * Constructor BPTree(int branchFactor, double density)
	 *
	 * Constructs new BPTree object with the given branching factor and given density
	 *
	 * @param branchFactor Branching factor for the tree
	 * @param density      Branching factor to use when rebalancing full leaves
	 *                     Allowed range 50.0 to 90.0%
	 *
	 *                     		If < 50 will default to 50
	 *                     		If > 90 will default to 90
	 */
	public BPTree(int branchFactor, double density) {
		initialize(branchFactor, density);
	}

	private void initialize(int branchFactor, double density) {
		if (density < minDensity) {
			density = minDensity;
		}

		if (density > maxDensity) {
			density = maxDensity;
		}
		config = new BPTConfig(branchFactor, density);

		this.firstLeaf = null;
		this.root = null;
		depth = 0;
	}

	public void dump(Node<K,V> node, int level, int depth) {
		Node<K,V> child = node.dump(level, depth);
		if (child != null) {
			dump(child, level+1, depth);
		}
	}
	
	public void dump() {
		log.debug("\nCurrent B+Tree structure");
		System.out.println("DEPTH: " + depth);
		if (root == null) {
			firstLeaf.dump(0, depth);
		} else {
			dump(root, 0, depth);
		}
	}

	/**
	 * LeafNode<K,V> findLeaf(InternalNode<K,V> node, K key)
	 *
	 * Recursively search through the Tree for the appropriate leaf.
	 *
	 * @param node	Current parent node
	 * @param key	Key to find
	 * @return
	 */
	private LeafNode<K,V> findLeaf(InternalNode<K,V> node, K key) {
		ArrayList<K> keys = node.keys;
		int i;
		log.debug("Searching " + node.keys);
		for (i = 0; i < keys.size(); i++) {
			if (key.compareTo(keys.get(i)) < 0) {
				// pointer is less than the current key
				break;
			}
		}

		// At this point the index i either contains rows = the last key checked
		// or > than the number of keys, so it points to the final list.
		log.debug("index: {}, children.size(): {}", i, node.children.size());
		log.debug("node children = {}" , node);
		log.debug("keys: {}", keys);
		Node<K,V> child = node.children.get(i);
		if (child instanceof LeafNode<?,?>) {
			return (LeafNode<K,V>) child;
		} else {
			return findLeaf((InternalNode<K,V>) child, key);
		}
	}
	
	private void updateRoot(Node<K,V> left, Node<K,V> right, K key) {
		ArrayList<K> keys = new ArrayList<K>();
		keys.add(key);
		
		ArrayList<Node<K,V>> children = new ArrayList<Node<K,V>>();
		children.add(left);
		children.add(right);
		InternalNode<K,V> newRoot = new InternalNode<K,V>(config, keys, children);
		right.parent = newRoot;
		left.parent = newRoot;
		root = newRoot;
		depth++;
	}

	public void splitInnerOld(InternalNode<K,V> node) {
		/*
		 * Keys
		 * [              12,              20,              30,              90                (100) ]
		 * [ [0, 1, 2, 3]    [12,14,15,16]    [20,22,23,24]    [30,32,33,34]   [90, 92, 93, 94]      [100,102] ]
		 * 
		 * [                                                 30                                        ]
		 * [               [12,              20]                              [90,                100] ]
		 * [ [0, 1, 2, 3]      [12,14,15,16]    [20,22,23,24]   [30,32,33,34]    [90, 92, 93, 94]      [100,102] ]
		 */
		log.debug("splitInner");
		InternalNode<K,V> rightNode =  node.split();
		if (node == root) {
			updateRoot(node, rightNode, rightNode.keys.get(0));
		} else {
			if (node.parent.insert(rightNode.keys.get(0), rightNode)) {
				splitInnerOld(node.parent);
			}
		}
	}

	public void splitInner(InternalNode<K,V> node) {
		/*
		 * Keys
		 * [                12,               20,              30,              90                || 100  ]
		 * [ [0, 1, 2, 3]      [12,14,15,16]     [20,22,23,24]    [30,32,33,34]   [90, 92, 93, 94]       [100,102] ]
		 *
		 * [                                                      30                                         ]
		 * [                    [12           , 20]                |            [30, 90,                 100] ]
		 * [ [0, 1, 2, 3]           [12,14,15,16]    [20,22,23,24]    [30,32,33,34]   [90, 92, 93, 94]       [100,102] ]
		 */
		InternalNode<K,V> rightNode =  node.split();
		K key = rightNode.keys.remove(0);

		if (node == root) {
			updateRoot(node, rightNode, key);
		} else {
			if (node.parent.insert(key, rightNode)) {
				splitInner(node.parent);
			}
		}
	}


	private void propogateKeyUpwards(InternalNode<K,V> node, K oldKey, K newKey) {
		while (node != null) {
			int index = node.keyIndex(oldKey);
			if (index < 0) {
				node = node.parent;
			} else {
				node.keys.set(index, newKey);
				node = node.parent;
			}
		}
	}

	/*
	 * Move record to left or right leaf
	 *
	 * Returns:
	 * 		True	Rebalance successful
	 * 		False	Rebalance failed
	 *
	 */
	public boolean rebalanceLeaves(LeafNode<K,V> leaf) {
		// Skip rebalance if rebalance flag not set
		if (config.rebalance == false) {
			return false;
		}

		int index = leaf.parent.leafIndex(leaf);
		//log.info("Index: {}",index);
		LeafNode<K,V> rightNode = null;
		LeafNode<K,V> leftNode = null;
		int size = leaf.records.size();
		if (index < size -1) {
			rightNode = (LeafNode<K, V>) leaf.rightNode;
		}
		if (index > 0) {
			leftNode =  (LeafNode<K, V>) leaf.leftNode;
		}

		if (leftNode != null && leftNode.records.size() < config.maxBranchRefactor) {
			/* Move Left single node:
			 *                            [30]
			 *                   [ 20,           30 ]
			 *            [ 10,       20,                30 ]
			 *    [ 1, 3]  [ 10, 19]    [ 20, 22, 23, 27 ]    [ 30, 34, 35 ]  << added, 27
			 *
			 * Becomes:                   [30]
			 *                   [ 22,           30 ]
			 *            [ 10,         22,                30 ]
			 *    [ 1, 3]  [ 10, 19, 20]   [ 22, 23, 27 ]    [ 30, 34, 35 ]  << added, 27
			 */
			/* Get the number of records to copy over to fill left record to 85%
			 * Save the current head key of the left node, then append to the left node
			 * And propagate the new key upwards.
			 */
			int freeSpace = config.maxBranchRefactor - leftNode.records.size();
			K oldKey = leaf.records.get(0).key;
			leftNode.records.addAll(leaf.records.subList(0,freeSpace));
			leaf.records.subList(0,freeSpace).clear();
			K newKey = leaf.records.get(0).key;
			propogateKeyUpwards(leaf.parent, oldKey, newKey);
			return true;
		}

		if (rightNode != null && rightNode.records.size() < config.maxBranchRefactor) {
			/*
			 * MoveRight:
			 *
			 *            [ 10,          20,                   30 ]
			 *    [ 1, 3]  [ 10, 17, 19]    [ 20, 22, 23, 27 ]    [ 30, 34 ]  << added, 27
             *
             * Becomes:
			 *            [ 10,          20,               27 ]
			 *    [ 1, 3]  [ 10, 17, 19]    [ 20, 22, 23]    [ 27, 30, 34 ]  << added, 27
			 *
			 */
			int freeSpace = config.maxBranchRefactor - rightNode.records.size();
			K oldKey = rightNode.records.get(0).key;
			rightNode.records.addAll(0, leaf.records.subList(leaf.records.size() - freeSpace,leaf.records.size()));
			leaf.records.subList(leaf.records.size() - freeSpace,leaf.records.size()).clear();
			K newKey = rightNode.records.get(0).key;
			propogateKeyUpwards(leaf.parent, oldKey, newKey);

			/*
			KeyValue<K,V> record = leaf.records.remove(leaf.records.size()-1);
			K oldKey = rightNode.records.get(0).key;
			K newKey = record.key;
			rightNode.records.add(0, record);
			propogateKeyUpwards(leaf.parent, oldKey, newKey);
			 */
			return true;
		}
		return false;
	}

	public void insert(K key, V value) {
		KeyValue<K,V> record = new KeyValue<K,V>(key,value);
		if (firstLeaf == null) {
			log.debug("Created initial leaf node");
			firstLeaf = new LeafNode<K,V>(config, record);
		} else {
			if (root == null) {
				if (firstLeaf.insert(record)) {
					log.debug("First root Inner Node");
					// root is full
					LeafNode<K,V> right = firstLeaf.split();
					updateRoot(firstLeaf, right, right.records.get(0).key);
				}
			} else {
				LeafNode<K,V> leaf = findLeaf(root, key);
				// Add record to leaf
				if (leaf.insert(record)) {
					if (!rebalanceLeaves(leaf)) {
						LeafNode<K, V> right = leaf.split();
						InternalNode<K, V> parent = leaf.parent;
						if (parent.insert(right.records.get(0).key, right)) {
							splitInner(parent);
						}
					}
				}
			}
		}
	}

	public V search(K key) {
		if (firstLeaf == null) {
			return null;
		}
		LeafNode<K,V> leaf = (root == null) ? firstLeaf : findLeaf(root, key);
		ArrayList<KeyValue<K,V>> records = leaf.records;
		//int index = binarySearch(records, key);
		int index = leaf.binarySearch(key);
		if (index < 0) {
			return null;
		} else {
			return records.get(index).value;
		}
	}
	public Node<K,V> getFirstNode(Node<K,V> node) {
		Node<K,V> first = node;
		while (first.leftNode != null) {
			first = first.leftNode;
		}
	return first;
	}

	public void calcLeafStats(Statistics stats, LeafNode<K,V> leaf) {
		LeafNode<K,V> leafNode = leaf;
		while (leafNode != null) {
			stats.numLeafs++;
			stats.leafItems += leafNode.records.size();
			leafNode = (LeafNode<K, V>) leafNode.rightNode;
		}
	}

	private void calcInnerStats(Statistics stats, InternalNode<K,V> node) {
		InternalNode<K,V> inner = node;
		while (inner != null) {
			stats.numInner++;
			stats.innerItems += inner.keys.size();
			inner = (InternalNode<K, V>) inner.rightNode;
		}
	}

	private void calcStats(Statistics stats, Node<K,V> node) {
		if  (node instanceof InternalNode<K,V>) {
			calcInnerStats(stats, (InternalNode<K,V>) node);
			calcStats(stats, getFirstNode(((InternalNode<K,V>)node).children.get(0)));
		} else {
			calcLeafStats(stats, (LeafNode<K, V>) node);
		}
	}

	public Statistics getStats() {
		Statistics stats = new Statistics();
		stats.depth = depth;
		stats.branchFactor = config.branchFactor;
		if (root == null) {
			calcLeafStats(stats, firstLeaf);
		} else {
			calcStats(stats, root);
		}
		stats.averageLeaf = (stats.leafItems *1.0) / stats.numLeafs;
		stats.averageInner = (stats.innerItems *1.0) / stats.numInner;
		stats.leafDensity = ((stats.averageLeaf / stats.branchFactor) * 10000) / 100;
		stats.leafDensity = ((stats.averageLeaf / stats.branchFactor) * 10000) / 100;
		return stats;
	}
	public void dumpStats() {
		Statistics stats = getStats();
		System.out.print("BPTree: branchFactor: " + config.branchFactor);
		if (config.rebalance) {
			System.out.println(" rebalancing to " + config.maxBranchRefactor);
		} else {
			System.out.println(" no rebalancing");
		}
		System.out.println(" Min Leaf Size: " + config.minLeaves);
		System.out.println("   depth:       " + stats.depth);
		System.out.println("   Inner Nodes: " + stats.numInner + "     keys: " + stats.innerItems + "  average: " + String.format("%.4f",stats.averageInner));
		System.out.println("    Leaf Nodes: " + stats.numLeafs + "  records: " + stats.leafItems + " average: " + String.format("%.4f", stats.averageLeaf));
		System.out.println("  Leaf Density: " + String.format("%.4f", stats.leafDensity));
	}
}
