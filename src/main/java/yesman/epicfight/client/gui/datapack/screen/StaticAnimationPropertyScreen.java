package yesman.epicfight.client.gui.datapack.screen;

import java.util.List;
import java.util.function.Consumer;

import com.google.common.collect.Lists;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.client.animation.property.JointMask;
import yesman.epicfight.api.client.animation.property.JointMaskReloadListener;
import yesman.epicfight.api.utils.ParseUtil;
import yesman.epicfight.client.gui.datapack.widgets.ComboBox;
import yesman.epicfight.client.gui.datapack.widgets.Grid;
import yesman.epicfight.client.gui.datapack.widgets.InputComponentList;
import yesman.epicfight.client.gui.datapack.widgets.PopupBox;
import yesman.epicfight.client.gui.datapack.widgets.ResizableComponent.HorizontalSizing;
import yesman.epicfight.client.gui.datapack.widgets.Static;

@OnlyIn(Dist.CLIENT)
public class StaticAnimationPropertyScreen extends Screen {
	private final InputComponentList<CompoundTag> inputComponentsList;
	private final ComboBox<LayerOptions> layerTypeCombo;
	private final Consumer<LayerOptions> responder;
	private final Screen parentScreen;
	private final CompoundTag rootTag;
	
	private Layer.Priority baseLayerPriority;
	private Layer.Priority compositeLayerPriority;
	private List<PackEntry<LivingMotion, List<JointMask>>> baseLayerMasks = Lists.newArrayList();
	private List<PackEntry<LivingMotion, List<JointMask>>> compositeLayerMasks = Lists.newArrayList();
	
