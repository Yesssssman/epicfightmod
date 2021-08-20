package maninhouse.epicfight.client.renderer.item;

import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.utils.math.OpenMatrix4f;
import maninhouse.epicfight.utils.math.Vec3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class RenderTrident extends RenderItemBase
{
	private OpenMatrix4f correctionMatrixReverse = new OpenMatrix4f();
	
	public RenderTrident()
	{
		correctionMatrix = new OpenMatrix4f();
		OpenMatrix4f.rotate((float)Math.toRadians(-80), new Vec3f(1,0,0), correctionMatrix, correctionMatrix);
		OpenMatrix4f.translate(new Vec3f(0.0F,0.1F,0.0F), correctionMatrix, correctionMatrix);
		
		OpenMatrix4f.rotate((float)Math.toRadians(-80), new Vec3f(1,0,0), correctionMatrixReverse, correctionMatrixReverse);
		OpenMatrix4f.translate(new Vec3f(0.0F,0.1F,0.0F), correctionMatrixReverse, correctionMatrixReverse);
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
			OpenMatrix4f mat = new OpenMatrix4f(correctionMatrix);
			OpenMatrix4f.translate(new Vec3f(0.0F, 0.4F, 0.0F), mat, mat);
			return mat;
		}
	}
}