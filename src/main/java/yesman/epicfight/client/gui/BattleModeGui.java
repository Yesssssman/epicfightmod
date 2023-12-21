package yesman.epicfight.client.gui;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.math.Vec2f;
import yesman.epicfight.api.utils.math.Vec2i;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.config.ClientConfig;
import yesman.epicfight.config.EpicFightOptions;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.Skill.ActivateType;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;

@OnlyIn(Dist.CLIENT)
public class BattleModeGui extends ModIngameGui {
	private static final Vec2f[] CLOCK_POS = {
		new Vec2f(0.5F, 0.5F),
		new Vec2f(0.5F, 0.0F),
		new Vec2f(0.0F, 0.0F),
		new Vec2f(0.0F, 1.0F),
		new Vec2f(1.0F, 1.0F),
		new Vec2f(1.0F, 0.0F)
	};
	
	public Font font;
	private int sliding;
	private boolean slidingToggle;
	private final List<SkillContainer> skillIcons = Lists.newLinkedList();
	private final EpicFightOptions config;

	public BattleModeGui(Minecraft minecraft) {
		this.sliding = 29;
		this.slidingToggle = false;
		this.font = minecraft.font;
		this.config = EpicFightMod.CLIENT_CONFIGS;
	}
	
	public void renderGui(LocalPlayerPatch playerpatch, GuiGraphics guiGraphics, float partialTicks) {
		if (!playerpatch.getOriginal().isAlive() || playerpatch.getOriginal().getVehicle() != null) {
			return;
		}
		
		if (this.sliding > 28) {
			return;
		} else if (this.sliding > 0) {
			if (this.slidingToggle) {
				this.sliding -= 2;
			} else {
				this.sliding += 2;
			}
		}
		
		Window sr = Minecraft.getInstance().getWindow();
		int width = sr.getGuiScaledWidth();
		int height = sr.getGuiScaledHeight();
		
		boolean depthTestEnabled = GL11.glGetBoolean(GL11.GL_DEPTH_TEST);
		boolean blendEnabled = GL11.glGetBoolean(GL11.GL_BLEND);
		
		if (depthTestEnabled) {
			RenderSystem.disableDepthTest();
		}
		
		if (!blendEnabled) {
			RenderSystem.enableBlend();
		}
		
		PoseStack poseStack = guiGraphics.pose();
		poseStack.pushPose();
		poseStack.setIdentity();

		RenderSystem.setShader(GameRenderer::getPositionTexShader);

		float maxStamina = playerpatch.getMaxStamina();
		float stamina = playerpatch.getStamina();
		
		if (maxStamina > 0.0F && stamina < maxStamina) {
			Vec2i pos = this.config.getStaminaPosition(width, height);
			float prevStamina = playerpatch.getPrevStamina();
			float ratio = (prevStamina + (stamina - prevStamina) * partialTicks) / maxStamina;

			poseStack.pushPose();
			poseStack.translate(0, this.sliding, 0);
			RenderSystem.setShaderColor(1.0F, ratio, 0.25F, 1.0F);
			guiGraphics.blit(EntityIndicator.BATTLE_ICON, pos.x, pos.y, 118, 4, 2, 38, 237, 9, 255, 255);
			guiGraphics.blit(EntityIndicator.BATTLE_ICON, pos.x, pos.y, (int)(118*ratio), 4, 2, 47, (int)(237*ratio), 9, 255, 255);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			poseStack.popPose();
		}
		
		if (playerpatch.isChargingSkill()) {
			int chargeAmount = playerpatch.getChargingSkill().getChargingAmount(playerpatch);
			int prevChargingAmount = playerpatch.getPrevChargingAmount();
			float ratio = Math.min((prevChargingAmount + (chargeAmount - prevChargingAmount) * partialTicks) / playerpatch.getChargingSkill().getMaxChargingTicks(), 1.0F);
			Vec2i pos = this.config.getChargingBarPosition(width, height);

			poseStack.pushPose();
			poseStack.translate(0, this.sliding, 0);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			guiGraphics.blit(EntityIndicator.BATTLE_ICON, pos.x, pos.y, 1, 71, 238, 13, 255, 255);
			guiGraphics.blit(EntityIndicator.BATTLE_ICON, pos.x, pos.y, 1, 57, (int)(238 * ratio), 13, 255, 255);

			ResourceLocation rl = new ResourceLocation(playerpatch.getChargingSkill().toString());
			String skillName = Component.translatable(String.format("skill.%s.%s", rl.getNamespace(), rl.getPath())).getString();
			
			int stringWidth = this.font.width(skillName);
			guiGraphics.drawString(this.font, skillName, (pos.x + 120 - stringWidth * 0.5F), pos.y - 12, 16777215, true);

			poseStack.popPose();
		}
		
		for (int i = 0; i < SkillSlots.ENUM_MANAGER.universalValues().size(); i++) {
			SkillContainer container = playerpatch.getSkill(i);
			
			if (!container.isEmpty()) {
				if (!this.skillIcons.contains(container) && container.getSkill().shouldDraw(container)) {
					this.skillIcons.add(container);
				}
			}
		}

		this.skillIcons.removeIf((skillContainer) -> skillContainer.isEmpty() || !skillContainer.getSkill().shouldDraw(skillContainer));
		SkillContainer innateSkillContainer = playerpatch.getSkill(SkillSlots.WEAPON_INNATE);

		if (!innateSkillContainer.isEmpty()) {
			this.drawWeaponInnateIcon(playerpatch, playerpatch.getSkill(SkillSlots.WEAPON_INNATE), guiGraphics, partialTicks);
		}

		ClientConfig.AlignDirection alignDirection = this.config.passivesAlignDirection.getValue();
		ClientConfig.HorizontalBasis horBasis = this.config.passivesXBase.getValue();
		ClientConfig.VerticalBasis verBasis = this.config.passivesYBase.getValue();
		int passiveX = horBasis.positionGetter.apply(width, this.config.passivesX.getValue());
		int passiveY = verBasis.positionGetter.apply(height, this.config.passivesY.getValue());
		int icons = this.skillIcons.size();
		Vec2i slotCoord = alignDirection.startCoordGetter.get(passiveX, passiveY, 24, 24, icons, horBasis, verBasis);

		for (SkillContainer container : this.skillIcons) {
			if (!container.isEmpty()) {
				Skill skill = container.getSkill();

				if (skill.shouldDraw(container)) {
					RenderSystem.enableBlend();
					RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
					RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
					
					skill.drawOnGui(this, container, guiGraphics, slotCoord.x, slotCoord.y);
					slotCoord = alignDirection.nextPositionGetter.getNext(horBasis, verBasis, slotCoord, 24, 24);
				}
			}
		}

		poseStack.popPose();

		if (depthTestEnabled) {
			RenderSystem.enableDepthTest();
		}
		
		if (!blendEnabled) {
			RenderSystem.disableBlend();
		}
	}
	
