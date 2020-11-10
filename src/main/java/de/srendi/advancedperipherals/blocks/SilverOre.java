package de.srendi.advancedperipherals.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class SilverOre extends Block {

    public SilverOre() {
        super(AbstractBlock.Properties.create(Material.ROCK)
                .hardnessAndResistance(3, 10)
                .harvestLevel(1)
                .sound(SoundType.STONE)
                .harvestTool(ToolType.PICKAXE)
                .setRequiresTool());
    }

}
