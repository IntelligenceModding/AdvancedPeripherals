package de.srendi.advancedperipherals.common.blocks.tileentity;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.CCEventManager;
import de.srendi.advancedperipherals.common.addons.computercraft.ILuaMethodProvider;
import de.srendi.advancedperipherals.common.addons.computercraft.LuaMethod;
import de.srendi.advancedperipherals.common.addons.computercraft.LuaMethodRegistry;
import de.srendi.advancedperipherals.common.events.TestEvent;
import de.srendi.advancedperipherals.common.setup.ModTileEntityTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ChatBoxTileEntity extends TileEntity implements ITickableTileEntity, ILuaMethodProvider {

    private final LuaMethodRegistry luaMethodRegistry = new LuaMethodRegistry(this);

    public final HashMap<BlockPos, String> anchors = new HashMap<>();

    private int tick;

    public ChatBoxTileEntity() {
        this(ModTileEntityTypes.CHAT_BOX.get());
    }

    public ChatBoxTileEntity(final TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    @Override
    public void tick() {
        tick++;
        if(tick == 40) {
            tick = 0;
        }
    }

    @Override
    public void addLuaMethods(LuaMethodRegistry registry) {
        registry.registerLuaMethod(new LuaMethod("sendMessage") {
            @Override
            public Object[] call(Object[] args) {
                requireArgs(args, 1, "<message>:String");
                if (Minecraft.getInstance().player.getEntityWorld() instanceof ServerWorld) {
                    ServerWorld world = (ServerWorld) Minecraft.getInstance().player.getEntityWorld();
                    for (ServerPlayerEntity player : world.getServer().getPlayerList().getPlayers()) {
                        player.sendMessage(new StringTextComponent((String) args[0]), UUID.randomUUID());
                    }
                } else {
                    Minecraft.getInstance().player.sendMessage(new StringTextComponent((String) args[0]), UUID.randomUUID());
                }
                return null;
            }
        });
        registry.registerLuaMethod(new LuaMethod("sendMessageToPlayer") {
            @Override
            public Object[] call(Object[] args) {
                requireArgs(args, 2, "<playerName>:String | <message>:String");
                if (Minecraft.getInstance().player.getEntityWorld() instanceof ServerWorld) {
                    ServerWorld world = (ServerWorld) Minecraft.getInstance().player.getEntityWorld();
                    for (ServerPlayerEntity player : world.getServer().getPlayerList().getPlayers()) {
                        if (player.getName().getString().equals(args[0])) {
                            player.sendMessage(new StringTextComponent((String) args[1]), UUID.randomUUID());
                        }
                    }
                } else {
                    if (Minecraft.getInstance().player.getName().getString().equals(args[0])) {
                        Minecraft.getInstance().player.sendMessage(new StringTextComponent((String) args[1]), UUID.randomUUID());
                    }
                }
                return null;
            }
        });
    }

    @Override
    public LuaMethodRegistry getLuaMethodRegistry() {
        return luaMethodRegistry;
    }

    @Override
    public String getPeripheralType() {
        return "chatBox";
    }
}