package de.srendi.advancedperipherals.lib.codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;

public interface StreamCodec<B, T> extends StreamEncoder<B, T>, StreamDecoder<B, T> {
    StreamCodec<ByteBuf, Boolean> BOOL = of(ByteBuf::writeBoolean, ByteBuf::readBoolean);
    StreamCodec<ByteBuf, Byte> BYTE = of((b, v) -> b.writeByte(v), ByteBuf::readByte);
    StreamCodec<ByteBuf, Short> SHORT = of((b, v) -> b.writeShort(v), ByteBuf::readShort);
    StreamCodec<ByteBuf, Float> FLOAT = of(ByteBuf::writeFloat, ByteBuf::readFloat);
    StreamCodec<ByteBuf, Double> DOUBLE = of(ByteBuf::writeDouble, ByteBuf::readDouble);

    // unsafe casts from ByteBuf to FriendlyByteBuf, but we only passing FriendlyByteBuf anyways
    StreamCodec<ByteBuf, Integer> VAR_INT = of((b, v) -> ((FriendlyByteBuf) b).writeVarInt(v), (b) -> ((FriendlyByteBuf) b).readVarInt());
    StreamCodec<ByteBuf, Long> VAR_LONG = of((b, v) -> ((FriendlyByteBuf) b).writeVarLong(v), (b) -> ((FriendlyByteBuf) b).readVarLong());
    StreamCodec<ByteBuf, String> STRING_UTF8 = of((b, v) -> ((FriendlyByteBuf) b).writeUtf(v), (b) -> ((FriendlyByteBuf) b).readUtf());

    static <B, T> StreamCodec<B, T> of(StreamEncoder<B, T> encoder, StreamDecoder<B, T> decoder) {
        return new StreamCodec<B, T>() {
            @Override
            public void encode(B buffer, T value) {
                encoder.encode(buffer, value);
            }

            @Override
            public T decode(B buffer) {
                return decoder.decode(buffer);
            }
        };
    }
}
