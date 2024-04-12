package yesman.epicfight.client.gui.datapack.screen;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.compress.utils.Lists;

import com.google.common.collect.Sets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.utils.ParseUtil;
import yesman.epicfight.client.gui.datapack.widgets.AnimatedModelPlayer;
import yesman.epicfight.client.gui.datapack.widgets.Grid;
import yesman.epicfight.client.gui.datapack.widgets.InputComponentList;
import yesman.epicfight.client.gui.datapack.widgets.PopupBox;
import yesman.epicfight.client.gui.datapack.widgets.ResizableComponent.HorizontalSizing;
import yesman.epicfight.client.gui.datapack.widgets.ResizableComponent.VerticalSizing;
import yesman.epicfight.client.gui.datapack.widgets.Static;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Styles;
import yesman.epicfight.world.capabilities.item.Style;

@OnlyIn(Dist.CLIENT)
public class ComboScreen extends Screen {
	private final Screen parentScreen;
	private Grid stylesGrid;
	private Grid comboGrid;
	private PopupBox.AnimationPopupBox dashAttackPopupbox;
	private PopupBox.AnimationPopupBox airSlashPopupbox;
	private final InputComponentList<ListTag> inputComponentsList;
	private final AnimatedModelPlayer animationModelPlayer;
	private final List<PackEntry<String, ListTag>> styles = Lists.newArrayList();
	private final CompoundTag rootTag;
	
