package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.common.addons.computercraft.base.IFeedableAutomataCore;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.setup.Items;
import de.srendi.advancedperipherals.common.util.EnumColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
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
    private final static Map<String, WeakAutomataCoreRecord> AUTOMATA_CORE_REGISTRY = new HashMap<String, WeakAutomataCoreRecord>() {{
        WeakAutomataCoreRecord endSoulRecord = new WeakAutomataCoreRecord(
                new HashMap<String, Integer>() {{
                    put(EntityType.ENDERMAN.getRegistryName().toString(), 10);
                }}, Items.END_AUTOMATA_CORE.get()
        );
        WeakAutomataCoreRecord husbandrySoulRecord = new WeakAutomataCoreRecord(
                new HashMap<String, Integer>() {{
                    put(EntityType.COW.getRegistryName().toString(), 3);
                    put(EntityType.SHEEP.getRegistryName().toString(), 3);
                    put(EntityType.CHICKEN.getRegistryName().toString(), 3);
                }}, Items.HUSBANDRY_AUTOMATA_CORE.get()
        );
        endSoulRecord.ingredients.keySet().forEach(entityType -> put(entityType, endSoulRecord));
        husbandrySoulRecord.ingredients.keySet().forEach(entityType -> put(entityType, husbandrySoulRecord));
    }};

    public WeakAutomataCore(Properties properties, @Nullable ResourceLocation turtleID, @Nullable ResourceLocation pocketID) {
        super(properties, turtleID, pocketID, () -> AdvancedPeripheralsConfig.enableWeakAutomataCore);
    }

    public WeakAutomataCore(@Nullable ResourceLocation turtleID, @Nullable ResourceLocation pocketID) {
        super(turtleID, pocketID, () -> AdvancedPeripheralsConfig.enableWeakAutomataCore);
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
        String entityType = EntityType.getKey(entity.getType()).toString();
        Class<? extends Entity> entityClass = entity.getClass();
        if (AUTOMATA_CORE_REGISTRY.containsKey(entityType)) {
            CompoundNBT tag = stack.getOrCreateTag();
            CompoundNBT consumedData = tag.getCompound(CONSUMER_ENTITY_COMPOUND);
            WeakAutomataCoreRecord record;
            if (consumedData.isEmpty()) {
                record = AUTOMATA_CORE_REGISTRY.get(entityType);
            } else {
                Optional<String> anyKey = consumedData.getAllKeys().stream().findAny();
                if (!anyKey.isPresent())
                    return ActionResultType.PASS;
                record = AUTOMATA_CORE_REGISTRY.get(anyKey.get());
            }
            if (!record.isSuitable(entityType, consumedData))
                return ActionResultType.PASS;
            entity.remove();
            CompoundNBT entityCompound = consumedData.getCompound(entityType);
            entityCompound.putInt(
                    CONSUMED_ENTITY_COUNT, entityCompound.getInt(CONSUMED_ENTITY_COUNT) + 1
            );
            entityCompound.putString(CONSUMED_ENTITY_NAME, entity.getName().getString());
            consumedData.put(entityType, entityCompound);
            if (record.isFinished(consumedData)) {
                player.setItemInHand(hand, new ItemStack(record.resultSoul));
            }
            tag.put(CONSUMER_ENTITY_COMPOUND, consumedData);
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    public static class WeakAutomataCoreRecord {
        public final Map<String, Integer> ingredients;
        public final Item resultSoul;

        public WeakAutomataCoreRecord(Map<String, Integer> ingredients, Item resultSoul) {
            this.ingredients = ingredients;
            this.resultSoul = resultSoul;
        }

        public int getRequiredCount(String entityType) {
            return this.ingredients.getOrDefault(entityType, 0);
        }

        public boolean isSuitable(String entityType, CompoundNBT consumedData) {
            if (!ingredients.containsKey(entityType))
                return false;
            int requiredCount = ingredients.get(entityType);
            int currentCount = consumedData.getCompound(entityType).getInt(CONSUMED_ENTITY_COUNT);
            return currentCount < requiredCount;
        }

        public boolean isFinished(CompoundNBT consumedData) {
            return ingredients.entrySet().stream().map(entry -> entry.getValue() == consumedData.getCompound(entry.getKey()).getInt(CONSUMED_ENTITY_COUNT)).reduce((a, b) -> a && b).orElse(true);
        }
    }
}
