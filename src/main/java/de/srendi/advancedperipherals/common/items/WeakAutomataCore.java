package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.items.base.BaseItem;
import de.srendi.advancedperipherals.common.setup.APItems;
import de.srendi.advancedperipherals.common.util.EnumColor;
import de.srendi.advancedperipherals.lib.metaphysics.IFeedableAutomataCore;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class WeakAutomataCore extends BaseItem implements IFeedableAutomataCore {

    private static final String CONSUMED_ENTITY_COUNT = "consumed_entity_count";
    private static final String CONSUMED_ENTITY_NAME = "consumed_entity_name";
    private static final String CONSUMER_ENTITY_COMPOUND = "consumed_entity_compound";
    private static final Map<String, WeakAutomataCoreRecord> AUTOMATA_CORE_REGISTRY = new HashMap<>();

    static {
        Map<String, Integer> endSouls = new HashMap<>();
        endSouls.put(ForgeRegistries.ENTITY_TYPES.getKey(EntityType.ENDERMAN).toString(), 10);
        WeakAutomataCoreRecord endSoulRecord = new WeakAutomataCoreRecord(endSouls, APItems.END_AUTOMATA_CORE.get());

        Map<String, Integer> husbandrySouls = new HashMap<>();
        husbandrySouls.put(ForgeRegistries.ENTITY_TYPES.getKey(EntityType.COW).toString(), 3);
        husbandrySouls.put(ForgeRegistries.ENTITY_TYPES.getKey(EntityType.SHEEP).toString(), 3);
        husbandrySouls.put(ForgeRegistries.ENTITY_TYPES.getKey(EntityType.CHICKEN).toString(), 3);
        WeakAutomataCoreRecord husbandrySoulRecord = new WeakAutomataCoreRecord(husbandrySouls, APItems.HUSBANDRY_AUTOMATA_CORE.get());

        endSoulRecord.ingredients.keySet().forEach(entityType -> AUTOMATA_CORE_REGISTRY.put(entityType, endSoulRecord));
        husbandrySoulRecord.ingredients.keySet().forEach(entityType -> AUTOMATA_CORE_REGISTRY.put(entityType, husbandrySoulRecord));
    }

    public WeakAutomataCore(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isEnabled() {
        return APConfig.METAPHYSICS_CONFIG.enableWeakAutomataCore.get();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag consumedData = tag.getCompound(CONSUMER_ENTITY_COMPOUND);
        consumedData.getAllKeys().forEach(key -> {
            WeakAutomataCoreRecord record = AUTOMATA_CORE_REGISTRY.get(key);
            CompoundTag recordData = consumedData.getCompound(key);
            tooltip.add(EnumColor.buildTextComponent(Component.literal(String.format("Consumed: %d/%d %s", recordData.getInt(CONSUMED_ENTITY_COUNT), record.getRequiredCount(key), recordData.getString(CONSUMED_ENTITY_NAME)))));
        });
    }

    @Override
    @NotNull
    public InteractionResult interactLivingEntity(@NotNull ItemStack stack, @NotNull Player player, @NotNull LivingEntity entity, @NotNull InteractionHand hand) {
        if (!(player instanceof FakePlayer)) {
            player.displayClientMessage(Component.translatable("text.advancedperipherals.automata_core_feed_by_player"), true);
            return InteractionResult.FAIL;
        }
        String entityType = EntityType.getKey(entity.getType()).toString();
        if (AUTOMATA_CORE_REGISTRY.containsKey(entityType)) {
            CompoundTag tag = stack.getOrCreateTag();
            CompoundTag consumedData = tag.getCompound(CONSUMER_ENTITY_COMPOUND);
            WeakAutomataCoreRecord record;
            if (consumedData.isEmpty()) {
                record = AUTOMATA_CORE_REGISTRY.get(entityType);
            } else {
                Optional<String> anyKey = consumedData.getAllKeys().stream().findAny();
                if (!anyKey.isPresent()) return InteractionResult.PASS;
                record = AUTOMATA_CORE_REGISTRY.get(anyKey.get());
            }
            if (!record.isSuitable(entityType, consumedData)) return InteractionResult.PASS;
            entity.remove(Entity.RemovalReason.KILLED);
            CompoundTag entityCompound = consumedData.getCompound(entityType);
            entityCompound.putInt(CONSUMED_ENTITY_COUNT, entityCompound.getInt(CONSUMED_ENTITY_COUNT) + 1);
            entityCompound.putString(CONSUMED_ENTITY_NAME, entity.getName().getString());
            consumedData.put(entityType, entityCompound);
            if (record.isFinished(consumedData)) {
                player.setItemInHand(hand, new ItemStack(record.resultSoul));
            }
            tag.put(CONSUMER_ENTITY_COMPOUND, consumedData);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public record WeakAutomataCoreRecord(Map<String, Integer> ingredients, Item resultSoul) {

        public int getRequiredCount(String entityType) {
            return this.ingredients.getOrDefault(entityType, 0);
        }

        public boolean isSuitable(String entityType, CompoundTag consumedData) {
            if (!ingredients.containsKey(entityType))
                return false;
            int requiredCount = ingredients.get(entityType);
            int currentCount = consumedData.getCompound(entityType).getInt(CONSUMED_ENTITY_COUNT);
            return currentCount < requiredCount;
        }

        public boolean isFinished(CompoundTag consumedData) {
            return ingredients.entrySet().stream().map(entry -> entry.getValue() == consumedData.getCompound(entry.getKey()).getInt(CONSUMED_ENTITY_COUNT)).reduce((a, b) -> a && b).orElse(true);
        }
    }
}
