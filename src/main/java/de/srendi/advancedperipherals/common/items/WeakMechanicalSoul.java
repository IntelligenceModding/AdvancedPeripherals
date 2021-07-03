package de.srendi.advancedperipherals.common.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.NotNull;

public class WeakMechanicalSoul extends APItem {

    public WeakMechanicalSoul(Properties properties, String turtleID, String pocketID, ITextComponent description) {
        super(properties, turtleID, pocketID, description);
    }

    public WeakMechanicalSoul(String turtleID, String pocketID, ITextComponent description) {
        super(turtleID, pocketID, description);
    }

    @Override
    public @NotNull ActionResultType interactLivingEntity(@NotNull ItemStack stack, @NotNull PlayerEntity player, @NotNull LivingEntity entity, @NotNull Hand hand) {
        if (!entity.getEntity().getName().getString().equals("minecraft:enderman")) {
            System.out.println("WHAT?");
            return ActionResultType.PASS;
        }
        return ActionResultType.PASS;
    }
}
