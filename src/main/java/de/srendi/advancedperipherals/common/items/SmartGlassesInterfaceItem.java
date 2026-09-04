package de.srendi.advancedperipherals.common.items;

import dan200.computercraft.shared.network.container.ComputerContainerData;
import de.srendi.advancedperipherals.common.items.base.BaseItem;
import de.srendi.advancedperipherals.common.setup.APTranslations;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesItemHandler;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesMenuProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class SmartGlassesInterfaceItem extends BaseItem {

    public SmartGlassesInterfaceItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack handItemStack = player.getItemInHand(hand);
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResultHolder.success(handItemStack);
        }

        final ItemStack glasses = SmartGlassesItem.getEquipped(player);

        if (!(glasses.getItem() instanceof SmartGlassesItem glassesItem)) {
            player.displayClientMessage(Component.translatable(APTranslations.SMART_GLASSES_NOT_WEARING), true);
            return InteractionResultHolder.fail(handItemStack);
        }

        SmartGlassesComputer computer = glassesItem.getOrCreateComputer(serverLevel, player, glasses);
        if (!computer.isOn()) {
            computer.turnOn();
        }

        SmartGlassesItemHandler itemHandler = new SmartGlassesItemHandler(glasses, computer);
        player.openMenu(
            new SmartGlassesMenuProvider(computer, glasses, itemHandler),
            new ComputerContainerData(computer, glasses)::toBytes
        );

        return InteractionResultHolder.consume(handItemStack);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
