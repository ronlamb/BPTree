package com.github.ronlamb.bplustree;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;
import java.util.zip.Checksum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class test_bptreemap {
    private static final Logger log = LogManager.getLogger(test_bptreemap.class);
    BPTreeMap<String, Double> map = null;
    @BeforeEach
    void setUp() {
        log.info("setUp");
        map = new BPTreeMap<String, Double>(9);
        populateConstants();
    }

    void populateConstants() {
        map.put("pi", Math.PI);
        map.put("tau", 	6.28318530717958647692);
        map.put("e", Math.E);
        map.put("c m/sec", 299792458.0);
        map.put("c km/sec", 299792.458);
        map.put("c miles/sec", 186282.39705122);
        map.put("sqrt(2)", 1.41421356237309504880);
        map.put("phi", 1.61803398874989484820);
        map.put("Kepler–Bouwkamp",0.11494204485329620070);
        map.put("ln(2)", 0.69314718055994530941);
        map.put("omega", 0.56714329040978387299);
        map.put("Ramanujan–Soldner",1.45136923488338105028);
        map.put("Ramanujan's whole", 262537412640768743.0);
        map.put("Ramanujan's fractional", 0.999999999999250073);
        map.put("Gauss's", 0.83462684167407318628);
    }

    Double checkEquals(String key, Double result) {
        Double value = map.get(key);
        log.info("map.get(\"{}\") = {}", key, value);
        assertEquals(value, result);
        return result;
    }

    //Test
    void test_valuesExist() {
        checkEquals("pi", Math.PI);
        checkEquals("e", Math.E);
        Double result = checkEquals("tau", Math.PI * 2);
        assertEquals(0.0, Math.PI*2 - result );
    }



    //Test
    void test_valuesDontExist() {
        checkEquals("3", null);
        checkEquals("pip", null);
    }

    @Test
    void test_EntrySet() {
        Map<String, Double> map2 = new BPTreeMap<String, Double>(9);
        Set<Map.Entry<String, Double>> x = map.entrySet();
        for (Map.Entry<String,Double> entry : map.entrySet()) {
            map2.put(entry.getKey(), entry.getValue());
            log.info(entry);
        }
    }

}
