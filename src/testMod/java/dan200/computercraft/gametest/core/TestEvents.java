package dan200.computercraft.gametest.core;

import net.minecraft.core.BlockPos;
import net.minecraftforge.event.level.NoteBlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = TestMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TestEvents {
    public static final Map<BlockPos, Integer> triggeredNoteBlocks = new HashMap<>();

    @SubscribeEvent
    public static void onNoteBlockTrigger(NoteBlockEvent.Play event) {
        if (event.getLevel().isClientSide()) return;
        triggeredNoteBlocks.put(event.getPos(), triggeredNoteBlocks.getOrDefault(event.getPos(), 0) + 1);
    }

}
