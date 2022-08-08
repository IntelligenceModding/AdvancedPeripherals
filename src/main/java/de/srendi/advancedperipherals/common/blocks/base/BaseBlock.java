package de.srendi.advancedperipherals.common.blocks.base;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;

public class BaseBlock extends Block implements IHarvesterBlock {

    private final TagKey<Block> harvestTag;

    public BaseBlock() {
        this(Tags.Blocks.NEEDS_WOOD_TOOL);
    }

    public BaseBlock(TagKey<Block> harvestTag) {
        this(Properties.of(Material.METAL).strength(1, 5).sound(SoundType.METAL).noOcclusion().requiresCorrectToolForDrops(), harvestTag);
    }

    public BaseBlock(Properties properties, TagKey<Block> harvestTag) {
        super(properties);
        this.harvestTag = harvestTag;
    }

    @NotNull
    @Override
    public RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    public TagKey<Block> getHarvestTag() {
        return harvestTag;
    }

}
