package com.github.ronlamb.perf;

public class CarNameKey implements Comparable<CarNameKey> {
    @Override
    public int compareTo(CarNameKey o) {
        if (manufacturer.compareTo(o.manufacturer) != 0) {
            return manufacturer.compareTo(o.manufacturer);
        }
        return name.compareTo(o.name);
    }

    String manufacturer;
    String name;

    public CarNameKey(String manufacturer, String name) {
        this.manufacturer = manufacturer;
        this.name = name;
    }

    public CarNameKey() {
    }

    @Override
    public String toString() {
        return "PerfKey{" +
                ", manufacturer='" + manufacturer + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

}
