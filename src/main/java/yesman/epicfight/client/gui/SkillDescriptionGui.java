package yesman.epicfight.client.gui;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.capabilities.ModCapabilities;
import yesman.epicfight.client.capabilites.player.ClientPlayerData;
import yesman.epicfight.item.SkillBookItem;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.ModNetworkManager;
import yesman.epicfight.network.client.CTSChangeSkill;
import yesman.epicfight.skill.Skill;

@OnlyIn(Dist.CLIENT)
public class SkillDescriptionGui extends Screen {
	public static final ResourceLocation BACKGROUND = new ResourceLocation(EpicFightMod.MODID, "textures/gui/skill_description.png");
	private final PlayerEntity opener;
	private final Skill skill;
	
	public SkillDescriptionGui(PlayerEntity opener, ItemStack stack) {
		super(StringTextComponent.EMPTY);
		this.opener = opener;
		this.skill = SkillBookItem.getContainSkill(stack);
	}
	
	@Override
	protected void init() {
		ClientPlayerData playerdata = (ClientPlayerData) this.opener.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		boolean isUsing = this.skill.equals(playerdata.getSkill(this.skill.getCategory()).getContaining());
		Skill priorSkill = this.skill.getPriorSkill();
		boolean condition = priorSkill == null ? true : playerdata.getSkill(priorSkill.getCategory()).getContaining() == priorSkill;
		
		Button.ITooltip tooltip = Button.EMPTY_TOOLTIP;
		
		if (!isUsing) {
			if (condition) {
				if (playerdata.getSkill(this.skill.getCategory()).getContaining() != null) {
					tooltip = (button, matrixStack, mouseX, mouseY) -> {
						this.renderTooltip(matrixStack, this.minecraft.fontRenderer.trimStringToWidth(new TranslationTextComponent(
								"gui." + EpicFightMod.MODID + ".replace", new TranslationTextComponent("skill." + EpicFightMod.MODID + "." +
									playerdata.getSkill(this.skill.getCategory()).getContaining().getSkillName()).getString()), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
					};
				}
			} else {
				tooltip = (button, matrixStack, mouseX, mouseY) -> {
					this.renderTooltip(matrixStack, this.minecraft.fontRenderer.trimStringToWidth(new TranslationTextComponent(
							"gui." + EpicFightMod.MODID + ".require_learning", new TranslationTextComponent("skill." + EpicFightMod.MODID + "." +
									priorSkill.getSkillName()).getString()), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
				};
			}
		}
		
		Button changeButton = new Button((this.width + 130) / 2, (this.height + 90) / 2, 46, 20,
			new TranslationTextComponent("gui." + EpicFightMod.MODID + (isUsing ? ".applied" : condition ? ".learn" : ".unusable")), (p_onPress_1_) -> {
				if (playerdata != null) {
					playerdata.getSkill(this.skill.getCategory()).setSkill(this.skill);
					this.minecraft.displayGuiScreen((Screen) null);
					ModNetworkManager.sendToServer(new CTSChangeSkill(this.skill.getCategory().getIndex(), this.skill.getSkillName()));
				}
			}, tooltip);
		
		if (isUsing || !condition) {
			changeButton.active = false;
		}
		
		this.addButton(changeButton);
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		int posX = (this.width - 250) / 2;
		int posY = (this.height - 200) / 2;
		this.minecraft.getTextureManager().bindTexture(BACKGROUND);
		this.blit(matrixStack, posX, posY, 0, 0, 256, 181);
		
		this.minecraft.getTextureManager().bindTexture(getSkillTexture(this.skill));
		
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GlStateManager.enableBlend();
		AbstractGui.blit(matrixStack, posX + 25, posY + 50, 50, 50, 0, 0, 64, 64, 64, 64);
		GlStateManager.disableBlend();
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		
		String skillName = new TranslationTextComponent("skill." + EpicFightMod.MODID + "." + this.skill.getSkillName()).getString();
		int width = this.font.getStringWidth(skillName);
		this.font.drawString(matrixStack, skillName, posX + 50 - width / 2, posY + 115, 0);
		
		String skillCategory = String.format("(%s)", new TranslationTextComponent("skill." + EpicFightMod.MODID + "." +
				this.skill.getCategory().toString().toLowerCase() + ".category").getString());
		width = this.font.getStringWidth(skillCategory);
		this.font.drawString(matrixStack, skillCategory, posX + 50 - width / 2, posY + 130, 0);
		
		List<IReorderingProcessor> list = this.font.trimStringToWidth(new TranslationTextComponent(
				"skill." + EpicFightMod.MODID + "." + this.skill.getSkillName() + ".tooltip", this.skill.getTooltipArgs().toArray(new Object[0])), 140);
		int height = posY + 20;
		
		for(int l1 = 0; l1 < list.size(); ++l1) {
            IReorderingProcessor ireorderingprocessor1 = list.get(l1);
            if (ireorderingprocessor1 != null) {
               this.font.func_238422_b_(matrixStack, ireorderingprocessor1, posX + 105, height, 0);
            }
            
            height+=10;
		}
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}
	
	private static ResourceLocation getSkillTexture(Skill skill) {
		ResourceLocation name = skill.getRegistryName();
		return new ResourceLocation(name.getNamespace(), "textures/gui/skills/" + name.getPath() + ".png");
	}
}