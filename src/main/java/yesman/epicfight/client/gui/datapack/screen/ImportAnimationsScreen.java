package yesman.epicfight.client.gui.datapack.screen;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

import com.google.common.collect.Lists;

import io.netty.util.internal.StringUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.MovementAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.model.JsonModelLoader;
import yesman.epicfight.api.utils.ParseUtil;
import yesman.epicfight.client.gui.datapack.animation.FakeAnimation;
import yesman.epicfight.client.gui.datapack.widgets.AnimatedModelPlayer;
import yesman.epicfight.client.gui.datapack.widgets.CheckBox;
import yesman.epicfight.client.gui.datapack.widgets.ComboBox;
import yesman.epicfight.client.gui.datapack.widgets.Grid;
import yesman.epicfight.client.gui.datapack.widgets.InputComponentList;
import yesman.epicfight.client.gui.datapack.widgets.ResizableComponent.HorizontalSizing;
import yesman.epicfight.client.gui.datapack.widgets.ResizableEditBox;
import yesman.epicfight.client.gui.datapack.widgets.Static;

@OnlyIn(Dist.CLIENT)
public class ImportAnimationsScreen extends Screen {
	private final Screen parentScreen;
	private final Grid grid;
	private final AnimatedModelPlayer animationModelPlayer;
	private final InputComponentList<CompoundTag> inputComponentsList;
	private final List<FakeAnimation> animationList = Lists.newLinkedList();
	
	public ImportAnimationsScreen(Screen parentScreen, Armature armature, AnimatedMesh mesh) {
		super(Component.literal("register_animation_screen"));
		
		this.parentScreen = parentScreen;
		this.animationModelPlayer = new AnimatedModelPlayer(4, 12, 0, 140, HorizontalSizing.LEFT_RIGHT, null);
		this.animationModelPlayer.setArmature(armature);
		this.animationModelPlayer.setMesh(mesh);
		
		ScreenRectangle screenRect = parentScreen.getRectangle();
		int split = screenRect.width() / 2 - 60;
		
		this.grid = Grid.builder(this, parentScreen.getMinecraft())
						.xy1(8, screenRect.top() + 14)
						.xy2(split - 10, screenRect.height() - 21)
						.rowHeight(26)
						.rowEditable(false)
						.transparentBackground(true)
						.rowpositionChanged((rowposition, values) -> {
							FakeAnimation animation = this.animationList.get(rowposition);
							this.rearrangeComponents(animation.getAnimationClass());
						})
						.addColumn(Grid.editbox("animation_name")
										.editWidgetCreated((editbox) -> editbox.setFilter((str) -> ResourceLocation.isValidResourceLocation(str)))
										.editable(true)
										.valueChanged((event) -> this.animationList.get(event.rowposition))
										.width(180))
						.build();
		
		this.inputComponentsList = new InputComponentList<>(this, 0, 0, 0, 0, 30) {
			@Override
			public void importTag(CompoundTag tag) {
				this.children().clear();
				
				String animation = tag.getString("animation");
			}
		};
	}
	
