package de.srendi.advancedperipherals.common.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ARGogglesItem extends ArmorItem {
    private static final String CONTROLLER_POS = "controller_pos";
    private static final String CONTROLLER_LEVEL = "controller_level";

    public ARGogglesItem() {
        super(ArmorMaterials.LEATHER, EquipmentSlot.HEAD, new Properties().stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level levelIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, levelIn, tooltip, flagIn);
        tooltip.add(Component.literal("Temporarily disabled until rewrite"));
    }

    /*public static void clientTick(LocalPlayer player, ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains(CONTROLLER_POS) && stack.getTag().contains(CONTROLLER_LEVEL)) {
            int[] arr = stack.getTag().getIntArray(CONTROLLER_POS);
            if (arr.length < 3) return;
            BlockPos pos = new BlockPos(arr[0], arr[1], arr[2]);
            String dimensionKey = stack.getTag().getString(CONTROLLER_LEVEL);
            Level level = player.level;
            if (!dimensionKey.equals(level.dimension().toString())) {
                MNetwork.sendToServer(new RequestHudCanvasMessage(pos, dimensionKey));
                return;
            }
            BlockEntity te = level.getBlockEntity(pos);
            if (!(te instanceof ARControllerEntity)) {
                //If distance to ARController is larger than view distance
                MNetwork.sendToServer(new RequestHudCanvasMessage(pos, dimensionKey));
                return;
            }

            ARControllerEntity controller = (ARControllerEntity) te;
            HudOverlayHandler.updateCanvas(controller.getCanvas());
        }
    }

    @Override
    public Component getDescription() {
        return Component.translatable("item.advancedperipherals.tooltip.ar_goggles");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level levelIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, levelIn, tooltip, flagIn);
        if (!InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_CONTROL)) {
            tooltip.add(EnumColor.buildTextComponent(Component.translatable("item.advancedperipherals.tooltip.show_desc")));
        } else {
            tooltip.add(EnumColor.buildTextComponent(getDescription()));
        }
        if (!APConfig.PERIPHERALS_CONFIG.enableARGoggles.get())
            tooltip.add(EnumColor.buildTextComponent(Component.translatable("item.advancedperipherals.tooltip.disabled")));
        if (stack.hasTag() && stack.getTag().contains(CONTROLLER_POS, CompoundTag.TAG_INT_ARRAY)) {
            int[] pos = stack.getTag().getIntArray(CONTROLLER_POS);
            tooltip.add(Component.translatable("item.advancedperipherals.tooltip.ar_goggles.binding", pos[0], pos[1], pos[2]));
        }
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
        if (!AdvancedPeripherals.ADDONS.curiosLoaded)
            return null;

        return CuriosHelper.createARGogglesProvider(stack);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return AdvancedPeripherals.MOD_ID + ":textures/models/ar_goggles.png";
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        // only need to tick client side, if client is wearing them himself
        if (!SideHelper.isClientPlayer(player))
            return;
        clientTick((LocalPlayer) player, stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos blockpos = context.getClickedPos();
        Level level = context.getLevel();
        if (!level.getBlockState(blockpos).is(Blocks.AR_CONTROLLER.get())) {
            return super.useOn(context);
        } else {
            BlockEntity entity = level.getBlockEntity(blockpos);
            if (!(entity instanceof ARControllerEntity))
                return super.useOn(context);
            ARControllerEntity controller = (ARControllerEntity) entity;
            if (!context.getLevel().isClientSide) {
                ItemStack item = context.getItemInHand();
                if (!item.hasTag())
                    item.setTag(new CompoundTag());
                CompoundTag nbt = item.getTag();
                BlockPos pos = controller.getBlockPos();
                nbt.putIntArray(CONTROLLER_POS, new int[]{pos.getX(), pos.getY(), pos.getZ()});
                nbt.putString(CONTROLLER_LEVEL, controller.getLevel().dimension().toString());
                item.setTag(nbt);
            }
            context.getPlayer().displayClientMessage(Component.translatable("text.advancedperipherals.linked_goggles"), true);
            return InteractionResult.SUCCESS;
        }
    }*/
}
