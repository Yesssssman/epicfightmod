package maninhouse.epicfight.client.renderer.item;

import maninhouse.epicfight.utils.math.OpenMatrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderCrossbow extends RenderShootableWeapon {
	public RenderCrossbow() {
		this.correctionMatrix = new OpenMatrix4f();
	}
}