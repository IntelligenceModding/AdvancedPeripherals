package de.srendi.advancedperipherals.common.events;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Events {

    private static final String PLAYED_BEFORE = "ap_played_before";

    @SubscribeEvent
    public static void onWorldJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();

        // if (APConfig.WORLD_CONFIG.givePlayerBookOnJoin.get()) {
        //     if (!hasPlayedBefore(player)) {
        //         ItemStack book = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("patchouli", "guide_book")));
        //         CompoundTag nbt = new CompoundTag();
        //         nbt.putString("patchouli:book", "advancedperipherals:manual");
        //         book.setTag(nbt);
        //         player.addItem(book);
        //     }
        // }
    }

    private static boolean hasPlayedBefore(Player player) {
        CompoundTag tag = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        if (tag.getBoolean(PLAYED_BEFORE)) {
            return true;
        } else {
            tag.putBoolean(PLAYED_BEFORE, true);
            player.getPersistentData().put(Player.PERSISTED_NBT_TAG, tag);
            return false;
        }
    }
}
