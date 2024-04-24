package yesman.epicfight.client.gui.datapack.screen;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.netty.util.internal.StringUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.types.datapack.FakeAnimation;
import yesman.epicfight.api.utils.ParseUtil;
import yesman.epicfight.client.gui.datapack.widgets.ComboBox;
import yesman.epicfight.client.gui.datapack.widgets.InputComponentList;
import yesman.epicfight.client.gui.datapack.widgets.ResizableComponent.HorizontalSizing;
import yesman.epicfight.client.gui.datapack.widgets.ResizableEditBox;
import yesman.epicfight.client.gui.datapack.widgets.Static;

@OnlyIn(Dist.CLIENT)
public class AttackAnimationPropertyScreen extends Screen {
	private final InputComponentList<CompoundTag> inputComponentsList;
	private final Screen parentScreen;
	private final FakeAnimation animation;
	private final ResizableEditBox startTime;
	private final ResizableEditBox endTime;
	private final ComboBox<Joint> joint;
	private final ComboBox<InteractionHand> hand;
	
	protected AttackAnimationPropertyScreen(Screen parentScreen, FakeAnimation animation, List<Joint> joints) {
		super(Component.translatable("datapack_edit.import_animation.client_data"));
		
		this.minecraft = parentScreen.getMinecraft();
		this.parentScreen = parentScreen;
		this.font = this.minecraft.font;
		this.animation = animation;
		
		this.inputComponentsList = new InputComponentList<> (this, 0, 0, 0, 0, 30) {
			@Override
			public void importTag(CompoundTag tag) {
				this.clearComponents();
				this.setComponentsActive(true);
			}
		};
		
		ScreenRectangle screenRect = this.getRectangle();
		
		this.startTime = new ResizableEditBox(this.font, 0, 35, 0, 15, Component.translatable("datapack_edit.import_animation.antic"), HorizontalSizing.LEFT_WIDTH, null);
		this.endTime = new ResizableEditBox(this.font, 0, 35, 0, 15, Component.translatable("datapack_edit.import_animation.preDelay"), HorizontalSizing.LEFT_WIDTH, null);
		this.joint = new ComboBox<>(this, this.font, 0, 124, 100, 15, HorizontalSizing.LEFT_WIDTH, null, 8, Component.translatable("datapack_edit.import_animation.joint"),
									joints, Joint::getName, (joint) -> {});
		this.hand = new ComboBox<>(this, this.font, 0, 124, 100, 15, HorizontalSizing.LEFT_WIDTH, null, 8,
									Component.translatable("datapack_edit.import_animation.hand"), List.of(InteractionHand.MAIN_HAND, InteractionHand.OFF_HAND),
									ParseUtil::snakeToSpacedCamel, (hand) -> {});
		
		this.startTime.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsable(context, Double::parseDouble));
		this.endTime.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsable(context, Double::parseDouble));
		
		this.inputComponentsList.newRow();
		this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 80, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.model_player.trail")));
		this.inputComponentsList.newRow();
		this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(12), 80, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.import_animation.client_data.start_time")));
		this.inputComponentsList.addComponentCurrentRow(this.startTime.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
		this.inputComponentsList.newRow();
		this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(12), 80, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.import_animation.client_data.end_time")));
		this.inputComponentsList.addComponentCurrentRow(this.endTime.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
		this.inputComponentsList.newRow();
		this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(12), 80, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.import_animation.client_data.joint")));
		this.inputComponentsList.addComponentCurrentRow(this.joint.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
		this.inputComponentsList.newRow();
		this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(12), 80, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.import_animation.hand")));
		this.inputComponentsList.addComponentCurrentRow(this.hand.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
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
			this.minecraft.setScreen(new MessageScreen<>("", "Do you want to quit without saving changes?", this,
				(button2) -> {
					this.onClose();
				}, (button2) -> {
					this.minecraft.setScreen(this);
				}, 180, 70));
		}).pos(this.width / 2 + 2, this.height - 28).size(160, 21).build());
	}
	
	@Override
	public void onClose() {
		this.minecraft.setScreen(this.parentScreen);
	}
	
	public void save() throws IllegalStateException {
		this.animation.getPropertiesJson().asMap().clear();
		
		JsonObject trailObj = new JsonObject();
		trailObj.addProperty("start_time", Double.parseDouble(this.startTime.getValue()));
		trailObj.addProperty("end_time", Double.parseDouble(this.endTime.getValue()));
		trailObj.addProperty("joint", this.joint.getValue().toString());
		trailObj.addProperty("item_skin_hand", this.hand.getValue().toString());
		
		JsonArray trailArray = new JsonArray();
		trailArray.add(trailObj);
		
		this.animation.getPropertiesJson().add("trail_effects", trailArray);
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