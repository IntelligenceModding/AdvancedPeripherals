package de.srendi.advancedperipherals.common.addons.computercraft.turtles;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.base.BaseTurtle;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ChunkyPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.KineticPeripheral;
import de.srendi.advancedperipherals.common.setup.Items;
import de.srendi.advancedperipherals.common.util.ChunkManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class TurtleKinetic extends BaseTurtle<KineticPeripheral> {

    protected static final Set<ChunkPos> loadedChunks = new HashSet<>();

    private int tick;

    public TurtleKinetic() {
        super("kinetic_turtle", "turtle.advancedperipherals.kinetic_turtle", new ItemStack(Items.KINETIC_ENGINE.get()));
    }

    @Override
    protected KineticPeripheral createPeripheral() {
        return new KineticPeripheral("kinetic", null);
    }

    @Override
    protected ModelResourceLocation getLeftModel() {
        return null; //Null, the turtle uses the chunk controller item model. See BaseTurtle.java
    }

    @Override
    protected ModelResourceLocation getRightModel() {
        return null;
    }
}
