package de.srendi.advancedperipherals.common.smartglasses;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.IComputerSystem;
import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.LuaTable;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.common.entity.SmartChestHand;
import de.srendi.advancedperipherals.common.items.SmartChestMountItem;
import de.srendi.advancedperipherals.common.setup.APComputerComponents;
import de.srendi.advancedperipherals.common.smartchestmount.SmartChestMountItemHandler;
import de.srendi.advancedperipherals.common.util.CoordUtil;
import de.srendi.advancedperipherals.common.util.EmptyLuaTable;
import de.srendi.advancedperipherals.common.util.LuaArgsHelper;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import de.srendi.advancedperipherals.common.util.fakeplayer.SmartHandFakePlayerProvider;
import de.srendi.advancedperipherals.common.util.inventory.ItemFilter;
import de.srendi.advancedperipherals.common.util.inventory.ItemUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;
import net.neoforged.neoforge.items.wrapper.PlayerInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class SmartGlassesAPI implements ILuaAPI {
    private final Supplier<SmartGlassesComputer> equipped;

    private SmartGlassesAPI(Supplier<SmartGlassesComputer> equipped) {
        this.equipped = equipped;
    }

    public static ILuaAPI create(IComputerSystem system) {
        final Supplier<SmartGlassesComputer> smartGlassesEquipped = system.getComponent(APComputerComponents.SMARTGLASSES_EQUIPPED);
        if (smartGlassesEquipped == null) {
            return null;
        }
        return new SmartGlassesAPI(smartGlassesEquipped);
    }

    @NotNull
    private SmartGlassesComputer getComputer() {
        return this.equipped.get();
    }

    @Override
    public String[] getNames() {
        return new String[]{"smartglasses"};
    }

    /**
     * isEquipped check if the smart glasses is equipped.
     * Only equipped smart glasses tick its modules.
     *
     * @return if the smart glasses is equipped
     */
    @LuaFunction(mainThread = true)
    public boolean isEquipped() {
        return this.getComputer().isEquipped();
    }

    @LuaFunction(mainThread = true)
    public Map<String, Object> getOwner() {
        Entity entity = this.getComputer().getEntity();
        if (entity == null) {
            return null;
        }
        if (!this.getComputer().isEquipped()) {
            return null;
        }

        Map<String, Object> data = LuaConverter.entityToLua(entity, LuaConverter.entityContextBuilder().detailed().build());
        CoordUtil.putXYZCoords(data, entity.getX(), entity.getY(), entity.getZ());
        return data;
    }

    private IItemHandler getOwnerInventory() {
        if (this.getComputer().getEntity() instanceof Player player) {
            return new PlayerInvWrapper(player.getInventory());
        }
        return EmptyItemHandler.INSTANCE;
    }

    private ItemStack getSmartChest() {
        Entity entity = this.getComputer().getEntity();
        if (entity instanceof LivingEntity livingEntity) {
            return SmartChestMountItem.getEquipped(livingEntity);
        }
        return ItemStack.EMPTY;
    }

    @LuaFunction(mainThread = true)
    public MethodResult pingSmartChest() {
        ItemStack stack = this.getSmartChest();
        if (stack.isEmpty()) {
            return MethodResult.of(false, "SMART_CHEST_NOT_EQUIPPED");
        }
        return MethodResult.of(true, SmartChestMountItemHandler.SLOTS);
    }

    @LuaFunction(mainThread = true)
    public MethodResult smartChestImportItem(Optional<Map<?, ?>> filterTable) throws LuaException {
        ItemStack stack = this.getSmartChest();
        if (stack.isEmpty()) {
            return MethodResult.of(null, "SMART_CHEST_NOT_EQUIPPED");
        }

        Pair<ItemFilter, String> filter = ItemFilter.parse(EmptyLuaTable.orEmpty(filterTable.orElse(null)));
        if (filter.rightPresent()) {
            return MethodResult.of(null, filter.right());
        }

        IItemHandler inventoryTo = this.getOwnerInventory();
        IItemHandler inventoryFrom = ((SmartChestMountItem) stack.getItem()).createItemHandlerCap(stack);
        return MethodResult.of(ItemUtil.moveItem(inventoryFrom, inventoryTo, filter.left()));
    }

    @LuaFunction(mainThread = true)
    public MethodResult smartChestExportItem(Optional<Map<?, ?>> filterTable) throws LuaException {
        ItemStack stack = this.getSmartChest();
        if (stack.isEmpty()) {
            return MethodResult.of(null, "SMART_CHEST_NOT_EQUIPPED");
        }

        Pair<ItemFilter, String> filter = ItemFilter.parse(EmptyLuaTable.orEmpty(filterTable.orElse(null)));
        if (filter.rightPresent()) {
            return MethodResult.of(null, filter.right());
        }

        IItemHandler inventoryFrom = this.getOwnerInventory();
        IItemHandler inventoryTo = ((SmartChestMountItem) stack.getItem()).createItemHandlerCap(stack);
        return MethodResult.of(ItemUtil.moveItem(inventoryFrom, inventoryTo, filter.left()));
    }

    @LuaFunction(mainThread = true)
    public MethodResult smartHandPos(int index) {
        index--;

        if (!(this.getComputer().getEntity() instanceof LivingEntity livingEntity)) {
            return MethodResult.of(null, "SMART_CHEST_NOT_EQUIPPED");
        }
        ItemStack stack = SmartChestMountItem.getEquipped(livingEntity);
        if (stack.isEmpty()) {
            return MethodResult.of(null, "SMART_CHEST_NOT_EQUIPPED");
        }
        if (index < 0 || index >= SmartChestMountItemHandler.SLOTS) {
            return MethodResult.of(null, "HAND_DOES_NOT_EXISTS");
        }

        SmartChestMountItem.DataStorage chestData = this.getComputer().chestDataStorage;
        SmartChestHand hand = chestData.getOrCreateHand(index, livingEntity, stack);
        Vector3f pos = hand.getRelativePos();
        return MethodResult.of(pos.x, pos.y, pos.z);
    }

    @LuaFunction(mainThread = true)
    public MethodResult smartHandMove(int index, double x, double y, double z) {
        index--;

        if (!(this.getComputer().getEntity() instanceof LivingEntity livingEntity)) {
            return MethodResult.of(false, "SMART_CHEST_NOT_EQUIPPED");
        }
        ItemStack stack = SmartChestMountItem.getEquipped(livingEntity);
        if (stack.isEmpty()) {
            return MethodResult.of(false, "SMART_CHEST_NOT_EQUIPPED");
        }
        if (index < 0 || index >= SmartChestMountItemHandler.SLOTS) {
            return MethodResult.of(false, "HAND_DOES_NOT_EXISTS");
        }

        SmartChestMountItem.DataStorage chestData = this.getComputer().chestDataStorage;
        chestData.getOrCreateHand(index, livingEntity, stack).setRelativePos((float) x, (float) y, (float) z);
        return MethodResult.of(true);
    }

    @LuaFunction(mainThread = true)
    public MethodResult smartHandDropItem(int index, Optional<Integer> optCount) {
        index--;

        if (!(this.getComputer().getEntity() instanceof LivingEntity livingEntity)) {
            return MethodResult.of(null, "SMART_CHEST_NOT_EQUIPPED");
        }
        ItemStack stack = SmartChestMountItem.getEquipped(livingEntity);
        if (stack.isEmpty()) {
            return MethodResult.of(null, "SMART_CHEST_NOT_EQUIPPED");
        }

        if (index < 0 || index >= SmartChestMountItemHandler.SLOTS) {
            return MethodResult.of(null, "HAND_DOES_NOT_EXISTS");
        }

        IItemHandlerModifiable chestItemHandler = ((SmartChestMountItem) stack.getItem()).createItemHandlerCap(stack);
        ItemStack extracted = chestItemHandler.extractItem(index, optCount.orElse(Integer.MAX_VALUE), false);
        if (extracted.isEmpty()) {
            return MethodResult.of(0);
        }

        SmartChestMountItem.DataStorage chestData = this.getComputer().chestDataStorage;
        SmartChestHand hand = chestData.getOrCreateHand(index, livingEntity, stack);
        Level level = hand.level();
        ItemEntity itemEntity = new ItemEntity(level, hand.getX(), hand.getY(), hand.getZ(), extracted);
        itemEntity.setThrower(livingEntity);
        if (livingEntity instanceof Player player) {
            ItemTossEvent event = new ItemTossEvent(itemEntity, player);
            if (NeoForge.EVENT_BUS.post(event).isCanceled()) {
                itemEntity = null;
            }
        }
        if (itemEntity != null) {
            level.addFreshEntity(itemEntity);
        }

        return MethodResult.of(extracted.getCount());
    }

    @LuaFunction(mainThread = true)
    public MethodResult smartHandCollectItem(IArguments arguments) throws LuaException {
        int index = arguments.getInt(0) - 1;

        LuaArgsHelper.Args uargs = LuaArgsHelper.getUnorderedArgs(arguments, 1, Number.class, String.class);
        int needs = uargs.get(Number.class, Integer.MAX_VALUE).intValue();
        String filter = uargs.get(String.class);

        Predicate<Entity> tester = (e) -> true;
        if (filter != null) {
            if (filter.length() > 0 && filter.charAt(0) == '#') {
                ResourceLocation id = ResourceLocation.tryParse(filter.substring(1));
                if (id == null) {
                    throw new LuaException("argument #1 is an invalid tag ID");
                }
                TagKey<EntityType<?>> tag = TagKey.create(Registries.ENTITY_TYPE, id);
                tester = (e) -> e.getType().is(tag);
            } else {
                ResourceLocation id = ResourceLocation.tryParse(filter);
                if (id == null) {
                    throw new LuaException("argument #1 is an invalid entity type");
                }
                EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(id);
                tester = entityType == null ? null : (e) -> e.getType() == entityType;
            }
        }

        if (!(this.getComputer().getEntity() instanceof LivingEntity livingEntity)) {
            return MethodResult.of(null, "SMART_CHEST_NOT_EQUIPPED");
        }
        ItemStack stack = SmartChestMountItem.getEquipped(livingEntity);
        if (stack.isEmpty()) {
            return MethodResult.of(null, "SMART_CHEST_NOT_EQUIPPED");
        }
        if (index < 0 || index >= SmartChestMountItemHandler.SLOTS) {
            return MethodResult.of(null, "HAND_DOES_NOT_EXISTS");
        }

        if (tester == null) {
            return MethodResult.of(0);
        }

        IItemHandlerModifiable chestItemHandler = ((SmartChestMountItem) stack.getItem()).createItemHandlerCap(stack);
        SmartChestMountItem.DataStorage chestData = this.getComputer().chestDataStorage;
        SmartChestHand hand = chestData.getOrCreateHand(index, livingEntity, stack);
        Level level = hand.level();

        ItemStack collecting = chestItemHandler.getStackInSlot(index);
        int initCount = collecting.getCount();
        needs = Math.min(needs, collecting.getMaxStackSize() - initCount);
        if (needs <= 0) {
            return MethodResult.of(0);
        }

        collecting = collecting.copy();

        for (Entity pickingEntity : level.getEntities(hand, hand.getBoundingBox().inflate(0.4f), tester)) {
            if (needs <= 0) {
                break;
            }
            if (pickingEntity instanceof ItemEntity ie) {
                ItemStack pickingStack = ie.getItem();
                if (!collecting.isEmpty() && !ItemStack.isSameItemSameComponents(collecting, pickingStack)) {
                    continue;
                }
                if (livingEntity instanceof Player player) {
                    if (net.neoforged.neoforge.event.EventHooks.fireItemPickupPre(ie, player).canPickup().isFalse()) {
                        continue;
                    }
                }
                ItemStack pickingStackCopy = pickingStack.copy();
                int transferring = Math.min(pickingStack.getCount(), needs);
                needs -= transferring;
                if (collecting.isEmpty()) {
                    collecting = pickingStack.copyWithCount(transferring);
                } else {
                    collecting.grow(transferring);
                }
                pickingStack.shrink(transferring);
                if (livingEntity instanceof Player player) {
                   net.neoforged.neoforge.event.EventHooks.fireItemPickupPost(ie, player, pickingStackCopy);
                }
                if (pickingStack.isEmpty()) {
                    ie.discard();
                }
                livingEntity.onItemPickup(ie);
                continue;
            }
            if (pickingEntity instanceof AbstractArrow arrow) {
                if (arrow.pickup != AbstractArrow.Pickup.ALLOWED) {
                    continue;
                }
                ItemStack pickingStack = arrow.getPickupItemStackOrigin().copy();
                if (!collecting.isEmpty() && !ItemStack.isSameItemSameComponents(collecting, pickingStack)) {
                    continue;
                }
                int transferring = Math.min(pickingStack.getCount(), needs);
                needs -= transferring;
                if (collecting.isEmpty()) {
                    collecting = pickingStack.copyWithCount(transferring);
                } else {
                    collecting.grow(transferring);
                }
                arrow.discard();
                continue;
            }
        }
        chestItemHandler.setStackInSlot(index, collecting);

        return MethodResult.of(collecting.getCount() - initCount);
    }

    @LuaFunction(mainThread = true)
    public MethodResult smartHandAttack(int index, Optional<LuaTable<?, ?>> optionsMap) throws LuaException {
        index--;

        if (!(this.getComputer().getEntity() instanceof LivingEntity livingEntity)) {
            return MethodResult.of(null, "SMART_CHEST_NOT_EQUIPPED");
        }
        ItemStack stack = SmartChestMountItem.getEquipped(livingEntity);
        if (stack.isEmpty()) {
            return MethodResult.of(null, "SMART_CHEST_NOT_EQUIPPED");
        }
        if (index < 0 || index >= SmartChestMountItemHandler.SLOTS) {
            return MethodResult.of(null, "HAND_DOES_NOT_EXISTS");
        }

        LuaTable<?, ?> options = EmptyLuaTable.orEmpty(optionsMap);

        boolean sneak = options.optBoolean("sneak").orElse(false);
        float yaw = options.optDouble("yaw").orElse(0d).floatValue();
        float pitch = options.optDouble("pitch").orElse(0d).floatValue();
        boolean ground = options.optBoolean("ground").orElse(false);

        SmartChestMountItem.DataStorage chestData = this.getComputer().chestDataStorage;
        SmartChestHand hand = chestData.getOrCreateHand(index, livingEntity, stack);

        return SmartHandFakePlayerProvider.doAction(
            livingEntity, index, hand.position(), stack,
            APFakePlayer.wrapActionWithShiftKey(
                sneak,
                APFakePlayer.wrapActionWithRot(
                    yaw, pitch,
                    APFakePlayer.wrapActionWithReachRange(
                        1,
                        (player) -> {
                            HitResult hitResult = player.findHit(false, true, (e) -> e.isAlive() && e.isPickable() && e != livingEntity);
                            if (hitResult.getType() != HitResult.Type.ENTITY) {
                                return MethodResult.of(false);
                            }
                            Entity target = ((EntityHitResult) hitResult).getEntity();
                            player.setAttackStrengthTicker(hand.getAttackStrengthTicker());
                            if (ground) {
                                player.setOnGround(true);
                                player.fallDistance = 0;
                            } else {
                                player.setOnGround(false);
                                player.fallDistance = 1;
                            }
                            player.attack(target);
                            hand.resetAttackStrengthTicker();
                            return MethodResult.of(true);
                        }
                    )
                )
            )
        );
    }
}
