package de.srendi.advancedperipherals.common.smartglasses.modules.nightvision;

import de.srendi.advancedperipherals.common.items.base.BaseItem;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesSideAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class NightVisionModuleItem extends BaseItem implements IModuleItem<NightVisionModule> {
    private static final int NIGHT_VISION_TICKS = 20 * 13 - 1; // minus 1 tick then the client timing won't flash

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    @NotNull
    public NightVisionModule createModule(SmartGlassesSideAccess access) {
        return new NightVisionModule();
    }

    @Override
    public void moduleTick(Level level, LivingEntity entity, int moduleSlot, SmartGlassesSideAccess access, NightVisionModule module) {
        if (level.isClientSide()) {
            return;
        }

        if (!module.isNightVisionEnabled()) {
            entity.removeEffect(MobEffects.NIGHT_VISION);
            return;
        }
        entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, NIGHT_VISION_TICKS, 0, false, false, true));
    }
}
