package yesman.epicfight.client.gui.datapack.screen;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.client.gui.datapack.widgets.InputComponentList;
import yesman.epicfight.client.gui.datapack.widgets.ResizableComponent.HorizontalSizing;
import yesman.epicfight.client.gui.datapack.widgets.Static;

@OnlyIn(Dist.CLIENT)
public class AttackAnimationPropertyScreen extends Screen {
	private final InputComponentList<CompoundTag> inputComponentsList;
	
	protected AttackAnimationPropertyScreen(Screen parentScreen, CompoundTag tag) {
		super(Component.translatable("gui.epicfight.animation_property"));
		
		this.minecraft = parentScreen.getMinecraft();
		this.font = this.minecraft.font;
		
		this.inputComponentsList = new InputComponentList<> (this, 0, 0, 0, 0, 30) {
			@Override
			public void importTag(CompoundTag tag) {
				this.clearComponents();
				this.setComponentsActive(true);
			}
		};
	}
	
	protected void rearrangeElements(LayerOptions layerType) {
		this.inputComponentsList.clearComponents();
		
		if (layerType == LayerOptions.BASE_LAYER || layerType == LayerOptions.COMPOSITE_LAYER) {
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 60, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.import_animation.client_data.layer_type")));
			//this.inputComponentsList.addComponentCurrentRow(convertTime.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 60, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.import_animation.client_data.priority")));
			//this.inputComponentsList.addComponentCurrentRow(convertTime.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
			
		} else if (layerType == LayerOptions.MULTILAYER) {
			this.inputComponentsList.newRow();
		}
	}
	
	@Override
	protected void init() {
		ScreenRectangle screenRect = this.getRectangle();
		
		this.inputComponentsList.updateSize(screenRect.width() - 36, screenRect.height() - 50, screenRect.top() + 14, screenRect.bottom() - 36);
		this.inputComponentsList.setLeftPos(18);
		
		this.addRenderableWidget(this.inputComponentsList);
		
		this.addRenderableWidget(Button.builder(CommonComponents.GUI_OK, (button) -> {
			this.onClose();
		}).pos(this.width / 2 - 162, this.height - 28).size(160, 21).build());
		
		this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (button) -> {
			this.minecraft.setScreen(new MessageScreen<>("", "Do you want to quit without saving changes?", this,
				(button2) -> {
					this.onClose();
				}, (button2) -> {
					this.minecraft.setScreen(this);
				}, 180, 70));
		}).pos(this.width / 2 + 2, this.height - 28).size(160, 21).build());
	}
	
	@OnlyIn(Dist.CLIENT)
	public enum LayerOptions {
		BASE_LAYER, COMPOSITE_LAYER, MULTILAYER
	}
}