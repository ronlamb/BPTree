import com.github.ronlamb.bplustree.BPTree;
import com.github.ronlamb.bplustree.Statistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    private static final Logger log = LogManager.getLogger(BPTree.class);

    public static void checkAll(BPTree<Integer, Double> tree, ArrayList<Integer> numbers) {
        int notFound = 0;
        int mismatch = 0;
        for (Integer number : numbers) {
            Double value = tree.search(number);
            Double expected = f(number);
            if (value == null) {
                log.error("Value not found for input {}" , number);
                notFound++;

            } else {
                if (Math.abs(expected - value) >= 0.0000001) {
                    log.error("Value mismatch for {}: expected {} found {}", number, expected, value);
                    mismatch++;
                }
            }
        }

        Statistics stats = tree.getStats();
        if (stats.leafItems != numbers.size()) {
            log.info("Expected " + numbers.size() + " items but found " + stats.leafItems);
        }
        log.info("Mismatches: {}", mismatch);
        log.info("Not Found: {}", notFound);
    }

    static Double f(int x) {
        return 18.0 - x / 100 + 12*x;
    }

    public static List<Integer> orderedList(int start, int end) {
        return Arrays.stream(IntStream.iterate(start, i-> i<=end, i->i+1).toArray()).boxed().collect(Collectors.toList());
    }

    public static void runTest(BPTree<Integer, Double> tree, ArrayList<Integer> numbers) {
        log.info("Testing {} numbers", numbers.size());
        for (Integer number : numbers) {
            Double value = f(number);
            tree.insert(number, value);
        }
        tree.dumpStats();
        checkAll(tree, numbers);
    }

    void runOnce() {
        BPTree<Integer, Double> tree = new BPTree<Integer, Double>(20, 66.666); // approx 68400 leaf nodes, 70 inner, 1300 keys
        //BPTree<Integer, Double> tree = new BPTree<Integer, Double>(50, 66.666); // approx 27400 leaf nodes, 85 inner, 2700 keys
        //BPTree<Integer, Double> tree = new BPTree<Integer, Double>(100, 66.666); // approx 13700 leaf nodes, 120 inner, 7500 keys
        List<Integer> list = orderedList(1, 1000000);
        Collections.shuffle(list);
        ArrayList<Integer> numbers = new ArrayList<>(list);

        log.info("Running testRandomInsert");
        //log.info("Numbers: {}", numbers);
        runTest(tree, numbers);
    }
    public static void main(String[] args) {
        Main app = new Main();
        for (int i = 0; i < 100; i++) {
            app.runOnce();
        }
    }
}
