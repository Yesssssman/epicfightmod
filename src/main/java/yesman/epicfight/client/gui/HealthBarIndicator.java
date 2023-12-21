package yesman.epicfight.client.gui;

import java.util.Collection;
import java.util.Iterator;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.client.renderer.EpicFightRenderTypes;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.config.ClientConfig;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.effect.VisibleMobEffect;

import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class HealthBarIndicator extends EntityIndicator {
	@Override
	public boolean shouldDraw(LivingEntity entityIn, @Nullable LivingEntityPatch<?> entitypatch, LocalPlayerPatch playerpatch) {
		ClientConfig.HealthBarShowOptions option = EpicFightMod.CLIENT_CONFIGS.healthBarShowOption.getValue();
		Minecraft mc = Minecraft.getInstance();
		
		if (option == ClientConfig.HealthBarShowOptions.NONE) {
			return false;
		} else if (!entityIn.canChangeDimensions() || entityIn.isInvisible() || entityIn == playerpatch.getOriginal().getVehicle()) {
			return false;
		} else if (entityIn.distanceToSqr(mc.getCameraEntity()) >= 400) {
			return false;
		} else if (entityIn instanceof Player playerIn) {
			if (playerIn == playerpatch.getOriginal() && playerpatch.getMaxStunShield() <= 0.0F) {
				return false;
			} else if (playerIn.isCreative() || playerIn.isSpectator()) {
				return false;
			}
		}
		
		if (option == ClientConfig.HealthBarShowOptions.TARGET) {
			return playerpatch.getTarget() == entityIn;
		}

		return (!entityIn.getActiveEffects().isEmpty() || !(entityIn.getHealth() >= entityIn.getMaxHealth())) && entityIn.deathTime < 19;
	}
	
	@Override
	public void drawIndicator(LivingEntity entityIn, @Nullable LivingEntityPatch<?> entitypatch, LocalPlayerPatch playerpatch, PoseStack matStackIn, MultiBufferSource bufferIn, float partialTicks) {
		Matrix4f mvMatrix = super.getMVMatrix(matStackIn, entityIn, 0.0F, entityIn.getBbHeight() + 0.25F, 0.0F, true, partialTicks);
		Collection<MobEffectInstance> activeEffects = entityIn.getActiveEffects(); 
		
		if (!activeEffects.isEmpty() && !entityIn.is(playerpatch.getOriginal())) {
			Iterator<MobEffectInstance> iter = activeEffects.iterator();
			int acives = activeEffects.size();
			int row = acives > 1 ? 1 : 0;
			int column = ((acives-1) / 2);
			float startX = -0.8F + -0.3F * row;
			float startY = -0.15F + 0.15F * column;
			
			for (int i = 0; i <= column; i++) {
				for (int j = 0; j <= row; j++) {
					MobEffectInstance effectInstance = iter.next();
					MobEffect effect = effectInstance.getEffect();
					ResourceLocation rl;
					
					if (effect instanceof VisibleMobEffect visibleMobEffect) {
						rl = visibleMobEffect.getIcon(effectInstance);
					} else {
						rl = new ResourceLocation(ForgeRegistries.MOB_EFFECTS.getKey(effect).getNamespace(), "textures/mob_effect/" + ForgeRegistries.MOB_EFFECTS.getKey(effect).getPath() + ".png");
					}
					
					Minecraft.getInstance().getTextureManager().bindForSetup(rl);
					float x = startX + 0.3F * j;
					float y = startY + -0.3F * i;
					
					VertexConsumer vertexBuilder1 = bufferIn.getBuffer(EpicFightRenderTypes.entityIndicator(rl));
					
					this.drawTexturedModalRect2DPlane(mvMatrix, vertexBuilder1, x, y, x + 0.3F, y + 0.3F, 0, 0, 256, 256);
					if (!iter.hasNext()) {
						break;
					}
				}
			}
		}
		
		VertexConsumer vertexBuilder = bufferIn.getBuffer(EpicFightRenderTypes.entityIndicator(BATTLE_ICON));
		
		float ratio = Mth.clamp(entityIn.getHealth() / entityIn.getMaxHealth(), 0.0F, 1.0F);
		float healthRatio = -0.5F + ratio;
		int textureRatio = (int) (62 * ratio);
		this.drawTexturedModalRect2DPlane(mvMatrix, vertexBuilder, -0.5F, -0.05F, healthRatio, 0.05F, 1, 15, textureRatio, 20);
		this.drawTexturedModalRect2DPlane(mvMatrix, vertexBuilder, healthRatio, -0.05F, 0.5F, 0.05F, textureRatio, 10, 62, 15);
		float absorption = entityIn.getAbsorptionAmount();
		
		if (absorption > 0.0D) {
			float absorptionRatio = Mth.clamp(absorption / entityIn.getMaxHealth(), 0.0F, 1.0F);
			int absTexRatio = (int) (62 * absorptionRatio);
			this.drawTexturedModalRect2DPlane(mvMatrix, vertexBuilder, -0.5F, -0.05F, absorptionRatio - 0.5F, 0.05F, 1, 20, absTexRatio, 25);
		}
		
		if (entitypatch != null) {
			this.renderStunShield(entitypatch, mvMatrix, vertexBuilder);
		}
	}
	
	private void renderStunShield(LivingEntityPatch<?> entitypatch, Matrix4f mvMatrix, VertexConsumer vertexBuilder) {
		if (entitypatch.getStunShield() == 0) {
			return;
		}
		
		float ratio = Mth.clamp(entitypatch.getStunShield() / entitypatch.getMaxStunShield(), 0.0F, 1.0F);
		float barRatio = -0.5F + ratio;
		int textureRatio = (int) (62 * ratio);
		
		this.drawTexturedModalRect2DPlane(mvMatrix, vertexBuilder, -0.5F, -0.1F, barRatio, -0.05F, 1, 5, textureRatio, 10);
		this.drawTexturedModalRect2DPlane(mvMatrix, vertexBuilder, barRatio, -0.1F, 0.5F, -0.05F, textureRatio, 0, 63, 5);
	}
}