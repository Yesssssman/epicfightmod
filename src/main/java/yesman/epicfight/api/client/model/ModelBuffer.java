package yesman.epicfight.api.client.model;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexBuffer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelBuffer {
	private final VertexBuffer.Usage usage;
	private int vertexBufferId;
	private int indexBufferId;
	private int arrayObjectId;
	
	public ModelBuffer(VertexBuffer.Usage usage) {
		this.usage = usage;
		RenderSystem.assertOnRenderThread();
		this.vertexBufferId = GlStateManager._glGenBuffers();
		this.indexBufferId = GlStateManager._glGenBuffers();
		this.arrayObjectId = GlStateManager._glGenVertexArrays();
	}
	
	
}
