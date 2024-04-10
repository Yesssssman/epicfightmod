package yesman.epicfight.client.gui.datapack.screen;

import java.io.File;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationClip;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.client.gui.datapack.widgets.AnimatedModelPlayer;
import yesman.epicfight.main.EpicFightMod;

@OnlyIn(Dist.CLIENT)
public class SelectAnimationScreen extends Screen {
	private static final Map<AnimationClip, File> USER_IMPORT_ANIMATIONS = Maps.newHashMap();
	
	public static final void addUserImportAnimation(AnimationClip animation, File file) {
		USER_IMPORT_ANIMATIONS.put(animation, file);
	}
	
	private final Screen parentScreen;
	private final AnimationList animationList;
	private final AnimatedModelPlayer animationModelPlayer;
	private final Consumer<StaticAnimation> selectCallback;
	private final Map<ResourceLocation, StaticAnimation> registeredAnimations;
	
	public SelectAnimationScreen(Screen parentScreen, Consumer<StaticAnimation> selectCallback, Predicate<StaticAnimation> filter, Armature armature, AnimatedMesh mesh) {
		super(Component.translatable("gui.epicfight.select.animations"));
		
		this.registeredAnimations = AnimationManager.getInstance().getAnimations(filter);
		this.animationModelPlayer = new AnimatedModelPlayer(10, 20, 36, 60, null, null);
		this.animationModelPlayer.setArmature(armature);
		this.animationModelPlayer.setMesh(mesh);
		
		this.animationList = new AnimationList(parentScreen.getMinecraft(), this.width, this.height, 36, this.height - 16, 21);
		this.animationList.setRenderTopAndBottom(false);
		this.parentScreen = parentScreen;
		this.selectCallback = selectCallback;
	}
	
	@Override
	protected void init() {
		int split = this.width / 2 - 80;
		
		this.animationModelPlayer.setWidth(split - 10);
		this.animationModelPlayer.setHeight(this.height - 68);
		this.animationModelPlayer.resize(null);
		
		this.animationList.updateSize(this.width - split, this.height, 36, this.height - 32);
		this.animationList.setLeftPos(split);
		
		EditBox editBox = new EditBox(this.minecraft.font, this.width / 2, 12, this.width / 2 - 12, 16, Component.literal(EpicFightMod.MODID + ":"));
		editBox.setResponder(this.animationList::applyFilter);
		
		this.addRenderableWidget(editBox);
		this.addRenderableWidget(Button.builder(Component.translatable("datapack_edit.import_animation"), (button) -> {
			Minecraft.getInstance().setScreen(new ImportAnimationsScreen(this, this.animationModelPlayer.getArmature(), this.animationModelPlayer.getMesh()));
		}).pos(10, 10).size(100, 21).build());
		
		this.addRenderableWidget(this.animationModelPlayer);
		this.addRenderableWidget(this.animationList);
		
		this.addRenderableWidget(Button.builder(CommonComponents.GUI_OK, (button) -> {
			if (this.animationList.getSelected() != null) {
				this.selectCallback.accept(this.animationList.getSelected().animation);
			}
			
			this.onClose();
		}).pos(this.width / 2 - 162, this.height - 28).size(160, 21).build());
		this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (button) -> {
			this.onClose();
		}).pos(this.width / 2 + 2, this.height - 28).size(160, 21).build());
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
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderDirtBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
	}
	
	@OnlyIn(Dist.CLIENT)
	class AnimationList extends ObjectSelectionList<AnimationList.AnimationEntry> {
		public AnimationList(Minecraft minecraft, int width, int height, int y0, int y1, int itemHeight) {
			super(minecraft, width, height, y0, y1, itemHeight);
			
			SelectAnimationScreen.this.registeredAnimations.values().stream().sorted((a1, a2) -> Integer.compare(a1.getId(), a2.getId())).map(AnimationEntry::new).forEach(this::addEntry);
		}
		
		@Override
		public void setSelected(@Nullable AnimationEntry selEntry) {
			super.setSelected(selEntry);
			
			SelectAnimationScreen.this.animationModelPlayer.clearAnimations();
			SelectAnimationScreen.this.animationModelPlayer.addAnimationToPlay(selEntry.animation);
		}
		
		@Override
		public int getRowWidth() {
			return this.width;
		}
		
		@Override
		protected int getScrollbarPosition() {
			return this.x1 - 6;
		}
		
		public void applyFilter(String keyward) {
			this.setScrollAmount(0.0D);
			this.children().clear();
			
			SelectAnimationScreen.this.registeredAnimations.values().stream().filter((animation) -> StringUtil.isNullOrEmpty(keyward) ? true : animation.getRegistryName().toString().contains(keyward))
																	.map(AnimationEntry::new).sorted((a1, a2) -> Integer.compare(a1.animation.getId(), a2.animation.getId())).forEach(this::addEntry);
		}
		
		@OnlyIn(Dist.CLIENT)
		class AnimationEntry extends ObjectSelectionList.Entry<AnimationList.AnimationEntry> {
			private final StaticAnimation animation;
			
			public AnimationEntry(StaticAnimation animation) {
				this.animation = animation;
			}
			
			@Override
			public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks) {
				guiGraphics.drawString(SelectAnimationScreen.this.minecraft.font, this.animation.getRegistryName().toString(), left + 5, top + 5, 16777215, false);
			}
			
			@Override
			public Component getNarration() {
				return Component.translatable("narrator.select");
			}
			
			@Override
			public boolean mouseClicked(double mouseX, double mouseY, int button) {
				if (button == 0) {
					if (AnimationList.this.getSelected() == this) {
						SelectAnimationScreen.this.selectCallback.accept(this.animation);
						SelectAnimationScreen.this.minecraft.setScreen(SelectAnimationScreen.this.parentScreen);
						return true;
					}
					
					AnimationList.this.setSelected(this);
					
					return true;
				} else {
					return false;
				}
			}
		}
	}
}
