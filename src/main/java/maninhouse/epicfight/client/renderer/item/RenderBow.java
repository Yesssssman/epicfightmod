package maninhouse.epicfight.client.renderer.item;

import maninhouse.epicfight.utils.math.OpenMatrix4f;
import maninhouse.epicfight.utils.math.Vec3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderBow extends RenderShootableWeapon {
	public RenderBow() {
		this.correctionMatrix = new OpenMatrix4f();
		OpenMatrix4f.rotate((float)Math.toRadians(-90.0F), new Vec3f(1.0F, 0.0F, 0.0F), this.correctionMatrix, this.correctionMatrix);
		OpenMatrix4f.rotate((float)Math.toRadians(-10.0F), new Vec3f(0.0F, 0.0F, 1.0F), this.correctionMatrix, this.correctionMatrix);
		OpenMatrix4f.translate(new Vec3f(0.06F, 0.1F, 0), this.correctionMatrix, this.correctionMatrix);
	}
}