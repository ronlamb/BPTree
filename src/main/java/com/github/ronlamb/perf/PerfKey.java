package com.github.ronlamb.perf;

public class PerfKey implements Comparable<PerfKey> {

    @Override
    public int compareTo(PerfKey o) {
        if (category.compareTo(o.category) != 0) {
            return category.compareTo(o.category);
        }
        if (quality.compareTo(o.quality) != 0) {
            return quality.compareTo(o.quality);
        }
        if (manufacturer.compareTo(o.manufacturer) != 0) {
            return manufacturer.compareTo(o.manufacturer);
        }
        return name.compareTo(o.name);
    }

    public enum Category {
        MOTORCYCLE,
        CAR,
        SEDAN,
        SPORT_SEDAN,
        SPORTS_CAR,
        SUPER_SPORTS_CAR,
        VAN,
        SUV,
        PLANE,
        BOAT,
        TRUCK,
        RV;
    }

    public enum Quality {
        LOW,
        STANDARD,
        HIGH,
        EXPENSIVE,
        LUXURY
    }
    Category category;
    Quality quality;
    String manufacturer;
    String name;

    public PerfKey(Category category, Quality quality, String manufacturer, String name) {
        this.category = category;
        this.quality = quality;
        this.manufacturer = manufacturer;
        this.name = name;
    }

    public PerfKey() {
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "PerfKey{" +
                "category=" + category +
                ", quality=" + quality +
                ", manufacturer='" + manufacturer + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
