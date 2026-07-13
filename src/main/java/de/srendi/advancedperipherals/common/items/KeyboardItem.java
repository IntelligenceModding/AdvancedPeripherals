package de.srendi.advancedperipherals.common.items;

import dan200.computercraft.shared.computer.blocks.AbstractComputerBlockEntity;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.core.ServerContext;
import de.srendi.advancedperipherals.client.KeyBindings;
import de.srendi.advancedperipherals.common.container.KeyboardContainer;
import de.srendi.advancedperipherals.common.items.base.BaseItem;
import de.srendi.advancedperipherals.common.network.toserver.GlassesHotkeyPacket;
import de.srendi.advancedperipherals.common.setup.APDataComponents;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesSideAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleItem;
import de.srendi.advancedperipherals.common.smartglasses.modules.keyboard.KeyboardModule;
import de.srendi.advancedperipherals.common.util.EnumColor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
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
import java.util.Map;
import java.util.WeakHashMap;

public class KeyboardItem extends BaseItem implements IModuleItem<KeyboardModule> {
    private Map<LivingEntity, Boolean> clientKeyboardHotkeyPressed = new WeakHashMap<>(); // client-only

    public KeyboardItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public ResourceLocation moduleId() {
        return KeyboardModule.ID;
    }

    @Override
    @NotNull
    public KeyboardModule createModule(SmartGlassesSideAccess access) {
        return new KeyboardModule(this);
    }

    @Override
    @NotNull
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || !context.isSecondaryUseActive()) {
            return super.useOn(context);
        }
        Level level = context.getLevel();

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity entity = level.getBlockEntity(context.getClickedPos());
        if (entity instanceof AbstractComputerBlockEntity blockEntity) {
            bind(player, context.getItemInHand(), blockEntity);
        } else {
            clear(player, context.getItemInHand());
        }
        return InteractionResult.CONSUME;
    }

    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.has(APDataComponents.BINDING_COMPUTER.get())) {
            if (level.isClientSide()) {
                player.displayClientMessage(EnumColor.buildTextComponent(Component.translatable("text.advancedperipherals.keyboard_notbound")), false);
            }
            return InteractionResultHolder.pass(stack);
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResultHolder.success(stack);
        }
        ServerComputer computer = null;
        int computerId = stack.get(APDataComponents.BINDING_COMPUTER.get());
        for (ServerComputer computr : ServerContext.get(serverLevel.getServer()).registry().getComputers()) {
            if (computr.getID() == computerId) {
                computer = computr;
                break;
            }
        }
        if (computer == null) {
            player.displayClientMessage(EnumColor.buildTextComponent(Component.translatable("text.advancedperipherals.keyboard.computer_notfound")), false);
            return InteractionResultHolder.fail(stack);
        }
        if (!computer.checkUsable(player)) {
            player.displayClientMessage(EnumColor.buildTextComponent(Component.translatable("text.advancedperipherals.keyboard.computer_unusable")), false);
            return InteractionResultHolder.fail(stack);
        }
        if (!computer.isOn()) {
            computer.turnOn();
        }
        ((ServerPlayer) player).openMenu(this.createContainerWithComputer(computer));
        // Run the super use which handles the IInventoryItem stuff to actually open the container
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void moduleTick(Level level, LivingEntity entity, int moduleSlot, SmartGlassesSideAccess access, KeyboardModule module) {
        if (!level.isClientSide() || !(entity instanceof LocalPlayer)) {
            return;
        }
        boolean pressed = KeyBindings.GLASSES_HOTKEY_KEYBINDING.isDown();
        if (this.clientKeyboardHotkeyPressed.getOrDefault(entity, false) == pressed) {
            return;
        }
        this.clientKeyboardHotkeyPressed.put(entity, pressed);
        if (!pressed) {
            return;
        }
        PacketDistributor.sendToServer(GlassesHotkeyPacket.KEYBOARD_OPEN_PACKET);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        if (stack.has(APDataComponents.BINDING_COMPUTER.get())) {
            tooltip.add(EnumColor.buildTextComponent(Component.translatable("item.advancedperipherals.tooltip.binding.bound_to", stack.get(APDataComponents.BINDING_COMPUTER.get()))));
        }
    }

    private void bind(Player player, ItemStack stack, AbstractComputerBlockEntity computer) {
        stack.remove(APDataComponents.BINDING_COMPUTER.get());

        int id = computer.getComputerID();
        if (id < 0) {
            player.getInventory().setChanged();
            player.displayClientMessage(EnumColor.buildTextComponent(Component.translatable("text.advancedperipherals.bind_keyboard.not_init")), true);
            return;
        }
        stack.set(APDataComponents.BINDING_COMPUTER.get(), id);

        player.getInventory().setChanged();
        player.displayClientMessage(EnumColor.buildTextComponent(Component.translatable("text.advancedperipherals.bind_keyboard", id)), true);
    }

    private void clear(Player player, ItemStack stack) {
        if (!stack.has(APDataComponents.BINDING_COMPUTER.get())) {
            return;
        }
        stack.remove(APDataComponents.BINDING_COMPUTER.get());

        player.getInventory().setChanged();
        player.displayClientMessage(EnumColor.buildTextComponent(Component.translatable("text.advancedperipherals.cleared_keyboard")), true);
    }

    public MenuProvider createContainerWithComputer(ServerComputer computer) {
        return new MenuProvider() {
            @Override
            @NotNull
            public Component getDisplayName() {
                return Component.empty();
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                return new KeyboardContainer(containerId, inventory, player.level(), computer);
            }
        };
    }

    public MenuProvider createContainerWithModule(SmartGlassesSideAccess access, KeyboardModule module) {
        return new MenuProvider() {
            @Override
            @NotNull
            public Component getDisplayName() {
                return Component.empty();
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                return new KeyboardContainer(containerId, inventory, player.level(), access, module);
            }
        };
    }
}
