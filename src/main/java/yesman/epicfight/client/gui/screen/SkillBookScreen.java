package yesman.epicfight.client.gui.screen;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.joml.Vector3d;
import org.joml.Vector3f;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModLoader;
import yesman.epicfight.api.client.forgeevent.WeaponCategoryIconRegisterEvent;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.client.CPChangeSkill;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;
import yesman.epicfight.world.capabilities.item.WeaponCategory;
import yesman.epicfight.world.item.EpicFightItems;
import yesman.epicfight.world.item.SkillBookItem;

@OnlyIn(Dist.CLIENT)
public class SkillBookScreen extends Screen {
	private static final ResourceLocation SKILLBOOK_BACKGROUND = new ResourceLocation(EpicFightMod.MODID, "textures/gui/screen/skillbook.png");
	private static final Map<WeaponCategory, ItemStack> WEAPON_CATEGORY_ICONS = Maps.newHashMap();
	
	public static void registerIconItems() {
		WEAPON_CATEGORY_ICONS.put(WeaponCategories.NOT_WEAPON, new ItemStack(Items.AIR));
		WEAPON_CATEGORY_ICONS.put(WeaponCategories.AXE, new ItemStack(Items.IRON_AXE));
		WEAPON_CATEGORY_ICONS.put(WeaponCategories.FIST, new ItemStack(EpicFightItems.GLOVE.get()));
		WEAPON_CATEGORY_ICONS.put(WeaponCategories.GREATSWORD, new ItemStack(EpicFightItems.IRON_GREATSWORD.get()));
		WEAPON_CATEGORY_ICONS.put(WeaponCategories.HOE, new ItemStack(Items.IRON_HOE));
		WEAPON_CATEGORY_ICONS.put(WeaponCategories.PICKAXE, new ItemStack(Items.IRON_PICKAXE));
		WEAPON_CATEGORY_ICONS.put(WeaponCategories.SHOVEL, new ItemStack(Items.IRON_SHOVEL));
		WEAPON_CATEGORY_ICONS.put(WeaponCategories.SWORD, new ItemStack(Items.IRON_SWORD));
		WEAPON_CATEGORY_ICONS.put(WeaponCategories.UCHIGATANA, new ItemStack(EpicFightItems.UCHIGATANA.get()));
		WEAPON_CATEGORY_ICONS.put(WeaponCategories.SPEAR, new ItemStack(EpicFightItems.IRON_SPEAR.get()));
		WEAPON_CATEGORY_ICONS.put(WeaponCategories.TACHI, new ItemStack(EpicFightItems.IRON_TACHI.get()));
		WEAPON_CATEGORY_ICONS.put(WeaponCategories.TRIDENT, new ItemStack(Items.TRIDENT));
		WEAPON_CATEGORY_ICONS.put(WeaponCategories.LONGSWORD, new ItemStack(EpicFightItems.IRON_LONGSWORD.get()));
		WEAPON_CATEGORY_ICONS.put(WeaponCategories.DAGGER, new ItemStack(EpicFightItems.IRON_DAGGER.get()));
		WEAPON_CATEGORY_ICONS.put(WeaponCategories.SHIELD, new ItemStack(Items.SHIELD));
		WEAPON_CATEGORY_ICONS.put(WeaponCategories.RANGED, new ItemStack(Items.BOW));
		
		WeaponCategoryIconRegisterEvent event = new WeaponCategoryIconRegisterEvent(WEAPON_CATEGORY_ICONS);
		ModLoader.get().postEvent(event);
	}
	
	protected final Player opener;
	protected final LocalPlayerPatch playerpatch;
	protected final Skill skill;
	protected final InteractionHand hand;
	protected final Screen parentScreen;
	protected final SkillTooltipList skillTooltipList;
	protected final AvailableItemsList availableWeaponCategoryList = new AvailableItemsList(0, 0);
	
	public SkillBookScreen(Player opener, ItemStack stack, InteractionHand hand) {
		this(opener, SkillBookItem.getContainSkill(stack), hand, null);
	}
	
	public SkillBookScreen(Player opener, Skill skill, InteractionHand hand, @Nullable Screen parentScreen) {
		super(Component.empty());
		
		this.opener = opener;
		this.playerpatch = EpicFightCapabilities.getEntityPatch(this.opener, LocalPlayerPatch.class);
		this.skill = skill;
		this.hand = hand;
		this.parentScreen = parentScreen;
		this.skillTooltipList = new SkillTooltipList(Minecraft.getInstance(), 0, 0, 0 ,0, Minecraft.getInstance().font.lineHeight);
		
		List<FormattedCharSequence> list = Minecraft.getInstance().font.split(Component.translatable(this.skill.getTranslationKey() + ".tooltip", this.skill.getTooltipArgsOfScreen(Lists.newArrayList()).toArray(new Object[0])), 148);
		list.forEach(this.skillTooltipList::add);
		
		if (this.skill.getAvailableWeaponCategories() != null) {
			this.skill.getAvailableWeaponCategories().forEach(this.availableWeaponCategoryList::addWeaponCategory);
		}
	}
	
