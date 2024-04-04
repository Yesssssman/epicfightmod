package yesman.epicfight.client.gui.datapack.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.ParseUtil;
import yesman.epicfight.client.gui.datapack.widgets.Grid;
import yesman.epicfight.client.gui.datapack.widgets.InputComponentList;
import yesman.epicfight.client.gui.datapack.widgets.PopupBox;
import yesman.epicfight.client.gui.datapack.widgets.ResizableComponent.HorizontalSizing;
import yesman.epicfight.client.gui.datapack.widgets.Static;
import yesman.epicfight.data.conditions.Condition.LivingEntityCondition;
import yesman.epicfight.data.conditions.EpicFightConditions;

@OnlyIn(Dist.CLIENT)
public class OffhandValidatorScreen extends Screen {
	private final Screen parentScreen;
	private final InputComponentList<CompoundTag> inputComponentsList;
	
	public OffhandValidatorScreen(Screen parentScreen, CompoundTag rootTag) {
		super(Component.translatable("datapack_edit.weapon_type.offhand_validator"));
		
		this.parentScreen = parentScreen;
		this.inputComponentsList = new InputComponentList<>(this, 0, 0, 0, 0, 22) {
			@Override
			public void importTag(CompoundTag tag) {
				this.setComponentsActive(true);
				Grid.PackImporter packImporter = new Grid.PackImporter();
				
				for (String key : ParseUtil.getOrDefaultTag(tag, "predicate", new CompoundTag()).getAllKeys()) {
					packImporter.newRow();
					packImporter.newValue("parameter_key", key);
					packImporter.newValue("parameter_value", tag.getCompound("predicate").getString(key));
				}
				
				this.setDataBindingComponenets(new Object[] {
					EpicFightConditions.getConditionOrNull(new ResourceLocation(tag.getString("condition"))),
					packImporter
				});
			}
		};
		this.inputComponentsList.setLeftPos(15);
		this.font = parentScreen.getMinecraft().font;
		
		final Grid parameterGrid = Grid.builder(this, parentScreen.getMinecraft())
										.xy1(this.inputComponentsList.getLeft() + 4, 40)
										.xy2(20, 80)
										.horizontalSizing(HorizontalSizing.LEFT_RIGHT)
										.rowHeight(26)
										.rowEditable(false)
										.transparentBackground(false)
										.addColumn(Grid.editbox("parameter_key").valueChanged((event) -> {
											CompoundTag predicate = ParseUtil.getOrDefaultTag(rootTag, "predicate", new CompoundTag());
											predicate.remove(ParseUtil.nullParam(event.prevValue));
											predicate.putString(ParseUtil.nullParam(event.postValue), ParseUtil.nullParam(event.grid.getValue(event.rowposition, "parameter_value")));
										}).editable(false))
										.addColumn(Grid.editbox("parameter_value").valueChanged((event) -> {
											CompoundTag predicate = ParseUtil.getOrDefaultTag(rootTag, "predicate", new CompoundTag());
											predicate.putString(ParseUtil.nullParam(event.grid.getValue(event.rowposition, "parameter_key")), ParseUtil.nullParam(event.postValue));
										}).width(150))
										.build();
		
		this.inputComponentsList.newRow();
		this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 80, 100, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.weapon_type.styles.condition")));
		this.inputComponentsList.addComponentCurrentRow(new PopupBox.RegistryPopupBox<>(this, this.font, this.inputComponentsList.nextStart(5), 21, 60, 15, HorizontalSizing.LEFT_RIGHT, null, Component.translatable("datapack_edit.weapon_type.styles.condition"),
																		EpicFightConditions.REGISTRY.get(), (conditionProvider) -> {
																			if (conditionProvider != null) {
																				parameterGrid.reset();
																				conditionProvider.get().getAcceptingParameters().forEach((e) -> parameterGrid.addRowWithDefaultValues("parameter_key", e.getKey()));
																				rootTag.putString("condition", ParseUtil.getRegistryName(conditionProvider, EpicFightConditions.REGISTRY.get()));
																			}
																		}).applyFilter((condition) -> condition.get() instanceof LivingEntityCondition));
		
		this.inputComponentsList.newRow();
		this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 80, 100, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.weapon_type.styles.parameters")));
		
		this.inputComponentsList.newRow();
		this.inputComponentsList.newRow();
		this.inputComponentsList.newRow();
		this.inputComponentsList.addComponentCurrentRow(parameterGrid);
		
		this.inputComponentsList.importTag(rootTag);
	}
	
	@Override
	protected void init() {
		ScreenRectangle screenRectangle = this.getRectangle();
		
		this.inputComponentsList.updateSize(screenRectangle.width() - 30, screenRectangle.height(), screenRectangle.top() + 45, screenRectangle.height() - 45);
		this.inputComponentsList.setLeftPos(15);
		
		this.addRenderableWidget(Button.builder(CommonComponents.GUI_OK, (button) -> {
			//this.selectCallback.accept(this.registryList.getSelected() == null ? null : this.registryList.getSelected().item);
			this.minecraft.setScreen(this.parentScreen);
		}).pos(this.width / 2 - 162, this.height - 32).size(160, 21).build());
		
		this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (button) -> {
			this.minecraft.setScreen(this.parentScreen);
		}).pos(this.width / 2 + 2, this.height - 32).size(160, 21).build());
		
		this.addRenderableWidget(this.inputComponentsList);
	}
	
	@Override
	public void onClose() {
		this.minecraft.setScreen(this.parentScreen);
	}
	
	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		int yBegin = 32;
		int yEnd = this.height - 45;
		
		guiGraphics.drawString(this.font, this.title, 20, 16, 16777215);
		
		guiGraphics.setColor(0.125F, 0.125F, 0.125F, 1.0F);
        guiGraphics.blit(Screen.BACKGROUND_LOCATION, 0, yBegin, (float)this.width, (float)yEnd - yBegin, this.width, yEnd, 32, 32);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
		
		guiGraphics.setColor(0.25F, 0.25F, 0.25F, 1.0F);
		guiGraphics.blit(Screen.BACKGROUND_LOCATION, 0, 0, 0.0F, 0.0F, this.width, yBegin, 32, 32);
        guiGraphics.blit(Screen.BACKGROUND_LOCATION, 0, yEnd, 0.0F, (float)yEnd - yBegin, this.width, yEnd, 32, 32);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        
        guiGraphics.fillGradient(RenderType.guiOverlay(), 0, yBegin, this.width, yBegin + 4, -16777216, 0, 0);
		guiGraphics.fillGradient(RenderType.guiOverlay(), 0, yEnd, this.width, yEnd + 1, 0, -16777216, 0);
		
		super.render(guiGraphics, mouseX, mouseY, partialTick);
	}
}