package de.srendi.advancedperipherals.common.smartglasses.modules.nightvision;

import de.srendi.advancedperipherals.common.items.base.BaseItem;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class NightVisionModuleItem extends BaseItem implements IModuleItem {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public IModule createModule(SmartGlassesAccess access) {
        return new NightVisionModule();
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int inventorySlot, boolean isCurrentItem, @Nullable SmartGlassesAccess access, @Nullable IModule module) {
        if (level.isClientSide() || !(entity instanceof Player player))
            return;

        if (module instanceof NightVisionModule nightVisionModule) {
            if (nightVisionModule.nightVisionEnabled) {
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, Integer.MAX_VALUE));
            } else {
                player.removeEffect(MobEffects.NIGHT_VISION);
            }
        }
    }
}
