package yesman.epicfight.client.gui;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.math.Vec2f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.Skill.ActivateType;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillContainer;

@SuppressWarnings("deprecation")
@OnlyIn(Dist.CLIENT)
public class BattleModeGui extends ModIngameGui {
	private static final Map<Integer, Vec3f> POSITION_MAP = Maps.<Integer, Vec3f>newHashMap();
	private int sliding;
	private boolean slidingToggle;
	private List<SkillContainer> skillIcons = Lists.newLinkedList();
	
	private final Minecraft mc;
	public FontRenderer font;
	
	static {
		POSITION_MAP.put(-1, new Vec3f(42F, 48F, 0.117F));
		POSITION_MAP.put(0, new Vec3f(70F, 36F, 0.078F));
		POSITION_MAP.put(1, new Vec3f(94F, 36F, 0.078F));
		POSITION_MAP.put(2, new Vec3f(116F, 36F, 0.078F));
	}
	
	public BattleModeGui(Minecraft minecraft) {
		this.sliding = 29;
		this.slidingToggle = false;
		this.mc = minecraft;
		this.font = minecraft.font;
	}
	
	private static final Vec2f[] CLOCK_POS = {
		new Vec2f(0.5F, 0.5F),
		new Vec2f(0.5F, 0.0F),
		new Vec2f(0.0F, 0.0F),
		new Vec2f(0.0F, 1.0F),
		new Vec2f(1.0F, 1.0F),
		new Vec2f(1.0F, 0.0F)
	};
	
	public void renderGui(LocalPlayerPatch playerpatch, float partialTicks) {
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
		
		MainWindow sr = Minecraft.getInstance().getWindow();
		int width = sr.getGuiScaledWidth();
		int height = sr.getGuiScaledHeight();
		
		boolean depthTestEnabled = GL11.glGetBoolean(GL11.GL_DEPTH_TEST);
		boolean blendEnabled = GL11.glGetBoolean(GL11.GL_BLEND);
		
		if (depthTestEnabled)
			RenderSystem.disableDepthTest();
		if (!blendEnabled)
			RenderSystem.enableBlend();
		
		MatrixStack matStack = new MatrixStack();
		//RenderSystem.setShader(GameRenderer::getPositionTexShader);
	    mc.textureManager.bind(EntityIndicator.BATTLE_ICON);
	    
		float maxStamina = playerpatch.getMaxStamina();
		float stamina = playerpatch.getStamina();
		float prevStamina = playerpatch.getPrevStamina();
		
		if (maxStamina > 0.0F && stamina < maxStamina) {
			float ratio = (prevStamina + (stamina - prevStamina) * partialTicks) / maxStamina;
			matStack.pushPose();
			matStack.translate(0, (float)this.sliding * 0.5F, 0);
			matStack.scale(0.5F, 0.5F, 1.0F);
			
			RenderSystem.color4f(1.0F, ratio, 0.25F, 1.0F);
			ModIngameGui.blit(matStack, (int)((width - 120) * 2.0F), (int)((height - 10) * 2.0F), 2.0F, 38.0F, 237, 9, 255, 255);
			ModIngameGui.blit(matStack, (int)((width - 120) * 2.0F), (int)((height - 10) * 2.0F), 2.0F, 47.0F, (int)(237*ratio), 9, 255, 255);
			matStack.popPose();
		}
		
		for (int i = 0; i < SkillCategory.ENUM_MANAGER.universalValues().size(); i++) {
			SkillContainer container = playerpatch.getSkill(i);
			if (container != null && !container.isEmpty()) {
				SkillCategory slot = container.getSkill().getCategory();
				if (slot == SkillCategories.WEAPON_SPECIAL_ATTACK) {
					this.drawSpecialAttack(playerpatch, container, matStack, partialTicks);
				} else {
					RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
					Skill skill = container.getSkill();
					if (skill != null && skill.shouldDraw(container)) {
						if (!this.skillIcons.contains(container)) {
							this.skillIcons.removeIf((showingContainer) -> showingContainer.getSkill().getCategory() == skill.getCategory());
							this.skillIcons.add(container);
						}
						Vec3f pos = POSITION_MAP.get(this.skillIcons.indexOf(container));
						RenderSystem.enableBlend();
						skill.drawOnGui(this, container, matStack, pos.x, pos.y, pos.z, width, height);
					} else {
						if (this.skillIcons.contains(container)) {
							this.skillIcons.removeIf((shownContainer) -> container == shownContainer);
						}
					}
				}
			}
		}
		
		if (depthTestEnabled)
			RenderSystem.enableDepthTest();
		if (!blendEnabled)
			RenderSystem.disableBlend();
	}
	
