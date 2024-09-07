package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.common.items.base.BaseBlockItem;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class APBlockItem extends BaseBlockItem {

    private final Supplier<Boolean> enabledSup;

    public APBlockItem(Block blockIn, Properties properties, Supplier<Boolean> enabledSup) {
        super(blockIn, properties);
        this.enabledSup = enabledSup;
    }

    public APBlockItem(Block blockIn, Supplier<Boolean> enabledSup) {
        super(blockIn);
        this.enabledSup = enabledSup;
    }

    @Override
    public boolean isEnabled() {
        return enabledSup.get();
    }

}
