package yesman.epicfight.client.gui;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.client.capabilites.player.ClientPlayerData;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.Skill.ActivateType;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.utils.math.Vec2f;
import yesman.epicfight.utils.math.Vec3f;

@OnlyIn(Dist.CLIENT)
public class BattleModeGui extends ModIngameGui {
	private static final Map<Integer, Vec3f> POSITION_MAP = Maps.<Integer, Vec3f>newHashMap();
	private int sliding;
	private boolean slidingToggle;
	private List<SkillContainer> skillIcons = Lists.newLinkedList();
	
	public FontRenderer font;
	
	static {
		POSITION_MAP.put(-1, new Vec3f(42F, 48F, 0.117F));
		POSITION_MAP.put(0, new Vec3f(70F, 36F, 0.078F));
		POSITION_MAP.put(1, new Vec3f(94F, 36F, 0.078F));
		POSITION_MAP.put(2, new Vec3f(116F, 36F, 0.078F));
	}
	
	public BattleModeGui() {
		this.sliding = 29;
		this.slidingToggle = false;
		
		this.font = Minecraft.getInstance().fontRenderer;
	}
	
	private static final Vec2f[] CLOCK_POS = {
		new Vec2f(0.5F, 0.5F),
		new Vec2f(0.5F, 0.0F),
		new Vec2f(0.0F, 0.0F),
		new Vec2f(0.0F, 1.0F),
		new Vec2f(1.0F, 1.0F),
		new Vec2f(1.0F, 0.0F)
	};
	
	@SuppressWarnings("deprecation")
	public void renderGui(ClientPlayerData playerdata, float partialTicks) {
		if (!playerdata.getOriginalEntity().isAlive() || playerdata.getOriginalEntity().getRidingEntity() != null) {
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
		
		MainWindow sr = Minecraft.getInstance().getMainWindow();
		int width = sr.getScaledWidth();
		int height = sr.getScaledHeight();
		
		boolean depthTestEnabled = GL11.glGetBoolean(GL11.GL_DEPTH_TEST);
		boolean blendEnabled = GL11.glGetBoolean(GL11.GL_BLEND);
		
		if(depthTestEnabled)
			GlStateManager.disableDepthTest();
		if(!blendEnabled)
			GlStateManager.enableBlend();
		
		MatrixStack matStack = new MatrixStack();
		Minecraft.getInstance().getTextureManager().bindTexture(EntityIndicator.BATTLE_ICON);
		
		float maxStamina = playerdata.getMaxStamina();
		float stamina = playerdata.getStamina();
		float prevStamina = playerdata.getPrevStamina();
		
		if (maxStamina > 0.0F && stamina < maxStamina) {
			float ratio = (prevStamina + (stamina - prevStamina) * partialTicks) / maxStamina;
			matStack.push();
			matStack.translate(0, (float)this.sliding * 0.5F, 0);
			matStack.scale(0.5F, 0.5F, 1.0F);
			GlStateManager.color4f(1.0F, ratio, 0.25F, 1.0F);
			AbstractGui.blit(matStack, (int)((width-120) * 2.0F), (int)((height-10) * 2.0F), 2.0F, 38.0F, 237, 9, 255, 255);
			AbstractGui.blit(matStack, (int)((width-120) * 2.0F), (int)((height-10) * 2.0F), 2.0F, 47.0F, (int)(237*ratio), 9, 255, 255);
			matStack.pop();
		}
		
		for (int i = 0; i < SkillCategory.values().length; i++) {
			SkillContainer container = playerdata.getSkill(i);
			if (container != null && !container.isEmpty()) {
				SkillCategory slot = container.getContaining().getCategory();
				if (slot == SkillCategory.WEAPON_SPECIAL_ATTACK) {
					this.drawSpecialAttack(playerdata, container, matStack, partialTicks);
				} else {
					GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
					Skill skill = container.getContaining();
					if (skill != null && skill.shouldDraw(container)) {
						if (!this.skillIcons.contains(container)) {
							this.skillIcons.removeIf((showingContainer) -> showingContainer.getContaining().getCategory() == skill.getCategory());
							this.skillIcons.add(container);
						}
						
						Vec3f pos = POSITION_MAP.get(this.skillIcons.indexOf(container));
						
						GL11.glDisable(GL11.GL_ALPHA_TEST);
						GlStateManager.enableBlend();
						skill.drawOnGui(this, container, matStack, pos.x, pos.y, pos.z, width, height);
					} else {
						if (this.skillIcons.contains(container)) {
							this.skillIcons.removeIf((shownContainer) -> container == shownContainer);
						}
					}
				}
			}
		}
		
		if(depthTestEnabled)
			GlStateManager.enableDepthTest();
		if(!blendEnabled)
			GlStateManager.disableBlend();
	}
	
	@SuppressWarnings("deprecation")
	private void drawSpecialAttack(ClientPlayerData playerdata, SkillContainer container, MatrixStack matStack, float partialTicks) {
		MainWindow sr = Minecraft.getInstance().getMainWindow();
		int width = sr.getScaledWidth();
		int height = sr.getScaledHeight();
		
		Vec3f pos = POSITION_MAP.get(-1);	
		int x = (int) pos.x;
		int y = (int) pos.y;
		float scale = 1.0F / pos.z;
		matStack.push();
		matStack.scale(pos.z, pos.z, 1.0F);
		matStack.translate(0, (float)sliding * scale, 0);
		
		boolean creative = playerdata.getOriginalEntity().isCreative();
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
			lastTexY = (cooldownRatio-0.125F) / 0.25F;
			lastVertexX = right;
			lastVertexY = top - iconSize * lastTexY;
		} else if (cooldownRatio < 0.625F) {
			vertexNum = 4;
			lastTexX = (cooldownRatio-0.375F) / 0.25F;
			lastTexY = 1.0F;
			lastVertexX = right + iconSize * lastTexX;
			lastVertexY = bottom;
			lastTexX = 1.0F - lastTexX;
		} else if (cooldownRatio < 0.875F) {
			vertexNum = 3;
			lastTexX = 0.0F;
			lastTexY = (cooldownRatio-0.625F) / 0.25F;
			lastVertexX = left;
			lastVertexY = bottom + iconSize * lastTexY;
			lastTexY = 1.0F - lastTexY;
		} else {
			vertexNum = 2;
			lastTexX = (cooldownRatio-0.875F) / 0.25F;
			lastTexY = 0.0F;
			lastVertexX = left - iconSize * lastTexX;
			lastVertexY = top;
		}
		
		GlStateManager.enableBlend();
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		Minecraft.getInstance().getTextureManager().bindTexture(container.getContaining().getSkillTexture());
		
		if (isCompatibleWeapon) {
			if (container.getStack() > 0) {
				GlStateManager.color4f(0.0F, 0.64F, 0.72F, 0.8F);
			} else {
				GlStateManager.color4f(0.0F, 0.5F, 0.5F, 0.6F);
			}
		} else {
			GlStateManager.color4f(0.5F, 0.5F, 0.5F, 0.6F);
		}
		
		Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_TEX);
        
