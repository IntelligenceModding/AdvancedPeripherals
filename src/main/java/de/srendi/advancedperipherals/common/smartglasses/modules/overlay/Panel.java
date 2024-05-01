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
package de.srendi.advancedperipherals.common.smartglasses.modules.overlay;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * A panel is the standard panel which can contain multiple render-able objects
 * in it.
 */
public class Panel extends RenderableObject {

    public Panel(String id, OverlayModule module, IArguments arguments) throws LuaException {
        super(id, module, arguments);
    }

    /**
     * constructor for the client side initialization
     *
     * @param id
     *            id of the object
     * @param player
     *            the target player
     */
    public Panel(String id, UUID player) {
        super(id, player);
    }

    @Override
    protected void encode(FriendlyByteBuf buffer) {
        super.encode(buffer);
        buffer.writeInt(color);
        buffer.writeDouble(opacity);
    }

    @Nullable public static Panel decode(FriendlyByteBuf buffer) {
        String id = buffer.readUtf();
        boolean hasValidUUID = buffer.readBoolean();
        if (!hasValidUUID) {
            AdvancedPeripherals.exception(
                    "Tried to decode a buffer for an OverlayObject but without a valid player as target.",
                    new IllegalArgumentException());
            return null;
        }
        UUID player = buffer.readUUID();
        int color = buffer.readInt();
        double opacity = buffer.readDouble();

        Panel clientPanel = new Panel(id, player);
        clientPanel.color = color;
        clientPanel.opacity = opacity;

        return clientPanel;
    }

    @Override
    protected void handle(NetworkEvent.Context context) {
        super.handle(context);
    }
}
