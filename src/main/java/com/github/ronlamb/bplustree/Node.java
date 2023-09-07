package com.github.ronlamb.bplustree;

public class Node<K extends Comparable<K>, V> {
    InternalNode<K,V> parent;
	int parentIndex;

	Node<K,V> leftNode;
	Node<K,V> rightNode;
	public Node<K,V> dump(int level, int depth) {
		System.out.println("Level: " + level);
		return parent;
	}
}
