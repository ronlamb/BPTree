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

    @BeforeEach
    void setUp() {
        log.info("setUp");
        tree = new BPTree<Integer, Double>(20, 66.666);
    }

    ArrayList<Node<Integer,Double>> createChildren(ArrayList<Integer> keys) {
        int i = 0;
        ArrayList<Node<Integer, Double>> children = new ArrayList<>();

        for (Integer key : keys) {
            if (i == 0) {
                ArrayList<Integer> subkeys = new ArrayList<>();
                subkeys.add(key-1);
                children.add(new InternalNode<Integer,Double>(tree.getConfig(), subkeys, null));
            }
            ArrayList<Integer> subkeys = new ArrayList<>();
            subkeys.add(key);
            children.add(new InternalNode<Integer,Double>(tree.getConfig(), subkeys, null));
            i++;
        }
        return children;
    }
    @Test
    void testFindChildNode() {
        ArrayList<Integer> keys = new ArrayList<Integer>(Arrays.asList(
                667252, 667430, 667675, 667903, 668172, 668403, 668624, 668845, 669063, 669273, 669550, 669816, 670096, 670371, 670573
        ));
        InternalNode<Integer, Double> node = new InternalNode<Integer, Double>(tree.getConfig(), keys, createChildren(keys));
        InternalNode<Integer,Double> child = (InternalNode<Integer, Double>) node.findChildNode(670371);

        log.info("Found: {}", child);

        assertTrue(child.keys.get(0) == 670371);
    }

    @Test
    void testFindLeafNode_2() {
        ArrayList<Integer> keys = new ArrayList<Integer>(Arrays.asList(
                347847, 347860, 347876, 347893, 347909, 347924, 347937, 347950, 347960, 347974, 347985, 348000, 348015, 348029, 348039, 348057, 348069
        ));
        InternalNode<Integer, Double> node = new InternalNode<Integer, Double>(tree.getConfig(), keys, createChildren(keys));
        InternalNode<Integer,Double> child = (InternalNode<Integer, Double>)  node.findChildNode(347974);
        log.info("Found: {}", child);
        assertTrue(child.keys.get(0) == 347974);
    }

}