	private void drawWeaponInnateIcon(LocalPlayerPatch playerpatch, SkillContainer container, GuiGraphics guiGraphics, float partialTicks) {
		PoseStack poseStack = guiGraphics.pose();
		Window sr = Minecraft.getInstance().getWindow();
		int width = sr.getGuiScaledWidth();
		int height = sr.getGuiScaledHeight();
		Vec2i pos = this.config.getWeaponInnatePosition(width, height);

		poseStack.pushPose();
		poseStack.translate(0, (float)this.sliding, 0);
		
		boolean creative = playerpatch.getOriginal().isCreative();
		boolean fullstack = creative || container.isFull();
		boolean canUse = !container.isDisabled() && container.getSkill().checkExecuteCondition(playerpatch);
		float cooldownRatio = (fullstack || container.isActivated()) ? 1.0F : container.getResource(partialTicks);
		int vertexNum = 0;
		float iconSize = 32.0F;
		float bottom = pos.y + iconSize;
		float right = pos.x + iconSize;
		float middle = pos.x + iconSize * 0.5F;
		float lastVertexX = 0;
		float lastVertexY = 0;
		float lastTexX = 0;
		float lastTexY = 0;
		
		if (cooldownRatio < 0.125F) {
			vertexNum = 6;
			lastTexX = cooldownRatio / 0.25F;
			lastTexY = 0.0F;
			lastVertexX = middle + iconSize * lastTexX;
			lastVertexY = pos.y;
			lastTexX += 0.5F;
		} else if (cooldownRatio < 0.375F) {
			vertexNum = 5;
			lastTexX = 1.0F;
			lastTexY = (cooldownRatio - 0.125F) / 0.25F;
			lastVertexX = right;
			lastVertexY = pos.y + iconSize * lastTexY;
		} else if (cooldownRatio < 0.625F) {
			vertexNum = 4;
			lastTexX = (cooldownRatio - 0.375F) / 0.25F;
			lastTexY = 1.0F;
			lastVertexX = right - iconSize * lastTexX;
			lastVertexY = bottom;
			lastTexX = 1.0F - lastTexX;
		} else if (cooldownRatio < 0.875F) {
			vertexNum = 3;
			lastTexX = 0.0F;
			lastTexY = (cooldownRatio - 0.625F) / 0.25F;
			lastVertexX = pos.x;
			lastVertexY = bottom - iconSize * lastTexY;
			lastTexY = 1.0F - lastTexY;
		} else {
			vertexNum = 2;
			lastTexX = (cooldownRatio - 0.875F) / 0.25F;
			lastTexY = 0.0F;
			lastVertexX = pos.x + iconSize * lastTexX;
			lastVertexY = pos.y;
		}
		
		RenderSystem.enableBlend();
		RenderSystem.setShaderTexture(0, container.getSkill().getSkillTexture());
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		
		if (canUse) {
			if (container.getStack() > 0) {
				RenderSystem.setShaderColor(0.0F, 0.64F, 0.72F, 0.8F);
			} else {
				RenderSystem.setShaderColor(0.0F, 0.5F, 0.5F, 0.6F);
			}
		} else {
			RenderSystem.setShaderColor(0.5F, 0.5F, 0.5F, 0.6F);
		}
		
		Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_TEX);
        
