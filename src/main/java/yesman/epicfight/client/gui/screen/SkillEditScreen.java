package yesman.epicfight.client.gui.screen;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.client.CPChangeSkill;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillSlot;
import yesman.epicfight.world.capabilities.skill.CapabilitySkill;

@OnlyIn(Dist.CLIENT)
public class SkillEditScreen extends Screen {
	private static final ResourceLocation SKILL_EDIT_UI = new ResourceLocation(EpicFightMod.MODID, "textures/gui/screen/skill_edit.png");
	private static final TranslatableComponent NO_SKILLS = new TranslatableComponent("gui.epicfight.no_skills");
	private static final int MAX_SHOWING_BUTTONS = 6;
	
	private final Player player;
	private final CapabilitySkill skills;
	private final Map<SkillSlot, SlotButton> slotButtons = Maps.newHashMap();
	private final List<LearnSkillButton> learnedSkillButtons = Lists.newArrayList();
	private int start;
	private SlotButton selectedSlotButton;
	
	public SkillEditScreen(Player player, CapabilitySkill skills) {
		super(new TranslatableComponent("gui.epicfight.skill_edit"));
		this.player = player;
		this.skills = skills;
	}
	
	@Override
	public void init() {
		int i = this.width / 2 - 96;
		int j = this.height / 2 - 82;
		
		this.slotButtons.clear();
		this.learnedSkillButtons.clear();
		
		for (SkillSlot skillSlot : SkillSlot.ENUM_MANAGER.universalValues()) {
			if (this.skills.hasCategory(skillSlot.category()) && skillSlot.category().learnable()) {
				SlotButton slotButton = new SlotButton(i, j, 18, 18, skillSlot, this.skills.skillContainers[skillSlot.universalOrdinal()].getSkill(), (button) -> {
					this.start = 0;
					
					for (Button shownButton : this.learnedSkillButtons) {
						this.children().remove(shownButton);
					}
					
					this.learnedSkillButtons.clear();
					int k = this.width / 2 - 69;
					int l = this.height / 2 - 78;
					
					Collection<Skill> learnedSkillCollection = this.skills.getLearnedSkills(skillSlot.category());
					
					for (Skill learnedSkill : learnedSkillCollection) {
						this.learnedSkillButtons.add(new LearnSkillButton(k, l, 147, 24, learnedSkill, new TranslatableComponent(learnedSkill.getTranslationKey()), (pressedButton) -> {
							if (this.minecraft.player.experienceLevel >= learnedSkill.getRequiredXp() || this.minecraft.player.isCreative()) {
								if (!this.canPress(pressedButton)) {
									return;
								}
								
								this.skills.skillContainers[skillSlot.universalOrdinal()].setSkill(learnedSkill);
								EpicFightNetworkManager.sendToServer(new CPChangeSkill(skillSlot.universalOrdinal(), -1, learnedSkill.toString(), !this.minecraft.player.isCreative()));
								this.onClose();
							}
						}).setActive(this.skills.getSkillContainer(learnedSkill) == null));
						
						l+=26;
					}
					
					for (Button shownButton : this.learnedSkillButtons) {
						this.addWidget(shownButton);
					}
					
					this.selectedSlotButton = (SlotButton)button;
					
				}, (button, PoseStack, x, y) -> {
					this.renderTooltip(PoseStack, this.minecraft.font.split(new TextComponent(SkillSlot.ENUM_MANAGER.toTranslated(skillSlot)), Math.max(this.width / 2 - 43, 170)), x, y);
				});
				
				this.slotButtons.put(skillSlot, slotButton);
				this.addWidget(slotButton);
				j+=18;
			}
		}
		
		if (this.selectedSlotButton != null) {
			this.selectedSlotButton = this.slotButtons.get(this.selectedSlotButton.slot);
			this.selectedSlotButton.onPress();
		}
	}
	
	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(poseStack);
		
		if (this.canScroll()) {
			int scrollPosition = (int)(140 * (this.start / (float)(this.learnedSkillButtons.size() - MAX_SHOWING_BUTTONS)));
			GuiComponent.blit(poseStack, this.width / 2 + 80, this.height / 2 - 80 + scrollPosition, 12, 15, 231, 2, 12, 15, 256, 256);
		}
		
		int maxShowingButtons = Math.min(this.learnedSkillButtons.size(), MAX_SHOWING_BUTTONS);
		
		for (int i = this.start; i < maxShowingButtons + this.start; ++i) {
			this.learnedSkillButtons.get(i).render(poseStack, mouseX, mouseY, partialTicks);
		}
		
