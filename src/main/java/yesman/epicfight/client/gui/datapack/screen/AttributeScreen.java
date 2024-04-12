package yesman.epicfight.client.gui.datapack.screen;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.compress.utils.Lists;

import com.google.common.collect.Sets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.ParseUtil;
import yesman.epicfight.client.gui.datapack.widgets.Grid;
import yesman.epicfight.client.gui.datapack.widgets.ResizableComponent.HorizontalSizing;
import yesman.epicfight.client.gui.datapack.widgets.ResizableComponent.VerticalSizing;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Styles;
import yesman.epicfight.world.capabilities.item.Style;

@OnlyIn(Dist.CLIENT)
public class AttributeScreen extends Screen {
	private static final List<String> EPICFIGHT_ATTRIBUTES = List.of("armor_negation", "impact", "max_strikes", "damage_bonus", "speed_bonus");
	
	private final Screen parentScreen;
	private Grid stylesGrid;
	private Grid attributesGrid;
	private final List<PackEntry<String, CompoundTag>> styles = Lists.newArrayList();
	private final CompoundTag rootTag;
	
	public AttributeScreen(Screen parentScreen, CompoundTag rootTag) {
		super(Component.translatable("datapack_edit.item_capability.attributes"));
		
		this.parentScreen = parentScreen;
		this.rootTag = rootTag;
		this.font = parentScreen.getMinecraft().font;
		
		this.stylesGrid = Grid.builder(this, parentScreen.getMinecraft())
								.xy1(20, 50)
								.xy2(90, 50)
								.horizontalSizing(HorizontalSizing.LEFT_WIDTH)
								.verticalSizing(VerticalSizing.TOP_BOTTOM)
								.rowHeight(21)
								.rowEditable(true)
								.transparentBackground(false)
								.rowpositionChanged((rowposition, values) -> {
									Grid.PackImporter packImporter = new Grid.PackImporter();
									
									for (Map.Entry<String, Tag> entry : this.styles.get(rowposition).getPackValue().tags.entrySet()) {
										packImporter.newRow().newValue("attribute", entry.getKey()).newValue("amount", entry.getValue().getAsString());
									}
									
									this.attributesGrid.setActive(true);
									this.attributesGrid.setValue(packImporter);
								})
								.addColumn(Grid.combo("style", Style.ENUM_MANAGER.universalValues())
												.valueChanged((event) -> this.styles.get(event.rowposition).setPackKey(ParseUtil.nullParam(event.postValue).toLowerCase(Locale.ROOT)))
												.defaultVal(Styles.ONE_HAND))
								.pressAdd((grid, button) -> {
									this.styles.add(PackEntry.of("", CompoundTag::new));
									int rowposition = grid.addRow();
									grid.setGridFocus(rowposition, "style");
								})
								.pressRemove((grid, button) -> {
									grid.removeRow((removedRow) -> this.styles.remove(removedRow));
									
									if (grid.children().size() == 0) {
										this.attributesGrid.setActive(false);
									}
								})
								.build();
		
		this.attributesGrid = Grid.builder(this, parentScreen.getMinecraft())
									.xy1(120, 50)
									.xy2(20, 50)
									.horizontalSizing(HorizontalSizing.LEFT_RIGHT)
									.verticalSizing(VerticalSizing.TOP_BOTTOM)
									.rowHeight(21)
									.rowEditable(true)
									.transparentBackground(false)
									.addColumn(Grid.combo("attribute", EPICFIGHT_ATTRIBUTES)
													.toDisplayText(ParseUtil::snakeToSpacedCamel)
													.valueChanged((event) -> {
														CompoundTag attributesCompound = this.styles.get(this.stylesGrid.getRowposition()).getPackValue();
														Tag tag = attributesCompound.get(ParseUtil.nullParam(event.prevValue));
														
														attributesCompound.remove(ParseUtil.nullParam(event.prevValue));
														attributesCompound.put(ParseUtil.nullParam(event.postValue), tag == null ? StringTag.valueOf("") : tag);
													})
													.width(100))
									.addColumn(Grid.editbox("amount")
													.editWidgetCreated((editbox) -> editbox.setFilter((context) -> ParseUtil.isParsable(context, Double::parseDouble)))
													.valueChanged((event) -> {
														CompoundTag attributesCompound = this.styles.get(this.stylesGrid.getRowposition()).getPackValue();
														attributesCompound.put(event.grid.getValue(event.rowposition, "attribute"), StringTag.valueOf(ParseUtil.nullParam(event.postValue)));
													})
													.width(150))
									.pressAdd((grid, button) -> {
										this.styles.get(this.stylesGrid.getRowposition()).getPackValue().put("", StringTag.valueOf(""));
										int rowposition = grid.addRow();
										grid.setGridFocus(rowposition, "attribute");
									})
									.pressRemove((grid, button) -> {
										grid.removeRow((removedRow) -> this.styles.get(this.stylesGrid.getRowposition()).getPackValue().remove(grid.getValue(removedRow, "attribute")));
									})
									.build();
		
		Grid.PackImporter packImporter = new Grid.PackImporter();
		
		for (Map.Entry<String, Tag> entry : rootTag.tags.entrySet()) {
			this.styles.add(PackEntry.of(entry.getKey(), () -> (CompoundTag)entry.getValue()));
			
			packImporter.newRow();
			packImporter.newValue("style", Style.ENUM_MANAGER.get(entry.getKey()));
		}
		
		this.stylesGrid.setValue(packImporter);
		this.attributesGrid.setActive(false);
	}
	
	@Override
	protected void init() {
		this.stylesGrid.resize(this.getRectangle());
		
		this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (button) -> {
			Set<String> styles = Sets.newHashSet();
			
			for (PackEntry<String, CompoundTag> entry : this.styles) {
				if (styles.contains(entry.getPackKey())) {
					this.minecraft.setScreen(new MessageScreen<>("Save Failed", "Unable to save because of duplicated style: " + entry.getPackKey(), this, (button2) -> {
						this.minecraft.setScreen(this);
					}, 180, 90));
					return;
				}
				styles.add(entry.getPackKey());
			}
			
			this.rootTag.tags.clear();
			
			for (PackEntry<String, CompoundTag> entry : this.styles) {
				this.rootTag.put(entry.getPackKey(), entry.getPackValue());
			}
			
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
		
		this.addRenderableWidget(this.stylesGrid);
		this.addRenderableWidget(this.attributesGrid);
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
