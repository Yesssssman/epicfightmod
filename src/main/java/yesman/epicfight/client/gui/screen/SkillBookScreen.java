package yesman.epicfight.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.client.CPChangeSkill;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.passive.PassiveSkill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.item.SkillBookItem;

import java.util.List;
import java.util.Map;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class SkillBookScreen extends Screen {
	private static final ResourceLocation BACKGROUND = new ResourceLocation(EpicFightMod.MODID, "textures/gui/screen/skillbook.png");
	protected final Player opener;
	protected final LocalPlayerPatch playerpatch;
	protected final Skill skill;
	protected final InteractionHand hand;
	protected final Screen lowerScreen;
	
	public SkillBookScreen(Player opener, ItemStack stack, InteractionHand hand) {
		super(Component.empty());
		this.opener = opener;
		this.playerpatch = EpicFightCapabilities.getEntityPatch(this.opener, LocalPlayerPatch.class);
		this.skill = SkillBookItem.getContainSkill(stack);
		this.hand = hand;
		this.lowerScreen = null;
	}
	
	public SkillBookScreen(Player opener, Skill skill, InteractionHand hand, Screen lowerScreen) {
		super(Component.empty());
		this.opener = opener;
		this.playerpatch = EpicFightCapabilities.getEntityPatch(this.opener, LocalPlayerPatch.class);
		this.skill = skill;
		this.hand = hand;
		this.lowerScreen = lowerScreen;
	}
	
	@Override
	protected void init() {
		SkillContainer thisSkill = this.playerpatch.getSkill(this.skill);
		SkillContainer priorSkill = this.skill == null ? null : this.playerpatch.getSkill(this.skill.getPriorSkill());
		
		boolean isUsing = thisSkill != null;
		boolean condition = this.skill.getPriorSkill() == null || priorSkill != null;
		Button.OnTooltip tooltip = Button.NO_TOOLTIP;
		
		if (!isUsing) {
			if (condition) {
				if (thisSkill != null) {
					tooltip = (button, matrixStack, mouseX, mouseY) -> {
						this.renderTooltip(matrixStack, this.minecraft.font.split(Component.translatable("gui." + EpicFightMod.MODID + ".replace",
								Component.translatable(this.skill.getTranslationKey()).getString()), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
					};
				}
			} else {
				tooltip = (button, matrixStack, mouseX, mouseY) -> {
					this.renderTooltip(matrixStack, this.minecraft.font.split(Component.translatable("gui." + EpicFightMod.MODID + ".require_learning",
							Component.translatable(this.skill.getPriorSkill().getTranslationKey()).getString()), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
				};
			}
		}
		
		Button changeButton = new Button((this.width + 150) / 2, (this.height + 110) / 2, 46, 20,
			Component.translatable("gui." + EpicFightMod.MODID + (isUsing ? ".applied" : condition ? ".learn" : ".unusable")), (p_onPress_1_) -> {
				Set<SkillContainer> skillContainers = this.playerpatch.getSkillCapability().getSkillContainersFor(this.skill.getCategory());
				
				if (skillContainers.size() == 1) {
					this.learnSkill(skillContainers.iterator().next());
				} else {
					SlotSelectScreen slotSelectScreen = new SlotSelectScreen(skillContainers, this);
					this.minecraft.setScreen(slotSelectScreen);
				}
			}, tooltip);
		
		if (isUsing || !condition) {
			changeButton.active = false;
		}
		
		if (this.hand == null) {
			changeButton.visible = false;
		}
		
		this.addRenderableWidget(changeButton);
	}
	
	protected void learnSkill(SkillContainer skillContainer) {
		skillContainer.setSkill(this.skill);
		this.minecraft.setScreen(null);
		this.playerpatch.getSkillCapability().addLearnedSkill(this.skill);
		int i = this.hand == InteractionHand.MAIN_HAND ? this.opener.getInventory().selected : 40;
		
		EpicFightNetworkManager.sendToServer(new CPChangeSkill(skillContainer.getSlot().universalOrdinal(), i, this.skill.toString(), false));
	}
	
	@Override
	public void onClose() {
		if (this.lowerScreen != null) {
			this.minecraft.setScreen(this.lowerScreen);
		} else {
			super.onClose();
		}
	}
	
	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.render(matrixStack, mouseX, mouseY, partialTicks, false);
	}
	
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks, boolean asBackground) {
		if (!asBackground) {
			this.renderBackground(matrixStack);
		}
		
		int posX = (this.width - 256) / 2;
		int posY = (this.height - 200) / 2;
		
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.setShaderTexture(0, BACKGROUND);
		
		matrixStack.pushPose();
		matrixStack.translate(posX + 128, posY + 90, 0.0F);
		matrixStack.scale(1.2F, 1.2F, 1.0F);
		this.blit(matrixStack, -128, -90, 0, 0, 256, 181);
		
		matrixStack.popPose();
		matrixStack.pushPose();
		
		matrixStack.translate(posX + 5, posY + 12, 0.0F);
		matrixStack.scale(1.2F, 1.2F, 1.0F);
		
		RenderSystem.setShaderTexture(0, this.skill.getSkillTexture());
		RenderSystem.enableBlend();
		GuiComponent.blit(matrixStack, 0, 0, 50, 50, 0, 0, 64, 64, 64, 64);
		RenderSystem.disableBlend();
		
		matrixStack.popPose();
		
		String translationName = this.skill.getTranslationKey();
		String skillName = Component.translatable(translationName).getString();
		int width = this.font.width(skillName);
		this.font.draw(matrixStack, skillName, posX + 36 - width / 2, posY + 85, 0);
		
		String skillCategory = String.format("(%s)", Component.translatable("skill." + EpicFightMod.MODID + "." + this.skill.getCategory().toString().toLowerCase() + ".category").getString());
		width = this.font.width(skillCategory);
		this.font.draw(matrixStack, skillCategory, posX + 36 - width / 2, posY + 100, 0);
		
		if (this.skill.getCategory() == SkillCategories.PASSIVE) {
			PassiveSkill passiveSkill = (PassiveSkill)this.skill;
			int i = 135;
			
			for (Map.Entry<Attribute, AttributeModifier> stat : passiveSkill.getModfierEntry()) {
				String attrName = Component.translatable(stat.getKey().getDescriptionId()).getString();
				String amt = ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(stat.getValue().getAmount());
				String operator = switch (stat.getValue().getOperation()) {
					case ADDITION -> "+";
					case MULTIPLY_BASE, MULTIPLY_TOTAL -> "x";
				};

				this.font.draw(matrixStack, operator + amt +" "+ attrName, posX + 23 - width / 2, posY + i, 0);
				i += 10;
			}
		}
		
		List<FormattedCharSequence> list = this.font.split(Component.translatable(translationName + ".tooltip", this.skill.getTooltipArgsOfScreen(Lists.newArrayList()).toArray(new Object[0])), 150);

		int height = posY + 20;

		for (FormattedCharSequence ireorderingprocessor1 : list) {
			if (ireorderingprocessor1 != null) {
				this.font.draw(matrixStack, ireorderingprocessor1, posX + 105, height, 0);
			}

			height += 10;
		}
		
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		
		if (asBackground) {
			this.renderBackground(matrixStack);
		}
	}
}