	protected StaticAnimationPropertyScreen(Screen parentScreen, CompoundTag tag) {
		super(Component.translatable("datapack_edit.import_animation.client_data"));
		
		this.parentScreen = parentScreen;
		this.minecraft = parentScreen.getMinecraft();
		this.font = this.minecraft.font;
		this.rootTag = tag;
		
		this.inputComponentsList = new InputComponentList<> (this, 0, 0, 0, 0, 30) {
			@Override
			public void importTag(CompoundTag tag) {
				this.clearComponents();
				this.setComponentsActive(true);
				
				LayerOptions layerOption = null;
				
				if (tag.contains("layer")) {
					layerOption = LayerOptions.valueOf(tag.getString("layer"));
				} else if (tag.contains("multilayer")) {
					layerOption = LayerOptions.MULTILAYER;
				}
				
				StaticAnimationPropertyScreen.this.rearrangeComponents(layerOption);
				
				Object[] data;
				
				if (layerOption == LayerOptions.BASE_LAYER) {
					Layer.Priority priority = Layer.Priority.valueOf(tag.getString("priority"));
					
					data = new Object[] {
						layerOption,
						priority
					};
				} else if (layerOption == LayerOptions.COMPOSITE_LAYER) {
					Layer.Priority priority = Layer.Priority.valueOf(tag.getString("priority"));
					Grid.PackImporter packImporter = new Grid.PackImporter();
					
					for (Tag maskTag : tag.getList("masks", Tag.TAG_COMPOUND)) {
						CompoundTag maskCompoundTag = (CompoundTag)maskTag;
						LivingMotion livingMotion = LivingMotion.ENUM_MANAGER.get(maskCompoundTag.getString("livingmotion"));
						List<JointMask> jointMask = JointMaskReloadListener.getJointMaskEntry(maskCompoundTag.getString("type"));
						
						packImporter.newRow();
						packImporter.newValue("living_motion", livingMotion);
						packImporter.newValue("joint_mask", jointMask);
						
						StaticAnimationPropertyScreen.this.compositeLayerMasks.add(PackEntry.ofValue(livingMotion, jointMask));
					}
					
					data = new Object[] {
						layerOption,
						priority,
						packImporter
					};
					
				} else if (layerOption == LayerOptions.MULTILAYER) {
					CompoundTag base = tag.getCompound("multilayer").getCompound("base");
					CompoundTag composite = tag.getCompound("multilayer").getCompound("composite");
					
					Layer.Priority basePriority = Layer.Priority.valueOf(base.getString("priority"));
					Grid.PackImporter basePackImporter = new Grid.PackImporter();
					
					for (Tag maskTag : base.getList("masks", Tag.TAG_COMPOUND)) {
						CompoundTag maskCompoundTag = (CompoundTag)maskTag;
						LivingMotion livingMotion = LivingMotion.ENUM_MANAGER.get(maskCompoundTag.getString("livingmotion"));
						List<JointMask> jointMask = JointMaskReloadListener.getJointMaskEntry(maskCompoundTag.getString("type"));
						
						basePackImporter.newRow();
						basePackImporter.newValue("living_motion", livingMotion);
						basePackImporter.newValue("joint_mask", jointMask);
						
						StaticAnimationPropertyScreen.this.baseLayerMasks.add(PackEntry.ofValue(livingMotion, jointMask));
					}
					
					Layer.Priority compositePriority = Layer.Priority.valueOf(composite.getString("priority"));
					Grid.PackImporter compositePackImporter = new Grid.PackImporter();
					
					for (Tag maskTag : composite.getList("masks", Tag.TAG_COMPOUND)) {
						CompoundTag maskCompoundTag = (CompoundTag)maskTag;
						LivingMotion livingMotion = LivingMotion.ENUM_MANAGER.get(maskCompoundTag.getString("livingmotion"));
						List<JointMask> jointMask = JointMaskReloadListener.getJointMaskEntry(maskCompoundTag.getString("type"));
						
						compositePackImporter.newRow();
						compositePackImporter.newValue("living_motion", livingMotion);
						compositePackImporter.newValue("joint_mask", jointMask);
						
						StaticAnimationPropertyScreen.this.compositeLayerMasks.add(PackEntry.ofValue(livingMotion, jointMask));
					}
					
					data = new Object[] {
						layerOption,
						basePriority,
						basePackImporter,
						compositePriority,
						compositePackImporter
					};
				} else {
					data = new Object[] {
						null
					};
				}
				
				StaticAnimationPropertyScreen.this.layerTypeCombo.setResponder(null);
				this.setDataBindingComponenets(data);
				StaticAnimationPropertyScreen.this.layerTypeCombo.setResponder(StaticAnimationPropertyScreen.this.responder);
			}
		};
		
		this.responder = (layerType) -> this.rearrangeComponents(layerType);
		this.layerTypeCombo = new ComboBox<>(this, this.font, 0, 124, 100, 15, HorizontalSizing.LEFT_WIDTH, null, 8, Component.translatable("datapack_edit.import_animation.client_data.layer_type"),
												List.of(LayerOptions.values()), ParseUtil::snakeToSpacedCamel, this.responder);
		
		this.inputComponentsList.importTag(tag);
	}
	
