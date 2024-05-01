package yesman.epicfight.client.gui.datapack.screen;

import java.util.ArrayList;
import java.util.Locale;

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
import yesman.epicfight.client.gui.datapack.widgets.InputComponentList;
import yesman.epicfight.client.gui.datapack.widgets.PopupBox;
import yesman.epicfight.client.gui.datapack.widgets.ResizableComponent.HorizontalSizing;
import yesman.epicfight.client.gui.datapack.widgets.ResizableComponent.VerticalSizing;
import yesman.epicfight.client.gui.datapack.widgets.Static;
import yesman.epicfight.data.conditions.Condition.LivingEntityCondition;
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
	
	public StylesScreen(Screen parentScreen, CompoundTag rootTag) {
		super(Component.translatable("datapack_edit.weapon_type.styles"));
		
		this.parentScreen = parentScreen;
		this.minecraft = parentScreen.getMinecraft();
		this.font = parentScreen.getMinecraft().font;
		
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
		
		this.inputComponentsList.setLeftPos(180);
		
		this.stylesGrid = Grid.builder(this, parentScreen.getMinecraft())
								.xy1(12, 60)
								.xy2(160, 76)
								.horizontalSizing(HorizontalSizing.LEFT_WIDTH)
								.verticalSizing(VerticalSizing.TOP_BOTTOM)
								.rowHeight(26)
								.rowEditable(true)
								.transparentBackground(false)
								.rowpositionChanged((rowposition, values) -> this.inputComponentsList.importTag(rootTag.getList("cases", Tag.TAG_COMPOUND).getCompound(rowposition)))
								.addColumn(Grid.combo("style", ParseUtil.remove(Style.ENUM_MANAGER.universalValues(), CapabilityItem.Styles.COMMON)).valueChanged((event) -> {
												ParseUtil.getOrDefaultTag(rootTag, "cases", new ListTag()).getCompound(event.rowposition).put("style", StringTag.valueOf(ParseUtil.nullParam(event.postValue).toLowerCase(Locale.ROOT)));
											}).defaultVal(Styles.ONE_HAND))
								.pressAdd((grid, button) -> {
									ParseUtil.getOrDefaultTag(rootTag, "cases", new ListTag()).add(grid.children().size(), new CompoundTag());
									int rowposition = grid.addRow();
									grid.setGridFocus(rowposition, "style");
								})
								.pressRemove((grid, button) -> {
									grid.removeRow((removedRow) -> {
										ListTag cases = rootTag.getList("cases", Tag.TAG_COMPOUND);
										cases.remove(removedRow);
										
										if (cases.size() == 0) {
											this.inputComponentsList.setComponentsActive(false);
										}
									});
								})
								.build();
		
		this.defaultStyle = new ComboBox<>(parentScreen, this.font, 55, 116, 15, 53, HorizontalSizing.LEFT_WIDTH, VerticalSizing.HEIGHT_BOTTOM, 8, Component.translatable("datapack_edit.weapon_type.styles.default"),
											new ArrayList<>(ParseUtil.remove(Style.ENUM_MANAGER.universalValues(), CapabilityItem.Styles.COMMON)), ParseUtil::snakeToSpacedCamel, (style) -> rootTag.putString("default", ParseUtil.nullParam(style).toLowerCase(Locale.ROOT)));
		
		this.font = parentScreen.getMinecraft().font;
		
		final Grid parameterGrid = Grid.builder(this, parentScreen.getMinecraft())
										.xy1(4, 40)
										.xy2(1, 100)
										.horizontalSizing(HorizontalSizing.LEFT_RIGHT)
										.rowHeight(26)
										.rowEditable(false)
										.transparentBackground(false)
										.addColumn(Grid.editbox("parameter_key").valueChanged((event) -> {
											CompoundTag predicate = ParseUtil.getOrDefaultTag(rootTag.getList("cases", Tag.TAG_COMPOUND).getCompound(this.stylesGrid.getRowposition()), "predicate", new CompoundTag());
											predicate.remove(ParseUtil.nullParam(event.prevValue));
											predicate.putString(ParseUtil.nullParam(event.postValue), ParseUtil.nullParam(event.grid.getValue(event.rowposition, "parameter_value")));
										}).editable(false))
										.addColumn(Grid.editbox("parameter_value").valueChanged((event) -> {
											CompoundTag predicate = ParseUtil.getOrDefaultTag(rootTag.getList("cases", Tag.TAG_COMPOUND).getCompound(this.stylesGrid.getRowposition()), "predicate", new CompoundTag());
											predicate.putString(ParseUtil.nullParam(event.grid.getValue(event.rowposition, "parameter_key")), ParseUtil.nullParam(event.postValue));
										}).width(150))
										.build();
		
		this.inputComponentsList.newRow();
		this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 80, 100, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.weapon_type.styles.condition")));
		this.inputComponentsList.addComponentCurrentRow(new PopupBox.RegistryPopupBox<>(this, this.font, this.inputComponentsList.nextStart(5), 1, 60, 15, HorizontalSizing.LEFT_RIGHT, null, Component.translatable("datapack_edit.weapon_type.styles.condition"),
																		EpicFightConditions.REGISTRY.get(), (name, conditionProvider) -> {
																			if (conditionProvider != null) {
																				parameterGrid.reset();
																				conditionProvider.get().getAcceptingParameters().forEach((e) -> parameterGrid.addRowWithDefaultValues("parameter_key", e.getKey()));
																				rootTag.getList("cases", Tag.TAG_COMPOUND).getCompound(this.stylesGrid.getRowposition()).putString("condition", ParseUtil.getRegistryName(conditionProvider, EpicFightConditions.REGISTRY.get()));
																			}
																		}).applyFilter((condition) -> condition.get() instanceof LivingEntityCondition));
		
		this.inputComponentsList.newRow();
		this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 80, 100, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.weapon_type.styles.parameters")));
		
		this.inputComponentsList.newRow();
		this.inputComponentsList.newRow();
		this.inputComponentsList.newRow();
		this.inputComponentsList.addComponentCurrentRow(parameterGrid);
		
		this.inputComponentsList.setComponentsActive(false);
		
		if (rootTag.contains("cases")) {
			Grid.PackImporter packImporter = new Grid.PackImporter();
			
			for (Tag caseTag : rootTag.getList("cases", Tag.TAG_COMPOUND)) {
				CompoundTag caseCompTag = (CompoundTag)caseTag;
				
				packImporter.newRow();
				packImporter.newValue("style", Style.ENUM_MANAGER.get(caseCompTag.getString("style")));
			}
			
			this.stylesGrid.setValue(packImporter);
		} else {
			rootTag.put("cases", new ListTag());
		}
		
		if (rootTag.contains("default")) {
			this.defaultStyle.setValue(Style.ENUM_MANAGER.get(rootTag.getString("default")));
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
			this.minecraft.setScreen(this.parentScreen);
		}).pos(this.width / 2 - 162, this.height - 32).size(160, 21).build());
		
		this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (button) -> {
			this.minecraft.setScreen(this.parentScreen);
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