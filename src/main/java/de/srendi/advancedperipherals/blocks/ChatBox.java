package de.srendi.advancedperipherals.blocks;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import de.srendi.advancedperipherals.addons.computercraft.AdvancedPeripheral;
import de.srendi.advancedperipherals.addons.computercraft.ILuaMethodProvider;
import de.srendi.advancedperipherals.blocks.tileentity.ChatBoxTileEntity;
import de.srendi.advancedperipherals.setup.ModTileEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class ChatBox extends Block implements IPeripheralProvider{


    public ChatBox() {
        super(Properties.create(Material.IRON)
                .hardnessAndResistance(1, 5)
                .harvestLevel(0)
                .sound(SoundType.METAL)
                .harvestTool(ToolType.PICKAXE)
                .setRequiresTool());
    }

    @NotNull
    @Override
    public LazyOptional<IPeripheral> getPeripheral(@NotNull World world, @NotNull BlockPos blockPos, @NotNull Direction direction) {
        BlockState block = world.getBlockState(blockPos);
        IPeripheral peripheral = new AdvancedPeripheral(ModTileEntityTypes.CHAT_BOX.get().create());
        return block.getBlock() instanceof ChatBox
                ? LazyOptional.of(() -> peripheral)
                : LazyOptional.empty();

    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntityTypes.CHAT_BOX.get().create();
    }
}
