package yesman.epicfight.client.renderer;

import java.util.function.BiConsumer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.VertexFormatElement;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.AnimatedMesh;

@OnlyIn(Dist.CLIENT)
public class EpicFightVertexFormatElement extends VertexFormatElement {
	private static AnimatedMesh drawing;
	
	public static void bindDrawing(AnimatedMesh mesh) {
		drawing = mesh;
	}
	
	public static void unbindDrawing() {
		drawing = null;
	}
	
	private final BiConsumer<AnimatedMesh, Integer> onSetupBuffer;
	
	public EpicFightVertexFormatElement(int index, Type type, Usage usage, int count, BiConsumer<AnimatedMesh, Integer> onSetupBuffer) {
		super(index, type, usage, count);
		
		this.onSetupBuffer = onSetupBuffer;
	}
	
	@Override
	public void setupBufferState(int id, long p_166967_, int p_166968_) {
		if (drawing == null) {
			throw new RuntimeException("No mesh bound");
		}
		
		GlStateManager._enableVertexAttribArray(id);
		this.onSetupBuffer.accept(drawing, id);
	}
	
	@Override
	public void clearBufferState(int id) {
		GlStateManager._disableVertexAttribArray(id);
	}
}