		for (SlotButton sb : this.slotButtons.values()) {
			sb.render(poseStack, mouseX, mouseY, partialTicks);
		}
		
		if (this.slotButtons.isEmpty()) {
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
	    this.blit(PoseStack, this.width / 2 - 104, this.height / 2 - 100, 0, 0, 208, 200);
	}
	
	private boolean canScroll() {
		return this.learnedSkillButtons.size() > MAX_SHOWING_BUTTONS;
	}
	
	private boolean canPress(Button button) {
		int buttonOrder = this.learnedSkillButtons.indexOf(button);
		
		return buttonOrder >= this.start && buttonOrder <= this.start + MAX_SHOWING_BUTTONS;
	}
	
	@Override
	public boolean mouseScrolled(double x, double y, double wheel) {
		if (!this.canScroll()) {
			return false;
		} else {
			if (wheel > 0.0F) {
				if (this.start > 0) {
					--this.start;
					
					for (Button button : this.learnedSkillButtons) {
						button.y += 26;
					}
					
					return true;
				}
			} else {
				if (this.start < (this.learnedSkillButtons.size() - MAX_SHOWING_BUTTONS)) {
					++this.start;
					
					for (Button button : this.learnedSkillButtons) {
						button.y -= 26;
					}
					
					return true;
				}
			}
			
			return false;
		}
	}
	
	class SlotButton extends Button {
		private Skill iconSkill;
		private SkillSlot slot;
		
		public SlotButton(int x, int y, int width, int height, SkillSlot slot, Skill skill, OnPress pressedAction, OnTooltip onTooltip) {
			super(x, y, width, height, TextComponent.EMPTY, pressedAction, onTooltip);
			this.iconSkill = skill;
			this.slot = slot;
		}
		
		@Override
		public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
			RenderSystem.setShaderTexture(0, SKILL_EDIT_UI);
			this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
			int y = (this.isHovered || SkillEditScreen.this.selectedSlotButton == this) ? 35 : 17;
			this.blit(poseStack, this.x, this.y, 237, y, this.width, this.height);
			
			if (this.iconSkill != null) {
				RenderSystem.enableBlend();
				RenderSystem.setShaderTexture(0, this.iconSkill.getSkillTexture());
				GuiComponent.blit(poseStack, this.x + 1, this.y + 1, 16, 16, 0, 0, 128, 128, 128, 128);
			}
			
			if (this.isHoveredOrFocused()) {
				this.renderToolTip(poseStack, mouseX, mouseY);
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
			int texY = (this.isHovered || !this.active) ? 224 : 200;
			this.blit(PoseStack, this.x, this.y, 0, texY, this.width, this.height);
			
			RenderSystem.enableBlend();
			RenderSystem.setShaderTexture(0, this.skill.getSkillTexture());
			GuiComponent.blit(PoseStack, this.x + 5, this.y + 4, 16, 16, 0, 0, 128, 128, 128, 128);
			drawString(PoseStack, SkillEditScreen.this.font, this.getMessage(), this.x+26, this.y + 2, -1);
			
			if (this.active) {
				int color = (SkillEditScreen.this.minecraft.player.experienceLevel >= this.skill.getRequiredXp() || SkillEditScreen.this.minecraft.player.isCreative()) ? 8453920 : 16736352;
				drawString(PoseStack, SkillEditScreen.this.font, new TranslatableComponent("gui.epicfight.changing_cost", this.skill.getRequiredXp()), this.x+70, this.y + 12, color);
			} else {
				drawString(PoseStack, SkillEditScreen.this.font, new TextComponent(
						SkillEditScreen.this.skills.getSkillContainer(this.skill).getSlot().toString().toLowerCase(Locale.ROOT)), this.x+26, this.y + 12, 16736352);
			}
		}
		
		@Override
		public boolean mouseClicked(double x, double y, int pressType) {
			if (this.visible && pressType == 1) {
				boolean flag = this.clickedNoCountActive(x, y);
				
				if (flag) {
					this.playDownSound(Minecraft.getInstance().getSoundManager());
					SkillEditScreen.this.minecraft.setScreen(new SkillBookScreen(SkillEditScreen.this.player, this.skill, (InteractionHand)null, SkillEditScreen.this));
					return true;
				}
			}
			
			return super.mouseClicked(x, y, pressType);
		}
		
		protected boolean clickedNoCountActive(double x, double y) {
			return this.visible && x >= (double) this.x && y >= (double) this.y && x < (double) (this.x + this.width) && y < (double) (this.y + this.height);
		}
		
		public LearnSkillButton setActive(boolean active) {
			this.active = active;
			return this;
		}
	}
}