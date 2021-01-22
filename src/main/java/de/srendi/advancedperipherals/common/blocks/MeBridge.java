package de.srendi.advancedperipherals.common.blocks;

import de.srendi.advancedperipherals.common.blocks.tileentity.MeBridgeTileEntity;
import de.srendi.advancedperipherals.common.blocks.tileentity.TileEntityList;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.setup.ModTileEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Nullable;

public class MeBridge extends Block {

    private MeBridgeTileEntity meBridge;

    public MeBridge() {
        super(Properties.create(Material.IRON).hardnessAndResistance(1, 5).harvestLevel(2).harvestTool(ToolType.PICKAXE).setRequiresTool().sound(SoundType.METAL));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return ModList.get().isLoaded("appliedenergistics2");
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        if(ModList.get().isLoaded("appliedenergistics2")) {
            if(ModTileEntityTypes.ME_BRIDGE.get().create() != null) {
                return ModTileEntityTypes.ME_BRIDGE.get().create();
            }
        } else {
            return null;
        }
        return null;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        if (!ModList.get().isLoaded("appliedenergistics2")) {
            return;
        }
        meBridge = (MeBridgeTileEntity) worldIn.getTileEntity(pos);
        if (meBridge == null || !(placer instanceof PlayerEntity)) {
            return;
        }
        meBridge.setPlayer((PlayerEntity)placer);
        TileEntityList.get(worldIn).setTileEntity(worldIn, pos);
    }

    @Override
    public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {
        super.onPlayerDestroy(worldIn, pos, state);
        if (!ModList.get().isLoaded("appliedenergistics2"))
            return;
        TileEntityList.get((World) worldIn).setTileEntity((World) worldIn, pos);
    }
}