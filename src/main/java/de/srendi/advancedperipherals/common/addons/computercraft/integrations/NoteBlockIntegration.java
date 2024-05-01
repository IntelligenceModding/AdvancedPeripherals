package de.srendi.advancedperipherals.common.addons.computercraft.integrations;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.BlockIntegrationPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

public class NoteBlockIntegration extends BlockIntegrationPeripheral<NoteBlock> {

    public NoteBlockIntegration(Level world, BlockPos pos) {
        super(world, pos);
    }

    @NotNull
    @Override
    public String getType() {
        return "noteBlock";
    }

    @LuaFunction(mainThread = true)
    public final int changeNote() {
        BlockState state = world.getBlockState(pos);
        int newNote = net.minecraftforge.common.ForgeHooks.onNoteChange(world, pos, state, state.getValue(NoteBlock.NOTE), state.cycle(NoteBlock.NOTE).getValue(NoteBlock.NOTE));
        if (newNote == -1) return -1;
        state = state.setValue(NoteBlock.NOTE, newNote);
        world.setBlock(pos, state, 3);
        return newNote;
    }

    @LuaFunction(mainThread = true)
    public final int changeNoteBy(int note) throws LuaException {
        BlockState state = world.getBlockState(pos);
        if (!(note >= 0 && note <= 24)) throw new LuaException("Note argument need to be in a range of 0 and 24");
        state = state.setValue(NoteBlock.NOTE, note);
        world.setBlock(pos, state, 3);
        return note;
    }

    @LuaFunction(mainThread = true)
    public final int getNote() {
        BlockState state = world.getBlockState(pos);
        return state.getValue(NoteBlock.NOTE);
    }

    @LuaFunction(mainThread = true)
    public final void playNote() {
        if (world.isEmptyBlock(pos.above())) {
            world.blockEvent(pos, getBlock(), 0, 0);
            world.gameEvent(null, GameEvent.NOTE_BLOCK_PLAY, pos);
        }
    }
}
