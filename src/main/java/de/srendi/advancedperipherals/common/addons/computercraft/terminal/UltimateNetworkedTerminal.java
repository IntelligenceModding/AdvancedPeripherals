package de.srendi.advancedperipherals.common.addons.computercraft.terminal;

import dan200.computercraft.shared.computer.terminal.NetworkedTerminal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Arrays;

public class UltimateNetworkedTerminal extends NetworkedTerminal {
    private static final int PALETTE_SIZE = 16; // sync with dan200.computercraft.shared.util.Palette

    private float panelDepth = 0;
    private final byte[] transparencies = new byte[PALETTE_SIZE];

    public UltimateNetworkedTerminal(int width, int height) {
        this(width, height, null);
    }

    public UltimateNetworkedTerminal(int width, int height, Runnable changedCallback) {
        super(width, height, true, changedCallback);
        for (int i = 0; i < this.transparencies.length; i++) {
            this.transparencies[i] = (byte)(0xff);
        }
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

    public int getPaletteTransparencyByte(int color) {
        return (int)(this.transparencies[color]) & 0xff;
    }

    public float getPaletteTransparency(int color) {
        if (color < 0 || color >= this.transparencies.length) {
            return 1;
        }
        return (float)((int)(this.transparencies[color]) & 0xff) / 255.0f;
    }

    public void setPaletteTransparency(int color, float a) {
        if (color < 0 || color >= this.transparencies.length) {
            return;
        }
        a = Math.min(Math.max(a, 0), 1);
        this.transparencies[color] = (byte)((int)(a * 0xff));
    }

    @Override
    public synchronized void reset() {
        super.reset();
        this.panelDepth = 0;
        for (int i = 0; i < this.transparencies.length; i++) {
            this.transparencies[i] = (byte)(0xff);
        }
        setChanged();
    }

    @Override
    public synchronized void write(FriendlyByteBuf buffer) {
        super.write(buffer);
        buffer.writeFloat(this.panelDepth);
        buffer.writeBytes(this.transparencies);
    }

    @Override
    public synchronized void read(FriendlyByteBuf buffer) {
        super.read(buffer);
        this.panelDepth = buffer.readFloat();
        buffer.readBytes(this.transparencies);
        setChanged();
    }

    @Override
    public synchronized CompoundTag writeToNBT(CompoundTag nbt) {
        super.writeToNBT(nbt);
        nbt.putFloat("term_panelDepth", this.panelDepth);
        nbt.putByteArray("term_paletteTransparencies", this.transparencies);
        return nbt;
    }

    @Override
    public synchronized void readFromNBT(CompoundTag nbt) {
        super.readFromNBT(nbt);
        this.panelDepth = nbt.getFloat("term_panelDepth");
        byte[] transparencies = nbt.getByteArray("term_paletteTransparencies");
        System.arraycopy(transparencies, 0, this.transparencies, 0, Math.min(transparencies.length, this.transparencies.length));
        setChanged();
    }
}
