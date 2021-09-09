package maninhouse.epicfight.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;

import maninhouse.epicfight.capabilities.entity.mob.CreeperData;
import maninhouse.epicfight.model.Armature;
import net.minecraft.client.renderer.entity.model.CreeperModel;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreeperRenderer extends ArmatureRenderer<CreeperEntity, CreeperData, CreeperModel<CreeperEntity>> {
	public static final ResourceLocation CREEPER_TEXTURE = new ResourceLocation("textures/entity/creeper/creeper.png");
	private final ResourceLocation customTexture;
	
	public CreeperRenderer() {
		this(null);
	}
	
	public CreeperRenderer(ResourceLocation customTextureLocation) {
		super();
		this.customTexture = customTextureLocation;
	}
	
	@Override
	protected ResourceLocation getEntityTexture(CreeperEntity entityIn) {
		if (this.customTexture != null) {
			return customTexture;
		} else {
			return CREEPER_TEXTURE;
		}
	}
	
	@Override
	protected void applyRotations(MatrixStack matStack, Armature armature, CreeperEntity entityIn, CreeperData entitydata, float partialTicks) {
		super.applyRotations(matStack, armature, entityIn, entitydata, partialTicks);
		this.transformJoint(2, armature, entitydata.getHeadMatrix(partialTicks));
	}
}