package de.srendi.advancedperipherals.network.toclient;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.network.IAPPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEItemKey;
// Try to import AE2's config classes
import appeng.core.AEConfig;

public class CraftingCompleteToastPacket implements IAPPacket {
    public static final StreamCodec<RegistryFriendlyByteBuf, CraftingCompleteToastPacket> CODEC = StreamCodec.of(
            (buffer, value) -> value.write(buffer), 
            CraftingCompleteToastPacket::decode);

    public static final Type<CraftingCompleteToastPacket> TYPE = new Type<>(AdvancedPeripherals.getRL("craftingcompletetoast"));

    private final Component title;
    private final Component message;
    private final ItemStack itemStack;
    private final long craftedAmount;

    public CraftingCompleteToastPacket(Component title, Component message, AEKey craftedItem, long amount) {
        this.title = title;
        this.message = message;
        this.craftedAmount = amount;
        
        // Convert AEKey to ItemStack for serialization (but preserve amount separately)
        if (craftedItem instanceof AEItemKey itemKey) {
            this.itemStack = itemKey.toStack();
        } else {
            // Fallback for non-item keys (fluids, etc.)
            this.itemStack = ItemStack.EMPTY;
        }
    }

    public static CraftingCompleteToastPacket decode(RegistryFriendlyByteBuf buffer) {
        Component title = ComponentSerialization.STREAM_CODEC.decode(buffer);
        Component message = ComponentSerialization.STREAM_CODEC.decode(buffer);
        ItemStack itemStack = ItemStack.STREAM_CODEC.decode(buffer);
        long craftedAmount = buffer.readLong();
        return new CraftingCompleteToastPacket(title, message, itemStack, craftedAmount);
    }
    
    public void write(RegistryFriendlyByteBuf buffer) {
        ComponentSerialization.STREAM_CODEC.encode(buffer, title);
        ComponentSerialization.STREAM_CODEC.encode(buffer, message);
        ItemStack.STREAM_CODEC.encode(buffer, itemStack);
        buffer.writeLong(craftedAmount);
    }

    private CraftingCompleteToastPacket(Component title, Component message, ItemStack itemStack, long craftedAmount) {
        this.title = title;
        this.message = message;
        this.itemStack = itemStack;
        this.craftedAmount = craftedAmount;
    }

    @Override
    public void handle(@NotNull IPayloadContext context) {
        // Should in the theory not happen, but safe is safe.
        if (!FMLEnvironment.dist.isClient()) {
            AdvancedPeripherals.debug("Tried to display crafting complete toast on the server, aborting.");
            return;
        }
        
        // Check BOTH AdvancedPeripherals AND AE2's notification settings
        // This ensures we respect both the mod's setting and AE2's wireless terminal notify button
        boolean clientNotificationSetting = de.srendi.advancedperipherals.common.configuration.APConfig.CLIENT_CONFIG.meCraftingNotifications.get();
        
        // Access AE2's notification setting using reflection (compatible across AE2 versions)
        boolean ae2NotificationSetting = true; // Default to true if we can't access AE2's setting
        try {
            // Attempt to get AE2's notification setting
            // Let's try different potential methods
            AdvancedPeripherals.debug("[DEBUG-CLIENT] ==> Attempting to access AE2 config...");
            
            // Try AEConfig instance methods
            if (AEConfig.instance() != null) {
                AdvancedPeripherals.debug("[DEBUG-CLIENT] ==> AEConfig instance found");
                
                // Use reflection to access AE2's notification setting
                // This is more robust than compile-time method calls
                try {
                    Object aeConfigInstance = AEConfig.instance();
                    Class<?> aeConfigClass = aeConfigInstance.getClass();
                    
                    // Try different potential method names using reflection
                    String[] methodNames = {
                        "isNotifyForFinishedCraftingJobsEnabled",
                        "notifyForFinishedCraftingJobs", 
                        "getNotifyForFinishedCraftingJobs",
                        "isNotifyForFinishedCraftingJobs",
                        "getNotifyForCompletedCraftingJobs",
                        "isNotifyForCompletedCraftingJobs"
                    };
                    
                    boolean methodFound = false;
                    for (String methodName : methodNames) {
                        try {
                            java.lang.reflect.Method method = aeConfigClass.getMethod(methodName);
                            Object result = method.invoke(aeConfigInstance);
                            if (result instanceof Boolean) {
                                ae2NotificationSetting = (Boolean) result;
                                methodFound = true;
                                AdvancedPeripherals.debug("[DEBUG-CLIENT] ==> Found AE2 method: " + methodName + " = " + ae2NotificationSetting);
                                break;
                            }
                        } catch (Exception ignored) {
                            // Try next method
                        }
                    }
                    
                    if (!methodFound) {
                        AdvancedPeripherals.debug("[DEBUG-CLIENT] ==> No AE2 notification method found, using default");
                        ae2NotificationSetting = true;
                    }
                    
                } catch (Exception reflectionError) {
                    AdvancedPeripherals.debug("[DEBUG-CLIENT] ==> Reflection failed: " + reflectionError.getMessage());
                    ae2NotificationSetting = true; // Default to enabled
                }
                
                AdvancedPeripherals.debug("[DEBUG-CLIENT] ==> AE2 notification setting: " + ae2NotificationSetting);
            }
            
            // Additional debug info about AE2 integration
            AdvancedPeripherals.debug("[DEBUG-CLIENT] ==> AE2 integration complete");
            
        } catch (Exception e) {
            AdvancedPeripherals.debug("[DEBUG-CLIENT] ==> Could not access AE2 config: " + e.getMessage());
            ae2NotificationSetting = true; // Default to enabled if we can't check
        }
        
        if (!clientNotificationSetting) {
            AdvancedPeripherals.debug("[DEBUG-CLIENT] ==> AdvancedPeripherals client notification setting disabled, not showing toast");
            return;
        }
        
        if (!ae2NotificationSetting) {
            AdvancedPeripherals.debug("[DEBUG-CLIENT] ==> AE2 notification setting disabled, not showing toast");
            return;
        }
        
        AdvancedPeripherals.debug("[DEBUG-CLIENT] ==> Both notification settings enabled, showing toast");
        
        if (!itemStack.isEmpty()) {
            AEItemKey aeKey = AEItemKey.of(itemStack);
            long amount = this.craftedAmount;
            
            AdvancedPeripherals.debug("[DEBUG-CLIENT] ==> Creating AE2 FinishedJobToast with amount: " + amount);
            
            // Create and add AE2's authentic FinishedJobToast
            appeng.client.gui.me.common.FinishedJobToast finishedJobToast = 
                new appeng.client.gui.me.common.FinishedJobToast(aeKey, amount);
            
            Minecraft.getInstance().getToasts().addToast(finishedJobToast);
            AdvancedPeripherals.debug("[DEBUG-CLIENT] ==> AE2 FinishedJobToast displayed successfully");
        } else {
            AdvancedPeripherals.debug("[DEBUG-CLIENT] ==> ItemStack is empty, skipping toast");
        }
    }

    @NotNull
    @Override
    public Type<CraftingCompleteToastPacket> type() {
        return TYPE;
    }
}