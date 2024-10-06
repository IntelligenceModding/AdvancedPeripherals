/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2022. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package dan200.computercraft.gametest.core;

import dan200.computercraft.export.Exporter;
import dan200.computercraft.gametest.api.GameTestHolder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegisterGameTestsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import org.objectweb.asm.Type;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Locale;
import java.util.function.Consumer;

@Mod(TestMod.MOD_ID)
public class TestMod {

    public static final String MOD_ID = "advancedperipheralstest";

    public TestMod() {
        TestHooks.init();

        var bus = MinecraftForge.EVENT_BUS;
        bus.addListener(EventPriority.LOW, (ServerStartedEvent e) -> TestHooks.onServerStarted(e.getServer()));
        bus.addListener((RegisterCommandsEvent e) -> CCTestCommand.register(e.getDispatcher()));
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> TestMod::onInitializeClient);

        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener((RegisterGameTestsEvent event) -> {
            var holder = Type.getType(GameTestHolder.class);
            ModList.get().getAllScanData().stream()
                    .map(ModFileScanData::getAnnotations)
                    .flatMap(Collection::stream)
                    .filter(a -> holder.equals(a.annotationType()))
                    .forEach(x -> registerClass(x.clazz().getClassName(), event::register));
        });
    }

    private static void onInitializeClient() {
        var bus = MinecraftForge.EVENT_BUS;

        bus.addListener((TickEvent.ServerTickEvent e) -> {
            if (e.phase == TickEvent.Phase.START) ClientTestHooks.onServerTick(e.getServer());
        });
        bus.addListener((ScreenEvent.Opening e) -> {
            if (ClientTestHooks.onOpenScreen(e.getScreen())) e.setCanceled(true);
        });
        bus.addListener((RegisterClientCommandsEvent e) -> Exporter.register(e.getDispatcher()));
    }

    private static Class<?> loadClass(String name) {
        try {
            return Class.forName(name, true, TestMod.class.getClassLoader());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static void registerClass(String className, Consumer<Method> fallback) {
        var klass = loadClass(className);
        for (var method : klass.getDeclaredMethods()) {
            TestHooks.registerTest(klass, method, fallback);
        }
    }
}
