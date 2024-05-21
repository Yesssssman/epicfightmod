package yesman.epicfight.client.gui.datapack.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.commons.compress.utils.Lists;

import com.google.common.collect.Sets;

import io.netty.util.internal.StringUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.ParseUtil;
import yesman.epicfight.client.gui.datapack.widgets.ComboBox;
import yesman.epicfight.client.gui.datapack.widgets.Grid;
import yesman.epicfight.client.gui.datapack.widgets.Grid.GridBuilder.RowEditButton;
import yesman.epicfight.client.gui.datapack.widgets.InputComponentList;
import yesman.epicfight.client.gui.datapack.widgets.PopupBox;
import yesman.epicfight.client.gui.datapack.widgets.ResizableComponent.HorizontalSizing;
import yesman.epicfight.client.gui.datapack.widgets.ResizableComponent.VerticalSizing;
import yesman.epicfight.client.gui.datapack.widgets.ResizableEditBox;
import yesman.epicfight.client.gui.datapack.widgets.Static;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.data.conditions.Condition.PlayerPatchCondition;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.data.conditions.EpicFightConditions;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Styles;
import yesman.epicfight.world.capabilities.item.Style;

@OnlyIn(Dist.CLIENT)
public class StylesScreen extends Screen {
	private final Screen parentScreen;
	private final Grid stylesGrid;
	private final ComboBox<Style> defaultStyle;
	private final InputComponentList<CompoundTag> inputComponentsList;
	private final List<CompoundTag> cases = Lists.newArrayList();
	private final CompoundTag rootTag;
	
