package de.srendi.advancedperipherals.common.util;

public record TransientValueHolder<T>(T value) {
    private static final TransientValueHolder<?> NIL = new TransientValueHolder<>(null);

    public static final <T> TransientValueHolder<T> nil() {
        return (TransientValueHolder<T>) NIL;
    }
}
