package de.srendi.advancedperipherals.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class ChatBox extends Block {

    public ChatBox() {
        super(Properties.create(Material.WOOD)
                .hardnessAndResistance(2, 10)
                .harvestLevel(0)
                .sound(SoundType.WOOD)
                .harvestTool(ToolType.AXE)
                .setRequiresTool());
    }

}
