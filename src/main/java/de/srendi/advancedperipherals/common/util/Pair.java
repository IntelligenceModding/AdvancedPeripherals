package de.srendi.advancedperipherals.common.util;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class Pair<T, V> {
    private final T left;
    private final V right;

    public Pair(T left, V right) {
        this.left = left;
        this.right = right;
    }

    public static <T, V> Pair<T, V> onlyRight(V right) {
        return new Pair<>(null, right);
    }

    public static <T, V> Pair<T, V> onlyLeft(T left) {
        return new Pair<>(left, null);
    }

    public static <T, V> Pair<T, V> of(T left, V right) {
        return new Pair<>(left, right);
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

    public void ifRightPresent(Consumer<V> consumer) {
        if (rightPresent())
            consumer.accept(right);
    }

    public void ifLeftPresent(Consumer<T> consumer) {
        if (leftPresent())
            consumer.accept(left);
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
