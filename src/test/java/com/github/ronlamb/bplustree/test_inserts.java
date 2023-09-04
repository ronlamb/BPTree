package com.github.ronlamb.bplustree;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class test_inserts {
    private static final Logger log = LogManager.getLogger(test_inserts.class);
    Double f(int x) {
        return 18.0 - x / 100 + 12*x;
    }

    void checkAll(BPTree<Integer, Double> tree, ArrayList<Integer> numbers) {
        for (Integer number : numbers) {
            Double value = tree.search(number);
            Double expected = f(number);
            assertTrue(value != null, "Value not found for input " + number);
            assertTrue(Math.abs(expected - value) <= 0.0000001, "Value " + expected + " not found for input " + number);
        }
    }

    public ArrayList<Integer> createOrderedList(int start, int stop) {
        ArrayList<Integer> numbers = new ArrayList<>();
        for (int i = start; i <= stop ; i++) {
            numbers.add(i);
        }
        return numbers;
    }

    void runTest(BPTree<Integer, Double> tree, ArrayList<Integer> numbers) {
        for (Integer number : numbers) {
            Double value = f(number);
            tree.insert(number, value);
        }
        checkAll(tree, numbers);
        tree.dumpStats();
    }
    @Test
    void testOrderedInsert() {
        BPTree<Integer, Double> tree = new BPTree<Integer, Double>(25, 75.0);
        ArrayList<Integer> numbers = createOrderedList(1, 10000);
        log.info("Running testOrderedInsert");
        runTest(tree, numbers);
    }

    @Test
    void testUnorderedInsert() {
        ArrayList<Integer> numbers = new ArrayList<Integer>(Arrays.asList(
                    10, -1, 20, 22, 30, 4, 5, 40, -7, 12, 13, 17, 21
                ));
        BPTree<Integer, Double> tree = new BPTree<Integer, Double>(5);
        log.info("Running testUnorderedInsert");
        runTest(tree, numbers);
    }

    List<Integer> orderedList(int start, int end) {
        return Arrays.stream(IntStream.iterate(1, i-> i<=end, i->i+1).toArray()).boxed().collect(Collectors.toList());
    }
    @Test
    void testRandomInsert() {
        BPTree<Integer, Double> tree = new BPTree<Integer, Double>(25, 75.0);
        List<Integer> list = orderedList(1, 10000000);
        Collections.shuffle(list);
        ArrayList<Integer> numbers = new ArrayList<>(list);
        //log.info("Numbers: {}", numbers);
        log.info("Running testRandomInsert");
        runTest(tree, numbers);
    }

}
