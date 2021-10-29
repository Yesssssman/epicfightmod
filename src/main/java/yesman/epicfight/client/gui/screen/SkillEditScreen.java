package yesman.epicfight.client.gui.screen;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import yesman.epicfight.capabilities.skill.CapabilitySkill;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.ModNetworkManager;
import yesman.epicfight.network.client.CTSChangeSkill;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;

public class SkillEditScreen extends Screen {
	private static final ResourceLocation SKILL_EDIT_UI = new ResourceLocation(EpicFightMod.MODID, "textures/gui/screen/skill_edit.png");
	private CapabilitySkill skills;
	private List<CategoryButton> categoryButtons = Lists.newArrayList();
	private List<LearnSkillButton> learnedSkillButtons = Lists.newArrayList();
	
	public SkillEditScreen(CapabilitySkill skills) {
		super(new TranslationTextComponent("gui.epicfight.skill_edit"));
		this.skills = skills;
	}
	
	@Override
	public void init() {
		int i = this.width / 2 - 80;
		int j = this.height / 2 - 82;
		
		this.categoryButtons.clear();
		this.learnedSkillButtons.clear();
		
		for (SkillContainer skillContainer : this.skills.skillContainers) {
			Skill skill = skillContainer.getSkill();
			if (skill != null && skill.getCategory().modifiable()) {
				CategoryButton categoryButton = new CategoryButton(i, j, 18, 18, skillContainer.getSkill(), (button) -> {
					for (Button shownButton : this.learnedSkillButtons) {
						this.buttons.remove(shownButton);
					}
					this.learnedSkillButtons.clear();
					
					int k = this.width / 2 - 53;
					int l = this.height / 2 - 78;
					
					for (Skill learnedSkill : this.skills.getLearnedSkills(skillContainer.getSkill().getCategory())) {
						ResourceLocation skillRegistryName = learnedSkill.getRegistryName();
						this.learnedSkillButtons.add(new LearnSkillButton(k, l, 117, 24, learnedSkill, new TranslationTextComponent("skill."+skillRegistryName.getNamespace()+"."+skillRegistryName.getPath()), (pressedButton) -> {
							if (this.minecraft.player.experienceLevel >= learnedSkill.getRequiredXp() || this.minecraft.player.isCreative()) {
								this.skills.skillContainers[learnedSkill.getCategory().getIndex()].setSkill(learnedSkill);
								ModNetworkManager.sendToServer(new CTSChangeSkill(learnedSkill.getCategory().getIndex(), learnedSkill.getName(), !this.minecraft.player.isCreative()));
								this.closeScreen();
							}
						}).setActive(!learnedSkill.equals(skillContainer.getSkill())));
						l+=26;
					}
					
					for (Button shownButton : this.learnedSkillButtons) {
						this.addButton(shownButton);
					}
				}, (button, matrixStack, x, y) -> {
					this.renderTooltip(matrixStack, this.minecraft.fontRenderer.trimStringToWidth(
							new StringTextComponent(skill.getCategory().toString()), Math.max(this.width / 2 - 43, 170)), x, y);
				});
				
				this.categoryButtons.add(categoryButton);
				this.addButton(categoryButton);
				j+=18;
			}
		}
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		
		for (int i = 0; i < this.learnedSkillButtons.size(); ++i) {
			this.learnedSkillButtons.get(i).render(matrixStack, mouseX, mouseY, partialTicks);
		}
		
		for (int i = 0; i < this.categoryButtons.size(); ++i) {
			this.categoryButtons.get(i).render(matrixStack, mouseX, mouseY, partialTicks);
		}
	}
	
	@Override
	public void renderBackground(MatrixStack matrixStack) {
		super.renderBackground(matrixStack);
	    this.minecraft.getTextureManager().bindTexture(SKILL_EDIT_UI);
	    this.blit(matrixStack, this.width / 2 - 88, this.height / 2 - 100, 0, 0, 177, 200);
	}
	
	class CategoryButton extends Button {
		private Skill skill;
		
		public CategoryButton(int x, int y, int width, int height, Skill skill, IPressable pressedAction, ITooltip onTooltip) {
			super(x, y, width, height, StringTextComponent.EMPTY, pressedAction, onTooltip);
			this.skill = skill;
		}
		
		@Override
		public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
			SkillEditScreen.this.minecraft.getTextureManager().bindTexture(SKILL_EDIT_UI);
			int y = this.isHovered ? 35 : 17;
			this.blit(matrixStack, this.x, this.y, 237, y, this.width, this.height);
			
			RenderSystem.enableBlend();
			
			SkillEditScreen.this.minecraft.getTextureManager().bindTexture(this.skill.getSkillTexture());
			AbstractGui.blit(matrixStack, this.x + 1, this.y + 1, 16, 16, 0, 0, 128, 128, 128, 128);
			
			if (this.isHovered()) {
				this.renderToolTip(matrixStack, mouseX, mouseY);
			}
		}
	}
	
	class LearnSkillButton extends Button {
		private Skill skill;
		
		public LearnSkillButton(int x, int y, int width, int height, Skill skill, ITextComponent title, IPressable pressedAction) {
			super(x, y, width, height, title, pressedAction, Button.EMPTY_TOOLTIP);
			this.skill = skill;
		}
		
		@Override
		public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
			SkillEditScreen.this.minecraft.getTextureManager().bindTexture(SKILL_EDIT_UI);
			int y = (this.isHovered || !this.active) ? 224 : 200;
			this.blit(matrixStack, this.x, this.y, 0, y, this.width, this.height);
			
			RenderSystem.enableBlend();
			
			SkillEditScreen.this.minecraft.getTextureManager().bindTexture(this.skill.getSkillTexture());
			AbstractGui.blit(matrixStack, this.x + 5, this.y + 4, 16, 16, 0, 0, 128, 128, 128, 128);
			
			drawString(matrixStack, SkillEditScreen.this.font, this.getMessage(), this.x+26, this.y+2, -1);
			
			if (this.active) {
				int color = (SkillEditScreen.this.minecraft.player.experienceLevel >= this.skill.getRequiredXp() || SkillEditScreen.this.minecraft.player.isCreative()) ? 8453920 : 16736352;
				drawString(matrixStack, SkillEditScreen.this.font, new TranslationTextComponent("gui.epicfight.changing_cost", this.skill.getRequiredXp()), this.x+70, this.y+12, color);
			}
		}
		
		public LearnSkillButton setActive(boolean active) {
			this.active = active;
			return this;
		}
	}
}