package yesman.epicfight.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.skill.SkillContainer;

import java.util.*;

public class SlotSelectScreen extends Screen {
	private static final ResourceLocation BACKGROUND = new ResourceLocation(EpicFightMod.MODID, "textures/gui/screen/slot_select.png");
	private final SkillBookScreen parent;
	private final List<SkillContainer> containers;
	
	public SlotSelectScreen(Set<SkillContainer> containers, SkillBookScreen parent) {
		super(Component.empty());
		this.parent = parent;
		this.containers = new ArrayList<>(containers);
		
		Collections.sort(this.containers, (c1, c2) -> {
			if (c1.getSlotId() > c2.getSlotId()) {
				return 1;
			} else if (c1.getSlotId() < c2.getSlotId()) {
				return -1;
			}
			
			return 0;
		});
	}
	
	@Override
	protected void init() {
		this.parent.init(this.minecraft, this.width, this.height);
		int k = this.width / 2 - 80;
		int l = this.height / 2 - 45;
		
		for (SkillContainer container : this.containers) {
			String slotName = container.getSlot().toString().toLowerCase(Locale.ROOT);
			String skillName = container.getSkill() == null ? "Empty" : Component.translatable(container.getSkill().getTranslationKey()).getString();
			SlotButton slotbutton = new SlotButton(k, l, 167, 17, Component.literal(slotName + ": "+ skillName), (button) -> {
				this.parent.learnSkill(container);
				this.onClose();
			});
			
			l+=22;
			
			this.addRenderableWidget(slotbutton);
		}
	}
	
	@Override
	public void onClose() {
		if (this.parent != null) {
			this.minecraft.setScreen(this.parent);
		} else {
			super.onClose();
		}
	}
	
	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		int posX = (this.width - 184) / 2;
		int posY = (this.height - 150) / 2;
		
		this.parent.render(matrixStack, mouseX, mouseY, partialTicks, true);
		
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.setShaderTexture(0, BACKGROUND);
		this.blit(matrixStack, posX, posY, 0, 0, 191, 154);
		
		Component component = Component.translatable("gui.epicfight.select_slot_tooltip");
		int lineHeight = 0;
		
		for (FormattedCharSequence s : this.font.split(component, 250)) {
			this.font.draw(matrixStack, s, this.width / 2 - 84, this.height / 2 - 66 + lineHeight, 3158064);
			
			lineHeight += 10;
		}
		
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}
	
	class SlotButton extends Button {
		public SlotButton(int x, int y, int width, int height, Component title, OnPress pressedAction) {
			super(x, y, width, height, title, pressedAction);
		}
		
		@Override
		public void render(PoseStack PoseStack, int mouseX, int mouseY, float partialTicks) {
			RenderSystem.setShaderTexture(0, BACKGROUND);
			this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
			int y = (this.isHovered || !this.active) ? 171 : 154;
			this.blit(PoseStack, this.x, this.y, 0, y, this.width, this.height);
			drawString(PoseStack, SlotSelectScreen.this.font, this.getMessage(), this.x+3, this.y+3, -1);
		}
	}
}