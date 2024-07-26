package de.srendi.advancedperipherals.common.addons.computercraft.terminal;

import dan200.computercraft.shared.computer.terminal.NetworkedTerminal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Arrays;

public class UltimateNetworkedTerminal extends NetworkedTerminal {
    private float panelDepth = 0;
    private int textTransparency = 0xff;
    private int backgroundTransparency = 0xff;
    private final int[] transparencies = new int[]{0, 0, 0};
    private final int[][] sideColors = new int[3][3];

    public UltimateNetworkedTerminal(int width, int height) {
        this(width, height, null);
    }

    public UltimateNetworkedTerminal(int width, int height, Runnable changedCallback) {
        super(width, height, true, changedCallback);
    }

    public float getPanelDepth() {
        return this.panelDepth;
    }

    public void setPanelDepth(float z) {
        z = Math.min(Math.max(z, 0), 1);
        if (this.panelDepth == z) {
            return;
        }
        this.panelDepth = z;
        setChanged();
    }

    public int getTextTransparency() {
        return this.textTransparency;
    }

    public void setTextTransparency(int transparency) {
        transparency = Math.min(Math.max(transparency, 0), 0xff);
        if (this.textTransparency == transparency) {
            return;
        }
        this.textTransparency = transparency;
        setChanged();
    }

    public int getBackgroundTransparency() {
        return this.backgroundTransparency;
    }

    public void setBackgroundTransparency(int transparency) {
        transparency = Math.min(Math.max(transparency, 0), 0xff);
        if (this.backgroundTransparency == transparency) {
            return;
        }
        this.backgroundTransparency = transparency;
        setChanged();
    }

    public int getSideTransparency(MonitorSide side) {
        return this.transparencies[side.getIndex()];
    }

    public void setSideTransparency(MonitorSide side, int transparency) {
        transparency = Math.min(Math.max(transparency, 0), 0xff);
        if (this.transparencies[side.getIndex()] == transparency) {
            return;
        }
        this.transparencies[side.getIndex()] = transparency;
        setChanged();
    }

    public int[] getSideColor(MonitorSide side) {
        return this.sideColors[side.getIndex()];
    }

    public void setSideColor(MonitorSide side, int[] color) {
        if (color.length != 3) {
            throw new IllegalArgumentException("color.length must be 3");
        }
        color[0] = Math.min(Math.max(color[0], 0), 0xff);
        color[1] = Math.min(Math.max(color[1], 0), 0xff);
        color[2] = Math.min(Math.max(color[2], 0), 0xff);
        if (Arrays.equals(this.sideColors[side.getIndex()], color)) {
            return;
        }
        this.sideColors[side.getIndex()] = color;
        setChanged();
    }

    @Override
    public synchronized void reset() {
        super.reset();
        this.panelDepth = 0;
        this.textTransparency = 0xff;
        this.backgroundTransparency = 0xff;
        this.transparencies[0] = 0;
        this.transparencies[1] = 0;
        this.transparencies[2] = 0;
        setChanged();
    }

    @Override
    public synchronized void write(FriendlyByteBuf buffer) {
        super.write(buffer);
        buffer.writeFloat(this.panelDepth);
        buffer.writeByte(this.textTransparency);
        buffer.writeByte(this.backgroundTransparency);
        buffer.writeByte(this.transparencies[0]);
        buffer.writeByte(this.transparencies[1]);
        buffer.writeByte(this.transparencies[2]);
    }

    @Override
    public synchronized void read(FriendlyByteBuf buffer) {
        super.read(buffer);
        this.panelDepth = buffer.readFloat();
        this.textTransparency = (int)(buffer.readByte()) & 0xff;
        this.backgroundTransparency = (int)(buffer.readByte()) & 0xff;
        this.transparencies[0] = (int)(buffer.readByte()) & 0xff;
        this.transparencies[1] = (int)(buffer.readByte()) & 0xff;
        this.transparencies[2] = (int)(buffer.readByte()) & 0xff;
        setChanged();
    }

    @Override
    public synchronized CompoundTag writeToNBT(CompoundTag nbt) {
        super.writeToNBT(nbt);
        nbt.putFloat("term_panelDepth", this.panelDepth);
        nbt.putByte("term_textTransparency", (byte) this.textTransparency);
        nbt.putByte("term_backgroundTransparency", (byte) this.backgroundTransparency);
        nbt.putByteArray("term_sideTransparencies", new byte[]{
            (byte) this.transparencies[0],
            (byte) this.transparencies[1],
            (byte) this.transparencies[2],
        });
        return nbt;
    }

    @Override
    public synchronized void readFromNBT(CompoundTag nbt) {
        super.readFromNBT(nbt);
        this.panelDepth = nbt.getFloat("term_panelDepth");
        this.textTransparency = (int)(nbt.getByte("term_textTransparency")) & 0xff;
        this.backgroundTransparency = (int)(nbt.getByte("term_backgroundTransparency")) & 0xff;
        byte[] transparencies = nbt.getByteArray("term_sideTransparencies");
        if (transparencies.length == 3) {
            this.transparencies[0] = (int)(transparencies[0]) & 0xff;
            this.transparencies[1] = (int)(transparencies[1]) & 0xff;
            this.transparencies[2] = (int)(transparencies[2]) & 0xff;
        }
        setChanged();
    }

    public static enum MonitorSide {
        FRONT(0),
        LEFT(1),
        TOP(2);

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
