package de.srendi.advancedperipherals.common.addons.computercraft.terminal;

import dan200.computercraft.shared.computer.terminal.NetworkedTerminal;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Arrays;

public class UltimateNetworkedTerminal extends NetworkedTerminal {
    private final int[] transparencies = new int[]{0xff, 0, 0, 0};
    private final int[][] sideColors = new int[3][3];

    public UltimateNetworkedTerminal(int width, int height, int[] transparencies) {
        this(width, height, transparencies, null);
    }

    public UltimateNetworkedTerminal(int width, int height, int[] transparencies, Runnable changedCallback) {
        super(width, height, true, changedCallback);
        if (transparencies != null) {
            System.arraycopy(transparencies, 0, this.transparencies, 0, 4);
        }
    }

    public int getTransparency() {
        return this.transparencies[0];
    }

    public void setTransparency(int transparency) {
        if (this.transparencies[0] == transparency) {
            return;
        }
        this.transparencies[0] = transparency;
        setChanged();
    }

    public int getSideTransparency(MonitorSide side) {
        return this.transparencies[side.getIndex()];
    }

    public void setSideTransparency(MonitorSide side, int transparency) {
        if (this.transparencies[side.getIndex()] == transparency) {
            return;
        }
        this.transparencies[side.getIndex()] = transparency;
        setChanged();
    }

    public int[] getSideColor(MonitorSide side) {
        return this.sideColors[side.getIndex() - 1];
    }

    public void setSideColor(MonitorSide side, int[] color) {
        if (color.length != 3) {
            throw new IllegalArgumentException("color.length must be 3");
        }
        if (Arrays.equals(this.sideColors[side.getIndex() - 1], color)) {
            return;
        }
        this.sideColors[side.getIndex() - 1] = color;
        setChanged();
    }

    @Override
    public synchronized void reset() {
        super.reset();
        this.transparencies[0] = 0xff;
        this.transparencies[1] = 0;
        this.transparencies[2] = 0;
        this.transparencies[3] = 0;
    }

    @Override
    public synchronized void write(FriendlyByteBuf buffer) {
        super.write(buffer);
        buffer.writeByte(this.transparencies[0]);
        buffer.writeByte(this.transparencies[1]);
        buffer.writeByte(this.transparencies[2]);
        buffer.writeByte(this.transparencies[3]);
    }

    @Override
    public synchronized void read(FriendlyByteBuf buffer) {
        super.read(buffer);
        this.transparencies[0] = buffer.readByte();
        this.transparencies[1] = buffer.readByte();
        this.transparencies[2] = buffer.readByte();
        this.transparencies[3] = buffer.readByte();
    }

    public static enum MonitorSide {
        PANEL(0),
        FRONT(1),
        LEFT(2),
        TOP(3);

        private final int index;

        private MonitorSide(int index){
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public static MonitorSide fromString(String s) {
            return switch (s.toLowerCase()) {
                case "front", "back", "z" -> FRONT;
                case "left", "right", "x" -> LEFT;
                case "top", "bottom", "y" -> TOP;
                default -> null;
            };
        }
    }
}
