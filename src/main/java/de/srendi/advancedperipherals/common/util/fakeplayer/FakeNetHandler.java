package de.srendi.advancedperipherals.common.util.fakeplayer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nonnull;

public class FakeNetHandler extends ServerGamePacketListenerImpl {

    public FakeNetHandler(FakePlayer player) {
        this(player.server, player);
    }

    public FakeNetHandler(MinecraftServer server, FakePlayer player) {
        super(server, new FakeNetworkManager(), player);
    }

    @Override
    public void handlePlayerInput(ServerboundPlayerInputPacket p_147358_1_) {
        super.handlePlayerInput(p_147358_1_);
    }

    @Override
    public void handleMoveVehicle(ServerboundMoveVehiclePacket p_184338_1_) {
        super.handleMoveVehicle(p_184338_1_);
    }

    @Override
    public void handleAcceptTeleportPacket(ServerboundAcceptTeleportationPacket p_184339_1_) {
        super.handleAcceptTeleportPacket(p_184339_1_);
    }

    @Override
    public void handleRecipeBookSeenRecipePacket(ServerboundRecipeBookSeenRecipePacket p_191984_1_) {
        super.handleRecipeBookSeenRecipePacket(p_191984_1_);
    }

    @Override
    public void handleRecipeBookChangeSettingsPacket(ServerboundRecipeBookChangeSettingsPacket p_241831_1_) {
        super.handleRecipeBookChangeSettingsPacket(p_241831_1_);
    }

    @Override
    public void handleSeenAdvancements(ServerboundSeenAdvancementsPacket p_194027_1_) {
        super.handleSeenAdvancements(p_194027_1_);
    }

    @Override
    public void handleCustomCommandSuggestions(ServerboundCommandSuggestionPacket p_195518_1_) {
        super.handleCustomCommandSuggestions(p_195518_1_);
    }

    @Override
    public void handleSetCommandBlock(ServerboundSetCommandBlockPacket p_210153_1_) {
        super.handleSetCommandBlock(p_210153_1_);
    }

    @Override
    public void handleSetCommandMinecart(ServerboundSetCommandMinecartPacket p_210158_1_) {
        super.handleSetCommandMinecart(p_210158_1_);
    }

    @Override
    public void handlePickItem(ServerboundPickItemPacket p_210152_1_) {
        super.handlePickItem(p_210152_1_);
    }

    @Override
    public void handleRenameItem(ServerboundRenameItemPacket p_210155_1_) {
        super.handleRenameItem(p_210155_1_);
    }

    @Override
    public void handleSetBeaconPacket(ServerboundSetBeaconPacket p_210154_1_) {
        super.handleSetBeaconPacket(p_210154_1_);
    }

    @Override
    public void handleSetStructureBlock(ServerboundSetStructureBlockPacket p_210157_1_) {
        super.handleSetStructureBlock(p_210157_1_);
    }

    @Override
    public void handleSetJigsawBlock(ServerboundSetJigsawBlockPacket p_217262_1_) {
        super.handleSetJigsawBlock(p_217262_1_);
    }

    @Override
    public void handleJigsawGenerate(ServerboundJigsawGeneratePacket p_230549_1_) {
        super.handleJigsawGenerate(p_230549_1_);
    }

    @Override
    public void handleSelectTrade(ServerboundSelectTradePacket p_210159_1_) {
        super.handleSelectTrade(p_210159_1_);
    }

    @Override
    public void handleEditBook(ServerboundEditBookPacket p_210156_1_) {
        super.handleEditBook(p_210156_1_);
    }

    @Override
    public void handleEntityTagQuery(ServerboundEntityTagQuery p_211526_1_) {
        super.handleEntityTagQuery(p_211526_1_);
    }

    @Override
    public void handleBlockEntityTagQuery(ServerboundBlockEntityTagQuery p_211525_1_) {
        super.handleBlockEntityTagQuery(p_211525_1_);
    }

    @Override
    public void handleMovePlayer(ServerboundMovePlayerPacket p_147347_1_) {
        super.handleMovePlayer(p_147347_1_);
    }

