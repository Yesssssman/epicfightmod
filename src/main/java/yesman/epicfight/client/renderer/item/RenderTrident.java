package yesman.epicfight.client.renderer.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.utils.math.OpenMatrix4f;
import yesman.epicfight.utils.math.Vec3f;

public class RenderTrident extends RenderItemBase {
	private OpenMatrix4f correctionMatrixReverse = new OpenMatrix4f();

	public RenderTrident() {
		this.correctionMatrix = new OpenMatrix4f();
		OpenMatrix4f.rotate((float)Math.toRadians(-80), new Vec3f(1,0,0), this.correctionMatrix, this.correctionMatrix);
		OpenMatrix4f.translate(new Vec3f(0.0F,0.1F,0.0F), this.correctionMatrix, this.correctionMatrix);
		
		OpenMatrix4f.rotate((float)Math.toRadians(-80), new Vec3f(1,0,0), this.correctionMatrixReverse, this.correctionMatrixReverse);
		OpenMatrix4f.translate(new Vec3f(0.0F,0.1F,0.0F), this.correctionMatrixReverse, this.correctionMatrixReverse);
	}
	
	@Override
	public OpenMatrix4f getCorrectionMatrix(ItemStack stack, LivingData<?> itemHolder, Hand hand)
	{
		if(itemHolder.getOriginalEntity().getItemInUseCount() > 0)
		{
			return new OpenMatrix4f(this.correctionMatrixReverse);
		}
		else
		{
			OpenMatrix4f mat = new OpenMatrix4f(this.correctionMatrix);
			OpenMatrix4f.translate(new Vec3f(0.0F, 0.4F, 0.0F), mat, mat);
			return mat;
		}
	}
}