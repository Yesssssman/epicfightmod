package yesman.epicfight.client.gui.screen;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.client.CPChangeSkill;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.world.capabilities.skill.CapabilitySkill;

@OnlyIn(Dist.CLIENT)
public class SkillEditScreen extends Screen {
	private static final ResourceLocation SKILL_EDIT_UI = new ResourceLocation(EpicFightMod.MODID, "textures/gui/screen/skill_edit.png");
	private static final TranslatableComponent NO_SKILLS = new TranslatableComponent("gui.epicfight.no_skills");
	private CapabilitySkill skills;
	private List<CategoryButton> categoryButtons = Lists.newArrayList();
	private List<LearnSkillButton> learnedSkillButtons = Lists.newArrayList();
	
	public SkillEditScreen(CapabilitySkill skills) {
		super(new TranslatableComponent("gui.epicfight.skill_edit"));
		this.skills = skills;
	}
	
	@Override
	public void init() {
		int i = this.width / 2 - 80;
		int j = this.height / 2 - 82;
		this.categoryButtons.clear();
		this.learnedSkillButtons.clear();
		
		for (SkillCategory skillCategory : SkillCategory.ENUM_MANAGER.universalValues()) {
			if (this.skills.hasCategory(skillCategory) && skillCategory.learnable()) {
				CategoryButton categoryButton = new CategoryButton(i, j, 18, 18, this.skills.skillContainers[skillCategory.universalOrdinal()].getSkill(), (button) -> {
					for (Button shownButton : this.learnedSkillButtons) {
						this.children().remove(shownButton);
					}
					this.learnedSkillButtons.clear();
					
					int k = this.width / 2 - 53;
					int l = this.height / 2 - 78;
					
					for (Skill learnedSkill : this.skills.getLearnedSkills(skillCategory)) {
						this.learnedSkillButtons.add(new LearnSkillButton(k, l, 117, 24, learnedSkill, new TranslatableComponent(learnedSkill.getTranslatableText()), (pressedButton) -> {
							if (this.minecraft.player.experienceLevel >= learnedSkill.getRequiredXp() || this.minecraft.player.isCreative()) {
								this.skills.skillContainers[learnedSkill.getCategory().universalOrdinal()].setSkill(learnedSkill);
								EpicFightNetworkManager.sendToServer(new CPChangeSkill(learnedSkill.getCategory().universalOrdinal(), -1, learnedSkill.toString(), !this.minecraft.player.isCreative()));
								this.onClose();
							}
						}).setActive(!learnedSkill.equals(this.skills.skillContainers[skillCategory.universalOrdinal()].getSkill())));
						l+=26;
					}
					
					for (Button shownButton : this.learnedSkillButtons) {
						this.addWidget(shownButton);
					}
				}, (button, PoseStack, x, y) -> {
					this.renderTooltip(PoseStack, this.minecraft.font.split(new TextComponent(skillCategory.toString()), Math.max(this.width / 2 - 43, 170)), x, y);
				});
				
				this.categoryButtons.add(categoryButton);
				this.addWidget(categoryButton);
				j+=18;
			}
		}
	}
	
	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(poseStack);
		
		for (int i = 0; i < this.learnedSkillButtons.size(); ++i) {
			this.learnedSkillButtons.get(i).render(poseStack, mouseX, mouseY, partialTicks);
		}
		
		for (int i = 0; i < this.categoryButtons.size(); ++i) {
			this.categoryButtons.get(i).render(poseStack, mouseX, mouseY, partialTicks);
		}
		
		if (this.categoryButtons.isEmpty()) {
			int lineHeight = 0;
			
			for (FormattedCharSequence s : this.font.split(NO_SKILLS, 110)) {
				this.font.draw(poseStack, s, this.width / 2 - 50, this.height / 2 - 72 + lineHeight, 3158064);
				
				lineHeight += 10;
			}
		}
	}
	
	@Override
	public void renderBackground(PoseStack PoseStack) {
		super.renderBackground(PoseStack);
		RenderSystem.setShaderTexture(0, SKILL_EDIT_UI);
	    this.blit(PoseStack, this.width / 2 - 88, this.height / 2 - 100, 0, 0, 177, 200);
	}
	
	class CategoryButton extends Button {
		private Skill skill;
		
		public CategoryButton(int x, int y, int width, int height, Skill skill, OnPress pressedAction, OnTooltip onTooltip) {
			super(x, y, width, height, TextComponent.EMPTY, pressedAction, onTooltip);
			this.skill = skill;
		}
		
		@Override
		public void render(PoseStack PoseStack, int mouseX, int mouseY, float partialTicks) {
			RenderSystem.setShaderTexture(0, SKILL_EDIT_UI);
			this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
			int y = this.isHovered ? 35 : 17;
			this.blit(PoseStack, this.x, this.y, 237, y, this.width, this.height);
			
			if (this.skill != null) {
				RenderSystem.enableBlend();
				RenderSystem.setShaderTexture(0, this.skill.getSkillTexture());
				GuiComponent.blit(PoseStack, this.x + 1, this.y + 1, 16, 16, 0, 0, 128, 128, 128, 128);
			}
			
			if (this.isHoveredOrFocused()) {
				this.renderToolTip(PoseStack, mouseX, mouseY);
			}
		}
	}
	
	class LearnSkillButton extends Button {
		private Skill skill;
		
		public LearnSkillButton(int x, int y, int width, int height, Skill skill, Component title, OnPress pressedAction) {
			super(x, y, width, height, title, pressedAction, Button.NO_TOOLTIP);
			this.skill = skill;
		}
		
		@Override
		public void render(PoseStack PoseStack, int mouseX, int mouseY, float partialTicks) {
			RenderSystem.setShaderTexture(0, SKILL_EDIT_UI);
			this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
			int y = (this.isHovered || !this.active) ? 224 : 200;
			this.blit(PoseStack, this.x, this.y, 0, y, this.width, this.height);
			
			RenderSystem.enableBlend();
			RenderSystem.setShaderTexture(0, this.skill.getSkillTexture());
			GuiComponent.blit(PoseStack, this.x + 5, this.y + 4, 16, 16, 0, 0, 128, 128, 128, 128);
			drawString(PoseStack, SkillEditScreen.this.font, this.getMessage(), this.x+26, this.y+2, -1);
			
			if (this.active) {
				int color = (SkillEditScreen.this.minecraft.player.experienceLevel >= this.skill.getRequiredXp() || SkillEditScreen.this.minecraft.player.isCreative()) ? 8453920 : 16736352;
				drawString(PoseStack, SkillEditScreen.this.font, new TranslatableComponent("gui.epicfight.changing_cost", this.skill.getRequiredXp()), this.x+70, this.y+12, color);
			}
		}
		
		public LearnSkillButton setActive(boolean active) {
			this.active = active;
			return this;
		}
	}
}