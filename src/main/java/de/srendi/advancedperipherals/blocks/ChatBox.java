package de.srendi.advancedperipherals.blocks;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import dan200.computercraft.core.asm.LuaMethod;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public class ChatBox extends Block implements IPeripheralProvider, IPeripheral {

    public ChatBox() {
        super(Properties.create(Material.WOOD)
                .hardnessAndResistance(2, 10)
                .harvestLevel(0)
                .sound(SoundType.WOOD)
                .harvestTool(ToolType.AXE)
                .setRequiresTool());
    }

    @NotNull
    @Override
    public LazyOptional<IPeripheral> getPeripheral(@NotNull World world, @NotNull BlockPos blockPos, @NotNull Direction direction) {
        IPeripheral peripheral = this;
        return LazyOptional.of(() -> peripheral);
    }


    @NotNull
    @Override
    public String getType() {
        return "chatBox";
    }

    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return false;
    }
}
