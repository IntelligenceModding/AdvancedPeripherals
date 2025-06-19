package de.srendi.advancedperipherals.common.items;

import dan200.computercraft.shared.computer.blocks.TileComputerBase;
import dan200.computercraft.shared.computer.core.ServerComputer;
import de.srendi.advancedperipherals.client.KeyBindings;
import de.srendi.advancedperipherals.common.container.KeyboardContainer;
import de.srendi.advancedperipherals.common.items.base.BaseItem;
import de.srendi.advancedperipherals.common.items.base.IInventoryItem;
import de.srendi.advancedperipherals.common.network.APNetworking;
import de.srendi.advancedperipherals.common.network.toserver.GlassesHotkeyPacket;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleItem;
import de.srendi.advancedperipherals.common.smartglasses.modules.keyboard.KeyboardModule;
import de.srendi.advancedperipherals.common.util.EnumColor;
import de.srendi.advancedperipherals.common.util.SideHelper;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KeyboardItem extends BaseItem implements IInventoryItem, IModuleItem {

    public static final String BIND_TAG = "bind";
    public static final String OPENING_TAG = "KeyboardOpening";

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
        if (!level.isClientSide()) {
            itemStack.removeTagKey(BIND_TAG);
            return;
        }
        if (!(entity instanceof LocalPlayer player)) {
            return;
        }
        boolean pressed = KeyBindings.GLASSES_HOTKEY_KEYBINDING.isDown();
        CompoundTag data = itemStack.getOrCreateTag();
        if (data.getBoolean(OPENING_TAG) == pressed) {
            return;
        }
        data.putBoolean(OPENING_TAG, pressed);
        if (!pressed) {
            return;
        }
        if (player.containerMenu instanceof KeyboardContainer openedKeyboard && openedKeyboard.getKeyboardItem().equals(itemStack)) {
            return;
        }
        APNetworking.sendToServer(new GlassesHotkeyPacket("", -1));
        return;
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
        CompoundTag data = playerIn.getItemInHand(handIn).getTag();
        if (data == null || !data.contains(BIND_TAG)) {
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
        if (data.contains(BIND_TAG)) {
            tooltip.add(EnumColor.buildTextComponent(Component.translatable("item.advancedperipherals.tooltip.binding.bound_to", data.getInt(BIND_TAG))));
        }
    }

    private void bind(Player player, ItemStack itemStack, Level world, BlockPos pos) {
        CompoundTag data = itemStack.getOrCreateTag();
        data.remove(BIND_TAG);

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

        player.displayClientMessage(EnumColor.buildTextComponent(Component.translatable("text.advancedperipherals.cleared_keyboard")), true);
    }

    @Override
    public MenuProvider createContainer(Player playerEntity, ItemStack itemStack) {
        return new MenuProvider() {
            @NotNull
            @Override
            public Component getDisplayName() {
                return Component.empty();
            }

            @Override
            public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory playerInv, @NotNull Player player) {
                return new KeyboardContainer(pContainerId, playerInv, player.blockPosition(), player.getLevel(), itemStack);
            }
        };
    }

    public MenuProvider createContainerWithComputer(Player playerEntity, ItemStack itemStack, ServerComputer computer) {
        return new MenuProvider() {
            @NotNull
            @Override
            public Component getDisplayName() {
                return Component.empty();
            }

            @Override
            public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory playerInv, @NotNull Player player) {
                return new KeyboardContainer(pContainerId, playerInv, player.blockPosition(), player.getLevel(), itemStack, computer);
            }
        };
    }

    @Override
    public void writeContainerData(Player player, ItemStack stack, FriendlyByteBuf buf) {
        buf.writeBlockPos(player.blockPosition());
        buf.writeItem(stack);
    }

    @Override
    public IModule createModule(SmartGlassesAccess access, ItemStack stack) {
        return new KeyboardModule(this, stack);
    }
}
