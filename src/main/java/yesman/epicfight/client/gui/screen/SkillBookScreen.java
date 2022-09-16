package yesman.epicfight.client.gui.screen;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.client.CPChangeSkill;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.item.SkillBookItem;

@OnlyIn(Dist.CLIENT)
public class SkillBookScreen extends Screen {
	public static final ResourceLocation BACKGROUND = new ResourceLocation(EpicFightMod.MODID, "textures/gui/screen/skillbook.png");
	private final PlayerEntity opener;
	private final Skill skill;
	private final Hand hand;
	
	public SkillBookScreen(PlayerEntity opener, ItemStack stack, Hand hand) {
		super(StringTextComponent.EMPTY);
		this.opener = opener;
		this.skill = SkillBookItem.getContainSkill(stack);
		this.hand = hand;
	}
	
	@Override
	protected void init() {
		LocalPlayerPatch playerpatch = (LocalPlayerPatch) this.opener.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		boolean isUsing = this.skill.equals(playerpatch.getSkill(this.skill.getCategory()).getSkill());
		Skill priorSkill = this.skill.getPriorSkill();
		boolean condition = priorSkill == null ? true : playerpatch.getSkill(priorSkill.getCategory()).getSkill() == priorSkill;
		Button.ITooltip tooltip = Button.NO_TOOLTIP;
		
		if (!isUsing) {
			if (condition) {
				if (playerpatch.getSkill(this.skill.getCategory()).getSkill() != null) {
					tooltip = (button, matrixStack, mouseX, mouseY) -> {
						this.renderTooltip(matrixStack, this.minecraft.font.split(new TranslationTextComponent("gui." + EpicFightMod.MODID + ".replace",
								new TranslationTextComponent(skill.getTranslatableText()).getString()), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
					};
				}
			} else {
				tooltip = (button, matrixStack, mouseX, mouseY) -> {
					this.renderTooltip(matrixStack, this.minecraft.font.split(new TranslationTextComponent("gui." + EpicFightMod.MODID + ".require_learning",
							new TranslationTextComponent(priorSkill.getTranslatableText()).getString()), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
				};
			}
		}
		
		Button changeButton = new Button((this.width + 130) / 2, (this.height + 90) / 2, 46, 20,
			new TranslationTextComponent("gui." + EpicFightMod.MODID + (isUsing ? ".applied" : condition ? ".learn" : ".unusable")), (p_onPress_1_) -> {
				if (playerpatch != null) {
					playerpatch.getSkill(this.skill.getCategory()).setSkill(this.skill);
					this.minecraft.setScreen((Screen) null);
					playerpatch.getSkillCapability().addLearnedSkill(this.skill);
					int i = this.hand == Hand.MAIN_HAND ? this.opener.inventory.selected : 40;
					EpicFightNetworkManager.sendToServer(new CPChangeSkill(this.skill.getCategory().universalOrdinal(), i, this.skill.toString(), false));
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
		
		this.minecraft.getTextureManager().bind(BACKGROUND);
		this.blit(matrixStack, posX, posY, 0, 0, 256, 181);
		
		this.minecraft.getTextureManager().bind(this.skill.getSkillTexture());
		
		RenderSystem.enableBlend();
		AbstractGui.blit(matrixStack, posX + 25, posY + 50, 50, 50, 0, 0, 64, 64, 64, 64);
		RenderSystem.disableBlend();
		
		String translationName = this.skill.getTranslatableText();
		
		String skillName = new TranslationTextComponent(translationName).getString();
		int width = this.font.width(skillName);
		this.font.draw(matrixStack, skillName, posX + 50 - width / 2, posY + 115, 0);
		
		String skillCategory = String.format("(%s)", new TranslationTextComponent("skill." + EpicFightMod.MODID + "." + this.skill.getCategory().toString().toLowerCase() + ".category").getString());
		width = this.font.width(skillCategory);
		this.font.draw(matrixStack, skillCategory, posX + 50 - width / 2, posY + 130, 0);
		
		List<IReorderingProcessor> list = this.font.split(new TranslationTextComponent(translationName + ".tooltip", this.skill.getTooltipArgs().toArray(new Object[0])), 140);
		int height = posY + 20;
		
		for (int l1 = 0; l1 < list.size(); ++l1) {
			IReorderingProcessor ireorderingprocessor1 = list.get(l1);
            if (ireorderingprocessor1 != null) {
               this.font.draw(matrixStack, ireorderingprocessor1, posX + 105, height, 0);
            }
            
            height+=10;
		}
		
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}
}