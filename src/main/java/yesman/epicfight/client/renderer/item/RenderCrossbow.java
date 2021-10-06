package yesman.epicfight.client.renderer.item;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.utils.math.OpenMatrix4f;

@OnlyIn(Dist.CLIENT)
public class RenderCrossbow extends RenderShootableWeapon {
	public RenderCrossbow() {
		this.correctionMatrix = new OpenMatrix4f();
	}
}