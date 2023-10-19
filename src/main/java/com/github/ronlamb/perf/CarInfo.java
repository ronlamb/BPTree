package com.github.ronlamb.perf;

public class CarInfo {
    public Double cost;
    public Double commission;

    public Category category;
    public Quality quality;
    public String manufacturer;
    public String name;

    Double MPG;
    Double topSpeed;

    public CarInfo(Category category, Quality quality, String manufacturer, String name, Double cost) {
        this.cost = cost;
        this.commission = 0.0;
        this.quality = quality;
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
