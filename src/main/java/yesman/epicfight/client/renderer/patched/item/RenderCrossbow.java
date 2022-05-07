package yesman.epicfight.client.renderer.patched.item;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.math.OpenMatrix4f;

@OnlyIn(Dist.CLIENT)
public class RenderCrossbow extends RenderShootableWeapon {
	public RenderCrossbow() {
		super(new OpenMatrix4f());
	}
}