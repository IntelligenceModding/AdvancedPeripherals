package de.srendi.advancedperipherals.common.blocks.base;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public abstract class BaseBlock extends Block {

    public BaseBlock() {
        super(Properties.create(Material.IRON).hardnessAndResistance(1, 5).harvestLevel(0).sound(SoundType.METAL).harvestTool(ToolType.PICKAXE).setRequiresTool());
    }

}
