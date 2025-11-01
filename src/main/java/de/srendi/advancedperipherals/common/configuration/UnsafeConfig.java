package de.srendi.advancedperipherals.common.configuration;

import net.neoforged.neoforge.common.ModConfigSpec;

public class UnsafeConfig {

    private static ModConfigSpec.BooleanValue enableUnsafe;
    private static ModConfigSpec.BooleanValue ignoreTurtlePeripheralItemNBT;

    public static void build(final ModConfigSpec.Builder builder) {
        enableUnsafe = builder.comment("By setting this value to true, I understand all operations below are danger to my adventure, and if they caused unexpected behavior in my world, I will not consider it as AP's liability").define("enableUnsafe", false);
        ignoreTurtlePeripheralItemNBT = builder.comment("Ignore turtle peripheral item's NBT when equipping. **YOU WILL LOSE ALL NBT ON THE ITEM**").define("ignoreTurtlePeripheralItemNBT", false);
    }

    public static boolean enabled() {
        return enableUnsafe.get();
    }

    public static boolean getIgnoreTurtlePeripheralItemNBT() {
        return enabled() && ignoreTurtlePeripheralItemNBT.get();
    }
}
