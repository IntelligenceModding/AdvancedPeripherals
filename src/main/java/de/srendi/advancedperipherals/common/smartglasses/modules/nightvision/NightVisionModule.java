package de.srendi.advancedperipherals.common.smartglasses.modules.nightvision;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesSideAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleFunctions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NightVisionModule implements IModule {
    private static final ResourceLocation ID = AdvancedPeripherals.getRL("night_vision");

    private volatile boolean nightVisionEnabled = true;

    public NightVisionModule() {
    }

    @Override
    @NotNull
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    @Nullable
    public IModuleFunctions getFunctions(SmartGlassesSideAccess access) {
        return new NightVisionFunctions(this);
    }

    @Override
    public void onUnequipped(SmartGlassesSideAccess access) {
        if (access.getEntity() instanceof Player player) {
            player.removeEffect(MobEffects.NIGHT_VISION);
        }
    }

    /**
     * isNightVisionEnabled is safety to be called concurrently
     */
    public boolean isNightVisionEnabled() {
        return nightVisionEnabled;
    }

    /**
     * enableNightVision is safety to be called concurrently
     */
    public void enableNightVision(boolean enable) {
        nightVisionEnabled = enable;
    }
}
