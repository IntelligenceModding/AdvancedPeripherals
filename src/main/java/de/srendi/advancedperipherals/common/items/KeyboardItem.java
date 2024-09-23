package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.common.container.KeyboardContainer;
import de.srendi.advancedperipherals.common.items.base.BaseItem;
import de.srendi.advancedperipherals.common.items.base.IInventoryItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class KeyboardItem extends BaseItem implements IInventoryItem {

    public KeyboardItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public boolean isEnabled() {
        return true;
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
                return new KeyboardContainer(pContainerId, playerInv, player.blockPosition(), player.getLevel());
            }
        };
    }
}
