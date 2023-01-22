package net.modificationstation.stationapi.impl.client.render;

import net.minecraft.class_214;
import net.minecraft.client.render.Tessellator;
import net.modificationstation.stationapi.api.client.render.StationTessellator;
import net.modificationstation.stationapi.mixin.render.client.TessellatorAccessor;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static net.modificationstation.stationapi.impl.client.texture.StationRenderImpl.LOGGER;

public class StationTessellatorImpl implements StationTessellator {

    private final Tessellator self;
    private final TessellatorAccessor access;
    private final int[] fastVertexData = new int[32];

    public StationTessellatorImpl(Tessellator tessellator) {
        self = tessellator;
        access = (TessellatorAccessor) tessellator;
    }

    @Override
    public void quad(int[] vertexData, float x, float y, float z, int colour0, int colour1, int colour2, int colour3, float normalX, float normalY, float normalZ) {
        byte by = (byte)(normalX * 128.0f);
        byte by2 = (byte)(normalY * 127.0f);
        byte by3 = (byte)(normalZ * 127.0f);
        int normal = by | by2 << 8 | by3 << 16;
        System.arraycopy(vertexData, 0, fastVertexData, 0, 32);
        fastVertexData[0] = Float.floatToRawIntBits((float) (Float.intBitsToFloat(fastVertexData[0]) + x + access.getXOffset()));
        fastVertexData[1] = Float.floatToRawIntBits((float) (Float.intBitsToFloat(fastVertexData[1]) + y + access.getYOffset()));
        fastVertexData[2] = Float.floatToRawIntBits((float) (Float.intBitsToFloat(fastVertexData[2]) + z + access.getZOffset()));
        fastVertexData[5] = colour0;
        fastVertexData[6] = normal;
        fastVertexData[8] = Float.floatToRawIntBits((float) (Float.intBitsToFloat(fastVertexData[8]) + x + access.getXOffset()));
        fastVertexData[9] = Float.floatToRawIntBits((float) (Float.intBitsToFloat(fastVertexData[9]) + y + access.getYOffset()));
        fastVertexData[10] = Float.floatToRawIntBits((float) (Float.intBitsToFloat(fastVertexData[10]) + z + access.getZOffset()));
        fastVertexData[13] = colour1;
        fastVertexData[14] = normal;
        fastVertexData[16] = Float.floatToRawIntBits((float) (Float.intBitsToFloat(fastVertexData[16]) + x + access.getXOffset()));
        fastVertexData[17] = Float.floatToRawIntBits((float) (Float.intBitsToFloat(fastVertexData[17]) + y + access.getYOffset()));
        fastVertexData[18] = Float.floatToRawIntBits((float) (Float.intBitsToFloat(fastVertexData[18]) + z + access.getZOffset()));
        fastVertexData[21] = colour2;
        fastVertexData[22] = normal;
        fastVertexData[24] = Float.floatToRawIntBits((float) (Float.intBitsToFloat(fastVertexData[24]) + x + access.getXOffset()));
        fastVertexData[25] = Float.floatToRawIntBits((float) (Float.intBitsToFloat(fastVertexData[25]) + y + access.getYOffset()));
        fastVertexData[26] = Float.floatToRawIntBits((float) (Float.intBitsToFloat(fastVertexData[26]) + z + access.getZOffset()));
        fastVertexData[29] = colour3;
        fastVertexData[30] = normal;
        access.setHasTexture(true);
        access.setHasColour(true);
        access.setHasNormals(true);
        System.arraycopy(fastVertexData, 0, access.stationapi$getBufferArray(), access.stationapi$getBufferPosition(), 24);
        System.arraycopy(fastVertexData, 0, access.stationapi$getBufferArray(), access.stationapi$getBufferPosition() + 24, 8);
        System.arraycopy(fastVertexData, 16, access.stationapi$getBufferArray(), access.stationapi$getBufferPosition() + 32, 16);
        access.stationapi$setVertexAmount(access.stationapi$getVertexAmount() + 4);
        access.stationapi$setBufferPosition(access.stationapi$getBufferPosition() + 48);
        access.stationapi$setVertexCount(access.stationapi$getVertexCount() + 6);
        ensureBufferCapacity(48);
    }

    @Override
    public void ensureBufferCapacity(int criticalCapacity) {
        if (access.stationapi$getBufferPosition() >= access.stationapi$getBufferSize() - criticalCapacity) {
            LOGGER.info("Tessellator is nearing its maximum capacity. Increasing the buffer size from {} to {}", access.stationapi$getBufferSize(), access.stationapi$getBufferSize() * 2);
            access.stationapi$setBufferSize(access.stationapi$getBufferSize() * 2);
            access.stationapi$setBufferArray(Arrays.copyOf(access.stationapi$getBufferArray(), access.stationapi$getBufferSize()));
            ByteBuffer newBuffer = class_214.method_744(access.stationapi$getBufferSize() * 4);
            access.stationapi$setByteBuffer(newBuffer);
            access.stationapi$setIntBuffer(newBuffer.asIntBuffer());
            access.stationapi$setFloatBuffer(newBuffer.asFloatBuffer());
        }
    }
}
