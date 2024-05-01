package de.srendi.advancedperipherals.common.smartglasses.modules.nightvision;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleFunctions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class NightVisionModule implements IModule {

    public boolean nightVisionEnabled = true;

    public NightVisionModule() {

    }

    @Override
    public ResourceLocation getName() {
        return AdvancedPeripherals.getRL("night_vision");
    }

    @Override
    @Nullable
    public IModuleFunctions getFunctions(SmartGlassesAccess smartGlassesAccess) {
        return new NightVisionFunctions(this);
    }

    @Override
    public void onUnequipped(SmartGlassesAccess smartGlassesAccess) {
        if (smartGlassesAccess.getEntity() != null) {
            if (smartGlassesAccess.getEntity() instanceof Player player) {
                player.removeEffect(MobEffects.NIGHT_VISION);
            }
        }

    }

    public boolean isNightVisionEnabled() {
        return nightVisionEnabled;
    }

    public void enableNightVision(boolean enable) {
        nightVisionEnabled = enable;
    }
}
