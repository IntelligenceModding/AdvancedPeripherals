package de.srendi.advancedperipherals.blocks.tileentity;

import de.srendi.advancedperipherals.addons.computercraft.ILuaMethodProvider;
import de.srendi.advancedperipherals.addons.computercraft.LuaMethod;
import de.srendi.advancedperipherals.addons.computercraft.LuaMethodRegistry;
import de.srendi.advancedperipherals.setup.ModTileEntityTypes;
import net.minecraft.command.impl.ListCommand;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class ChatBoxTileEntity extends TileEntity implements ITickableTileEntity, ILuaMethodProvider {

    private final LuaMethodRegistry luaMethodRegistry = new LuaMethodRegistry(this);

    private String peripheralType = "chatBox";
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
                //TODO: yeah i need to do much
                return null;
            }
        });
        registry.registerLuaMethod(new LuaMethod("sendMessageToPlayer") {
            @Override
            public Object[] call(Object[] args) {
                requireArgs(args, 2, "<playerName>:String | <message>:String");
                //TODO: yeah i need to do much
                return new Object[]{true};
            }
        });
    }

    @Override
    public LuaMethodRegistry getLuaMethodRegistry() {
        return luaMethodRegistry;
    }

    @Override
    public String getPeripheralType() {
        return peripheralType;
    }
}
