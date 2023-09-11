package com.github.ronlamb.bplustree;

public class KeyValue<K extends Comparable<K>, V> implements Comparable<KeyValue<K,V>> {
	K key;
	V value;

	public KeyValue(K key, V value) {
		this.key = key;
		this.value = value;
	}

	public KeyValue() {
		key = null;
		value = null;
	}

	public String toString() {
		return key + ": " + value;
	}

	@Override
	public int compareTo(KeyValue<K, V> o) {
		return key.compareTo(o.key);
	}

	@SuppressWarnings("rawtypes")
	public static ThreadLocal<KeyValue> tempKeyValue = new ThreadLocal<>() {
        @SuppressWarnings("unchecked")
		@Override
        protected KeyValue initialValue() {
            return new KeyValue(null, null);
        }

        @Override
        public KeyValue get() {
            return super.get();
        }
    };
}