	public ComboScreen(Screen parentScreen, CompoundTag rootTag) {
		super(Component.translatable("datapack_edit.weapon_type.combos"));
		
		this.parentScreen = parentScreen;
		
		this.inputComponentsList = new InputComponentList<>(this, 0, 0, 0, 0, 20) {
			@Override
			public void importTag(ListTag tag) {
				this.setComponentsActive(true);
				
				boolean isMount = ComboScreen.this.stylesGrid.getValue(ComboScreen.this.stylesGrid.getRowposition(), "style") == Styles.MOUNT;
				
				if (!isMount) {
					while (tag.size() < 2) {
						tag.add(StringTag.valueOf(""));
					}
				}
				
				Grid.PackImporter packImporter = new Grid.PackImporter();
				int tagSize = tag.size();
				
				if (isMount) {
					for (int i = 0; i < tagSize; i++) {
						packImporter.newRow().newValue("combo_animation", AnimationManager.getInstance().byKey(tag.getString(i)));
					}
					
					this.setDataBindingComponenets(new Object[] {packImporter});
				} else {
					for (int i = 0; i < tagSize - 2; i++) {
						packImporter.newRow().newValue("combo_animation", AnimationManager.getInstance().byKey(tag.getString(i)));
					}
					
					this.setDataBindingComponenets(new Object[] {
						packImporter,
						AnimationManager.getInstance().byKey(tag.getString(tagSize - 2)),
						AnimationManager.getInstance().byKey(tag.getString(tagSize - 1))
					});
				}
			}
		};
		this.inputComponentsList.setLeftPos(parentScreen.width - 205);
		this.rootTag = ParseUtil.getOrDefaultTag(rootTag, "combos", new CompoundTag());
		this.font = parentScreen.getMinecraft().font;
		
		this.stylesGrid = Grid.builder(this, parentScreen.getMinecraft())
								.xy1(12, 50)
								.xy2(85, 50)
								.horizontalSizing(HorizontalSizing.LEFT_WIDTH)
								.verticalSizing(VerticalSizing.TOP_BOTTOM)
								.rowHeight(21)
								.rowEditable(true)
								.transparentBackground(false)
								.rowpositionChanged((rowposition, values) -> {
									this.inputComponentsList.importTag(this.styles.get(rowposition).getPackValue());
									this.reloadAnimationPlayer();
									
									if (values.get("style") == Styles.MOUNT) {
										this.dashAttackPopupbox.setValue(null);
										this.airSlashPopupbox.setValue(null);
										this.dashAttackPopupbox.setActive(false);
										this.airSlashPopupbox.setActive(false);
									}
								})
								.addColumn(Grid.combo("style", Style.ENUM_MANAGER.universalValues())
												.valueChanged((event) -> {
													if (event.prevValue == Styles.MOUNT) {
														this.dashAttackPopupbox.setActive(true);
														this.airSlashPopupbox.setActive(true);
														
														ListTag combosList = this.styles.get(event.rowposition).getPackValue();
														combosList.add(StringTag.valueOf(""));
														combosList.add(StringTag.valueOf(""));
														
													} else if (event.postValue == Styles.MOUNT) {
														this.dashAttackPopupbox.setValue(null);
														this.airSlashPopupbox.setValue(null);
														this.dashAttackPopupbox.setActive(false);
														this.airSlashPopupbox.setActive(false);
														
														ListTag combosList = this.styles.get(event.rowposition).getPackValue();
														combosList.remove(combosList.size() - 1);
														combosList.remove(combosList.size() - 1);
													}
													
													this.styles.get(event.rowposition).setPackKey(ParseUtil.nullParam(event.postValue).toLowerCase(Locale.ROOT));
												})
												.defaultVal(Styles.ONE_HAND))
								.pressAdd((grid, button) -> {
									this.styles.add(PackEntry.of("", ListTag::new));
									int rowposition = grid.addRow();
									grid.setGridFocus(rowposition, "style");
								})
								.pressRemove((grid, button) -> {
									grid.removeRow((removedRow) -> this.styles.remove(removedRow));
									
									if (grid.children().size() == 0) {
										this.comboGrid.setActive(false);
									}
								})
								.build();
		
		this.comboGrid = Grid.builder(this, parentScreen.getMinecraft())
								.xy1(177, 40)
								.xy2(14, 80)
								.horizontalSizing(HorizontalSizing.WIDTH_RIGHT)
								.rowHeight(21)
								.rowEditable(true)
								.transparentBackground(false)
								.addColumn(Grid.popup("combo_animation", PopupBox.AnimationPopupBox::new)
												.filter((animation) -> animation instanceof AttackAnimation)
												.editWidgetCreated((popupBox) -> popupBox.setModel(() -> Armatures.BIPED, () -> Meshes.BIPED))
												.toDisplayText((animation) -> animation == null ? "" : animation.getRegistryName().toString())
												.valueChanged((event) -> {
													ListTag animationList = this.styles.get(this.stylesGrid.getRowposition()).getPackValue();
													
													animationList.remove(event.rowposition);
													animationList.add(event.rowposition, StringTag.valueOf(ParseUtil.nullOrApply(event.postValue, (animation) -> animation.getRegistryName().toString())));
													
													if (event.postValue != null) {
														this.reloadAnimationPlayer();
													}
												})
												.width(150))
								.pressAdd((grid, button) -> {
									this.styles.get(this.stylesGrid.getRowposition()).getPackValue().add(grid.children().size(), StringTag.valueOf(""));
									int rowposition = grid.addRow();
									grid.setGridFocus(rowposition, "combo_animation");
								})
								.pressRemove((grid, button) -> {
									grid.removeRow((removedRow) -> this.styles.get(this.stylesGrid.getRowposition()).getPackValue().remove(removedRow));
									this.reloadAnimationPlayer();
								})
								.build();
		
		this.animationModelPlayer = new AnimatedModelPlayer(110, 200, 50, 49, HorizontalSizing.LEFT_RIGHT, VerticalSizing.TOP_BOTTOM);
		this.animationModelPlayer.setArmature(Armatures.BIPED);
		this.animationModelPlayer.setMesh(Meshes.BIPED);
		
		CompoundTag colliderTag = rootTag.getCompound("collider");
		
		try {
			Collider collider = ColliderPreset.deserializeSimpleCollider(colliderTag);
			this.animationModelPlayer.setCollider(collider);
		} catch (IllegalArgumentException e) {
		}
		
		this.dashAttackPopupbox = new PopupBox.AnimationPopupBox(this, this.font, 110, -1, 15, 15, HorizontalSizing.WIDTH_RIGHT, null, Component.translatable("datapack_edit.weapon_type.styles.dash_attak"),
				(animation) -> {
					if (animation != null) {
						ListTag listTag = this.styles.get(this.stylesGrid.getRowposition()).getPackValue();
						listTag.remove(listTag.size() - 2);
						listTag.add(listTag.size() - 1, StringTag.valueOf(ParseUtil.nullOrApply(animation, (animation2) -> animation.getRegistryName().toString())));
						this.reloadAnimationPlayer();
					}
				});
		this.airSlashPopupbox = new PopupBox.AnimationPopupBox(this, this.font, 110, -1, 15, 15, HorizontalSizing.WIDTH_RIGHT, null, Component.translatable("datapack_edit.weapon_type.styles.air_slash"),
				(animation) -> {
					if (animation != null) {
						ListTag listTag = this.styles.get(this.stylesGrid.getRowposition()).getPackValue();
						listTag.remove(listTag.size() - 1);
						listTag.add(listTag.size(), StringTag.valueOf(ParseUtil.nullOrApply(animation, (animation2) -> animation.getRegistryName().toString())));
						this.reloadAnimationPlayer();
					}
				});
		
		this.dashAttackPopupbox.setModel(() -> Armatures.BIPED, () -> Meshes.BIPED);
		this.airSlashPopupbox.setModel(() -> Armatures.BIPED, () -> Meshes.BIPED);
		
		this.dashAttackPopupbox.applyFilter((animation) -> animation instanceof AttackAnimation);
		this.airSlashPopupbox.applyFilter((animation) -> animation instanceof AttackAnimation);
		
		this.inputComponentsList.newRow();
		this.inputComponentsList.addComponentCurrentRow(new Static(this.font, 80, 110, -1, 15, HorizontalSizing.WIDTH_RIGHT, null, Component.translatable("Combo Attacks")));
		this.inputComponentsList.newRow();
		this.inputComponentsList.newRow();
		this.inputComponentsList.newRow();
		this.inputComponentsList.addComponentCurrentRow(this.comboGrid);
		
		this.inputComponentsList.newRow();
		this.inputComponentsList.newRow();
		this.inputComponentsList.newRow();
		this.inputComponentsList.addComponentCurrentRow(new Static(this.font, 60, 130, -1, 15, HorizontalSizing.WIDTH_RIGHT, null, Component.translatable("datapack_edit.weapon_type.styles.dash_attak")));
		this.inputComponentsList.addComponentCurrentRow(this.dashAttackPopupbox);
		
		this.inputComponentsList.newRow();
		this.inputComponentsList.addComponentCurrentRow(new Static(this.font, 60, 130, -1, 15, HorizontalSizing.WIDTH_RIGHT, null, Component.translatable("datapack_edit.weapon_type.styles.air_slash")));
		this.inputComponentsList.addComponentCurrentRow(this.airSlashPopupbox);
		
		this.inputComponentsList.setComponentsActive(false);
		
		Grid.PackImporter packImporter = new Grid.PackImporter();
		
		for (String style : this.rootTag.getAllKeys()) {
			this.styles.add(PackEntry.of(style, () -> this.rootTag.getList(style, Tag.TAG_STRING)));
			
			packImporter.newRow();
			packImporter.newValue("style", Style.ENUM_MANAGER.get(style));
		}
		
		this.stylesGrid.setValue(packImporter);
	}
	
