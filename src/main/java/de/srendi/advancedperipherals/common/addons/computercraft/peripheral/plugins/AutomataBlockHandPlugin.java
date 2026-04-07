package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.plugins;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.LuaTable;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.shared.turtle.core.TurtlePlayer;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.SingleOperationContext;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.TurtlePeripheralOwner;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.CompassPeripheral;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.EmptyLuaTable;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static de.srendi.advancedperipherals.common.addons.computercraft.operations.SingleOperation.*;

public class AutomataBlockHandPlugin extends AutomataCorePlugin {

    public AutomataBlockHandPlugin(AutomataCorePeripheral automataCore) {
        super(automataCore);
    }

    @Override
    public @Nullable IPeripheralOperation<?>[] getOperations() {
        return new IPeripheralOperation[]{ACCURE_PLACE, DIG, UPDATE_BLOCK, USE_ON_BLOCK};
    }

    @LuaFunction(mainThread = true)
    public final MethodResult digBlock(@NotNull IArguments arguments) throws LuaException {
        LuaTable<?, ?> options = EmptyLuaTable.orEmpty(arguments.optTable(0).orElse(null));

        boolean sneak = options.optBoolean("sneak").orElse(false);
        float yaw = options.optDouble("yaw").orElse(0d).floatValue();
        float pitch = options.optDouble("pitch").orElse(0d).floatValue();

        return automataCore.withOperation(DIG, context -> {
            TurtlePeripheralOwner owner = automataCore.getPeripheralOwner();
            ItemStack selectedTool = owner.getToolInMainHand();
            int previousDamageValue = selectedTool.getDamageValue();
            Pair<Boolean, String> result = owner.withPlayer(APFakePlayer.wrapActionWithShiftKey(sneak, APFakePlayer.wrapActionWithRot(yaw, pitch, APFakePlayer::digBlock)));
            if (!result.left()) {
                return MethodResult.of(false, result.right());
            }
            if (automataCore.canActiveOverpower() && automataCore.afterOverpowerAction()) {
                selectedTool.setDamageValue(previousDamageValue);
            }
            return MethodResult.of(true, result.right());
        });
    }

    @LuaFunction(mainThread = true)
    public final MethodResult useOnBlock(@NotNull IArguments arguments) throws LuaException {
        LuaTable<?, ?> options = EmptyLuaTable.orEmpty(arguments.optTable(0).orElse(null));

        boolean sneak = options.optBoolean("sneak").orElse(false);
        float yaw = options.optDouble("yaw").orElse(0d).floatValue();
        float pitch = options.optDouble("pitch").orElse(0d).floatValue();

        return automataCore.withOperation(USE_ON_BLOCK, context -> {
            TurtlePeripheralOwner owner = automataCore.getPeripheralOwner();
            ItemStack selectedTool = owner.getToolInMainHand();
            int previousDamageValue = selectedTool.getDamageValue();
            InteractionResult result = owner.withPlayer(APFakePlayer.wrapActionWithShiftKey(sneak, APFakePlayer.wrapActionWithRot(yaw, pitch, APFakePlayer::useOnBlock)));
            if (result.consumesAction() && automataCore.canActiveOverpower() && automataCore.afterOverpowerAction()) {
                selectedTool.setDamageValue(previousDamageValue);
            }
            return MethodResult.of(result.consumesAction(), result.toString());
        });
    }

    /**
     * updateBlock method let turtle update specific block's status.
     * It require a compass to be equipped to perform actions.
     *
     * @param arguments A table contains where to find the block and how to update the block
     *                  yaw: relative yaw
     *                  pitch: relative pitch
     *                  <p>
     *                  text: the text going to write on the sign's front side. Default is null
     *                  backText: the text going to write on the sign's back side. Default is null
     */
    @LuaFunction(mainThread = true)
    public final MethodResult updateBlock(@NotNull IArguments arguments) throws LuaException {
        if (!automataCore.getPeripheralOwner().hasConnectedPeripheral(CompassPeripheral.class)) {
            return MethodResult.of(false, "COMPASS_NOT_EQUIPPED");
        }
        LuaTable<?, ?> opts = EmptyLuaTable.orEmpty(arguments.getTable(0));
        float yaw = opts.optFiniteDouble("yaw").orElse(0.0).floatValue();
        float pitch = opts.optFiniteDouble("pitch").orElse(0.0).floatValue();
        return automataCore.withOperation(UPDATE_BLOCK, context -> {
            TurtlePeripheralOwner owner = automataCore.getPeripheralOwner();
            ItemStack selectedTool = owner.getToolInMainHand();
            int previousDamageValue = selectedTool.getDamageValue();
            InteractionResult result = owner.withPlayer(APFakePlayer.wrapActionWithRot(yaw, pitch, (player) -> this.updateBlock(player, opts)));
            if (result.consumesAction() && automataCore.canActiveOverpower() && automataCore.afterOverpowerAction()) {
                selectedTool.setDamageValue(previousDamageValue);
            }
            return MethodResult.of(result.consumesAction(), result.toString());
        });
    }

