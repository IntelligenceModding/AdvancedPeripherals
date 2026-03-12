package de.srendi.advancedperipherals.common.items;

import dan200.computercraft.shared.network.container.ComputerContainerData;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.items.base.BaseItem;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesMenuProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class SmartGlassesInterfaceItem extends BaseItem {

    public SmartGlassesInterfaceItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull Level world, @NotNull Player player, InteractionHand hand) {
        if (world.isClientSide)
            return new InteractionResultHolder<>(InteractionResult.PASS, player.getItemInHand(hand));

        final ItemStack glasses = SmartGlassesItem.getEquipped(player);

        if (!(glasses.getItem() instanceof SmartGlassesItem glassesItem)) {
            player.displayClientMessage(Component.translatable("item.advancedperipherals.smartglasses.dontwear"), false);
            return super.use(world, player, hand);
        }

        SmartGlassesComputer computer = glassesItem.getOrCreateComputer((ServerLevel) world, player, player.getInventory(), glasses);
        computer.turnOn();

        IItemHandler itemHandler = glasses.getCapability(Capabilities.ItemHandler.ITEM);
        if (itemHandler != null) {
            player.openMenu(
                new SmartGlassesMenuProvider(computer, glasses, itemHandler),
                new ComputerContainerData(computer, glasses)::toBytes
            );
        }

        return super.use(world, player, hand);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
