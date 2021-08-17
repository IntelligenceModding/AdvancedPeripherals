package de.srendi.advancedperipherals.common.blocks.base;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;

public interface IHarvesterBlock {

    public Tag.Named<Block> getHarvestTag();

    default Tag.Named<Block> getToolTag() {
        return BlockTags.MINEABLE_WITH_PICKAXE;
    }
}
