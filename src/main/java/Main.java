import com.github.ronlamb.bplustree.BPTree;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger log = LogManager.getLogger(BPTree.class);

    public static void checkAll(BPTree<Integer, Double> tree, int max) {
        for (int i = 1 ; i <= max; i++) {
            Double value = tree.search(i);
            Double expected = f(i);
            if (value == null) {
                log.error("Value mismatch: i:{} found {} expected {}", i,value,expected);
            } else {
                if (Math.abs(expected - value) > 0.0000001) {
                    log.error("Value mismatch: i:{} found {} expected {}", i, value, expected);
                }
            }
        }
    }

    static Double f(int x) {
        return 18.0 - x / 100 + 12*x;
    }

    public static void main(String[] args) {
        BPTree<Integer, Double> tree = new BPTree<Integer, Double>(25, 75.0);
        log.info("Start");
        int max = 1000;
        for (int i = 1; i <= max; i++) {
            Double value = f(i);
            tree.insert(i, value);
        }
        log.info("BPTree: {}", tree);
        Main.checkAll(tree, max);
        tree.dumpStats();
    }

}
