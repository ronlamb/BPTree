package com.github.ronlamb.bplustree;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class test_find_leaf {
    private static final Logger log = LogManager.getLogger(test_find_leaf.class);

    BPTree<Integer, Double> tree;
//    InternalNode<K,V> node;
    @BeforeEach
    void setUp() {
        log.info("setUp");
        tree = new BPTree<Integer, Double>(20, 66.666);
    }
    @Test
    void testFindLeafNode_Equals() {
        ArrayList<Integer> keys = new ArrayList<Integer>(Arrays.asList(
                667252, 667430, 667675, 667903, 668172, 668403, 668624, 668845, 669063, 669273, 669550, 669816, 670096, 670371, 670573
        ));
        InternalNode<Integer, Double> node = new InternalNode<Integer, Double>(tree.getConfig(), keys, null);
        int prevKey = node.findPrevKey(670371);
        assertTrue(prevKey == 14);
        log.info("Found: {}", prevKey);
    }

    @Test
    void testFindLeafNode() {
        ArrayList<Integer> keys = new ArrayList<Integer>(Arrays.asList(
                347847, 347860, 347876, 347893, 347909, 347924, 347937, 347950, 347960, 347974, 347985, 348000, 348015, 348029, 348039, 348057, 348069
        ));
        InternalNode<Integer, Double> node = new InternalNode<Integer, Double>(tree.getConfig(), keys, null);
        int prevKey = node.findPrevKey(347974);
        log.info("Found: {}", prevKey);
        assertTrue(prevKey == 10);
    }

}
