package de.srendi.advancedperipherals.common.items;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.HudOverlayHandler;
import de.srendi.advancedperipherals.common.addons.curios.CuriosHelper;
import de.srendi.advancedperipherals.common.blocks.tileentity.ARControllerTileEntity;
import de.srendi.advancedperipherals.common.items.base.BaseItem;
import de.srendi.advancedperipherals.common.setup.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants.NBT;

public class ARGogglesItem extends BaseItem {
	private static final String CONTROLLER_POS = "controller_pos";
	private static final String CONTROLLER_WORLD = "controller_world";

	public ARGogglesItem() {
        super(new Properties().group(AdvancedPeripherals.TAB).maxStackSize(1));
    }

    @Override
    protected Optional<String> getPocketID() {
        return Optional.empty();
    }

    @Override
    protected Optional<String> getTurtleID() {
        return Optional.empty();
    }

    @Override
    protected ITextComponent getDescription() {
        return new TranslationTextComponent("item.advancedperipherals.tooltip.ar_goggles");
    }
    
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip,
    		ITooltipFlag flagIn) {
    	super.addInformation(stack, worldIn, tooltip, flagIn);
    	if (stack.hasTag() && stack.getTag().contains(CONTROLLER_POS, NBT.TAG_INT_ARRAY)) {
    		int[] pos = stack.getTag().getIntArray(CONTROLLER_POS);
    		tooltip.add(new TranslationTextComponent("item.advancedperipherals.tooltip.ar_goggles.binding", pos[0], pos[1], pos[2]));
    	}
    }
    
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
    	if (!AdvancedPeripherals.isCuriosLoaded()) {
    		return null;
    	}
    	return CuriosHelper.createARGogglesProvider(stack);
    }

	public static void tick(PlayerEntity player, ItemStack stack) {
		if (stack.hasTag() && stack.getTag().contains(CONTROLLER_POS) && stack.getTag().contains(CONTROLLER_WORLD)) {
    		int[] arr = stack.getTag().getIntArray(CONTROLLER_POS);
    		if (arr.length < 3) 
    			return;
    		BlockPos pos = new BlockPos(arr[0], arr[1], arr[2]);
    		String dimensionKey = stack.getTag().getString(CONTROLLER_WORLD);
    		World world = player.world;
    		if (!dimensionKey.equals(world.getDimensionKey().toString())) {
    			for (World w : world.getServer().getWorlds()) {
    				if (w.getDimensionKey().toString().equals(dimensionKey)) {
    					world = w;
    					break;
    				}
    			}
    		}
    		TileEntity te = world.getTileEntity(pos);
    		if (!(te instanceof ARControllerTileEntity))
    			return;
    		ARControllerTileEntity controller = (ARControllerTileEntity) te;
    		HudOverlayHandler.clearCanvas();
    		HudOverlayHandler.updateCanvas(controller.getCanvas());
    	}
	}
    
    @Override
    public EquipmentSlotType getEquipmentSlot(ItemStack stack) {
    	return EquipmentSlotType.HEAD;
    }
    
    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
    	tick(player, stack);
    }
    
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
    	BlockPos blockpos = context.getPos();
        World world = context.getWorld();
        if (!world.getBlockState(blockpos).matchesBlock(Blocks.AR_CONTROLLER.get())) {
           return super.onItemUse(context);
        } else {
        	TileEntity entity = world.getTileEntity(blockpos);
        	if (!(entity instanceof ARControllerTileEntity))
        		return super.onItemUse(context);
        	ARControllerTileEntity controller = (ARControllerTileEntity) entity;
        	if (!context.getWorld().isRemote) {
        		ItemStack item = context.getItem();
        		if (!item.hasTag())
        			item.setTag(new CompoundNBT());
        		CompoundNBT nbt = item.getTag();
        		BlockPos pos = controller.getPos();
				nbt.putIntArray(CONTROLLER_POS, new int[] { pos.getX(), pos.getY(), pos.getZ() });
				nbt.putString(CONTROLLER_WORLD, controller.getWorld().getDimensionKey().toString());
        		item.setTag(nbt);
        	}
        	return ActionResultType.SUCCESS;
        }
    }
}
