package yesman.epicfight.client.renderer.item;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.utils.math.OpenMatrix4f;
import yesman.epicfight.utils.math.Vec3f;

@OnlyIn(Dist.CLIENT)
public class RenderBow extends RenderShootableWeapon {
	public RenderBow() {
		this.correctionMatrix = new OpenMatrix4f();
		OpenMatrix4f.rotate((float)Math.toRadians(-90.0F), new Vec3f(1.0F, 0.0F, 0.0F), this.correctionMatrix, this.correctionMatrix);
		OpenMatrix4f.rotate((float)Math.toRadians(-10.0F), new Vec3f(0.0F, 0.0F, 1.0F), this.correctionMatrix, this.correctionMatrix);
		OpenMatrix4f.translate(new Vec3f(0.06F, 0.1F, 0), this.correctionMatrix, this.correctionMatrix);
	}
}