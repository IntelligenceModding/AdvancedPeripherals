package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim;

import com.mojang.blaze3d.platform.NativeImage;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.LuaTable;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.lua.ObjectLuaTable;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.APRenderTypes;
import de.srendi.advancedperipherals.common.setup.APOverlayObjects;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObjectType;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FloatingNumberProperty;
import de.srendi.advancedperipherals.common.util.EmptyLuaTable;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class TextureObject extends ThreeDimensionalObject implements AutoCloseable {
    private static final int MAX_DIM = 0xffff;
    private static final int[] ZERO_DATA = new int[0];

    @FloatingNumberProperty
    public float sizeX = 1;

    @FloatingNumberProperty
    public float sizeY = 1;

    public int width = 0;
    public int height = 0;
    public int[] image = ZERO_DATA;

    private boolean resized = false;
    private boolean imageChanged = false;
    private int changedMinX;
    private int changedMinY;
    private int changedMaxX;
    private int changedMaxY;

    private ResourceLocation textureId;
    private Function renderTypesMap;
    private Object /*DynamicTexture*/ texture = null;

    public TextureObject(OverlayModule module) {
        super(module);
    }

    public TextureObject(UUID player) {
        super(player);
    }

    @Override
    @NotNull
    public OverlayObjectType<?> getType() {
        return APOverlayObjects.TEXTURE.get();
    }

    @LuaFunction
    public final int getWidth() {
        return this.width;
    }

    @LuaFunction
    public final int getHeight() {
        return this.height;
    }

    @LuaFunction
    public final MethodResult getImageSize() {
        return MethodResult.of(this.width, this.height);
    }

    @LuaFunction
    public final void setWidth(int width) {
        width = clipDim(width);
        if (width == this.width) {
            return;
        }
        this.resize(width, this.height);
        this.resized = true;
        this.tryAutoUpdate();
    }

    @LuaFunction
    public final void setHeight(int height) {
        height = clipDim(height);
        if (height == this.height) {
            return;
        }
        this.resize(this.width, height);
        this.resized = true;
        this.tryAutoUpdate();
    }

    @LuaFunction
    public final void setImageSize(int width, int height) {
        width = clipDim(width);
        height = clipDim(height);
        if (width == this.width && height == this.height) {
            return;
        }
        this.resize(width, height);
        this.resized = true;
        this.tryAutoUpdate();
    }

    @LuaFunction
    public final void setPixel(int x, int y, int color) {
        this.setColor(x, y, color);
        this.tryAutoUpdate();
    }

    @LuaFunction
    public final void setPixels(IArguments args) throws LuaException {
        int minX = args.getInt(0);
        int minY = args.getInt(1);
        int width = 0, height = 0;
        Map<?, ?> imageData;

        if (args.get(2) instanceof Number) {
            width = args.getInt(2);
            height = args.getInt(3);
            imageData = args.getTable(4);
        } else {
            imageData = args.getTable(2);
        }
        FlattenedImage fimg = FlattenedImage.tryFlatten(new ObjectLuaTable(imageData), width, height);
        if (fimg == null) {
            throw new LuaException("no image data");
        }

        this.replace(minX, minY, fimg.width, fimg.height, fimg.data);
        this.tryAutoUpdate();
    }

    @Nullable
    public Function updateAndGetRenderTypes() {
        DynamicTexture texture = (DynamicTexture) this.texture;
        NativeImage nativeImage = texture == null ? null : texture.getPixels();
        if (nativeImage == null || nativeImage.getWidth() != this.width || nativeImage.getHeight() != this.height) {
            if (texture != null) {
                texture.close();
            }
            if (this.width == 0 || this.height == 0) {
                this.texture = null;
                return null;
            }
            texture = new DynamicTexture(this.width, this.height, false);
            this.texture = texture;
            this.imageChanged = true;
            Minecraft.getInstance().getTextureManager().register(this.textureId, texture);
        }
        if (this.imageChanged) {
            this.imageChanged = false;
            IntBuffer buffer = MemoryUtil.memIntBuffer(texture.getPixels().pixels, this.width * this.height);
            buffer.put(0, this.image);
            texture.upload();
        }
        return this.renderTypesMap;
    }

    @Override
    public void setPropertiesFromTable(LuaTable<?, ?> initFields) throws LuaException {
        super.setPropertiesFromTable(initFields);

        int width = clipDim(initFields.optInt("width").orElse(0)), height = clipDim(initFields.optInt("height").orElse(0));
        this.resize(width, height);

        LuaTable<?, ?> image = EmptyLuaTable.orEmpty(initFields.optTable("image"));
        FlattenedImage fimg = FlattenedImage.tryFlatten(image, width, height);
        if (fimg != null) {
            this.replace0(0, 0, fimg.width, fimg.height, fimg.data);
        }
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buffer) {
        super.encode(buffer);
        buffer.writeVarInt(this.width);
        buffer.writeVarInt(this.height);
        encodeImage(buffer, this.image, this.width, 0, 0, this.width, this.height);
    }

    @Override
    public void decode(RegistryFriendlyByteBuf buffer) {
        super.decode(buffer);
        this.textureId = AdvancedPeripherals.getRL("_texture_object_d_" + this.getId());
        this.renderTypesMap = APRenderTypes.createQuadsTex3DMap(this.textureId);
        this.width = buffer.readVarInt();
        this.height = buffer.readVarInt();
        this.image = new int[this.height * this.width];
        this.imageChanged = true;
        decodeImage(buffer, this.image, this.width, 0, 0, this.width, this.height);
    }

    @Override
    public void encodeUpdated(RegistryFriendlyByteBuf buffer) {
        super.encodeUpdated(buffer);
        buffer.writeBoolean(this.resized);
        if (this.resized) {
            this.resized = false;
            buffer.writeVarInt(this.width);
            buffer.writeVarInt(this.height);
        }
        buffer.writeBoolean(this.imageChanged);
        if (this.imageChanged) {
            this.imageChanged = false;
            int minX = this.changedMinX;
            int minY = this.changedMinY;
            int width = this.changedMaxX - this.changedMinX;
            int height = this.changedMaxY - this.changedMinY;
            buffer.writeVarInt(minX);
            buffer.writeVarInt(minY);
            buffer.writeVarInt(width);
            buffer.writeVarInt(height);
            encodeImage(buffer, this.image, this.width, minX, minY, width, height);
        }
    }

    @Override
    public void decodeUpdated(RegistryFriendlyByteBuf buffer) {
        super.decodeUpdated(buffer);
        boolean resized = buffer.readBoolean();
        if (resized) {
            int width = buffer.readVarInt();
            int height = buffer.readVarInt();
            this.resize(width, height);
        }
        boolean imageChanged = buffer.readBoolean();
        if (imageChanged) {
            this.imageChanged = true;
            int minX = buffer.readVarInt();
            int minY = buffer.readVarInt();
            int width = buffer.readVarInt();
            int height = buffer.readVarInt();
            decodeImage(buffer, this.image, this.width, minX, minY, width, height);
        }
    }

    @Override
    public void close() {
        if (this.texture instanceof DynamicTexture texture) {
            this.texture = null;
            texture.close();
        }
    }

    protected void resize(int width, int height) {
        int oldWidth = this.width, oldHeight = this.height;

        int[] newData = new int[height * width];

        if (height != 0 && width != 0 && oldHeight != 0 && oldWidth != 0) {
            int[] oldData = this.image;
            int h = Math.min(height, oldHeight), w = Math.min(width, oldWidth);
            for (int y = 0; y < h; y++) {
                System.arraycopy(oldData, y * width, newData, y * width, w);
            }
        }

        this.width = width;
        this.height = height;
        this.image = newData;
    }

    protected void setColor(int x, int y, int color) {
        this.image[y * width + x] = color;
        if (!this.imageChanged) {
            this.imageChanged = true;
            this.changedMinX = x;
            this.changedMinY = y;
            this.changedMaxX = x;
            this.changedMaxY = y;
        } else {
            this.changedMinX = Math.min(this.changedMinX, x);
            this.changedMinY = Math.min(this.changedMinY, y);
            this.changedMaxX = Math.max(this.changedMaxX, x);
            this.changedMaxY = Math.max(this.changedMaxY, y);
        }
    }

    protected void replace0(int minX, int minY, int width, int height, int[] newData) {
        int[] data = this.image;
        int maxX = minX + width, maxY = minY + height;
        for (int y = minY; y < maxY; y++) {
            System.arraycopy(data, y * width + minX, newData, (y - minY) * width, maxX - minX);
        }
    }

    protected void replace(int minX, int minY, int width, int height, int[] newData) {
        int[] data = this.image;
        int maxX = minX + width, maxY = minY + height;
        for (int y = minY; y < maxY; y++) {
            System.arraycopy(data, y * width + minX, newData, (y - minY) * width, maxX - minX);
        }
        if (!this.imageChanged) {
            this.imageChanged = true;
            this.changedMinX = minX;
            this.changedMinY = minY;
            this.changedMaxX = maxX;
            this.changedMaxY = maxY;
        } else {
            this.changedMinX = Math.min(this.changedMinX, minX);
            this.changedMinY = Math.min(this.changedMinY, minY);
            this.changedMaxX = Math.max(this.changedMaxX, maxX);
            this.changedMaxY = Math.max(this.changedMaxY, maxY);
        }
    }

    private static void encodeImage(FriendlyByteBuf buffer, int[] image, int imageWidth, int minX, int minY, int width, int height) {
        int[] data = new int[width * height];
        IntList palette = new IntArrayList(16);
        Int2IntMap toPalette = new Int2IntOpenHashMap(16);
        toPalette.defaultReturnValue(-1);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int b = image[(minY + y) * imageWidth + minX + x];
                int p = toPalette.get(b);
                if (p == -1) {
                    p = palette.size();
                    palette.add(b);
                    toPalette.put(b, p);
                }
                data[y * width + x] = p;
            }
        }

        buffer.writeVarInt(palette.size());
        for (int b : palette) {
            buffer.writeInt(b);
        }
        for (int p : data) {
            buffer.writeVarInt(p);
        }
    }

    private static void decodeImage(FriendlyByteBuf buffer, int[] image, int imageWidth, int minX, int minY, int width, int height) {
        int paletteSize = buffer.readVarInt();
        int[] palette = new int[paletteSize];
        for (int i = 0; i < paletteSize; i++) {
            palette[i] = buffer.readInt();
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = buffer.readVarInt();
                int b = palette[p];
                image[(minY + y) * imageWidth + minX + x] = b;
            }
        }
    }

    private static int clipDim(int dim) {
        if (dim <= 0) {
            return 0;
        }
        if (dim > MAX_DIM) {
            return MAX_DIM;
        }
        return dim;
    }

    private record FlattenedImage(int width, int height, int[] data) {
        static FlattenedImage tryFlatten(LuaTable<?, ?> image, int optWidth, int optHeight) throws LuaException {
            int length = image.length();
            if (length == 0) {
                return null;
            }

            int width, height;
            int[] data;

            boolean isFlat = image.get(1) instanceof Number || image.get(1.0) instanceof Number;
            if (isFlat) {
                if (optWidth == 0 || optHeight == 0) {
                    throw new LuaException("must provide both width and height with flattened image");
                }
                if (optWidth * optHeight != length) {
                    throw new LuaException("flattened image must have the size of provided width and height");
                }
                width = optWidth;
                height = optHeight;
                data = new int[length];
                for (int i = 1; i <= length; i++) {
                    data[i] = image.getInt(i);
                }
            } else {
                height = length;
                width = 0;
                LuaTable<?, ?>[] rows = new LuaTable[length];
                for (int y = 0; y < height; y++) {
                    LuaTable<?, ?> row = new ObjectLuaTable(image.getTable(y + 1));
                    rows[y] = row;
                    int w = row.length();
                    if (w > width) {
                        width = w;
                    }
                }
                data = new int[width * height];
                for (int y = 0; y < height; y++) {
                    LuaTable<?, ?> row = rows[y];
                    int w = row.length();
                    for (int x = 0; x < w; x++) {
                        data[y * width + x] = row.getInt(x + 1);
                    }
                }
            }
            return new FlattenedImage(width, height, data);
        }
    }
}
