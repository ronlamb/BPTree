package com.github.ronlamb.bplustree;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class test_inserts {
    private static final Logger log = LogManager.getLogger(test_inserts.class);
    Double f(int x) {
        return 18.0 - x / 100 + 12*x;
    }

    void checkAll(BPTree<Integer, Double> tree, ArrayList<Integer> numbers) {
        for (Integer number : numbers) {
            Double value = tree.search(number);
            Double expected = f(number);
            assertNotNull(value, "Value not found for input " + number);
            assertTrue(Math.abs(expected - value) <= 0.0000001, "Value " + expected + " not found for input " + number);
        }
        Statistics stats = tree.getStats();
        assertEquals(stats.leafItems, numbers.size(), "Expected " + numbers.size() + " items but found " + stats.leafItems);
    }

    List<Integer> orderedList(int start, int end) {
        return Arrays.stream(IntStream.iterate(start, i-> i<=end, i->i+1).toArray()).boxed().collect(Collectors.toList());
    }

    void runTest(BPTree<Integer, Double> tree, ArrayList<Integer> numbers) {
        log.info("Testing {} numbers", numbers.size());
        for (Integer number : numbers) {
            Double value = f(number);
            tree.insert(number, value);
        }
        tree.dumpStats();
        checkAll(tree, numbers);
    }

    //@Test
    //Disabled temporarily to see if I can reduce the size
    /**
     * Unit test pulled from testRandomInsert that failed insert.
     */
    void testUnorderedInsert_regression_1() {
        ArrayList<Integer> numbers = new ArrayList<>(Arrays.asList(
                314, 150, 468, 493, 924, 541, 193, 211, 160, 223, 214, 47, 950, 760, 127, 925, 509, 265, 902, 520, 516, 306, 186, 387,
                630, 456, 652, 829, 143, 206, 729, 870, 575, 576, 97, 230, 884, 123, 268, 453, 249, 875, 713, 295, 399, 320, 682, 589,
                538, 407, 960, 955, 601, 603, 625, 129, 419, 299, 744, 753, 31, 863, 558, 853, 560, 789, 307, 1, 341, 787, 825, 663,
                508, 974, 790, 35, 12, 937, 867, 710, 693, 821, 498, 808, 416, 203, 573, 552, 121, 929, 288, 283, 932, 425, 37, 848,
                310, 549, 803, 243, 571, 502, 843, 330, 669, 254, 827, 557, 951, 756, 290, 21, 215, 764, 257, 594, 887, 220, 743, 622,
                714, 1000, 259, 483, 728, 103, 922, 945, 850, 545, 912, 537, 475, 511, 806, 971, 93, 375, 730, 812, 736, 424, 297, 374,
                930, 543, 830, 595, 200, 224, 427, 865, 598, 183, 906, 581, 514, 532, 868, 157, 724, 74, 953, 961, 688, 963, 530, 8,
                481, 258, 110, 775, 391, 357, 935, 822, 58, 895, 733, 434, 791, 765, 445, 293, 525, 175, 138, 61, 726, 957, 723, 715,
                405, 577, 943, 861, 92, 4, 585, 10, 115, 628, 692, 738, 670, 698, 964, 166, 529, 979, 212, 804, 886, 611, 222, 156,
                604, 285, 949, 653, 68, 452, 716, 362, 835, 832, 597, 231, 173, 267, 694, 116, 279, 917, 995, 992, 81, 739, 664, 432,
                578, 592, 478, 201, 633, 99, 431, 280, 17, 593, 782, 727, 536, 857, 66, 126, 287, 572, 667, 699, 826, 147, 799, 722,
                44, 85, 709, 389, 639, 286, 810, 778, 372, 292, 95, 763, 646, 380, 467, 36, 487, 355, 983, 712, 105, 512, 410, 266,
                919, 59, 340, 647, 645, 691, 859, 469, 500, 980, 139, 334, 526, 141, 788, 587, 28, 225, 690, 528, 535, 965, 702, 680,
                344, 946, 16, 350, 750, 38, 795, 954, 188, 217, 596, 404, 155, 970, 135, 524, 76, 343, 492, 171, 321, 637, 986, 996,
                539, 649, 499, 683, 885, 973, 174, 49, 768, 600, 333, 337, 180, 523, 202, 296, 828, 759, 132, 602, 300, 6, 888, 661,
                989, 968, 294, 856, 82, 418, 347, 677, 551, 748, 697, 842, 474, 325, 338, 889, 117, 640, 507, 484, 815, 402, 191, 11,
                617, 246, 610, 877, 394, 455, 136, 657, 284, 100, 505, 161, 872, 485, 796, 118, 732, 745, 689, 564, 450, 802, 45, 329,
                582, 312, 903, 388, 876, 860, 851, 676, 282, 711, 46, 250, 777, 429, 819, 144, 71, 466, 89, 163, 651, 458, 900, 818, 944,
                346, 503, 41, 874, 69, 833, 195, 133, 176, 361, 189, 672, 771, 7, 134, 550, 847, 866, 687, 128, 159, 207, 605, 858, 704,
                244, 933, 270, 443, 931, 620, 412, 335, 319, 273, 148, 83, 614, 421, 241, 816, 497, 154, 721, 298, 948, 53, 781, 766,
                304, 25, 624, 678, 398, 301, 632, 700, 411, 731, 149, 185, 108, 881, 910, 488, 891, 655, 823, 153, 618, 914, 658, 814,
                665, 3, 623, 769, 239, 719, 229, 556, 813, 985, 654, 544
        ));
        BPTree<Integer, Double> tree = new BPTree<>(20, 66.666);
        log.info("Running testUnorderedInsert");
        runTest(tree, numbers);
    }


    /**
     * Simple unit test that checks unordered inserts of positive and negative
     * With some consecutive numbers
     */
    @Test
    void testUnorderedInsert_regression_2() {
        ArrayList<Integer> numbers = new ArrayList<>(Arrays.asList(
                10, -1, 20, 22, 30, 4, 5, 40, -7, 12, 13, 17, 21
        ));
        BPTree<Integer, Double> tree = new BPTree<>(5);
        log.info("Running testUnorderedInsert");
        runTest(tree, numbers);
    }

    @Test
    void testRandomInsert() {
        BPTree<Integer, Double> tree = new BPTree<>(20, 66.666);
        List<Integer> list = orderedList(1, 1000000);
        Collections.shuffle(list);
        ArrayList<Integer> numbers = new ArrayList<>(list);

        log.info("Running testRandomInsert");
        //log.info("Numbers: {}", numbers);
        runTest(tree, numbers);
    }
}
