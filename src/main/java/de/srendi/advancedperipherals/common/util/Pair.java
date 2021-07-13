package de.srendi.advancedperipherals.common.util;

import java.util.function.BiFunction;
import java.util.function.Function;

public class Pair<T, V> {
    private final T left;
    private final V right;

    public Pair(T t, V v) {
        left = t;
        right = v;
    }

    public static <T, V> Pair<T, V> onlyRight(V v) {
        return new Pair<>(null, v);
    }

    public static <T, V> Pair<T, V> onlyLeft(T t) {
        return new Pair<>(t, null);
    }

    public static <T, V> Pair<T, V> of(T t, V v) {
        return new Pair<>(t, v);
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

    public <T1> Pair<T1, V> mapLeft(Function<T, T1> mapFunc) {
        return new Pair<>(mapFunc.apply(left), right);
    }

    public <V1> Pair<T, V1> mapRight(Function<V, V1> mapFunc) {
        return new Pair<>(left, mapFunc.apply(right));
    }

    public <T1, V1> Pair<T1, V1> mapBoth(BiFunction<T, V, Pair<T1, V1>> mapFunc) {
        return mapFunc.apply(left, right);
    }

    public <T1> Pair<T1, V> ignoreLeft() {
        return new Pair<>(null, right);
    }

    public <V1> Pair<T, V1> ignoreRight() {
        return new Pair<>(left, null);
    }

    public <R> R reduce(BiFunction<T, V, R> reduceFunc) {
        return reduceFunc.apply(left, right);
    }
}
