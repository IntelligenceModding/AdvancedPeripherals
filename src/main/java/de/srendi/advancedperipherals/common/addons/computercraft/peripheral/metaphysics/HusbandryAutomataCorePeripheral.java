package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.AutomataCoreTier;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.plugins.AutomataEntityHandPlugin;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.plugins.AutomataEntityTransferPlugin;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.configuration.GeneralConfig;
import de.srendi.advancedperipherals.lib.metaphysics.IAutomataCoreTier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.function.Predicate;

public class HusbandryAutomataCorePeripheral extends WeakAutomataCorePeripheral {
    public static final String TYPE = "husbandryAutomata";

    private static final Predicate<Entity> isAnimal = entity1 -> entity1.getType().getCategory().isFriendly();
    private static final Predicate<Entity> isLivingEntity = entity1 -> entity1 instanceof LivingEntity;
    private static final Predicate<Entity> isNotPlayer = entity1 -> !(entity1 instanceof PlayerEntity);
    private static final Predicate<Entity> suitableEntity = isAnimal.and(isLivingEntity).and(isNotPlayer);

    public HusbandryAutomataCorePeripheral(ITurtleAccess turtle, TurtleSide side) {
        this(TYPE, turtle, side, AutomataCoreTier.TIER2);
    }

    protected HusbandryAutomataCorePeripheral(String type, ITurtleAccess turtle, TurtleSide side, IAutomataCoreTier tier) {
        super(type, turtle, side, tier);
        addPlugin(new AutomataEntityTransferPlugin(this, suitableEntity));
        addPlugin(new AutomataEntityHandPlugin(this, suitableEntity));
    }

    @Override
    public boolean isEnabled() {
        return APConfig.METAPHYSICS_CONFIG.ENABLE_HUSBANDRY_AUTOMATA_CORE.get();
    }
}
