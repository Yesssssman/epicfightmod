package yesman.epicfight.client.particle;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.ClientModel;

@OnlyIn(Dist.CLIENT)
public abstract class TexturedCustomModelParticle extends CustomModelParticle {
	protected final ResourceLocation texture;
	
	public TexturedCustomModelParticle(ClientWorld level, double x, double y, double z, double xd, double yd, double zd, ClientModel particleMesh, ResourceLocation texture) {
		super(level, x, y, z, xd, yd, zd, particleMesh);
		this.texture = texture;
	}
	
	@Override
	public void prepareDraw(MatrixStack poseStack, float partialTicks) {
		
		Minecraft mc = Minecraft.getInstance();
		mc.textureManager.bind(this.texture);
	}
}