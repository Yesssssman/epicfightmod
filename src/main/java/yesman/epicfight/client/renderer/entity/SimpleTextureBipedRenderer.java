package yesman.epicfight.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.capabilities.entity.LivingData;

@OnlyIn(Dist.CLIENT)
public class SimpleTextureBipedRenderer<E extends LivingEntity, T extends LivingData<E>, M extends BipedModel<E>> extends BipedRenderer<E, T, M> {
	public final ResourceLocation textureLocation;
	
	public SimpleTextureBipedRenderer(String texturePath) {
		this(texturePath, EquipmentSlotType.values());
	}
	
	public SimpleTextureBipedRenderer(String texturePath, EquipmentSlotType... renderSlots) {
		super(renderSlots);
		this.textureLocation = new ResourceLocation(texturePath);
	}
	
	@Override
	protected ResourceLocation getEntityTexture(E entityIn) {
		return this.textureLocation;
	}
}