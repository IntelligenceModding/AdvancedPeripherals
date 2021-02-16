package de.srendi.advancedperipherals.common.blocks;

import de.srendi.advancedperipherals.common.blocks.base.BaseBlock;
import de.srendi.advancedperipherals.common.blocks.tileentity.MeBridgeTileEntity;
import de.srendi.advancedperipherals.common.setup.ModTileEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Nullable;

public class MeBridge extends BaseBlock {

    private MeBridgeTileEntity meBridge;

    @Override
    public boolean hasTileEntity(BlockState state) {
        return ModList.get().isLoaded("appliedenergistics2");
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        if (ModList.get().isLoaded("appliedenergistics2")) {
            if (ModTileEntityTypes.ME_BRIDGE.get().create() != null) {
                return ModTileEntityTypes.ME_BRIDGE.get().create();
            }
        } else {
            return null;
        }
        return null;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (!ModList.get().isLoaded("appliedenergistics2"))
            return;
        meBridge = (MeBridgeTileEntity) worldIn.getTileEntity(pos);
        if (meBridge == null || !(placer instanceof PlayerEntity))
            return;
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        meBridge.setPlayer((PlayerEntity) placer);
    }

    @Override
    public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {
        if (!ModList.get().isLoaded("appliedenergistics2"))
            return;
        super.onPlayerDestroy(worldIn, pos, state);
    }
}