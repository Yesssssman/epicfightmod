package yesman.epicfight.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.VertexFormatElement;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EpicFightVertexFormatElement extends VertexFormatElement {
	public EpicFightVertexFormatElement(int index, Type type, Usage usage, int count) {
		super(index, type, usage, count);
	}
	
	@Override
	public void setupBufferState(int id, long p_166967_, int p_166968_) {
		GlStateManager._enableVertexAttribArray(id);
	}
	
	@Override
	public void clearBufferState(int id) {
		GlStateManager._disableVertexAttribArray(id);
	}
}