        for (int j = 0; j < vertexNum; j++) {
        	bufferbuilder.vertex(poseStack.last().pose(), pos.x + iconSize * CLOCK_POS[j].x, pos.y + iconSize * CLOCK_POS[j].y, 0.0F).uv(CLOCK_POS[j].x, CLOCK_POS[j].y).endVertex();
		}
        
        bufferbuilder.vertex(poseStack.last().pose(), lastVertexX, lastVertexY, 0.0F).uv(lastTexX, lastTexY).endVertex();
        tessellator.end();
        
        if (canUse) {
			RenderSystem.setShaderColor(0.08F, 0.79F, 0.95F, 1.0F);
		} else {
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		}
        
        RenderSystem.disableCull();
        bufferbuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_TEX);
        
        for (int j = 0; j < 2; j++) {
        	bufferbuilder.vertex(poseStack.last().pose(), pos.x + iconSize * CLOCK_POS[j].x, pos.y + iconSize * CLOCK_POS[j].y, 0.0F)
        		.uv(CLOCK_POS[j].x, CLOCK_POS[j].y).endVertex();
		}
		
		for (int j = CLOCK_POS.length - 1; j >= vertexNum; j--) {
        	bufferbuilder.vertex(poseStack.last().pose(), pos.x + iconSize * CLOCK_POS[j].x, pos.y + iconSize * CLOCK_POS[j].y, 0.0F)
        		.uv(CLOCK_POS[j].x, CLOCK_POS[j].y).endVertex();
		}
        
        bufferbuilder.vertex(poseStack.last().pose(), lastVertexX, lastVertexY, 0.0F).uv(lastTexX, lastTexY).endVertex();
        tessellator.end();
        
     	RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        
        if (container.isActivated() && (container.getSkill().getActivateType() == ActivateType.DURATION || container.getSkill().getActivateType() == ActivateType.DURATION_INFINITE)) {
			String s = String.format("%.0f", container.getRemainDuration() / 20.0F);
			int stringWidth = (this.font.width(s) - 6) / 3;
			guiGraphics.drawString(this.font, s, pos.x + 13 - stringWidth, pos.y + 13, 16777215, true);
		} else if (!fullstack) {
			String s = String.valueOf((int)(cooldownRatio * 100.0F));
			int stringWidth = (this.font.width(s) - 6) / 3;
			guiGraphics.drawString(this.font, s, pos.x + 13 - stringWidth, pos.y + 13, 16777215, true);
		}
		
		if (container.getSkill().getMaxStack() > 1) {
			String s = String.valueOf(container.getStack());
			int stringWidth = (this.font.width(s) - 6) / 3;
			guiGraphics.drawString(font, s, pos.x + 25 - stringWidth, pos.y + 22, 16777215, true);
		}
		
		poseStack.popPose();
	}
	
	public void slideUp() {
		this.sliding = 28;
		this.slidingToggle = true;
	}

	public void slideDown() {
		this.sliding = 1;
		this.slidingToggle = false;
	}
	
	public void reset() {
		this.skillIcons.clear();
	}

	public int getSlidingProgression() {
		return this.sliding;
	}
}