    @Override
    public void handlePlayerAction(ServerboundPlayerActionPacket p_147345_1_) {
        super.handlePlayerAction(p_147345_1_);
    }

    @Override
    public void handleUseItemOn(ServerboundUseItemOnPacket p_184337_1_) {
        super.handleUseItemOn(p_184337_1_);
    }

    @Override
    public void handleUseItem(ServerboundUseItemPacket p_147346_1_) {
        super.handleUseItem(p_147346_1_);
    }

    @Override
    public void handleTeleportToEntityPacket(ServerboundTeleportToEntityPacket p_175088_1_) {
        super.handleTeleportToEntityPacket(p_175088_1_);
    }

    @Override
    public void handleResourcePackResponse(ServerboundResourcePackPacket p_175086_1_) {
        super.handleResourcePackResponse(p_175086_1_);
    }

    @Override
    public void handlePaddleBoat(ServerboundPaddleBoatPacket p_184340_1_) {
        super.handlePaddleBoat(p_184340_1_);
    }

    @Override
    public void onDisconnect(@Nonnull Component chat) {
    }

    @Override
    public void send(Packet<?> p_147359_1_) {
    }

    @Override
    public void handleSetCarriedItem(ServerboundSetCarriedItemPacket p_147355_1_) {
    }

    @Override
    public void handleChat(ServerboundChatPacket p_147354_1_) {
    }

    @Override
    public void handleAnimate(ServerboundSwingPacket p_175087_1_) {
    }

    @Override
    public void handlePlayerCommand(ServerboundPlayerCommandPacket p_147357_1_) {
    }

    @Override
    public void handleInteract(ServerboundInteractPacket p_147340_1_) {
    }

    @Override
    public void handleClientCommand(ServerboundClientCommandPacket p_147342_1_) {
    }

    @Override
    public void handleContainerClose(ServerboundContainerClosePacket p_147356_1_) {
    }

    @Override
    public void handleContainerClick(ServerboundContainerClickPacket p_147351_1_) {
    }

    @Override
    public void handlePlaceRecipe(ServerboundPlaceRecipePacket p_194308_1_) {
    }

    @Override
    public void handleContainerButtonClick(ServerboundContainerButtonClickPacket p_147338_1_) {
    }

    @Override
    public void handleSetCreativeModeSlot(ServerboundSetCreativeModeSlotPacket p_147344_1_) {
    }

    @Override
    public void handleSignUpdate(ServerboundSignUpdatePacket p_147343_1_) {
    }

    @Override
    public void handleKeepAlive(ServerboundKeepAlivePacket p_147353_1_) {
    }

    @Override
    public void handlePlayerAbilities(ServerboundPlayerAbilitiesPacket p_147348_1_) {
    }

    @Override
    public void handleClientInformation(ServerboundClientInformationPacket p_147352_1_) {
    }

    @Override
    public void handleCustomPayload(ServerboundCustomPayloadPacket p_147349_1_) {
    }

    @Override
    public void handleChangeDifficulty(ServerboundChangeDifficultyPacket p_217263_1_) {
    }

    @Override
    public void handleLockDifficulty(ServerboundLockDifficultyPacket p_217261_1_) {
    }

    /*
    Highly inspired by https://github.com/SquidDev-CC/plethora/blob/minecraft-1.12/src/main/java/org/squiddev/plethora/utils/FakeNetHandler.java
     */
    public static class FakeNetworkManager extends Connection {
        private PacketListener handler;

        public FakeNetworkManager() {
            super(PacketFlow.CLIENTBOUND);
        }

        @Override
        public void channelActive(ChannelHandlerContext context) {
        }

        @Override
        public void channelInactive(ChannelHandlerContext context) {
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext context, @Nonnull Throwable e) {
        }

        public void setHandler(PacketListener handler) {
            this.handler = handler;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        }

        @Override
        public void disconnect(@Nonnull Component channel) {
        }

        @Override
        public boolean isMemoryConnection() {
            return false;
        }

        @Override
        public boolean isConnected() {
            return false;
        }

        @Nonnull
        @Override
        public Component getDisconnectedReason() {
            return null;
        }

        @Nonnull
        @Override
        public Channel channel() {
            return null;
        }
    }
}
