package de.srendi.advancedperipherals.common.smartglasses.modules.nightvision;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesSideAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleFunctions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NightVisionModule implements IModule {
    private static final ResourceLocation ID = AdvancedPeripherals.getRL("night_vision");
    private static final int NIGHT_VISION_TICKS = 20 * 13 - 1; // minus 1 tick then the client timing won't flash

    private volatile boolean nightVisionEnabled = true;
    private boolean shouldRemoveEffect = false;

    public NightVisionModule() {
    }

    @Override
    @NotNull
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public String getLuaAlias() {
        return "nightVision";
    }

    @Override
    @Nullable
    public IModuleFunctions getFunctions(SmartGlassesSideAccess access) {
        return new NightVisionFunctions(this);
    }

    @Override
    public void serverTick(SmartGlassesSideAccess access) {
        SmartGlassesComputer computer = access.getComputer();
        if (!computer.isEquipped()) {
            return;
        }
        if (!(computer.getEntity() instanceof LivingEntity entity)) {
            return;
        }
        if (!computer.isOn() || !this.nightVisionEnabled) {
            if (this.shouldRemoveEffect) {
                this.shouldRemoveEffect = false;
                entity.removeEffect(MobEffects.NIGHT_VISION);
            }
            return;
        }
        this.shouldRemoveEffect = true;
        entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, NIGHT_VISION_TICKS, 0, false, false, true));
    }

    @Override
    public void onUnequipped(SmartGlassesSideAccess access) {
        if (this.shouldRemoveEffect) {
            this.shouldRemoveEffect = false;
            if (access.getEntity() instanceof LivingEntity entity) {
                entity.removeEffect(MobEffects.NIGHT_VISION);
            }
        }
    }

    /**
     * isNightVisionEnabled is safety to be called concurrently
     */
    public boolean isNightVisionEnabled() {
        return this.nightVisionEnabled;
    }

    /**
     * enableNightVision is safety to be called concurrently
     */
    public void enableNightVision(boolean enable) {
        this.nightVisionEnabled = enable;
    }
}
