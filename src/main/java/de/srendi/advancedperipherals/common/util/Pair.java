package de.srendi.advancedperipherals.common.util;

public class Pair<T, V> {
    private final T left;
    private final V right;

    public Pair(T t, V v) {
        left = t;
        right = v;
    }

    public T getLeft() {
        return left;
    }

    public V getRight() {
        return right;
    }

    public boolean leftPresent() {
        return left != null;
    }

    public boolean rightPresent() {
        return right != null;
    }

    public static <T, V> Pair<T, V> onlyRight(V v) {
        return new Pair<>(null, v);
    }

    public static <T, V> Pair<T, V> onlyLeft(T t) {
        return new Pair<>(t, null);
    }
}
