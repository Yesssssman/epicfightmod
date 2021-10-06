package yesman.epicfight.client.renderer.item;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.utils.math.OpenMatrix4f;
import yesman.epicfight.utils.math.Vec3f;

@OnlyIn(Dist.CLIENT)
public class RenderShield extends RenderItemMirror {
	public RenderShield() {
		super();
		this.leftHandCorrectionMatrix = new OpenMatrix4f();
		OpenMatrix4f.translate(new Vec3f(0F,0.5F,-0.13F), this.leftHandCorrectionMatrix, this.leftHandCorrectionMatrix);
		OpenMatrix4f.rotate((float)Math.toRadians(180D), new Vec3f(0F,1F,0F), this.leftHandCorrectionMatrix, this.leftHandCorrectionMatrix);
		OpenMatrix4f.rotate((float)Math.toRadians(90D), new Vec3f(1F,0F,0F), this.leftHandCorrectionMatrix, this.leftHandCorrectionMatrix);
		OpenMatrix4f.translate(new Vec3f(0F,0.1F,0F), this.correctionMatrix, this.correctionMatrix);
	}
}