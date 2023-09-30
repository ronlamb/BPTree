package com.github.ronlamb.perf;

public class PerfValue {
    Double cost;
    Double commission;

    public PerfValue(Double cost, Double commission) {
        this.cost = cost;
        this.commission = commission;
    }

    public PerfValue(Double cost) {
        this.cost = cost;
    }
    public PerfValue() {
    }

    @Override
    public String toString() {
        return "PerfValue{" +
                "cost=" + cost +
                ", commission=" + commission +
                '}';
    }
}
