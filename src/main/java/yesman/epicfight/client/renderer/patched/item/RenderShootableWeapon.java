package yesman.epicfight.client.renderer.patched.item;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class RenderShootableWeapon extends RenderItemBase {
	@Override
	public void renderItemInHand(ItemStack stack, LivingEntityPatch<?> itemHolder, InteractionHand hand, MultiBufferSource buffer, PoseStack matrixStackIn, int packedLight) {
		super.renderItemInHand(stack, itemHolder, InteractionHand.OFF_HAND, buffer, matrixStackIn, packedLight);
	}
}