	@Override
	protected void init() {
		SkillContainer thisSkill = this.playerpatch.getSkill(this.skill);
		SkillContainer priorSkill = this.skill == null ? null : this.playerpatch.getSkill(this.skill.getPriorSkill());
		
		boolean isUsing = thisSkill != null;
		boolean condition = this.skill == null ? false : this.skill.getPriorSkill() == null || priorSkill != null;
		Component tooltip = CommonComponents.EMPTY;
		
		if (!isUsing) {
			if (condition) {
				if (thisSkill != null) {
					tooltip = Component.translatable("gui." + EpicFightMod.MODID + ".replace", Component.translatable(this.skill.getTranslationKey()).getString());
				}
			} else {
				tooltip = Component.translatable("gui." + EpicFightMod.MODID + ".require_learning", Component.translatable(this.skill.getPriorSkill().getTranslationKey()).getString());
			}
		}
		
		Button changeButton = Button.builder(Component.translatable("gui." + EpicFightMod.MODID + (isUsing ? ".applied" : condition ? ".learn" : ".unusable")), (button) -> {
			Set<SkillContainer> skillContainers = this.playerpatch.getSkillCapability().getSkillContainersFor(this.skill.getCategory());
			
			if (skillContainers.size() == 1) {
				this.learnSkill(skillContainers.iterator().next());
			} else {
				SlotSelectScreen slotSelectScreen = new SlotSelectScreen(skillContainers, this);
				this.minecraft.setScreen(slotSelectScreen);
			}
		}).bounds((this.width) / 2 + 54, (this.height) / 2 + 90, 67, 21).tooltip(Tooltip.create(tooltip, null)).build(LearnButton::new);
		
		if (isUsing || !condition) {
			changeButton.active = false;
		}
		
		if (this.hand == null) {
			changeButton.visible = false;
		}
		
		this.availableWeaponCategoryList.setX(this.width / 2 + 21);
		this.availableWeaponCategoryList.setY(this.height / 2 + 50);
		
		this.skillTooltipList.updateSize(210, 400, this.height / 2 - 100, (this.height + (this.availableWeaponCategoryList.availableCategories.size() == 0 ? 150 : 80)) / 2);
		this.skillTooltipList.setLeftPos(this.width / 2 - 40);
		
		this.addRenderableWidget(changeButton);
		this.addRenderableWidget(this.skillTooltipList);
		this.addRenderableWidget(this.availableWeaponCategoryList);
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
		if (this.parentScreen != null) {
			this.minecraft.setScreen(this.parentScreen);
		} else {
			super.onClose();
		}
	}
	
	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.render(guiGraphics, mouseX, mouseY, partialTicks, false);
	}
	
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, boolean asBackground) {
		if (!asBackground) {
			this.renderBackground(guiGraphics);
		}
		
		int posX = (this.width - 284) / 2;
		int posY = (this.height - 165) / 2;
		
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		guiGraphics.blit(SKILLBOOK_BACKGROUND, this.width / 2 - 192, this.height / 2 - 140, 384, 279, 0, 0, 256, 186, 256, 256);
		
		int iconStartX = 106;
		int iconStartY = 211;
		
		if (this.skill.getCategory() == SkillCategories.DODGE) {
			iconStartX += 9;
		} else if (this.skill.getCategory() == SkillCategories.GUARD) {
			iconStartX += 18;
		} else if (this.skill.getCategory() == SkillCategories.IDENTITY) {
			iconStartX += 27;
		} else if (this.skill.getCategory() == SkillCategories.MOVER) {
			iconStartX += 36;
		} else if (this.skill.getCategory() == SkillCategories.PASSIVE) {
			iconStartX += 45;
		}
		
		guiGraphics.blit(SKILLBOOK_BACKGROUND, this.width / 2 - 158, this.height / 2 - 72, 9, 9, iconStartX, iconStartY, 9, 9, 256, 256);
		
		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(this.width / 2 - 17, this.height / 2 - 72, 0.0D);
		
		guiGraphics.pose().scale(-1.0F, 1.0F, 1.0F);
		RenderSystem.disableCull();
		guiGraphics.blit(SKILLBOOK_BACKGROUND, 0, 0, 9, 9, iconStartX, iconStartY, 9, 9, 256, 256);
		RenderSystem.enableCull();
		guiGraphics.pose().popPose();
		
		RenderSystem.enableBlend();
		guiGraphics.blit(this.skill.getSkillTexture(), this.width / 2 - 119, this.height / 2 - 96, 60, 60, 0, 0, 64, 64, 64, 64);
		RenderSystem.disableBlend();
		
		String translationName = this.skill.getTranslationKey();
		String skillName = Component.translatable(translationName).getString();
		int width = this.font.width(skillName);
		guiGraphics.drawString(font, skillName, posX + 56 - width / 2, posY + 85, 0, false);
		
		String skillCategory = String.format("(%s)", Component.translatable("skill." + EpicFightMod.MODID + "." + this.skill.getCategory().toString().toLowerCase() + ".category").getString());
		width = this.font.width(skillCategory);
		guiGraphics.drawString(font, skillCategory, posX + 56 - width / 2, posY + 100, 0, false);
		
		if (!this.skill.getModfierEntry().isEmpty()) {
			int i = 135;
			
			for (Map.Entry<Attribute, AttributeModifier> stat : this.skill.getModfierEntry()) {
				String attrName = Component.translatable(stat.getKey().getDescriptionId()).getString();
				String amountString = "";
				String operator = "+";
				double amount = stat.getValue().getAmount();
				
				if (amount < 0) {
					operator = "-";
					amount = Math.abs(amount);
				}
				
				switch (stat.getValue().getOperation()) {
				case ADDITION:
					amountString = ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(amount);
					break;
				case MULTIPLY_BASE:
					amountString = ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(amount * 100.0D) + "%";
					break;
				case MULTIPLY_TOTAL:
					amountString = ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(amount * 100.0D) + "%";
					break;
				}
				
				guiGraphics.drawString(this.font, operator + amountString + " " + attrName, posX + 8 - width / 2, posY + i, 0, false);
				i += 10;
			}
		}
		
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		
		if (asBackground) {
			this.renderBackground(guiGraphics);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	private class SkillTooltipList extends ObjectSelectionList<SkillTooltipList.TooltipLine> {
		public SkillTooltipList(Minecraft minecraft, int width, int height, int y0, int y1, int itemHeight) {
			super(minecraft, width, height, y0, y1, itemHeight);
			
			this.setRenderBackground(false);
			this.setRenderHeader(false, 0);
			this.setRenderTopAndBottom(false);
		}
		
		public void add(FormattedCharSequence tooltip) {
			this.addEntry(new TooltipLine(tooltip));
		}
		
		@Override
		protected int getScrollbarPosition() {
			return this.x1 - 6;
		}
		
		@OnlyIn(Dist.CLIENT)
		private class TooltipLine extends ObjectSelectionList.Entry<SkillTooltipList.TooltipLine> {
			private final FormattedCharSequence tooltip;
			
			private TooltipLine(FormattedCharSequence string) {
				this.tooltip = string;
			}
			
			@Override
			public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks) {
				guiGraphics.drawString(SkillBookScreen.this.font, this.tooltip, left + 33 + /**/26, top, 0, false);
			}
			
			@Override
			public Component getNarration() {
				return Component.empty();
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	private class AvailableItemsList extends AbstractWidget {
		private final List<WeaponCategory> availableCategories = Lists.newArrayList();
		
		private AvailableItemsList(int x, int y) {
			super(x, y, 0, 0, Component.translatable("gui.epicfight.weapon_types_available"));
			
			this.width = 0;
			this.height = 28;
		}
		
		public void addWeaponCategory(WeaponCategory weaopnCategory) {
			this.availableCategories.add(weaopnCategory);
			this.width += 16;
		}
		
		@Override
		protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
			if (this.availableCategories.size() == 0) {
				return;
			}
			
			int x = this.getX();
			int y = this.getY() + 12;
			boolean updatedTooltip = false;
			
			for (WeaponCategory category : this.availableCategories) {
				guiGraphics.renderItem(WEAPON_CATEGORY_ICONS.get(category), x, y);
				
				if (mouseX >= x && mouseX <= x + 16 && mouseY >= y && mouseY <= y + 16) {
					this.setTooltip(Tooltip.create(Component.translatable("epicfight.weapon_category." + category.toString().toLowerCase(Locale.ROOT))));
					updatedTooltip = true;
				}
				
				x += 16;
			}
			
			if (!updatedTooltip) {
				this.setTooltip(null);
			}
			
			guiGraphics.drawString(SkillBookScreen.this.font, this.getMessage(), this.getX(), this.getY(), 0, false);
		}
		
		@Override
		protected void updateWidgetNarration(NarrationElementOutput p_259858_) {
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	private class LearnButton extends Button {
		protected LearnButton(Builder builder) {
			super(builder);
		}
		
		@Override
		protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
			Minecraft minecraft = Minecraft.getInstance();
			guiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
			RenderSystem.enableBlend();
			RenderSystem.enableDepthTest();
			
			int texX = 106;
			
			if (this.isHoveredOrFocused() || !this.isActive()) {
			   texX = 156;
			}
			
			guiGraphics.pose().pushPose();
			guiGraphics.blitNineSliced(SKILLBOOK_BACKGROUND, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 20, 4, 45, 15, texX, 193);
			guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
			guiGraphics.pose().popPose();
			
			int i = this.getFGColor();
			this.renderString(guiGraphics, minecraft.font, i | Mth.ceil(this.alpha * 255.0F) << 24);
		}
	}
}