	public StylesScreen(Screen parentScreen, CompoundTag rootTag) {
		super(Component.translatable("datapack_edit.weapon_type.styles"));
		
		this.parentScreen = parentScreen;
		this.minecraft = parentScreen.getMinecraft();
		this.font = parentScreen.getMinecraft().font;
		this.rootTag = rootTag;
		
		this.inputComponentsList = new InputComponentList<>(this, 0, 0, 0, 0, 22) {
			@Override
			public void importTag(CompoundTag tag) {
				this.setComponentsActive(true);
				
				Grid.PackImporter packImporter = new Grid.PackImporter();
				String condtionName = tag.getString("condition");
				
				if (!condtionName.contains(":")) {
					condtionName = EpicFightMod.MODID + ":" + condtionName;
				}
				
				Supplier<Condition<?>> conditionProvider = EpicFightConditions.getConditionOrNull(new ResourceLocation(condtionName));
				
				if (conditionProvider != null) {
					Condition<?> condition = conditionProvider.get();
					
					for (ParameterEditor paramEditor : condition.getAcceptingParameters(StylesScreen.this)) {
						packImporter.newRow();
						packImporter.newValue("parameter_key", paramEditor);
						packImporter.newValue("parameter_value", paramEditor.fromTag.apply(tag.getCompound("predicate").get(paramEditor.editWidget.getMessage().getString())));
					}
				}
				
				this.setDataBindingComponenets(new Object[] {
					conditionProvider,
					packImporter
				});
			}
		};
		
		this.inputComponentsList.setLeftPos(180);
		
		this.stylesGrid = Grid.builder(this, parentScreen.getMinecraft())
								.xy1(12, 60)
								.xy2(160, 76)
								.horizontalSizing(HorizontalSizing.LEFT_WIDTH)
								.verticalSizing(VerticalSizing.TOP_BOTTOM)
								.rowHeight(26)
								.rowEditable(RowEditButton.ADD_REMOVE)
								.transparentBackground(false)
								.rowpositionChanged((rowposition, values) -> this.inputComponentsList.importTag(this.cases.get(rowposition)))
								.addColumn(Grid.combo("style", ParseUtil.remove(Style.ENUM_MANAGER.universalValues(), CapabilityItem.Styles.COMMON)).valueChanged((event) -> {
												this.cases.get(event.rowposition).put("style", StringTag.valueOf(ParseUtil.nullParam(event.postValue).toLowerCase(Locale.ROOT)));
											}).defaultVal(Styles.ONE_HAND))
								.pressAdd((grid, button) -> {
									int rowposition = grid.addRow((addedRow) -> this.cases.add(addedRow, new CompoundTag()));
									grid.setGridFocus(rowposition, "style");
								})
								.pressRemove((grid, button) -> {
									grid.removeRow((removedRow) -> {
										this.cases.remove(removedRow);
										
										if (this.cases.size() == 0) {
											this.inputComponentsList.setComponentsActive(false);
										}
									});
								})
								.build();
		
		this.defaultStyle = new ComboBox<>(parentScreen, this.font, 55, 116, 15, 53, HorizontalSizing.LEFT_WIDTH, VerticalSizing.HEIGHT_BOTTOM, 8, Component.translatable("datapack_edit.weapon_type.styles.default"),
											new ArrayList<>(ParseUtil.remove(Style.ENUM_MANAGER.universalValues(), CapabilityItem.Styles.COMMON)), ParseUtil::snakeToSpacedCamel, null);
		
		this.font = parentScreen.getMinecraft().font;
		
		final Grid parameterGrid = Grid.builder(this, parentScreen.getMinecraft())
										.xy1(4, 40)
										.xy2(1, 100)
										.horizontalSizing(HorizontalSizing.LEFT_RIGHT)
										.rowHeight(26)
										.rowEditable(RowEditButton.NONE)
										.transparentBackground(false)
										.addColumn(Grid.<ParameterEditor, ResizableEditBox>wildcard("parameter_key")
														.editable(false)
														.toDisplayText((editor) -> editor.editWidget.getMessage().getString())
														.width(100))
										.addColumn(Grid.wildcard("parameter_value")
														.editWidgetProvider((row) -> {
															ParameterEditor editor = row.getValue("parameter_key");
															return editor.editWidget;
														})
														.toDisplayText(ParseUtil::snakeToSpacedCamel)
														.editable(true)
														.valueChanged((event) -> {
															CompoundTag predicate = ParseUtil.getOrDefaultTag(this.cases.get(this.stylesGrid.getRowposition()), "predicate", new CompoundTag());
															ParameterEditor editor = event.grid.getValue(event.rowposition, "parameter_key");
															predicate.put(editor.editWidget.getMessage().getString(), editor.toTag.apply(event.postValue));
														}).width(150))
										.build();
		
		this.inputComponentsList.newRow();
		this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 80, 100, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.weapon_type.styles.condition")));
		this.inputComponentsList.addComponentCurrentRow(new PopupBox.RegistryPopupBox<>(this, this.font, this.inputComponentsList.nextStart(5), 1, 60, 15, HorizontalSizing.LEFT_RIGHT, null, Component.translatable("datapack_edit.weapon_type.styles.condition"),
																		EpicFightConditions.REGISTRY.get(), (pair) -> {
																			if (pair.getSecond() != null) {
																				parameterGrid.reset();
																				pair.getSecond().get().getAcceptingParameters(this).forEach((widget) -> parameterGrid.addRowWithDefaultValues("parameter_key", widget));
																				this.cases.get(this.stylesGrid.getRowposition()).putString("condition", ParseUtil.getRegistryName(pair.getSecond(), EpicFightConditions.REGISTRY.get()));
																				this.cases.get(this.stylesGrid.getRowposition()).remove("predicate");
																			}
																		}).applyFilter((condition) -> condition.get() instanceof PlayerPatchCondition));
		
		this.inputComponentsList.newRow();
		this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 80, 100, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.weapon_type.styles.parameters")));
		
		this.inputComponentsList.newRow();
		this.inputComponentsList.newRow();
		this.inputComponentsList.newRow();
		this.inputComponentsList.addComponentCurrentRow(parameterGrid);
		
		this.inputComponentsList.setComponentsActive(false);
		
		if (rootTag.contains("styles")) {
			CompoundTag stylesCompound = rootTag.getCompound("styles");
			Grid.PackImporter packImporter = new Grid.PackImporter();
			
			for (Tag caseTag : stylesCompound.getList("cases", Tag.TAG_COMPOUND)) {
				CompoundTag caseCompTag = (CompoundTag)caseTag;
				packImporter.newRow();
				packImporter.newValue("style", Style.ENUM_MANAGER.get(caseCompTag.getString("style")));
				
				this.cases.add(caseCompTag);
			}
			
			this.stylesGrid._setValue(packImporter);
			this.defaultStyle._setValue(Style.ENUM_MANAGER.get(stylesCompound.getString("default")));
		}
	}
	
