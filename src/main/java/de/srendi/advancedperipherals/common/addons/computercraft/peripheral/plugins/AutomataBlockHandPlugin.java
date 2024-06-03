package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.plugins;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.core.apis.TableHelper;
import dan200.computercraft.shared.turtle.core.TurtlePlayer;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.SingleOperationContext;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.TurtlePeripheralOwner;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.CompassPeripheral;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.StringUtil;
import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import de.srendi.advancedperipherals.lib.peripherals.AutomataCorePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import static de.srendi.advancedperipherals.common.addons.computercraft.operations.SingleOperation.DIG;
import static de.srendi.advancedperipherals.common.addons.computercraft.operations.SingleOperation.USE_ON_BLOCK;
import static de.srendi.advancedperipherals.common.addons.computercraft.operations.SingleOperation.ACCURE_PLACE;

public class AutomataBlockHandPlugin extends AutomataCorePlugin {

    public AutomataBlockHandPlugin(AutomataCorePeripheral automataCore) {
        super(automataCore);
    }

    @Override
    public @Nullable IPeripheralOperation<?>[] getOperations() {
        return new IPeripheralOperation[]{DIG, USE_ON_BLOCK};
    }

    @LuaFunction(mainThread = true)
    public final MethodResult digBlock(@NotNull IArguments arguments) throws LuaException {
        Map<?, ?> opts = arguments.count() > 0 ? arguments.getTable(0) : Collections.emptyMap();
        boolean sneak = TableHelper.optBooleanField(opts, "sneak", false);
        float yaw = opts != null ? (float) TableHelper.optNumberField(opts, "yaw", 0) : 0;
        float pitch = opts != null ? (float) TableHelper.optNumberField(opts, "pitch", 0) : 0;
        return automataCore.withOperation(DIG, context -> {
            TurtlePeripheralOwner owner = automataCore.getPeripheralOwner();
            ItemStack selectedTool = owner.getToolInMainHand();
            int previousDamageValue = selectedTool.getDamageValue();
            Pair<Boolean, String> result = owner.withPlayer(APFakePlayer.wrapActionWithShiftKey(sneak, APFakePlayer.wrapActionWithRot(yaw, pitch, APFakePlayer::digBlock)));
            if (!result.getLeft()) {
                return MethodResult.of(null, result.getRight());
            }
            if (automataCore.hasAttribute(AutomataCorePeripheral.ATTR_STORING_TOOL_DURABILITY)) {
                selectedTool.setDamageValue(previousDamageValue);
            }
            return MethodResult.of(true);
        });
    }

    @LuaFunction(mainThread = true)
    public final MethodResult useOnBlock(@NotNull IArguments arguments) throws LuaException {
        Map<?, ?> opts = arguments.count() > 0 ? arguments.getTable(0) : Collections.emptyMap();
        boolean sneak = TableHelper.optBooleanField(opts, "sneak", false);
        float yaw = opts != null ? (float) TableHelper.optNumberField(opts, "yaw", 0) : 0;
        float pitch = opts != null ? (float) TableHelper.optNumberField(opts, "pitch", 0) : 0;
        return automataCore.withOperation(USE_ON_BLOCK, context -> {
            TurtlePeripheralOwner owner = automataCore.getPeripheralOwner();
            ItemStack selectedTool = owner.getToolInMainHand();
            int previousDamageValue = selectedTool.getDamageValue();
            InteractionResult result = owner.withPlayer(APFakePlayer.wrapActionWithShiftKey(sneak, APFakePlayer.wrapActionWithRot(yaw, pitch, APFakePlayer::useOnBlock)));
            if (automataCore.hasAttribute(AutomataCorePeripheral.ATTR_STORING_TOOL_DURABILITY)) {
                selectedTool.setDamageValue(previousDamageValue);
            }
            return MethodResult.of(result.consumesAction(), result.toString());
        });
    }

