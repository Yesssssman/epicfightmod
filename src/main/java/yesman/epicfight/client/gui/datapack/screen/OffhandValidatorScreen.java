package yesman.epicfight.client.gui.datapack.screen;

import java.util.function.Supplier;

import io.netty.util.internal.StringUtil;
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
import yesman.epicfight.client.gui.datapack.widgets.Grid.GridBuilder.RowEditButton;
import yesman.epicfight.client.gui.datapack.widgets.InputComponentList;
import yesman.epicfight.client.gui.datapack.widgets.PopupBox;
import yesman.epicfight.client.gui.datapack.widgets.ResizableComponent.HorizontalSizing;
import yesman.epicfight.client.gui.datapack.widgets.ResizableEditBox;
import yesman.epicfight.client.gui.datapack.widgets.Static;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.data.conditions.Condition.PlayerPatchCondition;
import yesman.epicfight.data.conditions.EpicFightConditions;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class OffhandValidatorScreen extends Screen {
	private final Screen parentScreen;
	private final InputComponentList<CompoundTag> inputComponentsList;
	private final CompoundTag rootTag;
	private final CompoundTag offhandValidatorTag;
	
	private Grid parameterGrid;
	private PopupBox<Supplier<Condition<?>>> conditionPopup;
	
	public OffhandValidatorScreen(Screen parentScreen, CompoundTag rootTag) {
		super(Component.translatable("datapack_edit.weapon_type.offhand_validator"));
		
		this.minecraft = parentScreen.getMinecraft();
		this.font = parentScreen.getMinecraft().font;
		
		this.rootTag = rootTag;
		this.offhandValidatorTag = rootTag.getCompound("offhand_item_compatible_predicate").copy();
		
		this.parentScreen = parentScreen;
		this.inputComponentsList = new InputComponentList<>(this, 0, 0, 0, 0, 22) {
			@Override
			public void importTag(CompoundTag tag) {
				this.setComponentsActive(true);
				
				Supplier<Condition<?>> conditionProvider = EpicFightConditions.getConditionOrNull(new ResourceLocation(tag.getString("condition")));
				Condition<?> condition = conditionProvider == null ? null : conditionProvider.get();
				
				Grid.PackImporter packImporter = new Grid.PackImporter();
				
				if (condition != null) {
					for (ParameterEditor editor : condition.getAcceptingParameters(OffhandValidatorScreen.this)) {
						packImporter.newRow();
						packImporter.newValue("parameter_key", editor);
						packImporter.newValue("parameter_value", editor.fromTag.apply(tag.getCompound("predicate").get(editor.editWidget.getMessage().getString())));
					}
				}
				
				this.setDataBindingComponenets(new Object[] {
					conditionProvider,
					packImporter
				});
			}
		};
		this.inputComponentsList.setLeftPos(15);
		this.font = parentScreen.getMinecraft().font;
		
		this.parameterGrid = Grid.builder(this, parentScreen.getMinecraft())
									.xy1(4, 40)
									.xy2(12, 80)
									.horizontalSizing(HorizontalSizing.LEFT_RIGHT)
									.rowHeight(26)
									.rowEditable(RowEditButton.NONE)
									.transparentBackground(false)
									.addColumn(Grid.<ParameterEditor, ResizableEditBox>wildcard("parameter_key")
													.editable(false)
													.toDisplayText((widget) -> widget.editWidget.getMessage().getString())
													.width(100))
									.addColumn(Grid.wildcard("parameter_value")
													.editWidgetProvider((row) -> {
														ParameterEditor editor = row.getValue("parameter_key");
														return editor.editWidget;
													})
													.toDisplayText(ParseUtil::snakeToSpacedCamel)
													.editable(true)
													.valueChanged((event) -> {
														CompoundTag predicate = ParseUtil.getOrDefaultTag(this.offhandValidatorTag, "predicate", new CompoundTag());
														ParameterEditor editor = event.grid.getValue(event.rowposition, "parameter_key");
														
														if (StringUtil.isNullOrEmpty(ParseUtil.nullParam(event.postValue))) {
															predicate.remove(editor.editWidget.getMessage().getString());
														} else {
															predicate.put(editor.editWidget.getMessage().getString(), editor.toTag.apply(event.postValue));
														}
													}).width(150))
									.build();
		
		this.conditionPopup = new PopupBox.RegistryPopupBox<>(this, this.font, 0, 13, 60, 15, HorizontalSizing.LEFT_RIGHT, null, Component.translatable("datapack_edit.weapon_type.styles.condition"),
																EpicFightConditions.REGISTRY.get(), (pair) -> {
																	if (pair.getSecond() != null) {
																		this.parameterGrid.reset();
																		pair.getSecond().get().getAcceptingParameters(this).forEach((widget) -> this.parameterGrid.addRowWithDefaultValues("parameter_key", widget));
																		this.offhandValidatorTag.putString("condition", ParseUtil.getRegistryName(pair.getSecond(), EpicFightConditions.REGISTRY.get()));
																	}
																}).applyFilter((condition) -> condition.get() instanceof PlayerPatchCondition);
		
		this.inputComponentsList.newRow();
		this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 80, 100, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.weapon_type.styles.condition")));
		this.inputComponentsList.addComponentCurrentRow(this.conditionPopup.relocateX(this.getRectangle(), this.inputComponentsList.nextStart(5)));
		
		this.inputComponentsList.newRow();
		this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 80, 100, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.weapon_type.styles.parameters")));
		
		this.inputComponentsList.newRow();
		this.inputComponentsList.newRow();
		this.inputComponentsList.newRow();
		this.inputComponentsList.addComponentCurrentRow(this.parameterGrid);
		
		this.inputComponentsList.importTag(rootTag.getCompound("offhand_item_compatible_predicate"));
	}
	
	@Override
	protected void init() {
		ScreenRectangle screenRectangle = this.getRectangle();
		
		this.inputComponentsList.updateSize(screenRectangle.width() - 30, screenRectangle.height(), screenRectangle.top() + 45, screenRectangle.height() - 45);
		this.inputComponentsList.setLeftPos(15);
		
		this.addRenderableWidget(Button.builder(CommonComponents.GUI_OK, (button) -> {
			if (this.conditionPopup._getValue() == null) {
				this.minecraft.setScreen(new MessageScreen<>("", "Condition is not defined!", this, (button2) -> this.minecraft.setScreen(this), 180, 70));
				return;
			}
			
			try {
				Supplier<Condition<LivingEntityPatch<?>>> conditionProvider = EpicFightConditions.getConditionOrThrow(new ResourceLocation(this.offhandValidatorTag.getString("condition")));
				conditionProvider.get().read(this.offhandValidatorTag.getCompound("predicate"));
			} catch (Exception e) {
				this.minecraft.setScreen(new MessageScreen<>("Invalid condition.", e.getMessage(), this, (button2) -> this.minecraft.setScreen(this), 180, 70));
				return;
			}
			
			this.rootTag.put("offhand_item_compatible_predicate", this.offhandValidatorTag);
			this.onClose();
		}).pos(this.width / 2 - 162, this.height - 32).size(160, 21).build());
		
		this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (button) -> {
			this.minecraft.setScreen(new MessageScreen<>("", "Do you want to quit without saving changes?", this,
														(button2) -> {
															this.onClose();
														}, (button2) -> {
															this.minecraft.setScreen(this);
														}, 180, 70));
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