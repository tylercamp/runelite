package net.runelite.client.plugins.gpu;

import com.jogamp.opengl.GL4;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import static net.runelite.client.plugins.gpu.GLUtil.*;

public class GpuBufferManager {
    private GL4 gl;
    private EnumMap<GpuBufferId, BufferInfo> buffers;

    private class BufferInfo
    {
        public int bufferId;
        public int lastLength;

        public boolean needsInit(int desiredLength)
        {
            return lastLength == 0 || lastLength != desiredLength;
        }
    }

    public GpuBufferManager(GL4 gl)
    {
        this.gl = gl;
        this.buffers = new EnumMap<>(GpuBufferId.class);
    }

    public void release()
    {
        for (BufferInfo bufferInfo : buffers.values())
        {
            glDeleteBuffer(gl, bufferInfo.bufferId);
        }
    }

    private BufferInfo getOrCreateBuffer(GpuBufferId nameId)
    {
        BufferInfo bufferInfo;
        if (!buffers.containsKey(nameId))
        {
            bufferInfo = new BufferInfo();
            bufferInfo.bufferId = glGenBuffers(gl);
            bufferInfo.lastLength = 0;
            buffers.put(nameId, bufferInfo);
        }
        else
        {
            bufferInfo = buffers.get(nameId);
        }
        return bufferInfo;
    }

    public int getBuffer(GpuBufferId nameId)
    {
        return getOrCreateBuffer(nameId).bufferId;
    }

    public int uploadBuffer(GpuBufferId nameId, Buffer buffer, int elementSize, int mode)
    {
        int newBufferLength = buffer.limit() * elementSize;
        BufferInfo bufferInfo = getOrCreateBuffer(nameId);

        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, bufferInfo.bufferId);
        if (bufferInfo.needsInit(newBufferLength))
        {
            gl.glBufferData(gl.GL_ARRAY_BUFFER, newBufferLength, buffer, mode);
        }
        else
        {
            gl.glBufferSubData(gl.GL_ARRAY_BUFFER, 0, newBufferLength, buffer);
        }

        bufferInfo.lastLength = newBufferLength;

        return bufferInfo.bufferId;
    }

    public int uploadFloatBuffer(GpuBufferId nameId, FloatBuffer buffer, int mode)
    {
        return uploadBuffer(nameId, buffer, Float.BYTES, mode);
    }

    public int uploadIntBuffer(GpuBufferId nameId, IntBuffer buffer, int mode)
    {
        return uploadBuffer(nameId, buffer, Integer.BYTES, mode);
    }
}
