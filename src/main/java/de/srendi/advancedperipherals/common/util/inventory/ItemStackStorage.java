package de.srendi.advancedperipherals.common.util.inventory;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class ItemStackStorage {
    public static final Codec<ItemStackStorage> CODEC = ItemStack.CODEC.listOf()
        .xmap(ItemStackStorage::of, (value) -> List.of(value.getAll()));

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemStackStorage> STREAM_CODEC = StreamCodec.of(
        (RegistryFriendlyByteBuf buf, ItemStackStorage value) ->
            buf.writeCollection(List.of(value.getAll()), (StreamCodec<? super FriendlyByteBuf, ItemStack>) ((StreamCodec<?, ItemStack>) ItemStack.STREAM_CODEC)),
        (buf) -> ItemStackStorage.of(
            buf.readList((StreamCodec<? super FriendlyByteBuf, ItemStack>) ((StreamCodec<?, ItemStack>) ItemStack.STREAM_CODEC))
        )
    );

    private final ItemStack[] items;

    private ItemStackStorage(ItemStack[] items) {
        this.items = items;
    }

    public static ItemStackStorage ofSize(int size) {
        ItemStack[] items = new ItemStack[size];
        for (int i = 0; i < size; i++) {
            items[i] = ItemStack.EMPTY;
        }
        return new ItemStackStorage(items);
    }

    public static ItemStackStorage of(@NotNull ItemStack... items) {
        return new ItemStackStorage(copyItems(items));
    }

    public static ItemStackStorage of(List<ItemStack> items) {
        ItemStack[] copies = new ItemStack[items.size()];
        for (int i = 0; i < copies.length; i++) {
            copies[i] = items.get(i).copy();
        }
        return new ItemStackStorage(copies);
    }

    public int size() {
        return this.items.length;
    }

    @NotNull
    public ItemStack get(int slot) {
        return this.items[slot].copy();
    }

    @NotNull
    public Item getItem(int slot) {
        return this.items[slot].getItem();
    }

    public ItemStack[] getAll() {
        return copyItems(this.items);
    }

    @CheckReturnValue
    public ItemStackStorage set(int slot, @NotNull ItemStack stack) {
        ItemStack[] items = copyItems(this.items);
        items[slot] = stack.copy();
        return new ItemStackStorage(items);
    }

    @CheckReturnValue
    public ItemStackStorage update(int slot, Consumer<ItemStack> processor) {
        ItemStack[] items = copyItems(this.items);
        processor.accept(items[slot]);
        return new ItemStackStorage(items);
    }

    @CheckReturnValue
    public ItemStackStorage updateAll(BiConsumer<Integer, ItemStack> processor) {
        ItemStack[] items = copyItems(this.items);
        for (int i = 0; i < items.length; i++) {
            processor.accept(i, items[i]);
        }
        return new ItemStackStorage(items);
    }

    public boolean isSameItemSameComponents(int slot, ItemStack stack) {
        return ItemStack.isSameItemSameComponents(this.items[slot], stack);
    }

    public ItemStack[] getAllUnsafe() {
        return this.items;
    }

    private static ItemStack[] copyItems(ItemStack[] items) {
        ItemStack[] copies = new ItemStack[items.length];
        for (int i = 0; i < items.length; i++) {
            copies[i] = items[i].copy();
        }
        return copies;
    }
}