    /**
     * placeBlock method will let turtle place a block with more details when compass has equipped.
     * It should not able to place fluids / use any item, because compass do not recognize them.
     *
     * @param options A table contains how to place the block:
     *   x: the x offset relative to the turtle. Default 0
     *   y: the y offset relative to the turtle. Default 0
     *   z: the z offset relative to the turtle. Default 0
     *   anchor: the direction the block is going to hanging on. Default is the direction of the turtle
     *   front: the direction the block is going to facing. Default is same as anchor
     *   top: the direction the block's top is going to facing. Default is TOP
     *   text: the text going to write on the sign's front side. Default is null
     *   backText: the text going to write on the sign's back side. Default is null
     */
    @LuaFunction(mainThread = true)
    public MethodResult placeBlock(@NotNull Map<?, ?> options) throws LuaException {
        ITurtleAccess turtle = automataCore.getPeripheralOwner().getTurtle();
        CompassPeripheral compassPeripheral = Stream.of(TurtleSide.values()).map(side -> turtle.getPeripheral(side) instanceof CompassPeripheral compass ? compass : null).filter(peripheral -> peripheral != null).findFirst().orElse(null);
        if (compassPeripheral == null || !compassPeripheral.isEnabled()) {
            return MethodResult.of(false, "COMPASS_NOT_EQUIPPED");
        }
        int x = TableHelper.optIntField(options, "x", 0);
        int y = TableHelper.optIntField(options, "y", 0);
        int z = TableHelper.optIntField(options, "z", 0);
        final int maxDist = APConfig.PERIPHERALS_CONFIG.compassAccurePlaceRadius.get();
        final int freeDist = APConfig.PERIPHERALS_CONFIG.compassAccurePlaceFreeRadius.get();
        if (Math.abs(x) > maxDist || Math.abs(y) > maxDist || Math.abs(z) > maxDist) {
            return MethodResult.of(null, "OUT_OF_RANGE");
        }
        String anchor = TableHelper.optStringField(options, "anchor", null);
        String front = TableHelper.optStringField(options, "front", null);
        String top = TableHelper.optStringField(options, "top", null);
        Direction anchorDir = anchor != null ? automataCore.validateSide(anchor) : null;
        Direction frontDir = front != null ? automataCore.validateSide(front) : null;
        Direction topDir = top != null ? automataCore.validateSide(top) : null;

        int distance =
            Math.max(0, Math.abs(x) - freeDist) +
            Math.max(0, Math.abs(y) - freeDist) +
            Math.max(0, Math.abs(z) - freeDist);
        return automataCore.withOperation(ACCURE_PLACE, new SingleOperationContext(1, distance), context -> {
            ItemStack stack = turtle.getInventory().getItem(turtle.getSelectedSlot());
            if (stack.isEmpty()) {
                return MethodResult.of(null, "EMPTY_SLOT");
            }
            BlockPos position = turtle.getPosition().offset(x, y, z);
            String err = deployOn(stack, position, anchorDir, frontDir, topDir, options);
            if (err != null) {
                return MethodResult.of(null, err);
            }
            return MethodResult.of(true);
        }, null);
    }

    /**
     * @return A nullable string of the error. <code>null</code> means the operation is successful
     */
    @Nullable
    private String deployOn(ItemStack stack, BlockPos position, Direction anchor, Direction front, Direction top, Map<?, ?> options) throws LuaException {
        ITurtleAccess turtle = automataCore.getPeripheralOwner().getTurtle();
        Level world = turtle.getLevel();
        if (anchor == null) {
            anchor = turtle.getDirection();
        }
        if (front == null) {
            front = anchor;
        }
        if (top == null) {
            top = Direction.UP;
        }
        TurtlePlayer turtlePlayer = TurtlePlayer.getWithPosition(turtle, position, front.getOpposite());
        BlockHitResult hit = BlockHitResult.miss(Vec3.atCenterOf(position), top, position);
        AdvanceDirectionalPlaceContext context = new AdvanceDirectionalPlaceContext(world, position, anchor, front, stack, top);
        PlayerInteractEvent.RightClickBlock event = ForgeHooks.onRightClickBlock(turtlePlayer.player(), InteractionHand.MAIN_HAND, position, hit);
        if (event.isCanceled()) {
            return "EVENT_CANCELED";
        }
        Item item = stack.getItem();
        if (!(item instanceof BlockItem)) {
            return "NOT_BLOCK";
        }
        BlockItem block = (BlockItem) item;
        InteractionResult res = block.place(context);
        if (!res.consumesAction()) {
            return "CANNOT_PLACE";
        }
        if (block instanceof SignItem) {
            BlockEntity blockEntity = world.getBlockEntity(position);
            if (blockEntity instanceof SignBlockEntity sign) {
                String text = StringUtil.convertAndToSectionMark(TableHelper.optStringField(options, "text", null));
                setSignText(world, sign, text, true);
                String backText = StringUtil.convertAndToSectionMark(TableHelper.optStringField(options, "backText", null));
                setSignText(world, sign, backText, false);
            }
        }
        return null;
    }

    private static void setSignText(Level world, SignBlockEntity block, String text, boolean front) {
        SignText sign = block.getText(front);
        if (text == null) {
            for (int i = 0; i < SignText.LINES; i++) {
                sign.setMessage(i, Component.literal(""));
            }
        } else {
            String[] lines = text.split("\n");
            for (int i = 0; i < SignText.LINES; i++) {
                sign.setMessage(i, Component.literal(i < lines.length ? lines[i] : ""));
            }
        }
        block.setChanged();
        world.sendBlockUpdated(block.getBlockPos(), block.getBlockState(), block.getBlockState(), Block.UPDATE_ALL);
    }

    private static class AdvanceDirectionalPlaceContext extends DirectionalPlaceContext {
        private final Direction anchor;

        AdvanceDirectionalPlaceContext(Level world, BlockPos pos, Direction anchor, Direction front, ItemStack stack, Direction top) {
            super(world, pos, front, stack, top);
            this.anchor = anchor;
        }

        @Override
        public Direction getNearestLookingDirection() {
            return this.anchor;
        }

        @Override
        public Direction[] getNearestLookingDirections() {
            return switch (this.anchor) {
                case DOWN -> new Direction[]{Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP};
                case UP -> new Direction[]{Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.DOWN};
                case NORTH -> new Direction[]{Direction.NORTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN, Direction.SOUTH};
                case SOUTH -> new Direction[]{Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN, Direction.NORTH};
                case WEST -> new Direction[]{Direction.WEST, Direction.SOUTH, Direction.UP, Direction.NORTH, Direction.DOWN, Direction.EAST};
                case EAST -> new Direction[]{Direction.EAST, Direction.SOUTH, Direction.UP, Direction.NORTH, Direction.DOWN, Direction.WEST};
            };
        }
    }
}
