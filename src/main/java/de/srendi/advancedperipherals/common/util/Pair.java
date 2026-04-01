package de.srendi.advancedperipherals.common.util;

import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public record Pair<T, V>(@Nullable T left, @Nullable V right) {
    public static <T, V> Pair<T, V> onlyRight(V right) {
        return new Pair<>(null, right);
    }

    public static <T, V> Pair<T, V> onlyLeft(T left) {
        return new Pair<>(left, null);
    }

    public static <T, V> Pair<T, V> of(T left, V right) {
        return new Pair<>(left, right);
    }

    public boolean leftPresent() {
        return left != null;
    }

    public boolean rightPresent() {
        return right != null;
    }

    public void ifRightPresent(Consumer<V> consumer) {
        if (right != null) {
            consumer.accept(right);
        }
    }

    public void ifLeftPresent(Consumer<T> consumer) {
        if (left != null) {
            consumer.accept(left);
        }
    }

    public <T1> Pair<T1, V> mapLeft(Function<@Nullable T, T1> mapFunc) {
        return new Pair<>(mapFunc.apply(left), right);
    }

    public <V1> Pair<T, V1> mapRight(Function<@Nullable V, V1> mapFunc) {
        return new Pair<>(left, mapFunc.apply(right));
    }

    public <T1, V1> Pair<T1, V1> mapBoth(BiFunction<@Nullable T, @Nullable V, Pair<T1, V1>> mapFunc) {
        return mapFunc.apply(left, right);
    }

    public <T1> Pair<T1, V> ignoreLeft() {
        return new Pair<>(null, right);
    }

    public <V1> Pair<T, V1> ignoreRight() {
        return new Pair<>(left, null);
    }

    public <R> R reduce(BiFunction<@Nullable T, @Nullable V, R> reduceFunc) {
        return reduceFunc.apply(left, right);
    }
}
