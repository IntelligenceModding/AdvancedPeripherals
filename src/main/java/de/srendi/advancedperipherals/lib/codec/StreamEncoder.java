package de.srendi.advancedperipherals.lib.codec;

@FunctionalInterface
public interface StreamEncoder<B, T> {
    void encode(B buffer, T value);
}