	protected void rearrangeComponents(LayerOptions layerType) {
		ScreenRectangle screenRect = this.getRectangle();
		this.inputComponentsList.clearComponents();
		
		this.baseLayerPriority = null;
		this.compositeLayerPriority = null;
		this.baseLayerMasks.clear();
		this.compositeLayerMasks.clear();
		
		this.inputComponentsList.newRow();
		this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 90, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.import_animation.client_data.layer_type")));
		this.inputComponentsList.addComponentCurrentRow(this.layerTypeCombo.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
		
		if (layerType == LayerOptions.BASE_LAYER || layerType == LayerOptions.COMPOSITE_LAYER) {
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 90, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.import_animation.client_data.priority")));
			this.inputComponentsList.addComponentCurrentRow(new ComboBox<>(this, this.font, this.inputComponentsList.nextStart(5), 124, 100, 15, HorizontalSizing.LEFT_WIDTH, null, 8, Component.translatable("datapack_edit.import_animation.client_data.layer_type"),
																			List.of(Layer.Priority.values()), ParseUtil::snakeToSpacedCamel, (priority) -> {
																				if (layerType == LayerOptions.BASE_LAYER) {
																					this.baseLayerPriority = priority;
																				} else {
																					this.compositeLayerPriority = priority;
																				}
																			}));
			
			if (layerType == LayerOptions.COMPOSITE_LAYER) {
				this.inputComponentsList.newRow();
				this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 90, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.import_animation.client_data.mask")));
				
				this.inputComponentsList.newRow();
				this.inputComponentsList.newRow();
				this.inputComponentsList.addComponentCurrentRow(
					Grid.builder(this, parentScreen.getMinecraft())
						.xy1(5, 120)
						.xy2(16, 80)
						.horizontalSizing(HorizontalSizing.LEFT_RIGHT)
						.rowHeight(21)
						.rowEditable(true)
						.transparentBackground(false)
						.addColumn(Grid.combo("living_motion", LivingMotion.ENUM_MANAGER.universalValues())
										.valueChanged((event) -> this.compositeLayerMasks.get(event.rowposition).setPackKey(event.postValue)).defaultVal(LivingMotions.IDLE))
						.addColumn(Grid.popup("joint_mask", PopupBox.JointMaskPopupBox::new)
										.valueChanged((event) -> this.compositeLayerMasks.get(event.rowposition).setPackValue(event.postValue))
										.toDisplayText((jointMask) -> ParseUtil.nullOrApply(jointMask, (m) -> JointMaskReloadListener.getKey(m).toString()))
										.width(150))
						.pressAdd((grid, button) -> {
							this.compositeLayerMasks.add(PackEntry.ofValue(LivingMotions.IDLE, null));
							int rowposition = grid.addRow();
							grid.setGridFocus(rowposition, "style");
						})
						.pressRemove((grid, button) -> {
							grid.removeRow((removedRow) -> this.compositeLayerMasks.remove(removedRow));
						})
						.build()
				);
				this.inputComponentsList.newRow();
			}
		} else if (layerType == LayerOptions.MULTILAYER) {
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(12), 90, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.import_animation.client_data.base_layer")));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(26), 90, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.import_animation.client_data.priority")));
			this.inputComponentsList.addComponentCurrentRow(new ComboBox<>(this, this.font, this.inputComponentsList.nextStart(5), 124, 100, 15, HorizontalSizing.LEFT_WIDTH, null, 8, Component.translatable("datapack_edit.import_animation.client_data.layer_type"),
																			List.of(Layer.Priority.values()), ParseUtil::snakeToSpacedCamel, (priority) -> {
																				this.baseLayerPriority = priority;
																			}));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(
				Grid.builder(this, parentScreen.getMinecraft())
					.xy1(26, 120)
					.xy2(16, 80)
					.horizontalSizing(HorizontalSizing.LEFT_RIGHT)
					.rowHeight(21)
					.rowEditable(true)
					.transparentBackground(false)
					.addColumn(Grid.combo("living_motion", LivingMotion.ENUM_MANAGER.universalValues())
							.valueChanged((event) -> this.baseLayerMasks.get(event.rowposition).setPackKey(event.postValue)).defaultVal(LivingMotions.IDLE))
					.addColumn(Grid.popup("joint_mask", PopupBox.JointMaskPopupBox::new)
							.valueChanged((event) -> this.baseLayerMasks.get(event.rowposition).setPackValue(event.postValue))
							.toDisplayText((jointMask) -> ParseUtil.nullOrApply(jointMask, (m) -> JointMaskReloadListener.getKey(m).toString()))
							.width(150))
					.pressAdd((grid, button) -> {
						this.baseLayerMasks.add(PackEntry.ofValue(LivingMotions.IDLE, null));
						int rowposition = grid.addRow();
						grid.setGridFocus(rowposition, "style");
					})
					.pressRemove((grid, button) -> {
						grid.removeRow((removedRow) -> this.baseLayerMasks.remove(removedRow));
					})
					.build()
			);
			this.inputComponentsList.newRow();
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(12), 90, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.import_animation.client_data.composite_layer")));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(26), 90, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.import_animation.client_data.priority")));
			this.inputComponentsList.addComponentCurrentRow(new ComboBox<>(this, this.font, this.inputComponentsList.nextStart(5), 124, 100, 15, HorizontalSizing.LEFT_WIDTH, null, 8, Component.translatable("datapack_edit.import_animation.client_data.layer_type"),
																			List.of(Layer.Priority.values()), ParseUtil::snakeToSpacedCamel, (priority) -> {
																				this.compositeLayerPriority = priority;
																			}));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(
				Grid.builder(this, parentScreen.getMinecraft())
					.xy1(26, 120)
					.xy2(16, 80)
					.horizontalSizing(HorizontalSizing.LEFT_RIGHT)
					.rowHeight(21)
					.rowEditable(true)
					.transparentBackground(false)
					.addColumn(Grid.combo("living_motion", LivingMotion.ENUM_MANAGER.universalValues())
									.valueChanged((event) -> this.compositeLayerMasks.get(event.rowposition).setPackKey(event.postValue)).defaultVal(LivingMotions.IDLE))
					.addColumn(Grid.popup("joint_mask", PopupBox.JointMaskPopupBox::new)
									.valueChanged((event) -> this.compositeLayerMasks.get(event.rowposition).setPackValue(event.postValue))
									.toDisplayText((jointMask) -> ParseUtil.nullOrApply(jointMask, (m) -> JointMaskReloadListener.getKey(m).toString()))
									.width(150))
					.pressAdd((grid, button) -> {
						this.compositeLayerMasks.add(PackEntry.ofValue(LivingMotions.IDLE, null));
						int rowposition = grid.addRow();
						grid.setGridFocus(rowposition, "style");
					})
					.pressRemove((grid, button) -> {
						grid.removeRow((removedRow) -> this.compositeLayerMasks.remove(removedRow));
					})
					.build()
			);
			this.inputComponentsList.newRow();
		}
		
		this.inputComponentsList.updateSize(screenRect.width() - 15, screenRect.height() - 68, screenRect.top() + 32, screenRect.bottom() - 48);
		this.inputComponentsList.setLeftPos(15);
	}
	
	@Override
	protected void init() {
		ScreenRectangle screenRect = this.getRectangle();
		
		this.inputComponentsList.updateSize(screenRect.width() - 15, screenRect.height() - 68, screenRect.top() + 32, screenRect.bottom() - 48);
		this.inputComponentsList.setLeftPos(15);
		
		this.addRenderableWidget(this.inputComponentsList);
		
		this.addRenderableWidget(Button.builder(CommonComponents.GUI_OK, (button) -> {
			try {
				this.save();
				this.onClose();
			} catch (IllegalStateException e) {
				this.minecraft.setScreen(new MessageScreen<>("Failed to save", e.getMessage(), this, (button3) -> this.minecraft.setScreen(this), 300, 70).autoCalculateHeight());
			}
		}).pos(this.width / 2 - 162, this.height - 28).size(160, 21).build());
		
		this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (button) -> {
			this.minecraft.setScreen(new MessageScreen<>("", "Do you want to quit without saving changes?", this, (button2) -> this.onClose(), (button2) -> this.minecraft.setScreen(this), 180, 70));
		}).pos(this.width / 2 + 2, this.height - 28).size(160, 21).build());
	}
	
	public void save() throws IllegalStateException {
		this.rootTag.tags.clear();
		LayerOptions layerOption = this.layerTypeCombo.getValue();
		
		if (layerOption == null) {
			throw new IllegalStateException("Layer type is not defined!");
		} else if (layerOption == LayerOptions.MULTILAYER) {
			
			if (this.baseLayerPriority == null) {
				throw new IllegalStateException("Base layer priority is not defined!");
			}
			
			if (this.compositeLayerPriority == null) {
				throw new IllegalStateException("Composite layer priority is not defined!");
			}
			
			CompoundTag multilayer = new CompoundTag();
			CompoundTag base = new CompoundTag();
			CompoundTag composite = new CompoundTag();
			
			base.putString("priority", this.baseLayerPriority.toString());
			composite.putString("priority", this.compositeLayerPriority.toString());
			
			ListTag baseMasks = new ListTag();
			int idx = 0;
			
			for (PackEntry<LivingMotion, List<JointMask>> entry : this.baseLayerMasks) {
				idx++;
				
				CompoundTag jointEntryTag = new CompoundTag();
				
				if (entry.getPackKey() == null) {
					throw new IllegalStateException(String.format("Row %s: Living motion is not defined!", idx));
				}
				
				if (entry.getPackValue() == null) {
					throw new IllegalStateException(String.format("Row %s: Joint mask is not defined!", idx));
				}
				
				jointEntryTag.putString("livingmotion", entry.getPackKey().toString());
				jointEntryTag.putString("type", JointMaskReloadListener.getKey(entry.getPackValue()).toString());
				baseMasks.add(jointEntryTag);
			}
			
			ListTag compositeMasks = new ListTag();
			idx = 0;
			
			for (PackEntry<LivingMotion, List<JointMask>> entry : this.compositeLayerMasks) {
				idx++;
				
				CompoundTag jointEntryTag = new CompoundTag();
				
				if (entry.getPackKey() == null) {
					throw new IllegalStateException(String.format("Row %s: Living motion is not defined!", idx));
				}
				
				if (entry.getPackValue() == null) {
					throw new IllegalStateException(String.format("Row %s: Joint mask is not defined!", idx));
				}
				
				jointEntryTag.putString("livingmotion", entry.getPackKey().toString());
				jointEntryTag.putString("type", JointMaskReloadListener.getKey(entry.getPackValue()).toString());
				
				compositeMasks.add(jointEntryTag);
			}
			
			base.put("masks", baseMasks);
			composite.put("masks", compositeMasks);
			multilayer.put("base", base);
			multilayer.put("composite", composite);
			this.rootTag.put("multilayer", multilayer);
		} else {
			this.rootTag.putString("layer", layerOption.toString());
			this.rootTag.putString("priority", layerOption == LayerOptions.BASE_LAYER ? this.baseLayerPriority.toString() : this.compositeLayerPriority.toString());
			
			ListTag masks = new ListTag();
			List<PackEntry<LivingMotion, List<JointMask>>> list = layerOption == LayerOptions.BASE_LAYER ? this.baseLayerMasks : this.compositeLayerMasks;
			
			//System.out.println(this.baseLayerPriority +" "+ this.compositeLayerPriority);
			//System.out.println(this.baseLayerMasks +" "+ this.compositeLayerMasks);
			
			int idx = 0;
			
			for (PackEntry<LivingMotion, List<JointMask>> entry : list) {
				idx++;
				
				CompoundTag jointEntryTag = new CompoundTag();
				
				if (entry.getPackKey() == null) {
					throw new IllegalStateException(String.format("Row %s: Living motion is not defined!", idx));
				}
				
				if (entry.getPackValue() == null) {
					throw new IllegalStateException(String.format("Row %s: Joint mask is not defined!", idx));
				}
				
				jointEntryTag.putString("livingmotion", entry.getPackKey().toString());
				jointEntryTag.putString("type", JointMaskReloadListener.getKey(entry.getPackValue()).toString());
				masks.add(jointEntryTag);
			}
			
			this.rootTag.put("masks", masks);
		}
	}
	
	@Override
	public void onClose() {
		this.minecraft.setScreen(this.parentScreen);
	}
	
	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderDirtBackground(guiGraphics);
		
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
		
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
	}
	
	@OnlyIn(Dist.CLIENT)
	public enum LayerOptions {
		BASE_LAYER, COMPOSITE_LAYER, MULTILAYER
	}
}