	private void drawSpecialAttack(LocalPlayerPatch playerpatch, SkillContainer container, MatrixStack matStack, float partialTicks) {
		MainWindow sr = Minecraft.getInstance().getWindow();
		int width = sr.getGuiScaledWidth();
		int height = sr.getGuiScaledHeight();
		
		Vec3f pos = POSITION_MAP.get(-1);	
		int x = (int) pos.x;
		int y = (int) pos.y;
		float scale = 1.0F / pos.z;
		matStack.pushPose();
		matStack.scale(pos.z, pos.z, 1.0F);
		matStack.translate(0, (float)sliding * scale, 0);
		
		boolean creative = playerpatch.getOriginal().isCreative();
		boolean fullstack = creative || container.isFull();
		float cooldownRatio = fullstack ? 1.0F : container.getResource(partialTicks);
		boolean isCompatibleWeapon = !container.isDisabled();
		int vertexNum = 0;
		float iconSize = 32.0F;
		float iconSizeDiv = iconSize * 0.5F;
		float top = y;
		float bottom = y - iconSize;
		float left = x;
		float right = x - iconSize;
		float middle = x - iconSizeDiv;
		float lastVertexX = 0;
		float lastVertexY = 0;
		float lastTexX = 0;
		float lastTexY = 0;
		
		if (cooldownRatio < 0.125F) {
			vertexNum = 6;
			lastTexX = cooldownRatio / 0.25F;
			lastTexY = 0.0F;
			lastVertexX = middle - iconSize * lastTexX;
			lastVertexY = top;
			lastTexX+=0.5F;
		} else if (cooldownRatio < 0.375F) {
			vertexNum = 5;
			lastTexX = 1.0F;
			lastTexY = (cooldownRatio - 0.125F) / 0.25F;
			lastVertexX = right;
			lastVertexY = top - iconSize * lastTexY;
		} else if (cooldownRatio < 0.625F) {
			vertexNum = 4;
			lastTexX = (cooldownRatio - 0.375F) / 0.25F;
			lastTexY = 1.0F;
			lastVertexX = right + iconSize * lastTexX;
			lastVertexY = bottom;
			lastTexX = 1.0F - lastTexX;
		} else if (cooldownRatio < 0.875F) {
			vertexNum = 3;
			lastTexX = 0.0F;
			lastTexY = (cooldownRatio - 0.625F) / 0.25F;
			lastVertexX = left;
			lastVertexY = bottom + iconSize * lastTexY;
			lastTexY = 1.0F - lastTexY;
		} else {
			vertexNum = 2;
			lastTexX = (cooldownRatio - 0.875F) / 0.25F;
			lastTexY = 0.0F;
			lastVertexX = left - iconSize * lastTexX;
			lastVertexY = top;
		}
		
		RenderSystem.enableBlend();
		mc.textureManager.bind(container.getSkill().getSkillTexture());
		RenderSystem.color4f(lastVertexX, lastVertexY, lastTexX, lastTexY);
		//RenderSystem.setShader(GameRenderer::getPositionTexShader);
		
		if (isCompatibleWeapon) {
			if (container.getStack() > 0) {
				RenderSystem.color4f(0.0F, 0.64F, 0.72F, 0.8F);
			} else {
				RenderSystem.color4f(0.0F, 0.5F, 0.5F, 0.6F);
			}
		} else {
			RenderSystem.color4f(0.5F, 0.5F, 0.5F, 0.6F);
		}
		
		Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        bufferbuilder.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_TEX);
        
        for (int j = 0; j < vertexNum; j++) {
        	bufferbuilder.vertex(matStack.last().pose(), (width - (left-iconSize*CLOCK_POS[j].x)) * scale, (height - (top-iconSize*CLOCK_POS[j].y)) * scale, 0.0F)
        		.uv(CLOCK_POS[j].x, CLOCK_POS[j].y).endVertex();
		}
        bufferbuilder.vertex(matStack.last().pose(), (width - lastVertexX) * scale, (height - lastVertexY) * scale, 0.0F)
			.uv(lastTexX, lastTexY).endVertex();
        
        tessellator.end();
        
        if (isCompatibleWeapon) {
			RenderSystem.color4f(0.08F, 0.79F, 0.95F, 1.0F);
		} else {
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		}
        
        RenderSystem.disableCull();
        bufferbuilder.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_TEX);
        
        for (int j = 0; j < 2; j++) {
        	bufferbuilder.vertex(matStack.last().pose(), (width - (left - iconSize * CLOCK_POS[j].x)) * scale, (height - (top - iconSize * CLOCK_POS[j].y)) * scale, 0.0F)
        		.uv(CLOCK_POS[j].x, CLOCK_POS[j].y).endVertex();
		}
		
		for (int j = CLOCK_POS.length - 1; j >= vertexNum; j--) {
        	bufferbuilder.vertex(matStack.last().pose(), (width - (left - iconSize * CLOCK_POS[j].x)) * scale, (height - (top - iconSize * CLOCK_POS[j].y)) * scale, 0.0F)
        		.uv(CLOCK_POS[j].x, CLOCK_POS[j].y).endVertex();
		}
        
        bufferbuilder.vertex(matStack.last().pose(), (width - lastVertexX) * scale, (height - lastVertexY) * scale, 0.0F).uv(lastTexX, lastTexY).endVertex();
        tessellator.end();
        matStack.scale(scale, scale, 1.0F);
        
        if (!fullstack) {
			String s = String.valueOf((int)(cooldownRatio * 100.0F));
			int stringWidth = (this.font.width(s) - 6) / 3;
			this.font.drawShadow(matStack, s, ((float)width - x+13-stringWidth), ((float)height - y+13), 16777215);
		} else if (container.getRemainDuration() > 0 && container.getSkill().getActivateType() != ActivateType.TOGGLE) {
			String s = String.valueOf(container.getRemainDuration());
			int stringWidth = (this.font.width(s) - 6) / 3;
			this.font.drawShadow(matStack, s, ((float)width - x+13-stringWidth), ((float)height - y+13), 16777215);
		}
		
		if (container.getSkill().getMaxStack() > 1) {
			String s = String.valueOf(container.getStack());
			int stringWidth = (this.font.width(s) - 6) / 3;
			this.font.drawShadow(matStack, s, ((float)width - x+25-stringWidth), ((float)height - y+22), 16777215);
		}
		
		matStack.popPose();
	}
	
	public void slideUp() {
		this.sliding = 28;
		this.slidingToggle = true;
	}

	public void slideDown() {
		this.sliding = 1;
		this.slidingToggle = false;
	}
	
	public int getSlidingProgression() {
		return this.sliding;
	}
}