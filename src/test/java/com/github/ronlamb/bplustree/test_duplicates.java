package com.github.ronlamb.bplustree;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class test_duplicates {
    private static final Logger log = LogManager.getLogger(test_duplicates.class);

    Double f(int x, int i) {
        return 18.0 - x / 100.0 + 12*x + i/100.0;
    }

    void checkAll(BPTree<Integer, Double> tree, ArrayList<Integer> numbers) {
        int i= 0;
        for (Integer number : numbers) {
            Double value = tree.search(number);
            Double expected = f(number, i++);
            assertTrue(value != null, "Value not found for input " + number);
            assertTrue(Math.abs(expected - value) <= 0.0000001, "Value " + expected + " not found for input " + number);
        }
        Statistics stats = tree.getStats();
        assertTrue(stats.leafItems == numbers.size(), "Expected " + numbers.size() + " items but found " + stats.leafItems);
    }

    void runTest(BPTree<Integer, Double> tree, ArrayList<Integer> numbers) {
        log.info("Testing {} numbers", numbers.size());
        int i = 0;
        for (Integer number : numbers) {
            Double value = f(number, i++);
            tree.insert(number, value);
        }
        tree.dump();
        tree.dumpStats();
    }

    @Test
    void test_duplicatesSmall_dont_allow() {
        ArrayList<Integer> numbers = new ArrayList<Integer>(Arrays.asList(
                1, 3 , 7 ,12, 11, 1, 2, 7, 17, 13, 4, 3, 1 ,7, 4, 3, 2, 3, 3, 3, 23
        ));
        BPTree<Integer, Double> tree = new BPTree<Integer, Double>(5);
        log.info("Running testUnorderedInsert");
        runTest(tree, numbers);
    }
}
