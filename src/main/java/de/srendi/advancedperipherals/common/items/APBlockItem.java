package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.common.items.base.BaseBlockItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class APBlockItem extends BaseBlockItem {

    @Nullable
    private final ResourceLocation turtleID;
    @Nullable
    private final ResourceLocation pocketID;
    private final Supplier<Boolean> enabledSup;

    public APBlockItem(Block blockIn, Properties properties, @Nullable ResourceLocation turtleID, @Nullable ResourceLocation pocketID, Supplier<Boolean> enabledSup) {
        super(blockIn, properties);
        this.turtleID = turtleID;
        this.pocketID = pocketID;
        this.enabledSup = enabledSup;
    }

    public APBlockItem(Block blockIn, @Nullable ResourceLocation turtleID, @Nullable ResourceLocation pocketID, Supplier<Boolean> enabledSup) {
        super(blockIn);
        this.turtleID = turtleID;
        this.pocketID = pocketID;
        this.enabledSup = enabledSup;
    }

    @Override
    public boolean isEnabled() {
        return enabledSup.get();
    }
}
