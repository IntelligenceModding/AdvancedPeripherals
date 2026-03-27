package de.srendi.advancedperipherals.common.items;

import dan200.computercraft.shared.computer.blocks.AbstractComputerBlockEntity;
import dan200.computercraft.shared.computer.core.ServerComputer;
import de.srendi.advancedperipherals.client.KeyBindings;
import de.srendi.advancedperipherals.common.container.KeyboardContainer;
import de.srendi.advancedperipherals.common.items.base.BaseItem;
import de.srendi.advancedperipherals.common.items.base.IInventoryItem;
import de.srendi.advancedperipherals.common.network.toserver.GlassesHotkeyPacket;
import de.srendi.advancedperipherals.common.setup.APDataComponents;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesSideAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleItem;
import de.srendi.advancedperipherals.common.smartglasses.modules.keyboard.KeyboardModule;
import de.srendi.advancedperipherals.common.util.EnumColor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
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
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KeyboardItem extends BaseItem implements IInventoryItem, IModuleItem {

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

        if (player.level().isClientSide)
            return InteractionResult.PASS;

        if (!player.isShiftKeyDown())
            return InteractionResult.PASS;

        BlockEntity entity = context.getLevel().getBlockEntity(context.getClickedPos());
        if (entity instanceof AbstractComputerBlockEntity) {
            bind(player, context.getItemInHand(), context.getLevel(), context.getClickedPos());
        } else {
            clear(player, context.getItemInHand());
        }
        return super.useOn(context);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int inventorySlot, boolean isCurrentItem, @Nullable SmartGlassesSideAccess access, @Nullable IModule module) {
        if (!level.isClientSide()) {
            itemStack.remove(APDataComponents.BINDING_COMPUTER.get());
            return;
        }
        if (!(entity instanceof LocalPlayer player)) {
            return;
        }
        boolean pressed = KeyBindings.GLASSES_HOTKEY_KEYBINDING.isDown();
        if (itemStack.getOrDefault(APDataComponents.KEYBOARD_OPENED.get(), false).booleanValue() == pressed) {
            return;
        }
        itemStack.set(APDataComponents.KEYBOARD_OPENED.get(), pressed);
        if (!pressed) {
            return;
        }
        if (player.containerMenu instanceof KeyboardContainer openedKeyboard && openedKeyboard.getKeyboardItem().equals(itemStack)) {
            return;
        }
        PacketDistributor.sendToServer(new GlassesHotkeyPacket("", -1));
        return;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if (playerIn.level().isClientSide()) {
            return new InteractionResultHolder<>(InteractionResult.PASS, playerIn.getItemInHand(handIn));
        }
        // Used to prevent the menu from opening when we just want to bind/unbind the keyboard
        if (playerIn.isShiftKeyDown()) {
            return new InteractionResultHolder<>(InteractionResult.PASS, playerIn.getItemInHand(handIn));
        }
        ItemStack stack = playerIn.getItemInHand(handIn);
        if (!stack.has(APDataComponents.BINDING_COMPUTER.get())) {
            playerIn.displayClientMessage(EnumColor.buildTextComponent(Component.translatable("text.advancedperipherals.keyboard_notbound")), false);
            return new InteractionResultHolder<>(InteractionResult.PASS, playerIn.getItemInHand(handIn));
        }
        // Run the super use which handles the IInventoryItem stuff to actually open the container
        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        if (stack.has(APDataComponents.BINDING_COMPUTER.get())) {
            tooltip.add(EnumColor.buildTextComponent(Component.translatable("item.advancedperipherals.tooltip.binding.bound_to", stack.get(APDataComponents.BINDING_COMPUTER.get()))));
        }
    }

    private void bind(Player player, ItemStack stack, Level world, BlockPos pos) {
        stack.remove(APDataComponents.BINDING_COMPUTER.get());

        if (!(world.getBlockEntity(pos) instanceof AbstractComputerBlockEntity computer)) {
            // TODO: should it show bind failed message?
            return;
        }

        int id = computer.getComputerID();
        if (id < 0) {
            // TODO: show computer not initialized error?
            return;
        }
        stack.set(APDataComponents.BINDING_COMPUTER.get(), id);

        player.displayClientMessage(EnumColor.buildTextComponent(Component.translatable("text.advancedperipherals.bind_keyboard", pos.toShortString())), true);
    }

    private void clear(Player player, ItemStack stack) {
        stack.remove(APDataComponents.BINDING_COMPUTER.get());

        player.displayClientMessage(EnumColor.buildTextComponent(Component.translatable("text.advancedperipherals.cleared_keyboard")), true);
    }

    @Override
    public MenuProvider createContainer(Player playerEntity, ItemStack stack) {
        return new MenuProvider() {
            @NotNull
            @Override
            public Component getDisplayName() {
                return Component.empty();
            }

            @Override
            public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory playerInv, @NotNull Player player) {
                return new KeyboardContainer(pContainerId, playerInv, player.level(), stack);
            }
        };
    }

    public MenuProvider createContainerWithComputer(Player playerEntity, ItemStack stack, ServerComputer computer) {
        return new MenuProvider() {
            @NotNull
            @Override
            public Component getDisplayName() {
                return Component.empty();
            }

            @Override
            public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory playerInv, @NotNull Player player) {
                return new KeyboardContainer(pContainerId, playerInv, player.level(), stack, computer);
            }
        };
    }

    @Override
    public void writeContainerData(Player player, ItemStack stack, RegistryFriendlyByteBuf buf) {
        ItemStack.STREAM_CODEC.encode(buf, stack);
    }

    @Override
    public IModule createModule(SmartGlassesSideAccess access, ItemStack stack) {
        return new KeyboardModule(this, stack);
    }
}
