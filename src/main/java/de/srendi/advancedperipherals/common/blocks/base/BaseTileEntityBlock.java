package de.srendi.advancedperipherals.common.blocks.base;

import de.srendi.advancedperipherals.common.blocks.tileentity.InventoryManagerTileEntity;
import de.srendi.advancedperipherals.common.util.WorldPos;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public abstract class BaseTileEntityBlock extends BaseBlock {

    public BaseTileEntityBlock() {
        this(Properties.of(Material.METAL).strength(1, 5).harvestLevel(0).sound(SoundType.METAL).noOcclusion().harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops());
    }

    public BaseTileEntityBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.isClientSide) return ActionResultType.SUCCESS;
        TileEntity tileEntity = worldIn.getBlockEntity(pos);
        if (tileEntity != null && !(tileEntity instanceof IInventoryBlock)) return ActionResultType.PASS;
        INamedContainerProvider namedContainerProvider = this.getMenuProvider(state, worldIn, pos);
        if (namedContainerProvider != null) {
            if (!(player instanceof ServerPlayerEntity)) return ActionResultType.PASS;
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
            NetworkHooks.openGui(serverPlayerEntity, namedContainerProvider, pos);
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity tileEntity = worldIn.getBlockEntity(pos);
            if(tileEntity instanceof IInventoryBlock)
            InventoryHelper.dropContents(worldIn, pos, (IInventory) tileEntity);
            super.onRemove(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        //Used for the lua function getName()
        worldIn.getBlockEntity(pos).getTileData().putString("CustomName", stack.getDisplayName().getString());
        TileEntityList.get(worldIn).setTileEntity(worldIn, new WorldPos(pos, worldIn), true);
    }

    @Override
    public void destroy(IWorld worldIn, BlockPos pos, BlockState state) {
        super.destroy(worldIn, pos, state);
        TileEntityList.get((World) worldIn).setTileEntity((World) worldIn, new WorldPos(pos, (World) worldIn), false);
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader worldIn) {
        return hasTileEntity(defaultBlockState()) ? createTileEntity(defaultBlockState(), worldIn) : null;
    }
}
