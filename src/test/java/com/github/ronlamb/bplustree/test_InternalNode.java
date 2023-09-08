package com.github.ronlamb.bplustree;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class test_InternalNode {
    private static final Logger log = LogManager.getLogger(test_InternalNode.class);

    BPTree<Integer, Double> tree;
    BPTConfig config;
    ArrayList<Node<Integer, Double>> children;
    ArrayList<Integer> keys;
    LeafNode<Integer, Double> leaf0;
    LeafNode<Integer, Double> leaf1;
    LeafNode<Integer, Double> leaf2;

    InternalNode<Integer, Double> node;

    @BeforeEach
    void setUp() {
        log.info("setUp");
        tree = new BPTree<Integer, Double>(5);
        BPTConfig config = tree.getConfig();

        /* Define initial list */
        keys = new ArrayList<Integer>(Arrays.asList(
                10, 21
        ));
        ArrayList<KeyValue<Integer, Double>> arr0 = new ArrayList<>(Arrays.asList(
                new KeyValue<Integer, Double>(1,1.9), new KeyValue<Integer,Double>(2, 2.7)
        ));
        ArrayList<KeyValue<Integer, Double>> arr1 = new ArrayList<>(Arrays.asList(
                new KeyValue<Integer, Double>(10, 9.8), new KeyValue<Integer,Double>(12, 9.9)
        ));
        ArrayList<KeyValue<Integer, Double>> arr2 = new ArrayList<>(Arrays.asList(
                new KeyValue<Integer, Double>(21,21.12), new KeyValue<Integer,Double>(22, 22.12)
        ));

        leaf0 = new LeafNode<>(config, arr0, null);
        leaf0.parentIndex = 0;
        leaf1 = new LeafNode<>(config, arr1, null);
        leaf0.rightNode = leaf1;
        leaf1.leftNode = leaf0;
        leaf1.parentIndex = 1;
        leaf2 = new LeafNode<>(config, arr2, null);
        leaf1.rightNode = leaf2;
        leaf2.leftNode = leaf1;
        leaf2.parentIndex = 2;
        children = new ArrayList<>();

        children.add(leaf0);
        children.add(leaf1);
        children.add(leaf2);
        node = new InternalNode<Integer, Double>(tree.getConfig(), keys, children);
        leaf0.parent =node;
        leaf1.parent = node;
        leaf2.parent = node;
    }

    boolean checkNodesPtrs(Node<Integer, Double> firstLeft, Node<Integer,Double> lastRight) {
        int testsFailed = 0;
        log.info("Children.size() = {}", children.size());
        for (int i = 0; i < children.size(); i++) {
            Node<Integer, Double> child = children.get(i);
            if (child.parentIndex != i) {
                log.info("Mismatch in parentIndex: i: {} != parentIndex: {}", i , child.parentIndex);
                testsFailed++;
            }
            if (i == 0) {
                if (child.leftNode != firstLeft) {
                    log.info("First leaf not correct");
                    testsFailed++;
                }
            } else {
                if (child.leftNode != children.get(i-1)) {
                    log.info("Index {} leftNode not previous node", i);
                    testsFailed++;
                }
            }
            if (i == (children.size() - 1)) {
                if (child.rightNode != lastRight) {
                    log.info("Last leaf not correct");
                    testsFailed++;
                }
            } else {
                if (child.rightNode != children.get(i+1)) {
                    log.info("Index {} rightNode not next node", i);
                    testsFailed++;
                }
            }
        }
        if (testsFailed > 0) {
            log.info("Tests failed: {}", testsFailed);
            return false;
        }
        log.info("Check node ptrs success");
        return true;
    }

    @Test
    void test_insertKeyChild_childLeaf_First() {
        ArrayList<KeyValue<Integer, Double>> arr0_1 = new ArrayList<>(Arrays.asList(
                new KeyValue<Integer, Double>(4,3.6), new KeyValue<Integer,Double>(5, 4.7)
        ));

        LeafNode<Integer, Double> leaf0_1 = new LeafNode<>(config, arr0_1, null);
        leaf0_1.leftNode = null;
        leaf0_1.rightNode = leaf1;
        ArrayList<Node<Integer, Double>> children = new ArrayList<>();
        //leaf2_1.parent = node;

        node.insertKeyChild(0, 4, leaf0_1);
        log.info("Added leaf0_1");

        boolean nodePtrsCheck = checkNodesPtrs(null,null);
    }

    @Test
    void test_insertKeyChild_childLeaf_Middle() {
        ArrayList<KeyValue<Integer, Double>> arr1_1 = new ArrayList<>(Arrays.asList(
                new KeyValue<Integer, Double>(14,13.12), new KeyValue<Integer,Double>(15, 13.12)
        ));

        LeafNode<Integer, Double> leaf1_1 = new LeafNode<>(config, arr1_1, null);
        leaf1_1.leftNode = leaf1;
        leaf1_1.rightNode = leaf2;
        ArrayList<Node<Integer, Double>> children = new ArrayList<>();
        //leaf2_1.parent = node;

        node.insertKeyChild(1, 14, leaf1_1);
        log.info("Added leaf1_1");

        boolean nodePtrsCheck = checkNodesPtrs(null,null);
    }

    @Test
    void test_insertKeyChild_childLeaf_End() {
        ArrayList<KeyValue<Integer, Double>> arr2_1 = new ArrayList<>(Arrays.asList(
                new KeyValue<Integer, Double>(44,47.12), new KeyValue<Integer,Double>(45, 48.2)
        ));

        ArrayList<KeyValue<Integer, Double>> arr3_1 = new ArrayList<>(Arrays.asList(
                new KeyValue<Integer, Double>(69,87.12), new KeyValue<Integer,Double>(71, 77.2)
        ));

        LeafNode<Integer, Double> leaf2_1 = new LeafNode<>(config, arr2_1, null);
        LeafNode<Integer, Double> leaf3_1 = new LeafNode<>(config, arr3_1, null);
        leaf2_1.leftNode = leaf2;
        leaf2_1.rightNode = leaf3_1;
        ArrayList<Node<Integer, Double>> children = new ArrayList<>();
        //leaf2_1.parent = node;

        node.insertKeyChild(2, 44, leaf2_1);
        log.info("Added leaf2_1");

        boolean nodePtrsCheck = checkNodesPtrs(null,leaf3_1);
    }
}
