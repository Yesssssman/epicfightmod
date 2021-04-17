package maninthehouse.epicfight.client.renderer.item;

import maninthehouse.epicfight.utils.math.Vec3f;
import maninthehouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderShield extends RenderItemMirror {
	public RenderShield() {
		super();
		this.leftHandCorrectionMatrix = new VisibleMatrix4f();
		VisibleMatrix4f.translate(new Vec3f(0F,0.5F,-0.13F), this.leftHandCorrectionMatrix, this.leftHandCorrectionMatrix);
		VisibleMatrix4f.rotate((float)Math.toRadians(180D), new Vec3f(0F,1F,0F), this.leftHandCorrectionMatrix, this.leftHandCorrectionMatrix);
		VisibleMatrix4f.rotate((float)Math.toRadians(90D), new Vec3f(1F,0F,0F), this.leftHandCorrectionMatrix, this.leftHandCorrectionMatrix);
		VisibleMatrix4f.translate(new Vec3f(0F,0.1F,0F), this.correctionMatrix, this.correctionMatrix);
	}
}