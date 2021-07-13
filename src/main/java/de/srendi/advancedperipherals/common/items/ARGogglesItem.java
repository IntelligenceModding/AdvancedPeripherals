package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.HudOverlayHandler;
import de.srendi.advancedperipherals.common.addons.curios.CuriosHelper;
import de.srendi.advancedperipherals.common.blocks.tileentity.ARControllerTile;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.setup.Blocks;
import de.srendi.advancedperipherals.common.util.EnumColor;
import de.srendi.advancedperipherals.common.util.SideHelper;
import de.srendi.advancedperipherals.network.MNetwork;
import de.srendi.advancedperipherals.network.messages.RequestHudCanvasMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants.NBT;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class ARGogglesItem extends ArmorItem {
    private static final String CONTROLLER_POS = "controller_pos";
    private static final String CONTROLLER_WORLD = "controller_world";

    public ARGogglesItem() {
        super(ArmorMaterial.LEATHER, EquipmentSlotType.HEAD,
                new Properties().tab(AdvancedPeripherals.TAB).stacksTo(1));
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientTick(ClientPlayerEntity player, ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains(CONTROLLER_POS) && stack.getTag().contains(CONTROLLER_WORLD)) {
            int[] arr = stack.getTag().getIntArray(CONTROLLER_POS);
            if (arr.length < 3)
                return;
            BlockPos pos = new BlockPos(arr[0], arr[1], arr[2]);
            String dimensionKey = stack.getTag().getString(CONTROLLER_WORLD);
            World world = player.level;
            if (!dimensionKey.equals(world.dimension().toString())) {
                MNetwork.sendToServer(new RequestHudCanvasMessage(pos, dimensionKey));
                return;
            }
            TileEntity te = world.getBlockEntity(pos);
            if (!(te instanceof ARControllerTile)) {
                //If distance to ARController is larger than view distance
                MNetwork.sendToServer(new RequestHudCanvasMessage(pos, dimensionKey));
                return;
            }

            ARControllerTile controller = (ARControllerTile) te;
            HudOverlayHandler.updateCanvas(controller.getCanvas());
        }
    }

    public ITextComponent getDescription() {
        return new TranslationTextComponent("item.advancedperipherals.tooltip.ar_goggles");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip,
                                ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (!InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_CONTROL)) {
            tooltip.add(EnumColor
                    .buildTextComponent(new TranslationTextComponent("item.advancedperipherals.tooltip.hold_ctrl")));
        } else {
            tooltip.add(EnumColor.buildTextComponent(getDescription()));
        }
        if (!AdvancedPeripheralsConfig.enableARGoggles)
            tooltip.add(EnumColor.buildTextComponent(new TranslationTextComponent("item.advancedperipherals.tooltip.disabled")));
        if (stack.hasTag() && stack.getTag().contains(CONTROLLER_POS, NBT.TAG_INT_ARRAY)) {
            int[] pos = stack.getTag().getIntArray(CONTROLLER_POS);
            tooltip.add(new TranslationTextComponent("item.advancedperipherals.tooltip.ar_goggles.binding", pos[0],
                    pos[1], pos[2]));
        }
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        if (!AdvancedPeripherals.isCuriosLoaded()) {
            return null;
        }
        return CuriosHelper.createARGogglesProvider(stack);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
        return AdvancedPeripherals.MOD_ID + ":textures/models/ar_goggles.png";
    }

    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
        // only need to tick client side, if client is wearing them himself
        if (!SideHelper.isClientPlayer(player))
            return;
        clientTick((ClientPlayerEntity) player, stack);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        BlockPos blockpos = context.getClickedPos();
        World world = context.getLevel();
        if (!world.getBlockState(blockpos).is(Blocks.AR_CONTROLLER.get())) {
            return super.useOn(context);
        } else {
            TileEntity entity = world.getBlockEntity(blockpos);
            if (!(entity instanceof ARControllerTile))
                return super.useOn(context);
            ARControllerTile controller = (ARControllerTile) entity;
            if (!context.getLevel().isClientSide) {
                ItemStack item = context.getItemInHand();
                if (!item.hasTag())
                    item.setTag(new CompoundNBT());
                CompoundNBT nbt = item.getTag();
                BlockPos pos = controller.getBlockPos();
                nbt.putIntArray(CONTROLLER_POS, new int[]{pos.getX(), pos.getY(), pos.getZ()});
                nbt.putString(CONTROLLER_WORLD, controller.getLevel().dimension().toString());
                item.setTag(nbt);
            }
            context.getPlayer().displayClientMessage(new TranslationTextComponent("text.advancedperipherals.linked_goggles"), true);
            return ActionResultType.SUCCESS;
        }
    }
}
