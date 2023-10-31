package com.github.ronlamb.perf;

public class CategoryKey implements Comparable<CategoryKey> {

    @Override
    public int compareTo(CategoryKey o) {
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

    Category category;
    Quality quality;
    String manufacturer;
    String name;

    public CategoryKey(Category category, Quality quality, String manufacturer, String name) {
        this.category = category;
        this.quality = quality;
        this.manufacturer = manufacturer;
        this.name = name;
    }

    public CategoryKey() {
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
