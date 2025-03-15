package de.srendi.advancedperipherals.common.items;

import dan200.computercraft.shared.computer.blocks.TileComputerBase;
import de.srendi.advancedperipherals.client.KeyBindings;
import de.srendi.advancedperipherals.common.container.KeyboardContainer;
import de.srendi.advancedperipherals.common.items.base.BaseItem;
import de.srendi.advancedperipherals.common.items.base.IInventoryItem;
import de.srendi.advancedperipherals.common.network.APNetworking;
import de.srendi.advancedperipherals.common.network.toclient.KeyboardMouseCapturePacket;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleItem;
import de.srendi.advancedperipherals.common.smartglasses.modules.keyboard.KeyboardModule;
import de.srendi.advancedperipherals.common.util.EnumColor;
import de.srendi.advancedperipherals.common.util.SideHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KeyboardItem extends BaseItem implements IInventoryItem, IModuleItem {

    public static final String BIND_TAG = "bind";
    public static final String GLASSES_BIND_TAG = "glasses_id";
    public static final String BOUND_TYPE_TAG = "bind_type";

    public KeyboardItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null)
            return InteractionResult.PASS;

        if (SideHelper.isClientPlayer(player))
            return InteractionResult.PASS;

        if (!player.isShiftKeyDown())
            return InteractionResult.PASS;

        BlockEntity entity = context.getLevel().getBlockEntity(context.getClickedPos());
        if (entity instanceof TileComputerBase) {
            bind(player, context.getItemInHand(), context.getLevel(), context.getClickedPos());
        } else {
            clear(player, context.getItemInHand());
        }
        return super.useOn(context);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int inventorySlot, boolean isCurrentItem, @Nullable SmartGlassesAccess access, @Nullable IModule module) {
        if (level.isClientSide()) {
            return;
        }

        if (access == null || !(module instanceof KeyboardModule keyboadModule)) {
            return;
        }

        CompoundTag data = itemStack.getOrCreateTag();
        int instanceId = access.getComputer().getInstanceID();
        int oldInstanceId = -1;

        if (data.contains(GLASSES_BIND_TAG)) {
            oldInstanceId = data.getInt(GLASSES_BIND_TAG);
        }

        if (!data.contains(BOUND_TYPE_TAG) || ((oldInstanceId != -1 && oldInstanceId != instanceId)) || !data.getBoolean(BOUND_TYPE_TAG)) {
            data.putBoolean(BOUND_TYPE_TAG, true);
            data.putInt(GLASSES_BIND_TAG, access.getComputer().getInstanceID());
            data.remove(BIND_TAG);
        }

        if (!(entity instanceof ServerPlayer serverPlayer)) {
            return;
        }
        // TODO: this for sure won't work on dedicated server
        if (!KeyBindings.GLASSES_HOTKEY_KEYBINDING.isDown()) {
            return;
        }

        access.getComputer().queueEvent("keyboard_open");
        if (serverPlayer.containerMenu instanceof KeyboardContainer openedKeyboard && openedKeyboard.getKeyboardItem() == itemStack) {
            return;
        }

        NetworkHooks.openScreen(serverPlayer, this.createContainer(serverPlayer, itemStack), buf -> {
            buf.writeBlockPos(serverPlayer.blockPosition());
            buf.writeItem(itemStack);
        });
        if (keyboadModule.isCapturingMouse()) {
            APNetworking.sendTo(new KeyboardMouseCapturePacket(true), serverPlayer);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if (playerIn.level.isClientSide()) {
            return new InteractionResultHolder<>(InteractionResult.PASS, playerIn.getItemInHand(handIn));
        }
        // Used to prevent the menu from opening when we just want to bind/unbind the keyboard
        if (playerIn.isShiftKeyDown()) {
            return new InteractionResultHolder<>(InteractionResult.PASS, playerIn.getItemInHand(handIn));
        }
        if (!playerIn.getItemInHand(handIn).getOrCreateTag().contains(BIND_TAG)) {
            playerIn.displayClientMessage(EnumColor.buildTextComponent(Component.translatable("text.advancedperipherals.keyboard_notbound")), false);
            return new InteractionResultHolder<>(InteractionResult.PASS, playerIn.getItemInHand(handIn));
        }
        // Run the super use which handles the IInventoryItem stuff to actually open the container
        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level levelIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, levelIn, tooltip, flagIn);
        CompoundTag data = stack.getOrCreateTag();
        if (data.contains(BOUND_TYPE_TAG) && !data.getBoolean(BOUND_TYPE_TAG)) {
            if (data.contains(BIND_TAG)) {
                tooltip.add(EnumColor.buildTextComponent(Component.translatable("item.advancedperipherals.tooltip.binding.bound_to", data.getInt(BIND_TAG))));
            }
        } else {
            if (data.contains(GLASSES_BIND_TAG)) {
                tooltip.add(EnumColor.buildTextComponent(Component.translatable("item.advancedperipherals.tooltip.binding.bound_to_glasses", data.getInt(GLASSES_BIND_TAG))));
            }
        }
    }

    private void bind(Player player, ItemStack itemStack, Level world, BlockPos pos) {
        CompoundTag data = itemStack.getOrCreateTag();
        data.putBoolean(BOUND_TYPE_TAG, false);

        if (!(world.getBlockEntity(pos) instanceof TileComputerBase computer)) {
            // TODO: should it show bind failed message?
            return;
        }

        int id = computer.getComputerID();
        if (id < 0) {
            // TODO: show computer not initialized error?
            return;
        }
        data.putInt(BIND_TAG, id);

        player.displayClientMessage(EnumColor.buildTextComponent(Component.translatable("text.advancedperipherals.bind_keyboard", pos.toShortString())), true);
    }

    private void clear(Player player, ItemStack itemStack) {
        CompoundTag data = itemStack.getOrCreateTag();
        data.remove(BIND_TAG);
        data.putBoolean(BOUND_TYPE_TAG, false);

        player.displayClientMessage(EnumColor.buildTextComponent(Component.translatable("text.advancedperipherals.cleared_keyboard")), true);
    }

    @Override
    public MenuProvider createContainer(Player playerEntity, ItemStack itemStack) {
        return new MenuProvider() {
            @NotNull
            @Override
            public Component getDisplayName() {
                return Component.literal("");
            }

            @Override
            public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory playerInv, @NotNull Player player) {
                return new KeyboardContainer(pContainerId, playerInv, player.blockPosition(), player.getLevel(), itemStack);
            }
        };
    }

    @Override
    public IModule createModule(SmartGlassesAccess access) {
        return new KeyboardModule();
    }
}
