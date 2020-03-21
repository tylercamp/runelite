package net.runelite.client.plugins.gpu;

import com.jogamp.opengl.GL4;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import static net.runelite.client.plugins.gpu.GLUtil.*;

public class GpuBufferManager {
    GL4 gl;
    Map<Integer, BufferInfo> buffers;

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
        this.buffers = new HashMap<>();
    }

    public void release()
    {
        for (BufferInfo bufferInfo : buffers.values())
        {
            glDeleteBuffer(gl, bufferInfo.bufferId);
        }
    }

    private BufferInfo getOrCreateBuffer(int nameId, int mode)
    {
        BufferInfo bufferInfo;
        if (!buffers.containsKey(nameId))
        {
            bufferInfo = new BufferInfo();
            bufferInfo.bufferId = glGenBuffers(gl);
            bufferInfo.lastLength = 0;
        }
        else
        {
            bufferInfo = buffers.get(nameId);
        }
        return bufferInfo;
    }

    public int uploadBuffer(int nameId, Buffer buffer, int elementSize, int mode)
    {
        int newBufferLength = buffer.limit() * elementSize;
        BufferInfo bufferInfo = getOrCreateBuffer(nameId, mode);

        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, bufferInfo.bufferId);
        if (bufferInfo.needsInit(newBufferLength))
        {
            gl.glBufferData(gl.GL_ARRAY_BUFFER, newBufferLength, buffer, mode);
        }
        else
        {
            gl.glBufferSubData(gl.GL_ARRAY_BUFFER, 0, newBufferLength, buffer);
        }

        return bufferInfo.bufferId;
    }

    public int uploadFloatBuffer(int nameId, FloatBuffer buffer, int mode)
    {
        return uploadBuffer(nameId, buffer, Float.BYTES, mode);
    }

    public int uploadIntBuffer(int nameId, IntBuffer buffer, int mode)
    {
        return uploadBuffer(nameId, buffer, Integer.BYTES, mode);
    }
}
