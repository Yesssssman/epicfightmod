package yesman.epicfight.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.client.renderer.layer.ElytraAnimatedLayer;
import yesman.epicfight.client.renderer.layer.HeadAnimatedLayer;
import yesman.epicfight.client.renderer.layer.HeldItemAnimatedLayer;
import yesman.epicfight.client.renderer.layer.WearableItemLayer;
import yesman.epicfight.model.Armature;
import yesman.epicfight.utils.math.OpenMatrix4f;
import yesman.epicfight.utils.math.Vec3f;

@OnlyIn(Dist.CLIENT)
public abstract class BipedRenderer<E extends LivingEntity, T extends LivingData<E>, M extends BipedModel<E>> extends ArmatureRenderer<E, T, M> {
	public BipedRenderer() {
		this(EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET);
	}
	
	public BipedRenderer(EquipmentSlotType... renderSlots) {
		this.layerRendererReplace.put(ElytraLayer.class, new ElytraAnimatedLayer<>());
		this.layerRendererReplace.put(HeldItemLayer.class, new HeldItemAnimatedLayer<>());
		this.layerRendererReplace.put(BipedArmorLayer.class, new WearableItemLayer<>(renderSlots));
		this.layerRendererReplace.put(HeadLayer.class, new HeadAnimatedLayer<>());
	}
	
	@Override
	protected void applyTransforms(MatrixStack matStack, Armature armature, E entityIn, T entitydata, float partialTicks) {
		super.applyTransforms(matStack, armature, entityIn, entitydata, partialTicks);
		if (entityIn.isSneaking()) {
			matStack.translate(0.0D, 0.15D, 0.0D);
		}
		if (entityIn.isChild()) {
			this.transformJoint(9, armature, new OpenMatrix4f().scale(new Vec3f(1.25F, 1.25F, 1.25F)));
		}
		
		this.transformJoint(9, armature, entitydata.getHeadMatrix(partialTicks));
	}
	
	@Override
	protected int getRootJointIndex() {
		return 7;
	}
	
	@Override
	protected double getLayerCorrection() {
		return 0.75F;
	}
}