package com.github.ronlamb.bplustree;

import com.github.ronlamb.perf.PerfKey;
import com.github.ronlamb.perf.PerfValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class test_performance {
    private static final Logger log = LogManager.getLogger(test_performance.class);
    BPTreeMap<PerfKey, PerfValue> treeMap = null;
    HashMap<PerfKey, PerfValue> hashMap = null;


    @BeforeEach
    void setUp() {
        log.info("setUp");
        treeMap = new BPTreeMap<>(100);
        hashMap = new HashMap<>();
        populate();
    }

    void put(PerfKey key, PerfValue value) {
        treeMap.put(key, value);
        hashMap.put(key, value);
    }

    File[] getDirs(String path) {
        return new File(path).listFiles();
    }

    void processCars(PerfKey.Category category, PerfKey.Quality quality, String manufacturer, String filePath) {
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
                    PerfKey key = new PerfKey(category, quality, manufacturer, name);
                    treeMap.put(key, new PerfValue(cost));
                    hashMap.put(key, new PerfValue(cost));
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
            PerfKey.Category category = PerfKey.Category.valueOf(categoryName.toUpperCase());
            for (File qualityDir : getDirs(categoryDir.toString())) {
                String qualityName = Paths.get(qualityDir.getPath()).getFileName().toString();
                PerfKey.Quality quality = PerfKey.Quality.valueOf(qualityName.toUpperCase());

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

    void entrySetSpeed(Map<PerfKey, PerfValue> map) {
        log.info("Processing {}", map.getClass().toString());
        int rows = 0;
        long start = System.nanoTime();
        for (Map.Entry<PerfKey, PerfValue> entry : map.entrySet()) {
            PerfKey key = entry.getKey();
            PerfValue value = entry.getValue();
            rows++;
        }
        long end = System.nanoTime();
        log.info("Read {} rows in {}", rows, showTime(end - start));
    }

    private Object showTime(long l) {
        long seconds = l / 1000000000;
        long nanoseconds = l % 1000000000;
        while (nanoseconds % 10 == 0) {
            nanoseconds /= 10;
        }
        String rval = seconds + "." + nanoseconds;
        return rval;

    }

    @Test
    void text_performance() {
        entrySetSpeed(hashMap);
        entrySetSpeed(treeMap);
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