    private InteractionResult updateBlock(APFakePlayer player, LuaTable<?, ?> options) throws LuaException {
        Level world = player.level();
        HitResult hit = player.findHit(true, false);
        if (!(hit instanceof BlockHitResult blockHit)) {
            return InteractionResult.PASS;
        }
        BlockPos pos = blockHit.getBlockPos();
        BlockEntity block = world.getBlockEntity(pos);
        if (block instanceof SignBlockEntity sign) {
            String text = StringUtil.convertAndToSectionMark(options.optString("text").orElse(null));
            if (text != null) {
                setSignText(world, sign, text, true);
            }
            String backText = StringUtil.convertAndToSectionMark(options.optString("backText").orElse(null));
            if (backText != null) {
                setSignText(world, sign, backText, false);
            }
            if (text != null || backText != null) {
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.PASS;
    }

    /**
     * placeBlock method will let turtle place a block with more details when compass has equipped.
     * It should not able to place fluids / use any item, because compass does not recognize them.
     *
     * @param arguments A table contains how to place the block:
     *                  x: the x offset relative to the turtle. Default 0
     *                  y: the y offset relative to the turtle. Default 0
     *                  z: the z offset relative to the turtle. Default 0
     *                  anchor: the direction the block is going to hanging on. Default is the direction of the turtle
     *                  front: the direction the block is going to facing. Default is same as anchor
     *                  top: the direction the block's top is going to facing. Default is TOP
     *                  text: the text going to write on the sign's front side. Default is null
     *                  backText: the text going to write on the sign's back side. Default is null
     */
    @LuaFunction(mainThread = true)
    public final MethodResult placeBlock(@NotNull IArguments arguments) throws LuaException {
        LuaTable<?, ?> options = EmptyLuaTable.orEmpty(arguments.getTable(0));

        ITurtleAccess turtle = automataCore.getPeripheralOwner().getTurtle();
        if (!automataCore.getPeripheralOwner().hasConnectedPeripheral(CompassPeripheral.class)) {
            return MethodResult.of(false, "COMPASS_NOT_EQUIPPED");
        }
        int x = options.optInt("x").orElse(0);
        int y = options.optInt("y").orElse(0);
        int z = options.optInt("z").orElse(0);
        final int maxDist = APConfig.PERIPHERALS_CONFIG.compassAccurePlaceRadius.get();
        final int freeDist = APConfig.PERIPHERALS_CONFIG.compassAccurePlaceFreeRadius.get();
        if (Math.abs(x) > maxDist || Math.abs(y) > maxDist || Math.abs(z) > maxDist) {
            return MethodResult.of(null, "OUT_OF_RANGE");
        }
        String anchor = options.optString("anchor").orElse(null);
        String front = options.optString("front").orElse(null);
        String top = options.optString("top").orElse(null);
        Direction anchorDir = anchor != null ? automataCore.mapDirection(anchor) : null;
        Direction frontDir = front != null ? automataCore.mapDirection(front) : null;
        Direction topDir = top != null ? automataCore.mapDirection(top) : null;

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
    private String deployOn(ItemStack stack, BlockPos position, Direction anchor, Direction front, Direction top, LuaTable<?, ?> options) throws LuaException {
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
        PlayerInteractEvent.RightClickBlock event = CommonHooks.onRightClickBlock(turtlePlayer.player(), InteractionHand.MAIN_HAND, position, hit);
        if (event.isCanceled()) {
            return "EVENT_CANCELED";
        }
        Item item = stack.getItem();
        if (!(item instanceof BlockItem blockItem)) {
            return "NOT_BLOCK";
        }
        InteractionResult res = blockItem.place(context);
        if (!res.consumesAction()) {
            return "CANNOT_PLACE";
        }
        if (blockItem instanceof SignItem) {
            BlockEntity blockEntity = world.getBlockEntity(position);
            if (blockEntity instanceof SignBlockEntity sign) {
                String text = StringUtil.convertAndToSectionMark(options.optString("text").orElse(null));
                setSignText(world, sign, text, true);
                String backText = StringUtil.convertAndToSectionMark(options.optString("backText").orElse(null));
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
                case DOWN ->
                        new Direction[]{Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP};
                case UP ->
                        new Direction[]{Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.DOWN};
                case NORTH ->
                        new Direction[]{Direction.NORTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN, Direction.SOUTH};
                case SOUTH ->
                        new Direction[]{Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN, Direction.NORTH};
                case WEST ->
                        new Direction[]{Direction.WEST, Direction.SOUTH, Direction.UP, Direction.NORTH, Direction.DOWN, Direction.EAST};
                case EAST ->
                        new Direction[]{Direction.EAST, Direction.SOUTH, Direction.UP, Direction.NORTH, Direction.DOWN, Direction.WEST};
            };
        }
    }
}