	@Override
	protected void init() {
		ScreenRectangle screenRectangle = this.getRectangle();
		
		this.stylesGrid.resize(screenRectangle);
		this.defaultStyle.resize(screenRectangle);
		
		this.inputComponentsList.updateSize(screenRectangle.width() - 190, screenRectangle.height(), screenRectangle.top() + 37, screenRectangle.height() - 45);
		this.inputComponentsList.setLeftPos(180);
		
		this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (button) -> {
			ListTag caseListTag = new ListTag();
			int idx = 0;
			
			if (this.defaultStyle._getValue() == null) {
				this.minecraft.setScreen(new MessageScreen<>("Save Failed", "Define a default style", this, (button2) -> {
					this.minecraft.setScreen(this);
				}, 180, 90).autoCalculateHeight());
				return;
			}
			
			Set<String> styleNames = Sets.newHashSet();
			
			for (CompoundTag tag : this.cases) {
				try {
					if (styleNames.contains(tag.getString("style"))) {
						throw new IllegalStateException("Duplicated style");
					}
					
					if (!tag.contains("condition") || StringUtil.isNullOrEmpty(tag.getString("condition"))) {
						throw new IllegalStateException("Define a condition");
					}
					
					if (!tag.contains("style") || StringUtil.isNullOrEmpty(tag.getString("style"))) {
						throw new IllegalStateException("Define a style");
					}
					
					styleNames.add(tag.getString("style"));
					
					Condition<?> condition = EpicFightConditions.getConditionOrThrow(new ResourceLocation(tag.getString("condition"))).get();
					condition.read(tag.getCompound("predicate"));
					
					caseListTag.add(tag);
					idx++;
				} catch (Exception e) {
					this.minecraft.setScreen(new MessageScreen<>("Save Failed", "Failed to save " + ParseUtil.snakeToSpacedCamel(this.stylesGrid.getValue(idx, "style")) + ": " + e.getMessage(), this, (button2) -> {
						this.minecraft.setScreen(this);
					}, 180, 90).autoCalculateHeight());
					
					return;
				}
			}
			
			CompoundTag stylesCompound = new CompoundTag();
			stylesCompound.put("cases", caseListTag);
			stylesCompound.putString("default", ParseUtil.nullParam(this.defaultStyle._getValue()).toLowerCase(Locale.ROOT));
			
			this.rootTag.remove("styles");
			this.rootTag.put("styles", stylesCompound);
			
			this.onClose();
		}).pos(this.width / 2 - 162, this.height - 32).size(160, 21).build());
		
		this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (button) -> {
			this.minecraft.setScreen(new MessageScreen<>("", "Do you want to quit without saving changes?", this, (button2) -> this.onClose(), (button2) -> this.minecraft.setScreen(this), 180, 70));
		}).pos(this.width / 2 + 2, this.height - 32).size(160, 21).build());
		
		Static defaultStyleTitle = new Static(this.font, 12, 116, 15, 53, HorizontalSizing.LEFT_WIDTH, VerticalSizing.HEIGHT_BOTTOM, "datapack_edit.weapon_type.styles.default");
		defaultStyleTitle.resize(screenRectangle);
		
		this.addRenderableWidget(new Static(this.font, 12, 60, 40, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.styles"), Component.translatable("datapack_edit.styles.tooltip.optional")));
		this.addRenderableWidget(this.stylesGrid);
		this.addRenderableWidget(defaultStyleTitle);
		this.addRenderableWidget(this.defaultStyle);
		this.addRenderableWidget(this.inputComponentsList);
	}
	
	@Override
	public void onClose() {
		this.minecraft.setScreen(this.parentScreen);
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int action) {
		if (this.getFocused() != null) {
			if (this.getFocused().mouseClicked(mouseX, mouseY, action)) {
				if (action == 0) {
					this.setDragging(true);
				}

				return true;
			}
		}
		
		for (GuiEventListener guieventlistener : this.children()) {
			if (guieventlistener == this.getFocused()) {
				continue;
			}
			
			if (guieventlistener.mouseClicked(mouseX, mouseY, action)) {
				this.setFocused(guieventlistener);
				
				if (action == 0) {
					this.setDragging(true);
				}

				return true;
			}
		}

		return false;
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