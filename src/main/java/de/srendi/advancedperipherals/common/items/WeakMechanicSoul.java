package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.common.setup.Items;
import de.srendi.advancedperipherals.common.util.EnumColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeakMechanicSoul extends APItem {

    public static class MechanicalSoulRecord {
        public final int requiredCount;
        public final Class<? extends Entity> entityClass;
        public final Item resultSoul;

        public MechanicalSoulRecord(int requiredCount, Class<? extends Entity> entityClass, Item resultSoul) {
            this.requiredCount = requiredCount;
            this.entityClass = entityClass;
            this.resultSoul = resultSoul;
        }
    }

    // So, I have
    private final static Map<Class<? extends Entity>, Integer> entityRegister = new HashMap<Class<? extends Entity>, Integer>() {{
        put(EndermanEntity.class, 1);
    }};

    private final static Map<Integer, Class<? extends Entity>> reverseEntityRegister = new HashMap<Integer, Class<? extends Entity>>() {{
        entityRegister.forEach((aClass, integer) -> put(integer, aClass));
    }};

    private final static Map<Class<? extends Entity>, MechanicalSoulRecord> MECHANICAL_SOUL_REGISTRY = new HashMap<Class<? extends Entity>, MechanicalSoulRecord>() {{
        put(EndermanEntity.class, new MechanicalSoulRecord(
                10, EndermanEntity.class, Items.END_MECHANIC_SOUL.get()
        ));
    }};

    private final String CONSUMED_ENTITY_INDEX = "consumed_entity_index";
    private final String CONSUMED_ENTITY_COUNT = "consumed_entity_count";
    private final String CONSUMED_ENTITY_NAME = "consumed_entity_name";

    public WeakMechanicSoul(Properties properties, String turtleID, String pocketID, ITextComponent description) {
        super(properties, turtleID, pocketID, description);
    }

    public WeakMechanicSoul(String turtleID, String pocketID, ITextComponent description) {
        super(turtleID, pocketID, description);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        CompoundNBT tag = stack.getOrCreateTag();
        int entityIndex = tag.getInt(CONSUMED_ENTITY_INDEX);
        if (entityIndex != 0) {
            MechanicalSoulRecord record = MECHANICAL_SOUL_REGISTRY.get(reverseEntityRegister.get(entityIndex));
            tooltip.add(EnumColor.buildTextComponent(new StringTextComponent(String.format("Consumed: %d/%d %s", tag.getInt(CONSUMED_ENTITY_COUNT), record.requiredCount, tag.getString(CONSUMED_ENTITY_NAME)))));
        }
    }

    @Override
    public @NotNull ActionResultType interactLivingEntity(@NotNull ItemStack stack, @NotNull PlayerEntity player, @NotNull LivingEntity entity, @NotNull Hand hand) {
        if (!(player instanceof FakePlayer)) {
            player.displayClientMessage(new TranslationTextComponent("text.advancedperipherals.weak_mechanical_player_used_by_player"), true);
            return ActionResultType.FAIL;
        }
        if (MECHANICAL_SOUL_REGISTRY.containsKey(entity.getClass())) {
            int entityIndex = entityRegister.get(entity.getClass());
            CompoundNBT tag = stack.getOrCreateTag();
            int consumedEntityIndex = tag.getInt(CONSUMED_ENTITY_INDEX);
            if (consumedEntityIndex != 0 && consumedEntityIndex != entityIndex) {
                return ActionResultType.PASS;
            }
            MechanicalSoulRecord record = MECHANICAL_SOUL_REGISTRY.get(entity.getClass());
            entity.remove();
            tag.putInt(CONSUMED_ENTITY_INDEX, entityIndex);
            tag.putInt(CONSUMED_ENTITY_COUNT, tag.getInt(CONSUMED_ENTITY_COUNT) + 1);
            tag.putString(CONSUMED_ENTITY_NAME, entity.getName().getString());
            if (tag.getInt(CONSUMED_ENTITY_COUNT) == record.requiredCount) {
                player.setItemInHand(hand, new ItemStack(record.resultSoul));
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }
}
