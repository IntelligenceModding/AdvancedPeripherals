package de.srendi.advancedperipherals.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class Speaker extends Block {

    public Speaker() {
        super(Properties.create(Material.WOOD)
                .hardnessAndResistance(2, 10)
                .harvestLevel(0)
                .sound(SoundType.WOOD)
                .harvestTool(ToolType.AXE)
                .setRequiresTool());
    }

}