	public void rearrangeComponents(Class<?> animationClass) {
		ScreenRectangle screenRect = this.getRectangle();
		
		this.inputComponentsList.children().clear();
		this.inputComponentsList.newRow();
		
		this.animationModelPlayer.clearAnimations();
		this.animationModelPlayer.addAnimationToPlay(this.animationList.get(this.grid.getRowposition()));
		
		ComboBox<Class<?>> comboBox = new ComboBox<>(this, this.font, 0, 124, 100, 15, HorizontalSizing.LEFT_WIDTH, null, 8,
														Component.translatable("datapack_edit.import_animation.type"), List.of(StaticAnimation.class, MovementAnimation.class, AttackAnimation.class), (clz) -> clz.getSimpleName(),
														(clz) -> {});
		
		comboBox.setValue(animationClass);
		comboBox.setResponder((clz) -> this.rearrangeComponents(clz));
		
		this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 60, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.import_animation.type")));
		this.inputComponentsList.addComponentCurrentRow(comboBox.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
		
		if (animationClass == StaticAnimation.class || animationClass == MovementAnimation.class) {
			final ResizableEditBox convertTime = new ResizableEditBox(this.font, 0, 0, 35, 15, Component.translatable("datapack_edit.import_animation.convert_time"), HorizontalSizing.LEFT_WIDTH, null);
			final CheckBox repeat = new CheckBox(this.font, 0, 60, 0, 10, HorizontalSizing.LEFT_WIDTH, null, false, Component.literal(""), (value) -> {});
			
			convertTime.setResponder((input) -> {
				float f = StringUtil.isNullOrEmpty(input) ? 0 : Float.valueOf(input);
				this.animationList.get(this.grid.getRowposition());
			});
			
			convertTime.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsable(context, Float::parseFloat));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 60, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.import_animation.convert_time")));
			this.inputComponentsList.addComponentCurrentRow(convertTime.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 60, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.import_animation.repeat")));
			this.inputComponentsList.addComponentCurrentRow(repeat.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
		} else if (animationClass == AttackAnimation.class) {
			final ResizableEditBox convertTime = new ResizableEditBox(this.font, 0, 0, 35, 15, Component.translatable("datapack_edit.import_animation.convert_time"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox antic = new ResizableEditBox(this.font, 0, 0, 35, 15, Component.translatable("datapack_edit.import_animation.antic"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox preDelay = new ResizableEditBox(this.font, 0, 0, 35, 15, Component.translatable("datapack_edit.import_animation.preDelay"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox contact = new ResizableEditBox(this.font, 0, 0, 35, 15, Component.translatable("datapack_edit.import_animation.contact"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox recovery = new ResizableEditBox(this.font, 0, 0, 35, 15, Component.translatable("datapack_edit.import_animation.recovery"), HorizontalSizing.LEFT_WIDTH, null);
			
			convertTime.setResponder((input) -> {
				float f = StringUtil.isNullOrEmpty(input) ? 0 : Float.valueOf(input);
				this.animationList.get(this.grid.getRowposition());
			});
			
			/*
			 * 
			 */
			
			convertTime.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsable(context, Float::parseFloat));
			antic.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsable(context, Float::parseFloat));
			preDelay.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsable(context, Float::parseFloat));
			contact.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsable(context, Float::parseFloat));
			recovery.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsable(context, Float::parseFloat));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 60, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.import_animation.convert_time")));
			this.inputComponentsList.addComponentCurrentRow(convertTime.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 60, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.import_animation.antic")));
			this.inputComponentsList.addComponentCurrentRow(antic.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 60, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.import_animation.pre_delay")));
			this.inputComponentsList.addComponentCurrentRow(preDelay.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 60, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.import_animation.contact")));
			this.inputComponentsList.addComponentCurrentRow(contact.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 60, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.import_animation.recovery")));
			this.inputComponentsList.addComponentCurrentRow(recovery.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
			
			// float convertTime, float antic, float preDelay, float contact, float recovery, InteractionHand hand, @Nullable Collider collider, Joint colliderJoint, String path
		}
		
		this.inputComponentsList.newRow();
		this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 60, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.import_animation.preview")));
		this.inputComponentsList.newRow();
		this.inputComponentsList.newRow();
		this.inputComponentsList.newRow();
		
		int split = screenRect.width() / 2 - 60;
		
		this.inputComponentsList.addComponentCurrentRow(this.animationModelPlayer);
		this.inputComponentsList.newRow();
		this.inputComponentsList.newRow();
		
		this.inputComponentsList.updateSize(screenRect.width() - (split + 8), screenRect.height() - 21, screenRect.top() + 14, screenRect.bottom() - 36);
		this.inputComponentsList.setLeftPos(split + 2);
	}
	
	@Override
	protected void init() {
		ScreenRectangle screenRect = this.getRectangle();
		int split = screenRect.width() / 2 - 60;
		
		this.grid.updateSize(split - 10, screenRect.height() - 21, screenRect.top() + 14, screenRect.bottom() - 36);
		this.grid.setLeftPos(8);
		this.grid.resize(screenRect);
		this.inputComponentsList.updateSize(screenRect.width() - (split + 8), screenRect.height() - 21, screenRect.top() + 14, screenRect.bottom() - 36);
		this.inputComponentsList.setLeftPos(split + 2);
		
		this.addRenderableWidget(this.grid);
		this.addRenderableWidget(this.inputComponentsList);
		
		this.addRenderableWidget(Button.builder(CommonComponents.GUI_OK, (button) -> {
			this.onClose();
		}).pos(this.width / 2 - 162, this.height - 28).size(160, 21).build());
		this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (button) -> {
			this.onClose();
		}).pos(this.width / 2 + 2, this.height - 28).size(160, 21).build());
	}
	
	@Override
	public void tick() {
		this.grid.tick();
		this.inputComponentsList.tick();
	}
	
	@Override
	public void onClose() {
		this.animationModelPlayer.onDestroy();
		this.minecraft.setScreen(this.parentScreen);
	}
	
	@Override
	public void onFilesDrop(List<Path> paths) {
		this.minecraft.setScreen(new MessageScreen<>("", "Enter the mod id", this,
			(modid) -> {
				for (Path path : paths) {
					try {
						File file = path.toFile();
						InputStream stream = new FileInputStream(file);
						JsonModelLoader jsonLoader = new JsonModelLoader(stream);
						this.animationList.add(new FakeAnimation(this.animationModelPlayer.getArmature(), jsonLoader.loadAnimationClip(this.animationModelPlayer.getArmature())));
						this.grid.addRowWithDefaultValues("animation_name", modid + ":" + this.animationModelPlayer.getArmature().toString() + "/" + file.getName());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				this.minecraft.setScreen(this);
			},
			(button) -> this.minecraft.setScreen(this),
			new ResizableEditBox(this.minecraft.font, 0, 0, 0, 16, Component.literal("datapack_edit.import_animation.input"), null, null), 120, 80));
	}
	
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
		if (this.animationModelPlayer.mouseDragged(mouseX, mouseY, button, dx, dy)) {
			return true;
		}
		
		return super.mouseDragged(mouseX, mouseY, button, dx, dy);
	}
	
	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderDirtBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
	}
}