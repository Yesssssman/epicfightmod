package yesman.epicfight.mixin;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.BitSet;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.primitives.Floats;

import it.unimi.dsi.fastutil.ints.IntArrays;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

@Mixin(value = BufferBuilder.class)
public abstract class MixinBufferBuilder {
	@Shadow private ByteBuffer buffer;
	@Shadow private int mode;
	@Shadow private int vertices;
	@Shadow private VertexFormat format;
	@Shadow private int totalRenderedBytes = 0;
	
	@Shadow protected abstract void limitToVertex(FloatBuffer p_227829_1_, int p_227829_2_);
	
	@Inject(at = @At(value = "HEAD"), method = "limitToVertex(Ljava/nio/FloatBuffer;I)V", cancellable = true)
	public void epicfight_limitToVertex(FloatBuffer p_227829_1_, int p_227829_2_, CallbackInfo callbackInfo) {
		if (this.mode == GL11.GL_TRIANGLES) {
			int i = this.format.getIntegerSize() * 3;
			((Buffer) p_227829_1_).limit(this.totalRenderedBytes / 4 + (p_227829_2_ + 1) * i);
			((Buffer) p_227829_1_).position(this.totalRenderedBytes / 4 + p_227829_2_ * i);
			
			callbackInfo.cancel();
		}
	}
	
	@Inject(at = @At(value = "HEAD"), method = "sortQuads(FFF)V", cancellable = true)
	public void epicfight_sortQuads(float x, float y, float z, CallbackInfo callbackInfo) {
		if (this.mode == GL11.GL_TRIANGLES) {
			((Buffer) this.buffer).clear();
			FloatBuffer floatbuffer = this.buffer.asFloatBuffer();
			int i = this.vertices / 3;
			float[] afloat = new float[i];

			for (int j = 0; j < i; ++j) {
				afloat[j] = getTriangleDistanceFromPlayer(floatbuffer, x, y, z,
						this.format.getIntegerSize(), this.totalRenderedBytes / 3 + j * this.format.getVertexSize());
			}
			
			int[] aint = new int[i];

			for (int k = 0; k < aint.length; aint[k] = k++) {
			}
			
			IntArrays.mergeSort(aint, (p_227830_1_, p_227830_2_) -> {
				return Floats.compare(afloat[p_227830_2_], afloat[p_227830_1_]);
			});
			BitSet bitset = new BitSet();
			FloatBuffer floatbuffer1 = GLAllocation.createFloatBuffer(this.format.getIntegerSize() * 3);

			for (int l = bitset.nextClearBit(0); l < aint.length; l = bitset.nextClearBit(l + 1)) {
				int i1 = aint[l];
				if (i1 != l) {
					this.limitToVertex(floatbuffer, i1);
					((Buffer) floatbuffer1).clear();
					floatbuffer1.put(floatbuffer);
					int j1 = i1;

					for (int k1 = aint[i1]; j1 != l; k1 = aint[k1]) {
						this.limitToVertex(floatbuffer, k1);
						FloatBuffer floatbuffer2 = floatbuffer.slice();
						this.limitToVertex(floatbuffer, j1);
						floatbuffer.put(floatbuffer2);
						bitset.set(j1);
						j1 = k1;
					}

					this.limitToVertex(floatbuffer, l);
					((Buffer) floatbuffer1).flip();
					floatbuffer.put(floatbuffer1);
				}

				bitset.set(l);
			}
			
			callbackInfo.cancel();
		}
	}
	
	private static float getTriangleDistanceFromPlayer(FloatBuffer p_181665_0_, float x, float y, float z, int p_181665_4_, int p_181665_5_) {
		float x1 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 0 + 0);
		float y1 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 0 + 1);
		float z1 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 0 + 2);
		float x2 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 1 + 0);
		float y2 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 1 + 1);
		float z2 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 1 + 2);
		float x3 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 2 + 0);
		float y3 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 2 + 1);
		float z3 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 2 + 2);
		
		float[] lineLength = new float[3];
		lineLength[0] = MathHelper.sqrt(MathHelper.square(x2 - x1) + MathHelper.square(y2 - y1) + MathHelper.square(z2 - z1));
		lineLength[1] = MathHelper.sqrt(MathHelper.square(x3 - x2) + MathHelper.square(y3 - y2) + MathHelper.square(z3 - z2));
		lineLength[2] = MathHelper.sqrt(MathHelper.square(x1 - x3) + MathHelper.square(y1 - y3) + MathHelper.square(z1 - z3));
		int longest = 0;

		for (int i = 1; i < 3; i++) {
			if (lineLength[i] > lineLength[longest]) {
				longest = i;
			}
		}
		
		Vector3f center = null;
		
		switch (longest) {
		case 0:
			center = new Vector3f((x1 + x2) * 0.5F, (y1 + y2) * 0.5F, (z1 + z2) * 0.5F);
		case 1:
			center = new Vector3f((x2 + x3) * 0.5F, (y2 + y3) * 0.5F, (z2 + z3) * 0.5F);
		case 2:
			center = new Vector3f((x3 + x1) * 0.5F, (y3 + y1) * 0.5F, (z3 + z1) * 0.5F);
		}
		
		return MathHelper.sqrt(MathHelper.square(center.x() - x) + MathHelper.square(center.y() - y) + MathHelper.square(center.z() - z));
	}
}