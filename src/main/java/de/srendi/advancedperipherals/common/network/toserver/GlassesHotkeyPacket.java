/*
 *     Copyright 2024 Intelligence Modding @ https://intelligence-modding.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.srendi.advancedperipherals.common.network.toserver;

import de.srendi.advancedperipherals.common.items.SmartGlassesItem;
import de.srendi.advancedperipherals.common.network.base.IPacket;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.UUID;

public class GlassesHotkeyPacket implements IPacket {

    private final UUID player;
    private final String keyBind;
    private final int keyPressDuration;

    public GlassesHotkeyPacket(UUID player, String keyBind, int keyPressDuration) {
        this.player = player;
        this.keyBind = keyBind;
        this.keyPressDuration = keyPressDuration;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        ServerPlayer serverPlayer = server.getPlayerList().getPlayer(player);
        if (serverPlayer == null)
            return;

        for (ItemStack stack : serverPlayer.getAllSlots()) {
            if (stack.getItem() instanceof SmartGlassesItem) {
                SmartGlassesComputer computer = SmartGlassesItem.getServerComputer(server, stack);

                if (computer != null)
                    computer.queueEvent("glassesKeyPressed", new Object[]{keyBind, keyPressDuration});
            }
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(player);
        buffer.writeUtf(keyBind);
        buffer.writeInt(keyPressDuration);
    }

    public static GlassesHotkeyPacket decode(FriendlyByteBuf buffer) {
        return new GlassesHotkeyPacket(buffer.readUUID(), buffer.readUtf(), buffer.readInt());
    }
}
