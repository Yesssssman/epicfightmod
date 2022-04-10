package yesman.epicfight.client.renderer.patched.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class RenderTrident extends RenderItemBase {
	private OpenMatrix4f correctionMatrixReverse;

	public RenderTrident() {
		this.correctionMatrix = new OpenMatrix4f().rotateDeg(-80F, Vec3f.X_AXIS).translate(0.0F, 0.1F, 0.0F);
		this.correctionMatrixReverse = new OpenMatrix4f().rotateDeg(-80F, Vec3f.X_AXIS).translate(0.0F, 0.1F, 0.0F);
	}
	
	@Override
	public OpenMatrix4f getCorrectionMatrix(ItemStack stack, LivingEntityPatch<?> entitypatch, InteractionHand hand) {
		if (entitypatch.getOriginal().getUseItemRemainingTicks() > 0) {
			return new OpenMatrix4f(this.correctionMatrixReverse);
		} else {
			OpenMatrix4f mat = new OpenMatrix4f(this.correctionMatrix).translate(0.0F, 0.4F, 0.0F);
			return mat;
		}
	}
}