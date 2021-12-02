package de.srendi.advancedperipherals.common.blocks.base;

import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.Tags;

public class BaseBlock extends Block implements IHarvesterBlock {

    private final Tag.Named<Block> harvestTag;

    public BaseBlock() {
        this(Tags.Blocks.NEEDS_WOOD_TOOL);
    }

    public BaseBlock(Tag.Named<Block> harvestTag) {
        this(Properties.of(Material.METAL).strength(1, 5).sound(SoundType.METAL).noOcclusion().requiresCorrectToolForDrops(), harvestTag);
    }

    public BaseBlock(Properties properties,Tag.Named<Block> harvestTag) {
        super(properties);
        this.harvestTag = harvestTag;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public Tag.Named<Block> getHarvestTag() {
        return harvestTag;
    }

}
