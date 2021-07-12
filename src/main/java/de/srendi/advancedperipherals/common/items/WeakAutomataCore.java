package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.api.metaphysics.IFeedableAutomataCore;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.setup.Items;
import de.srendi.advancedperipherals.common.util.EnumColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
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
import java.util.Optional;

public class WeakAutomataCore extends APItem implements IFeedableAutomataCore {

    private static final String CONSUMED_ENTITY_COUNT = "consumed_entity_count";
    private static final String CONSUMED_ENTITY_NAME = "consumed_entity_name";
    private static final String CONSUMER_ENTITY_COMPOUND = "consumed_entity_compound";

    public static class WeakAutomataCoreRecord {
        public final Map<Class<? extends Entity>, Integer> ingredients;
        public final Item resultSoul;

        public WeakAutomataCoreRecord(Map<Class<? extends Entity>, Integer> ingredients, Item resultSoul) {
            this.ingredients = ingredients;
            this.resultSoul = resultSoul;
        }

        public int getRequiredCount(Class<? extends Entity> entityClass) {
            return this.ingredients.getOrDefault(entityClass, 0);
        }

        public boolean isSuitable(Class<? extends Entity> entityClass, CompoundNBT consumedData) {
            if (!ingredients.containsKey(entityClass))
                return false;
            int requiredCount = ingredients.get(entityClass);
            int currentCount = consumedData.getCompound(entityRegister.get(entityClass).toString()).getInt(CONSUMED_ENTITY_COUNT);
            return currentCount < requiredCount;
        }

        public boolean isFinished(CompoundNBT consumedData) {
            return ingredients.entrySet().stream().map(entry -> {
                String entityCode = entityRegister.get(entry.getKey()).toString();
                return entry.getValue() == consumedData.getCompound(entityCode).getInt(CONSUMED_ENTITY_COUNT);
            }).reduce((a, b) -> a && b).orElse(true);
        }
    }

    private final static Map<Class<? extends Entity>, Integer> entityRegister = new HashMap<Class<? extends Entity>, Integer>() {{
        put(EndermanEntity.class, 1);
        put(CowEntity.class, 2);
        put(SheepEntity.class, 3);
        put(ChickenEntity.class, 4);
        put(HorseEntity.class, 5);
    }};

    private final static Map<Integer, Class<? extends Entity>> reverseEntityRegister = new HashMap<Integer, Class<? extends Entity>>() {{
        entityRegister.forEach((aClass, integer) -> put(integer, aClass));
    }};

    private final static Map<Class<? extends Entity>, WeakAutomataCoreRecord> AUTOMATA_CORE_REGISTRY = new HashMap<Class<? extends Entity>, WeakAutomataCoreRecord>() {{
        WeakAutomataCoreRecord endSoulRecord = new WeakAutomataCoreRecord(
                new HashMap<Class<? extends Entity>, Integer>(){{ put(EndermanEntity.class, 10); }}, Items.END_AUTOMATA_CORE.get()
        );
        WeakAutomataCoreRecord husbandrySoulRecord = new WeakAutomataCoreRecord(
                new HashMap<Class<? extends Entity>, Integer>() {{
                    put(CowEntity.class, 3);
                    put(SheepEntity.class, 3);
                    put(ChickenEntity.class, 3);
                    put(HorseEntity.class, 1);
                }}, Items.HUSBANDRY_AUTOMATA_CORE.get()
        );
        endSoulRecord.ingredients.keySet().forEach(entityClass -> put(entityClass, endSoulRecord));
        husbandrySoulRecord.ingredients.keySet().forEach(entityClass -> put(entityClass, husbandrySoulRecord));
    }};

    public WeakAutomataCore(Properties properties, String turtleID, String pocketID, ITextComponent description) {
        super(properties, turtleID, pocketID, description, () -> AdvancedPeripheralsConfig.enableWeakAutomataCore);
    }

    public WeakAutomataCore(String turtleID, String pocketID, ITextComponent description) {
        super(turtleID, pocketID, description, () -> AdvancedPeripheralsConfig.enableWeakAutomataCore);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        CompoundNBT tag = stack.getOrCreateTag();
        CompoundNBT consumedData = tag.getCompound(CONSUMER_ENTITY_COMPOUND);
        consumedData.getAllKeys().forEach(key -> {
            Class<? extends Entity> entityClass = reverseEntityRegister.get(Integer.parseInt(key));
            WeakAutomataCoreRecord record = AUTOMATA_CORE_REGISTRY.get(entityClass);
            CompoundNBT recordData = consumedData.getCompound(key);
            tooltip.add(EnumColor.buildTextComponent(new StringTextComponent(
                    String.format("Consumed: %d/%d %s", recordData.getInt(CONSUMED_ENTITY_COUNT), record.getRequiredCount(entityClass), recordData.getString(CONSUMED_ENTITY_NAME)))
            ));
        });
    }

    @Override
    public @NotNull ActionResultType interactLivingEntity(@NotNull ItemStack stack, @NotNull PlayerEntity player, @NotNull LivingEntity entity, @NotNull Hand hand) {
        if (!(player instanceof FakePlayer)) {
            player.displayClientMessage(new TranslationTextComponent("text.advancedperipherals.automata_core_feed_by_player"), true);
            return ActionResultType.FAIL;
        }
        Class<? extends Entity> entityClass = entity.getClass();
        if (AUTOMATA_CORE_REGISTRY.containsKey(entityClass)) {
            CompoundNBT tag = stack.getOrCreateTag();
            CompoundNBT consumedData = tag.getCompound(CONSUMER_ENTITY_COMPOUND);
            WeakAutomataCoreRecord record;
            if (consumedData.isEmpty()) {
                record = AUTOMATA_CORE_REGISTRY.get(entityClass);
            } else {
                Optional<String> anyKey = consumedData.getAllKeys().stream().findAny();
                if (!anyKey.isPresent())
                    return ActionResultType.PASS;
                record = AUTOMATA_CORE_REGISTRY.get(reverseEntityRegister.get(Integer.parseInt(anyKey.get())));
            }
            if (!record.isSuitable(entity.getClass(), consumedData))
                return ActionResultType.PASS;
            entity.remove();
            String entityCode = entityRegister.get(entityClass).toString();
            CompoundNBT entityCompound = consumedData.getCompound(entityCode);
            entityCompound.putInt(
                    CONSUMED_ENTITY_COUNT, consumedData.getCompound(entityCode).getInt(CONSUMED_ENTITY_COUNT) + 1
            );
            entityCompound.putString(CONSUMED_ENTITY_NAME, entity.getName().getString());
            consumedData.put(entityCode, entityCompound);
            if (record.isFinished(consumedData)) {
                player.setItemInHand(hand, new ItemStack(record.resultSoul));
            }
            tag.put(CONSUMER_ENTITY_COMPOUND, consumedData);
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }
}