	@Override
	protected void init() {
		ScreenRectangle screenRectangle = this.getRectangle();
		
		this.stylesGrid.resize(this.getRectangle());
		
		this.inputComponentsList.updateSize(205, screenRectangle.height(), screenRectangle.top() + 34, screenRectangle.height() - 45);
		this.inputComponentsList.setLeftPos(this.width - 205);
		this.animationModelPlayer.resize(screenRectangle);
		
		this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (button) -> {
			Set<String> styles = Sets.newHashSet();
			
			for (PackEntry<String, ListTag> entry : this.styles) {
				if (styles.contains(entry.getPackKey())) {
					this.minecraft.setScreen(new MessageScreen<>("Save Failed", "Unable to save because of duplicated style: " + entry.getPackKey(), this, (button2) -> {
						this.minecraft.setScreen(this);
					}, 180, 90));
					return;
				}
				styles.add(entry.getPackKey());
			}
			
			this.rootTag.tags.clear();
			
			for (PackEntry<String, ListTag> entry : this.styles) {
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
		this.addRenderableWidget(this.animationModelPlayer);
		this.addRenderableWidget(this.inputComponentsList);
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
	
	private void reloadAnimationPlayer() {
		List<StaticAnimation> animations = Lists.newArrayList();
		
		this.comboGrid.visitRows((values) -> {
			StaticAnimation animation = (StaticAnimation)values.get("combo_animation");
			
			if (animation != null) {
				animations.add(animation);
			}
		});
		
		StaticAnimation dashAttack = this.dashAttackPopupbox.getValue();
		StaticAnimation airSlash = this.airSlashPopupbox.getValue();
		
		if (dashAttack != null) {
			animations.add(dashAttack);
		}
		
		if (airSlash != null) {
			animations.add(airSlash);
		}
		
		this.animationModelPlayer.clearAnimations();
		animations.forEach(this.animationModelPlayer::addAnimationToPlay);
	}
}