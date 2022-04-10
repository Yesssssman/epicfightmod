package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class SimpleTextureHumanoidRenderer<E extends LivingEntity, T extends LivingEntityPatch<E>, M extends HumanoidModel<E>> extends PHumanoidRenderer<E, T, M> {
	public final ResourceLocation textureLocation;
	
	public SimpleTextureHumanoidRenderer(String texturePath) {
		this(texturePath, EquipmentSlot.values());
	}
	
	public SimpleTextureHumanoidRenderer(String texturePath, EquipmentSlot... renderSlots) {
		super(renderSlots);
		this.textureLocation = new ResourceLocation(texturePath);
	}
	
	@Override
	protected ResourceLocation getEntityTexture(T entitypatch) {
		return this.textureLocation;
	}
}