package dan200.computercraft.gametest.core;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ToastAddEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = TestMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientTestEvents {
    public static final List<Toast> toasts = Lists.newArrayList();

    @SubscribeEvent
    public static void onToast(ToastAddEvent event) {
        toasts.add(event.getToast());
    }

}
