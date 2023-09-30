package com.github.ronlamb.perf;

public class CarInfo {
    Double cost;
    Double commission;

    Category category;
    Quality quality;
    String manufacturer;
    String name;

    Double MPG;
    Double topSpeed;

    public CarInfo(Category category, Quality quality, String manufacturer, String name, Double cost) {
        this.cost = cost;
        this.commission = commission;
        this.category = category;
        this.manufacturer = manufacturer;
        this.name = name;
    }

    public CarInfo() {
    }

    @Override
    public String toString() {
        return "PerfValue{" +
                "cost=" + cost +
                ", commission=" + commission +
                '}';
    }
}
