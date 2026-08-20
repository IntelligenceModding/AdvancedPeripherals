package de.srendi.advancedperipherals.lib.codec;

@FunctionalInterface
public interface StreamDecoder<B, T> {
    T decode(B buffer);
}
