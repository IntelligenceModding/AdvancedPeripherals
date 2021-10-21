package de.srendi.advancedperipherals.common.addons.computercraft.integrations;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.BlockIntegrationPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class NoteblockIntegration extends BlockIntegrationPeripheral<NoteBlock> {

    public NoteblockIntegration(Level level, BlockPos pos) {
        super(level, pos);
    }

    @Nonnull
    @Override
    public String getType() {
        return "noteBlock";
    }

    @LuaFunction(mainThread = true)
    public int changeNote() {
        BlockState state = level.getBlockState(pos);
        int newNote = net.minecraftforge.common.ForgeHooks.onNoteChange(level, pos, state, state.getValue(NoteBlock.NOTE), state.cycle(NoteBlock.NOTE).getValue(NoteBlock.NOTE));
        if (newNote == -1) return -1;
        state = state.setValue(NoteBlock.NOTE, newNote);
        level.setBlock(pos, state, 3);
        return newNote;
    }

    @LuaFunction(mainThread = true)
    public int changeNoteBy(int note) throws LuaException {
        BlockState state = level.getBlockState(pos);
        if (!(note >= 0 && note <= 24))
            throw new LuaException("Note argument need to be in a range of 0 and 24");
        state = state.setValue(NoteBlock.NOTE, note);
        level.setBlock(pos, state, 3);
        return note;
    }

    @LuaFunction(mainThread = true)
    public int getNote() {
        BlockState state = level.getBlockState(pos);
        return state.getValue(NoteBlock.NOTE);
    }

    @LuaFunction(mainThread = true)
    public void playNote() {
        if (level.isEmptyBlock(pos.above())) {
            level.blockEvent(pos, getBlock(), 0, 0);
        }
    }
}
