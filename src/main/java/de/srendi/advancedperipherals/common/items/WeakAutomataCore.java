package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.api.metaphysics.IFeedableAutomataCore;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.setup.Items;
import de.srendi.advancedperipherals.common.util.EnumColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
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
        public final Map<String, Integer> ingredients;
        public final Item resultSoul;

        public WeakAutomataCoreRecord(Map<String, Integer> ingredients, Item resultSoul) {
            this.ingredients = ingredients;
            this.resultSoul = resultSoul;
        }

        public int getRequiredCount(String entityKey) {
            return this.ingredients.getOrDefault(entityKey, 0);
        }

        public boolean isSuitable(String entityKey, CompoundNBT consumedData) {
            if (!ingredients.containsKey(entityKey))
                return false;
            int requiredCount = ingredients.get(entityKey);
            int currentCount = consumedData.getCompound(entityKey).getInt(CONSUMED_ENTITY_COUNT);
            return currentCount < requiredCount;
        }

        public boolean isFinished(CompoundNBT consumedData) {
            return ingredients.entrySet().stream().map(entry -> entry.getValue() == consumedData.getCompound(entry.getKey()).getInt(CONSUMED_ENTITY_COUNT)).reduce((a, b) -> a && b).orElse(true);
        }
    }

    public static void addFeedSoulRecipe(WeakAutomataCoreRecord record){
        record.ingredients.keySet().forEach(entityKey -> AUTOMATA_CORE_REGISTRY.put(entityKey, record));
    }

    private final static Map<String, WeakAutomataCoreRecord> AUTOMATA_CORE_REGISTRY = new HashMap<String, WeakAutomataCoreRecord>() {{
        WeakAutomataCoreRecord endSoulRecord = new WeakAutomataCoreRecord(
                new HashMap<String, Integer>(){{
                    put(EntityType.ENDERMAN.getRegistryName().toString(), 10);
                }}, Items.END_AUTOMATA_CORE.get()
        );
        WeakAutomataCoreRecord husbandrySoulRecord = new WeakAutomataCoreRecord(
                new HashMap<String, Integer>() {{
                    put(EntityType.COW.getRegistryName().toString(), 3);
                    put(EntityType.SHEEP.getRegistryName().toString(), 3);
                    put(EntityType.CHICKEN.getRegistryName().toString(), 3);
                    put(EntityType.HORSE.getRegistryName().toString(), 1);
                }}, Items.HUSBANDRY_AUTOMATA_CORE.get()
        );
        endSoulRecord.ingredients.keySet().forEach(entityKey -> put(entityKey, endSoulRecord));
        husbandrySoulRecord.ingredients.keySet().forEach(entityKey -> put(entityKey, husbandrySoulRecord));
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
            WeakAutomataCoreRecord record = AUTOMATA_CORE_REGISTRY.get(key);
            CompoundNBT recordData = consumedData.getCompound(key);
            tooltip.add(EnumColor.buildTextComponent(new StringTextComponent(
                    String.format("Consumed: %d/%d %s", recordData.getInt(CONSUMED_ENTITY_COUNT), record.getRequiredCount(key), recordData.getString(CONSUMED_ENTITY_NAME)))
            ));
        });
    }

    @Override
    public @NotNull ActionResultType interactLivingEntity(@NotNull ItemStack stack, @NotNull PlayerEntity player, @NotNull LivingEntity entity, @NotNull Hand hand) {
        if (!(player instanceof FakePlayer)) {
            player.displayClientMessage(new TranslationTextComponent("text.advancedperipherals.automata_core_feed_by_player"), true);
            return ActionResultType.FAIL;
        }
        String entityCode = EntityType.getKey(entity.getType()).toString();
        if (AUTOMATA_CORE_REGISTRY.containsKey(entityCode)) {
            CompoundNBT tag = stack.getOrCreateTag();
            CompoundNBT consumedData = tag.getCompound(CONSUMER_ENTITY_COMPOUND);
            WeakAutomataCoreRecord record;
            if (consumedData.isEmpty()) {
                record = AUTOMATA_CORE_REGISTRY.get(entityCode);
            } else {
                Optional<String> anyKey = consumedData.getAllKeys().stream().findAny();
                if (!anyKey.isPresent())
                    return ActionResultType.PASS;
                record = AUTOMATA_CORE_REGISTRY.get(anyKey.get());
            }
            if (!record.isSuitable(entityCode, consumedData))
                return ActionResultType.PASS;
            entity.remove();
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
