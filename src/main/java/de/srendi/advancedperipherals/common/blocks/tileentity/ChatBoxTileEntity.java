package de.srendi.advancedperipherals.common.blocks.tileentity;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.ILuaMethodProvider;
import de.srendi.advancedperipherals.common.addons.computercraft.LuaMethod;
import de.srendi.advancedperipherals.common.addons.computercraft.LuaMethodRegistry;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.setup.ModTileEntityTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ChatBoxTileEntity extends TileEntity implements ITickableTileEntity, ILuaMethodProvider {

    private final LuaMethodRegistry luaMethodRegistry = new LuaMethodRegistry(this);

    private int tick = 0;

    public ChatBoxTileEntity() {
        this(ModTileEntityTypes.CHAT_BOX.get());
    }

    public ChatBoxTileEntity(final TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    @Override
    public void tick() {
        tick++;
    }

    @Override
    public void addLuaMethods(LuaMethodRegistry registry) {
        if(AdvancedPeripheralsConfig.enableChatBox) {
            registry.registerLuaMethod(new LuaMethod("sendMessage") {
                @Override
                public Object[] call(Object[] args) {
                    requireArgs(args, 1, "<message>:String");
                    if (tick >= AdvancedPeripheralsConfig.chatBoxCooldown) {
                        if (AdvancedPeripherals.playerController.getWorld() instanceof ServerWorld) {
                            ServerWorld world = (ServerWorld) AdvancedPeripherals.playerController.getWorld();
                            for (ServerPlayerEntity player : world.getServer().getPlayerList().getPlayers()) {
                                player.sendMessage(new StringTextComponent("" + args[0]), UUID.randomUUID());
                            }
                        } else {
                            Minecraft.getInstance().player.sendMessage(new StringTextComponent("" + args[0]), UUID.randomUUID());
                        }
                        tick = 0;
                    } else {
                        return new Object[]{"You are sending messages too often. You can modify the cooldown in the config."};
                    }
                    return null;
                }
            });
            registry.registerLuaMethod(new LuaMethod("sendMessageToPlayer") {
                @Override
                public Object[] call(Object[] args) {
                    requireArgs(args, 2, "<playerName>:String | <message>:String");
                    if (tick >= AdvancedPeripheralsConfig.chatBoxCooldown) {
                        if (AdvancedPeripherals.playerController.getWorld() instanceof ServerWorld) {
                            ServerWorld world = (ServerWorld) AdvancedPeripherals.playerController.getWorld();
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
                        tick = 0;
                    } else {
                        return new Object[]{"You are sending messages too often. You can modify the cooldown in the config."};
                    }
                    return null;
                }
            });
        }
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