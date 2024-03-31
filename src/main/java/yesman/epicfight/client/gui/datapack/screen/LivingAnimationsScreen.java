package yesman.epicfight.client.gui.datapack.screen;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.compress.utils.Lists;

import com.google.common.collect.Sets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.MainFrameAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.utils.ParseUtil;
import yesman.epicfight.client.gui.datapack.widgets.AnimatedModelPlayer;
import yesman.epicfight.client.gui.datapack.widgets.Grid;
import yesman.epicfight.client.gui.datapack.widgets.PopupBox;
import yesman.epicfight.client.gui.datapack.widgets.ResizableComponent.HorizontalSizing;
import yesman.epicfight.client.gui.datapack.widgets.ResizableComponent.VerticalSizing;
import yesman.epicfight.client.gui.datapack.widgets.Static;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Styles;
import yesman.epicfight.world.capabilities.item.Style;

@OnlyIn(Dist.CLIENT)
public class LivingAnimationsScreen extends Screen {
	private final Screen parentScreen;
	private final Grid stylesGrid;
	private Grid animationsGrid;
	private final AnimatedModelPlayer animationModelPlayer;
	
	private final List<PackEntry<String, CompoundTag>> styles = Lists.newArrayList();
	private final CompoundTag rootTag;
	
	public LivingAnimationsScreen(Screen parentScreen, CompoundTag rootTag) {
		super(Component.translatable("datapack_edit.weapon_type.living_animations"));
		
		this.parentScreen = parentScreen;
		this.rootTag = rootTag;
		this.font = parentScreen.getMinecraft().font;
		
		this.animationModelPlayer = new AnimatedModelPlayer(106, 200, 60, 49, HorizontalSizing.LEFT_RIGHT, VerticalSizing.TOP_BOTTOM);
		this.animationModelPlayer.setArmature(Armatures.BIPED);
		this.animationModelPlayer.setMesh(Meshes.BIPED);
		
		this.stylesGrid = Grid.builder(this, parentScreen.getMinecraft())
								.xy1(12, 60)
								.xy2(85, 50)
								.horizontalSizing(HorizontalSizing.LEFT_WIDTH)
								.verticalSizing(VerticalSizing.TOP_BOTTOM)
								.rowHeight(21)
								.rowEditable(true)
								.transparentBackground(false)
								.rowpositionChanged((rowposition, values) -> {
									Grid.PackImporter importer = new Grid.PackImporter();
									
									for (String livingMotion : this.styles.get(rowposition).getTag().getAllKeys()) {
										importer.newRow().newValue("living_motion", LivingMotion.ENUM_MANAGER.get(livingMotion));
									}
									
									this.animationsGrid.setActive(true);
									this.animationsGrid.setValue(importer);
								})
								.addColumn(Grid.combo("style", Style.ENUM_MANAGER.universalValues())
												.valueChanged((event) -> this.styles.get(event.rowposition).setPackName(ParseUtil.nullParam(event.postValue).toLowerCase(Locale.ROOT)))
												.defaultVal(Styles.ONE_HAND))
								.pressAdd((grid, button) -> {
									this.styles.add(PackEntry.of("", CompoundTag::new));
									int rowposition = grid.addRow();
									grid.setGridFocus(rowposition, "style");
								})
								.pressRemove((grid, button) -> {
									grid.removeRow((removedRow) -> this.styles.remove(removedRow));
									
									if (grid.children().size() == 0) {
										this.animationsGrid.setActive(false);
									}
								})
								.build();
		
		this.animationsGrid = Grid.builder(this, parentScreen.getMinecraft())
									.xy1(177, 60)
									.xy2(14, 49)
									.horizontalSizing(HorizontalSizing.WIDTH_RIGHT)
									.verticalSizing(VerticalSizing.TOP_BOTTOM)
									.rowHeight(21)
									.rowEditable(true)
									.transparentBackground(false)
									.addColumn(Grid.combo("living_motion", LivingMotion.ENUM_MANAGER.universalValues())
															.valueChanged((event) -> {
																CompoundTag tag = this.styles.get(this.stylesGrid.getRowposition()).getTag();
																String oldMotion = ParseUtil.nullParam(event.prevValue).toLowerCase(Locale.ROOT);
																Tag animationTag = tag.get(oldMotion);
																tag.remove(oldMotion);
																tag.put(ParseUtil.nullParam(event.postValue).toLowerCase(Locale.ROOT), animationTag == null ? StringTag.valueOf("") : animationTag);
															}).defaultVal(LivingMotions.IDLE))
									.addColumn(Grid.popup("living_animation", PopupBox.AnimationPopupBox::new)
													.filter((animation) -> !(animation instanceof MainFrameAnimation))
													.editWidgetCreated((popupBox) -> popupBox.setModel(() -> Armatures.BIPED, () -> Meshes.BIPED))
													.valueChanged((event) -> {
														this.animationModelPlayer.clearAnimations();
														
														LivingMotion livingMotion = event.grid.getValue(event.rowposition, "living_motion");
														CompoundTag tag = this.styles.get(this.stylesGrid.getRowposition()).getTag();
														tag.put(ParseUtil.nullParam(livingMotion).toLowerCase(Locale.ROOT), StringTag.valueOf(ParseUtil.nullOrApply(event.postValue, (animation) -> animation.getRegistryName().toString())));
														
														if (event.postValue != null) {
															this.animationModelPlayer.addAnimationToPlay(event.postValue);
														}
													})
													.toDisplayText((animation) -> animation == null ? "" : animation.getRegistryName().toString())
													.width(150))
									.pressAdd((grid, button) -> {
										this.styles.get(this.stylesGrid.getRowposition()).getTag().put("", StringTag.valueOf(""));
										int rowposition = grid.addRow();
										grid.setGridFocus(rowposition, "living_animation");
									})
									.pressRemove((grid, button) -> {
										this.styles.get(this.stylesGrid.getRowposition()).getTag().remove(ParseUtil.nullParam(grid.getValue(grid.getRowposition(), "living_motion")).toLowerCase(Locale.ROOT));
										grid.removeRow();
									})
									.rowpositionChanged((rowposition, values) -> {
										this.animationModelPlayer.clearAnimations();
										StaticAnimation animation = (StaticAnimation)values.get("living_animation");
										
										if (animation != null) {
											this.animationModelPlayer.addAnimationToPlay((StaticAnimation)values.get("living_animation"));
										}
									})
									.build();
		
		this.animationsGrid.setActive(false);
		
		Grid.PackImporter packImporter = new Grid.PackImporter();
		
		for (Map.Entry<String, Tag> entry : rootTag.tags.entrySet()) {
			this.styles.add(PackEntry.of(entry.getKey(), () -> (CompoundTag)entry.getValue()));
			
			packImporter.newRow();
			packImporter.newValue("style", Style.ENUM_MANAGER.get(entry.getKey()));
		}
		
		this.stylesGrid.setValue(packImporter);
	}
	
