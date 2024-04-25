package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.core.apis.TableHelper;
import dan200.computercraft.shared.turtle.core.TurtlePlayer;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.TurtlePeripheralOwner;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import java.util.Map;

public class CompassPeripheral extends BasePeripheral<TurtlePeripheralOwner> {

    public static final String PERIPHERAL_TYPE = "compass";

    public CompassPeripheral(ITurtleAccess turtle, TurtleSide side) {
        super(PERIPHERAL_TYPE, new TurtlePeripheralOwner(turtle, side));
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableCompassTurtle.get();
    }

    @LuaFunction(mainThread = true)
    public String getFacing() {
        return owner.getFacing().toString();
    }

    @LuaFunction(mainThread = true)
    /**
     * 
     * @param options A table contains how to place the block:
     *   x: the x offset relative to the turtle
     *   y: the y offset relative to the turtle
     *   z: the z offset relative to the turtle
     *   forward: the direction the block is going to facing. Default is the facing direction of the turtle
     *   top: the direction the block's top is going to facing. Default is TOP
     */
    public MethodResult place(Map<?, ?> options) throws LuaException {
        int x = TableHelper.optIntField(options, "x", 0);
        int y = TableHelper.optIntField(options, "y", 0);
        int z = TableHelper.optIntField(options, "z", 0);
        final int maxRange = APConfig.PERIPHERALS_CONFIG.compassTurtleRadius.get();
        if (x > maxRange || y > maxRange || z > maxRange) {
            return MethodResult.of(false, "OUT_OF_RANGE");
        }
        String forward = TableHelper.optStringField(options, "forward", null);
        String top = TableHelper.optStringField(options, "top", null)
        Direction forwardDir, topDir;
        if (forward != null && (forwardDir = Direction.byName(forward.toLowerCase())) == null) {
            throw new LuaException(forward + "is not a valid direction");
        }
        if (top != null && (topDir = Direction.byName(top.toLowerCase())) == null) {
            throw new LuaException(top + "is not a valid direction");
        }

        ITurtleAccess turtle = owner.getTurtle();
        ItemStack stack = turtle.getInventory().getItem(turtle.getSelectedSlot());
        if (stack.isEmpty()) {
            return MethodResult.of(false, "EMPTY_SLOT");
        }
        BlockPos position = turtle.getPosition().offset(x, y, z);
        return deployOn(stack, position, forwardDir, topDir, options);
    }

    private MethodResult deployOn(ItemStack stack, BlockPos position, Direction forward, Direction top, Map<?, ?> options) throws LuaException {
        ITurtleAccess turtle = owner.getTurtle();
        Level world = turtle.getLevel();
        if (forward == null) {
            forward = turtle.getDirection();
        }
        if (top == null) {
            top = Direction.UP;
        }
        TurtlePlayer turtlePlayer = TurtlePlayer.getWithPosition(turtle, position, forward.getOpposite());
        BlockHitResult hit = BlockHitResult.miss(Vec3.atCenterOf(position), top, position);
        DirectionalPlaceContext context = new DirectionalPlaceContext(world, position, forward, stack, top);
        PlayerInteractEvent.RightClickBlock event = ForgeHooks.onRightClickBlock(turtlePlayer, InteractionHand.MAIN_HAND, position, hit);
        if (event.isCanceled()) {
            return MethodResult.of(false, "EVENT_CANCELED");
        }
        if (stack.getItem() instanceof BlockItem block) {
            InteractionResult res = block.place(context);
            if (!res.consumesAction()) {
                return MethodResult.of(false, "CANNOT_PLACE");
            }
        } else {
            return MethodResult.of(false, "NOT_BLOCK");
        }
        return MethodResult.of(true);
    }
}
