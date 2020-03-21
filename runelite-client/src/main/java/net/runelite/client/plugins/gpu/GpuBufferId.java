package net.runelite.client.plugins.gpu;

public enum GpuBufferId {
	// Static scene data
	SceneVertexBuffer,
	SceneUvBuffer,

	// Dynamic model data
	DynamicVertexBuffer,
	DynamicUvBuffer,

	// Provides offsets of model data within the dynamic buffer
	ModelIndexUnorderedBuffer,
	ModelIndexSmallBuffer,
	ModelIndexLargeBuffer,

	// Buffers containing output of compute shaders
	ComputeOutVertexBuffer,
	ComputeOutUvBuffer
}
