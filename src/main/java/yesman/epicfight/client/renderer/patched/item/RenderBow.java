package yesman.epicfight.client.renderer.patched.item;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;

@OnlyIn(Dist.CLIENT)
public class RenderBow extends RenderShootableWeapon {
	public RenderBow() {
		super(new OpenMatrix4f().rotateDeg(-90.0F, Vec3f.X_AXIS).rotateDeg(-10.0F, Vec3f.Z_AXIS).translate(0.06F, 0.1F, 0F));
	}
}