		for (int j = 0; j < vertexNum; j++) {
        	bufferbuilder.pos(matStack.getLast().getMatrix(), (width - (left-iconSize*CLOCK_POS[j].x)) * scale, (height - (top-iconSize*CLOCK_POS[j].y)) * scale, 0.0F)
        		.tex(CLOCK_POS[j].x, CLOCK_POS[j].y).endVertex();
		}
        bufferbuilder.pos(matStack.getLast().getMatrix(), (width - lastVertexX) * scale, (height - lastVertexY) * scale, 0.0F)
			.tex(lastTexX, lastTexY).endVertex();
        
        tessellator.draw();
        
        if (isCompatibleWeapon) {
        	GlStateManager.color4f(0.08F, 0.79F, 0.95F, 1.0F);
		} else {
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		}
        
        GlStateManager.disableCull();
        
        bufferbuilder.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_TEX);
        
		for (int j = 0; j < 2; j++) {
        	bufferbuilder.pos(matStack.getLast().getMatrix(), (width - (left-iconSize*CLOCK_POS[j].x)) * scale, (height - (top-iconSize*CLOCK_POS[j].y)) * scale, 0.0F)
        		.tex(CLOCK_POS[j].x, CLOCK_POS[j].y).endVertex();
		}
		
		for (int j = CLOCK_POS.length - 1; j >= vertexNum; j--) {
        	bufferbuilder.pos(matStack.getLast().getMatrix(), (width - (left-iconSize*CLOCK_POS[j].x)) * scale, (height - (top-iconSize*CLOCK_POS[j].y)) * scale, 0.0F)
        		.tex(CLOCK_POS[j].x, CLOCK_POS[j].y).endVertex();
		}
        
        bufferbuilder.pos(matStack.getLast().getMatrix(), (width - lastVertexX) * scale, (height - lastVertexY) * scale, 0.0F)
		.tex(lastTexX, lastTexY).endVertex();
        
        bufferbuilder.finishDrawing();
        WorldVertexBufferUploader.draw(bufferbuilder);
        
        matStack.scale(scale, scale, 1.0F);
        
		if (!fullstack) {
			String s = String.valueOf((int)(cooldownRatio * 100.0F));
			int stringWidth = (this.font.getStringWidth(s) - 6) / 3;
			this.font.drawStringWithShadow(matStack, s, ((float)width - x+13-stringWidth), ((float)height - y+13), 16777215);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
		} else if (container.getRemainDuration() > 0 && container.getContaining().getActivateType() != ActivateType.TOGGLE) {
			String s = String.valueOf(container.getRemainDuration());
			int stringWidth = (this.font.getStringWidth(s) - 6) / 3;
			this.font.drawStringWithShadow(matStack, s, ((float)width - x+13-stringWidth), ((float)height - y+13), 16777215);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
		}
		
		if (container.getContaining().getMaxStack() > 1) {
			String s = String.valueOf(container.getStack());
			int stringWidth = (this.font.getStringWidth(s) - 6) / 3;
			this.font.drawStringWithShadow(matStack, s, ((float)width - x+25-stringWidth), ((float)height - y+22), 16777215);
		}
		
		matStack.pop();
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