package com.github.ronlamb.bplustree;

import com.github.ronlamb.perf.Category;
import com.github.ronlamb.perf.CategoryKey;
import com.github.ronlamb.perf.CarInfo;
import com.github.ronlamb.perf.Quality;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This performance test is the original reason the BPTree class was created.
 * One of the programs I wrote originally was to load several thousand rows
 * From a database and perform multiple transforms on the data and write it
 * before writing.  The initial version I tried both a HashMap and TreeMap
 *
 */
class PerformanceInfo {
    public String stat;
    public long hashMapTime;
    public long treeMapTime;
    public long btTreeMapTime;

    PerformanceInfo(String stat) {
        this.stat = stat;
    }

    private String showTime(long l) {
        long seconds = l / 1000000000;
        long nanoseconds = l % 1000000000;
        while (nanoseconds % 10 == 0) {
            nanoseconds /= 10;
        }
        nanoseconds %= 10000;
        while (nanoseconds < 1000) {
            nanoseconds *= 10;
        }
        String rval = seconds + "." + nanoseconds;
        return rval;
    }

    @Override
    public String toString() {
        return String.format("%-15.15s    %20s  %20s  %20s", stat, showTime(treeMapTime), showTime(hashMapTime), showTime(btTreeMapTime));
    }
}

public class test_performance {
    private static final Logger log = LogManager.getLogger(test_performance.class);
    BPTreeMap<CategoryKey, CarInfo> bpTreeMap = null;
    HashMap<CategoryKey, CarInfo> hashMap = null;
    TreeMap<CategoryKey, CarInfo> treeMap = null;

    HashMap<String, PerformanceInfo> performanceInfo = new HashMap<>();

    @BeforeEach
    void setUp() {
        log.info("setUp");
        bpTreeMap = new BPTreeMap<>(100, 90);
        hashMap = new HashMap<>();
        treeMap = new TreeMap<>();
        performanceInfo = new HashMap<String, PerformanceInfo>();
        populate();
    }

    void put(CategoryKey key, CarInfo value) {
        bpTreeMap.put(key, value);
        hashMap.put(key, value);
        treeMap.put(key, value);
    }

    File[] getDirs(String path) {
        return new File(path).listFiles();
    }

    void processCars(Category category, Quality quality, String manufacturer, String filePath) {
        //log.info("Processing cars: { category: " +category + ", quality: " + quality + ",  manufacturer: " + manufacturer + " }");
        String line = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            int lineNbr = 0;
            try {
                lineNbr++;
                while ((line = reader.readLine()) != null) {
                    String[] arr = line.split(",");
                    String name = arr[0].trim();
                    Double cost = Double.parseDouble(arr[1]);
                    CategoryKey key = new CategoryKey(category, quality, manufacturer, name);
                    bpTreeMap.put(key, new CarInfo(category, quality, manufacturer, name, cost));
                    hashMap.put(key, new CarInfo(category, quality, manufacturer, name, cost));
                    treeMap.put(key, new CarInfo(category, quality, manufacturer, name, cost));
                }
            } catch (Exception e) {
                    log.info("Error processing file: {}\n {}: {}", filePath, lineNbr, line);
                    log.info("  Error: {}", e.getMessage());
            }
        } catch (Exception e) {
            log.info("Error processing file: {}", filePath);
        }

    }
    void populate() {
        URL dbLoc = getClass().getResource("/database");
        for (File categoryDir : getDirs(dbLoc.getFile().toString())) {
            String categoryName = Paths.get(categoryDir.getPath()).getFileName().toString();
            Category category = Category.valueOf(categoryName.toUpperCase());
            for (File qualityDir : getDirs(categoryDir.toString())) {
                String qualityName = Paths.get(qualityDir.getPath()).getFileName().toString();
                Quality quality = Quality.valueOf(qualityName.toUpperCase());

                for (File manufacturerPath : getDirs(qualityDir.toString())) {
                    String manufacturer = Paths.get(manufacturerPath.getPath()).getFileName().toString();
                    int index = manufacturer.lastIndexOf('.');
                    if (index>=0) {
                        manufacturer=manufacturer.substring(0, index);
                    }
                    processCars(category, quality, manufacturer, manufacturerPath.getPath());
                }
            }
        }
    }

    long entrySetSpeed(Map<CategoryKey, CarInfo> map) {
        log.info("Processing {}", map.getClass().toString());
        int rows = 0;
        long start = System.nanoTime();
        for (Map.Entry<CategoryKey, CarInfo> entry : map.entrySet()) {
            CategoryKey key = entry.getKey();
            CarInfo value = entry.getValue();
            rows++;
        }
        long end = System.nanoTime();
        //log.info("Read {} rows in {}", rows, showTime(end - start));
        return end - start;
    }

    @Test
    void test_performance() {
        PerformanceInfo info = new PerformanceInfo("EntrySet");

        info.hashMapTime = entrySetSpeed(hashMap);
        info.treeMapTime = entrySetSpeed(treeMap);
        info.btTreeMapTime = entrySetSpeed(bpTreeMap);
        performanceInfo.put("EntrySet", info);
        showPerformance();
    }

    private void showPerformance() {
        System.out.println("Stat               TreeMap time          HashMap time          BPTreeMap time");
        System.out.println("---------------    --------------------  --------------------  --------------------");
        for (PerformanceInfo info : performanceInfo.values()) {
            System.out.println(info);
        }
        System.out.println("\n\n");
        bpTreeMap.showStats();
    }
//    Double checkEquals(String key, Double result) {
//        Double value = map.get(key);
//        log.info("map.get(\"{}\") = {}", key, value);
//        assertEquals(value, result);
//        return result;
//    }
//
//    //Test
//    void test_valuesExist() {
//        checkEquals("pi", Math.PI);
//        checkEquals("e", Math.E);
//        Double result = checkEquals("tau", Math.PI * 2);
//        assertEquals(0.0, Math.PI*2 - result );
//    }
//
//
//
//    //Test
//    void test_valuesDontExist() {
//        checkEquals("3", null);
//        checkEquals("pip", null);
//    }
//
//    @Test
//    void test_EntrySet() {
//        Map<String, Double> map2 = new BPTreeMap<String, Double>(9);
//        Set<Map.Entry<String, Double>> x = map.entrySet();
//        for (Map.Entry<String,Double> entry : map.entrySet()) {
//            map2.put(entry.getKey(), entry.getValue());
//            log.info(entry);
//        }
//    }

}
