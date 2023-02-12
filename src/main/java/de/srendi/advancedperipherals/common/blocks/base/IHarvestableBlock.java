package de.srendi.advancedperipherals.common.blocks.base;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public interface IHarvestableBlock {

    TagKey<Block> getHarvestTag();

    default TagKey<Block> getToolTag() {
        return BlockTags.MINEABLE_WITH_PICKAXE;
    }
}
