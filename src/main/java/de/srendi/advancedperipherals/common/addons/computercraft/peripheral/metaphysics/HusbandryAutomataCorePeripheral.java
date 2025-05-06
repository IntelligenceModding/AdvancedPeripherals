package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.AutomataCoreTier;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.plugins.AutomataEntityHandPlugin;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.plugins.AutomataEntityTransferPlugin;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.lib.metaphysics.IAutomataCoreTier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.function.Predicate;

public class HusbandryAutomataCorePeripheral extends WeakAutomataCorePeripheral {
    public static final String TYPE = "husbandry_automata";

    private static final Predicate<Entity> isAnimal = entity1 -> entity1.getType().getCategory().isFriendly();
    private static final Predicate<Entity> isLivingEntity = LivingEntity.class::isInstance;
    private static final Predicate<Entity> isNotPlayer = entity1 -> !(entity1 instanceof Player);
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
        return APConfig.METAPHYSICS_CONFIG.enableHusbandryAutomataCore.get();
    }
}