	@Override
	protected void init() {
		ScreenRectangle screenRectangle = this.getRectangle();
		
		this.stylesGrid.resize(screenRectangle);
		this.animationsGrid.resize(screenRectangle);
		this.animationModelPlayer.resize(screenRectangle);
		
		this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (button) -> {
			Set<String> styles = Sets.newHashSet();
			
			for (PackEntry<String, CompoundTag> entry : this.styles) {
				if (styles.contains(entry.getPackName())) {
					this.minecraft.setScreen(new MessageScreen("Save Failed", "Unable to save because of duplicated style: " + entry.getPackName(), this, (button2) -> {
						this.minecraft.setScreen(this);
					}, 180, 90));
					return;
				}
				styles.add(entry.getPackName());
			}
			
			this.rootTag.tags.clear();
			
			for (PackEntry<String, CompoundTag> entry : this.styles) {
				this.rootTag.put(entry.getPackName(), entry.getTag());
			}
			
			this.onClose();
		}).pos(this.width / 2 - 162, this.height - 32).size(160, 21).build());
		
		this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (button) -> {
			this.minecraft.setScreen(new MessageScreen("", "Do you want to quit without saving changes?", this,
														(button2) -> {
															this.onClose();
														}, (button2) -> {
															this.minecraft.setScreen(this);
														}, 180, 70));
		}).pos(this.width / 2 + 2, this.height - 32).size(160, 21).build());
		
		this.addRenderableWidget(new Static(this.font, 14, 100, 40, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.styles")));
		this.addRenderableWidget(this.stylesGrid);
		this.addRenderableWidget(this.animationModelPlayer);
		this.addRenderableWidget(new Static(this.font, this.width - 188, 100, 40, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.weapon_type.living_animations.modifiers")));
		this.addRenderableWidget(this.animationsGrid);
	}
	
	@Override
	public void onClose() {
		this.animationModelPlayer.onDestroy();
		this.minecraft.setScreen(this.parentScreen);
	}
	
	@Override
	public void tick() {
		this.animationModelPlayer.tick();
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