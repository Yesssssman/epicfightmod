package yesman.epicfight.client.gui.datapack.screen;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;

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
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.animation.types.datapack.ClipHoldingAnimation;
import yesman.epicfight.api.animation.types.datapack.FakeAnimation;
import yesman.epicfight.api.client.animation.ClientAnimationDataReader;
import yesman.epicfight.api.client.animation.property.TrailInfo;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.collider.MultiOBBCollider;
import yesman.epicfight.api.collider.OBBCollider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.model.JsonModelLoader;
import yesman.epicfight.api.utils.InstantiateInvoker;
import yesman.epicfight.api.utils.ParseUtil;
import yesman.epicfight.client.gui.datapack.widgets.AnimatedModelPlayer;
import yesman.epicfight.client.gui.datapack.widgets.CheckBox;
import yesman.epicfight.client.gui.datapack.widgets.ComboBox;
import yesman.epicfight.client.gui.datapack.widgets.Grid;
import yesman.epicfight.client.gui.datapack.widgets.InputComponentList;
import yesman.epicfight.client.gui.datapack.widgets.PopupBox;
import yesman.epicfight.client.gui.datapack.widgets.ResizableComponent.HorizontalSizing;
import yesman.epicfight.client.gui.datapack.widgets.ResizableEditBox;
import yesman.epicfight.client.gui.datapack.widgets.RowSpliter;
import yesman.epicfight.client.gui.datapack.widgets.Static;
import yesman.epicfight.client.gui.datapack.widgets.SubScreenOpenButton;
import yesman.epicfight.gameasset.ColliderPreset;

@OnlyIn(Dist.CLIENT)
public class ImportAnimationsScreen extends Screen {
	private final SelectAnimationScreen caller;
	private final Grid animationGrid;
	private final AnimatedModelPlayer animationModelPlayer;
	private final List<FakeAnimation> fakeAnimations = Lists.newArrayList();
	private InputComponentList<FakeAnimation> inputComponentsList;
	private ComboBox<FakeAnimation.AnimationType> animationType;
	private Consumer<FakeAnimation.AnimationType> responder;
	
	public ImportAnimationsScreen(SelectAnimationScreen caller, Armature armature, AnimatedMesh mesh) {
		super(Component.literal("register_animation_screen"));
		
		this.fakeAnimations.addAll(USER_ANIMATIONS.values().stream().map(PackEntry::getPackKey).map(FakeAnimation::deepCopy).toList());
		
		this.caller = caller;
		this.animationModelPlayer = new AnimatedModelPlayer(10, 15, 0, 140, HorizontalSizing.LEFT_RIGHT, null, armature, mesh);
		this.animationModelPlayer.setCollider(ColliderPreset.FIST);
		
		this.minecraft = caller.getMinecraft();
		this.font = caller.getMinecraft().font;
		
		ScreenRectangle screenRect = caller.getRectangle();
		int split = screenRect.width() / 2 - 60;
		
		this.animationGrid = Grid.builder(this, caller.getMinecraft())
						.xy1(8, screenRect.top() + 14)
						.xy2(split - 10, screenRect.height() - 21)
						.rowHeight(26)
						.rowEditable(false)
						.transparentBackground(true)
						.rowpositionChanged((rowposition, values) -> {
							this.inputComponentsList.importTag(this.fakeAnimations.get(rowposition));
							this.animationModelPlayer.setTrailInfo();
							
							if (this.fakeAnimations.get(rowposition).getAnimationClass() == FakeAnimation.AnimationType.ATTACK) {
								this.inputComponentsList.getComponent(7, 1)._setActive(false);
								this.inputComponentsList.getComponent(8, 1)._setActive(false);
								this.inputComponentsList.getComponent(9, 1)._setActive(false);
								this.inputComponentsList.getComponent(10, 1)._setActive(false);
								this.inputComponentsList.getComponent(11, 1)._setActive(false);
								this.inputComponentsList.getComponent(12, 1)._setActive(false);
								this.inputComponentsList.getComponent(13, 1)._setActive(false);
								this.inputComponentsList.getComponent(14, 2)._setActive(false);
								this.inputComponentsList.getComponent(14, 4)._setActive(false);
								this.inputComponentsList.getComponent(14, 6)._setActive(false);
								this.inputComponentsList.getComponent(15, 2)._setActive(false);
								this.inputComponentsList.getComponent(15, 4)._setActive(false);
								this.inputComponentsList.getComponent(15, 6)._setActive(false);
								this.inputComponentsList.getComponent(16, 1)._setActive(false);
								
								if (this.fakeAnimations.get(rowposition).getPropertiesJson().has("trail_effects")) {
									JsonArray trailArr = this.fakeAnimations.get(rowposition).getPropertiesJson().getAsJsonArray("trail_effects");
									TrailInfo[] trailInfos = new TrailInfo[trailArr.size()];
									
									int i = 0;
									
									for (JsonElement element : trailArr) {
										JsonObject trailObj = element.getAsJsonObject();
										
										TrailInfo.Builder builder = TrailInfo.builder()
												.time(trailObj.get("start_time").getAsFloat(), trailObj.get("end_time").getAsFloat())
												.joint(trailObj.get("joint").getAsString())
												.itemSkinHand(InteractionHand.valueOf(trailObj.get("item_skin_hand").getAsString().toUpperCase(Locale.ROOT)));
		
										if (trailObj.has("lifetime")) {
											builder.lifetime(trailObj.get("lifetime").getAsInt());
										}
										
										if (trailObj.has("interpolations")) {
											builder.interpolations(trailObj.get("interpolations").getAsInt());
										}
										
										trailInfos[i] = AttackAnimationPropertyScreen.DEFAULT_TRAIL.overwrite(builder.create());
										i++;
									}
									
									this.animationModelPlayer.setTrailInfo(trailInfos);
								}
							}
						})
						.addColumn(Grid.editbox("animation_name")
										.editWidgetCreated((editbox) -> editbox.setFilter(ResourceLocation::isValidResourceLocation))
										.editable(true)
										.valueChanged((event) -> this.fakeAnimations.get(event.rowposition).setParameter("path", event.postValue))
										.width(180))
						.build();
		
		this.inputComponentsList = new InputComponentList<>(this, 0, 0, 0, 0, 30) {
			@Override
			public void importTag(FakeAnimation fakeAnim) {
				this.clearComponents();
				
				ImportAnimationsScreen.this.rearrangeComponents(fakeAnim.getAnimationClass());
				this.setComponentsActive(true);
				
				if (fakeAnim.getAnimationClass() != null) {
				switch (fakeAnim.getAnimationClass()) {
				case STATIC, MOVEMENT:
					ImportAnimationsScreen.this.animationType.setResponder(null);
					
					this.setDataBindingComponenets(new Object[] {
						fakeAnim.getAnimationClass(),
						ParseUtil.nullParam(fakeAnim.getParameter("convertTime")),
						fakeAnim.getParameter("isRepeat")
					});
					
					ImportAnimationsScreen.this.animationType.setResponder(ImportAnimationsScreen.this.responder);
					
					break;
				case ATTACK:
					
					CompoundTag colliderTag = new CompoundTag();
					Collider collider = (Collider)fakeAnim.getParameter("collider");
					
					if (collider != null) {
						collider.serialize(colliderTag);
					}
					
					ImportAnimationsScreen.this.animationType.setResponder(null);
					
					Grid.PackImporter packImporter = new Grid.PackImporter();
					ListTag phasesTag = fakeAnim.getParameter("phases");
					
					for (int i = 0; i < phasesTag.size(); i++) {
						packImporter.newRow();
						packImporter.newValue("phase", String.format("Phase%s", ++i));
					}
					
					this.setDataBindingComponenets(new Object[] {
						fakeAnim.getAnimationClass(),
						ParseUtil.nullParam(fakeAnim.getParameter("convertTime")),
						packImporter,
						ParseUtil.nullParam(fakeAnim.getParameter("antic")),
						ParseUtil.nullParam(fakeAnim.getParameter("preDelay")),
						ParseUtil.nullParam(fakeAnim.getParameter("contact")),
						ParseUtil.nullParam(fakeAnim.getParameter("recovery")),
						fakeAnim.getParameter("hand"),
						null,
						ParseUtil.nullParam(colliderTag.get("number")),
						colliderTag.contains("center") ? ParseUtil.nullParam(ParseUtil.nullOrToString(colliderTag.getList("center", Tag.TAG_DOUBLE).get(0), Tag::getAsString)) : "",
						colliderTag.contains("center") ? ParseUtil.nullParam(ParseUtil.nullOrToString(colliderTag.getList("center", Tag.TAG_DOUBLE).get(1), Tag::getAsString)) : "",
						colliderTag.contains("center") ? ParseUtil.nullParam(ParseUtil.nullOrToString(colliderTag.getList("center", Tag.TAG_DOUBLE).get(2), Tag::getAsString)) : "",
						colliderTag.contains("size") ? ParseUtil.nullParam(ParseUtil.nullOrToString(colliderTag.getList("size", Tag.TAG_DOUBLE).get(0), Tag::getAsString)) : "",
						colliderTag.contains("size") ? ParseUtil.nullParam(ParseUtil.nullOrToString(colliderTag.getList("size", Tag.TAG_DOUBLE).get(1), Tag::getAsString)) : "",
						colliderTag.contains("size") ? ParseUtil.nullParam(ParseUtil.nullOrToString(colliderTag.getList("size", Tag.TAG_DOUBLE).get(2), Tag::getAsString)) : "",
						fakeAnim.getParameter("colliderJoint")
					});
					
					ImportAnimationsScreen.this.animationType.setResponder(ImportAnimationsScreen.this.responder);
					
					if (fakeAnim.getPropertiesJson().has("trail_effects")) {
						JsonArray trailList = fakeAnim.getPropertiesJson().get("trail_effects").getAsJsonArray();
						TrailInfo[] trailArr = new TrailInfo[trailList.size()];
						
						int i = 0;
						
						for (JsonElement element : trailList) {
							JsonObject trailObj = element.getAsJsonObject();
							TrailInfo.Builder builder = TrailInfo.builder()
																.time(trailObj.get("start_time").getAsFloat(), trailObj.get("end_time").getAsFloat())
																.joint(trailObj.get("joint").getAsString())
																.itemSkinHand(InteractionHand.valueOf(trailObj.get("item_skin_hand").getAsString().toUpperCase(Locale.ROOT)));
							
							if (trailObj.has("lifetime")) {
								builder.lifetime(trailObj.get("lifetime").getAsInt());
							}
							
							if (trailObj.has("interpolations")) {
								builder.interpolations(trailObj.get("interpolations").getAsInt());
							}
							
							trailArr[i] = AttackAnimationPropertyScreen.DEFAULT_TRAIL.overwrite(builder.create());
							i++;
						}
						
						ImportAnimationsScreen.this.animationModelPlayer.setTrailInfo(trailArr);
					}
					
					break;
				default:
					ImportAnimationsScreen.this.animationType.setValue(null);
					break;
				}
				} else {
					ImportAnimationsScreen.this.animationType.setResponder(null);
					ImportAnimationsScreen.this.animationType.setValue(null);
					ImportAnimationsScreen.this.animationType.setResponder(ImportAnimationsScreen.this.responder);
				}
			}
		};
		
		if (this.fakeAnimations.isEmpty()) {
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 5, 60, 15, HorizontalSizing.LEFT_RIGHT, null, Component.translatable("datapack_edit.import_animation.place_tooltip")));
		}
		
		this.responder = (clz) -> {
			if (clz != null) {
				FakeAnimation fakeAnim = this.fakeAnimations.get(this.animationGrid.getRowposition());
				fakeAnim.setAnimationClass(clz);
				this.rearrangeComponents(clz);
			}
		};
		
		this.animationType = new ComboBox<>(this, this.font, 0, 124, 100, 15, HorizontalSizing.LEFT_WIDTH, null, 8, Component.translatable("datapack_edit.import_animation.type"),
											List.of(FakeAnimation.AnimationType.values()), (type) -> type.toString(), this.responder);
		
		for (PackEntry<FakeAnimation, ClipHoldingAnimation> entry : USER_ANIMATIONS.values()) {
			this.animationGrid.addRowWithDefaultValues("animation_name", entry.getPackKey().getParameter("path"));
		}
	}
	
	public void rearrangeComponents(FakeAnimation.AnimationType animationClass) {
		ScreenRectangle screenRect = this.getRectangle();
		
		this.animationModelPlayer.setCollider(null);
		this.animationModelPlayer.setColliderJoint(null);
		this.animationModelPlayer.clearAnimations();
		this.animationModelPlayer.addAnimationToPlay(this.fakeAnimations.get(this.animationGrid.getRowposition()));
		
		this.inputComponentsList.children().clear();
		this.inputComponentsList.newRow();
		this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 85, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.import_animation.type")));
		this.inputComponentsList.addComponentCurrentRow(this.animationType.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
		
		if (animationClass != null) {
		switch (animationClass) {
		case STATIC, MOVEMENT:
		{
			final ResizableEditBox convertTime = new ResizableEditBox(this.font, 0, 35, 0, 15, Component.translatable("datapack_edit.import_animation.convert_time"), HorizontalSizing.LEFT_WIDTH, null);
			final CheckBox repeat = new CheckBox(this.font, 0, 60, 0, 10, HorizontalSizing.LEFT_WIDTH, null, false, Component.literal(""), (value) -> this.fakeAnimations.get(this.animationGrid.getRowposition()).setParameter("isRepeat", value));
			
			convertTime.setResponder((input) -> {
				Object f = StringUtil.isNullOrEmpty(input) ? null : Float.valueOf(input);
				this.fakeAnimations.get(this.animationGrid.getRowposition()).setParameter("convertTime", f);
			});
			
			convertTime.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsable(context, Float::parseFloat));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 85, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.import_animation.convert_time")));
			this.inputComponentsList.addComponentCurrentRow(convertTime.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 85, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.import_animation.repeat")));
			this.inputComponentsList.addComponentCurrentRow(repeat.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 85, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.import_animation.client_data")));
			this.inputComponentsList.addComponentCurrentRow(SubScreenOpenButton.builder().subScreen(() -> {
				return new StaticAnimationPropertyScreen(this, this.fakeAnimations.get(this.animationGrid.getRowposition()));
			}).bounds(this.inputComponentsList.nextStart(4), 0, 15, 15).build());
		}
		break;
		case ATTACK:
		{
			this.animationModelPlayer.setCollider(ColliderPreset.FIST);
			
			final ResizableEditBox convertTime = new ResizableEditBox(this.font, 0, 35, 0, 15, Component.translatable("datapack_edit.import_animation.convert_time"), HorizontalSizing.LEFT_WIDTH, null);
			
			convertTime.setResponder((input) -> {
				Object f = StringUtil.isNullOrEmpty(input) ? null : Float.valueOf(input);
				this.fakeAnimations.get(this.animationGrid.getRowposition()).setParameter("convertTime", f);
			});
			convertTime.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsable(context, Float::parseFloat));
			
			final ResizableEditBox antic = new ResizableEditBox(this.font, 0, 35, 0, 15, Component.translatable("datapack_edit.import_animation.antic"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox preDelay = new ResizableEditBox(this.font, 0, 35, 0, 15, Component.translatable("datapack_edit.import_animation.preDelay"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox contact = new ResizableEditBox(this.font, 0, 35, 0, 15, Component.translatable("datapack_edit.import_animation.contact"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox recovery = new ResizableEditBox(this.font, 0, 35, 0, 15, Component.translatable("datapack_edit.import_animation.recovery"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox colliderCount = new ResizableEditBox(this.font, 0, 40, 0, 15, Component.translatable("datapack_edit.collider.count"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox colliderCenterX = new ResizableEditBox(this.font, 0, 35, 0, 15, Component.translatable("datapack_edit.collider.center.x"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox colliderCenterY = new ResizableEditBox(this.font, 0, 35, 0, 15, Component.translatable("datapack_edit.collider.center.y"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox colliderCenterZ = new ResizableEditBox(this.font, 0, 35, 0, 15, Component.translatable("datapack_edit.collider.center.z"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox colliderSizeX = new ResizableEditBox(this.font, 0, 35, 0, 15, Component.translatable("datapack_edit.collider.size.x"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox colliderSizeY = new ResizableEditBox(this.font, 0, 35, 0, 15, Component.translatable("datapack_edit.collider.size.y"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox colliderSizeZ = new ResizableEditBox(this.font, 0, 35, 0, 15, Component.translatable("datapack_edit.collider.size.z"), HorizontalSizing.LEFT_WIDTH, null);
			
			final ComboBox<Joint> colliderJoint = new ComboBox<>(this, this.font, 0, 124, 100, 15, HorizontalSizing.LEFT_WIDTH, null, 8, Component.translatable("datapack_edit.import_animation.joint"),
																	this.animationModelPlayer.getArmature().getRootJoint().getAllJoints(), Joint::getName, null);
			final ComboBox<InteractionHand> interactionHand = new ComboBox<>(this, this.font, 0, 124, 100, 15, HorizontalSizing.LEFT_WIDTH, null, 8, Component.translatable("datapack_edit.import_animation.hand"),
																				List.of(InteractionHand.MAIN_HAND, InteractionHand.OFF_HAND), ParseUtil::snakeToSpacedCamel, null);
			final PopupBox.ColliderPopupBox colliderPopup = new PopupBox.ColliderPopupBox(this, font, 0, 30, 130, 15, HorizontalSizing.LEFT_RIGHT, null, Component.translatable("datapack_edit.collider"), null);
			
			antic._setActive(false);
			preDelay._setActive(false);
			contact._setActive(false);
			recovery._setActive(false);
			colliderCount._setActive(false);
			colliderCenterX._setActive(false);
			colliderCenterY._setActive(false);
			colliderCenterZ._setActive(false);
			colliderSizeX._setActive(false);
			colliderSizeY._setActive(false);
			colliderSizeZ._setActive(false);
			colliderJoint._setActive(false);
			interactionHand._setActive(false);
			colliderPopup._setActive(false);
			
			final Grid phasesGrid = Grid.builder(this, this.minecraft)
										.xy1(8, 0)
										.xy2(12, 80)
										.horizontalSizing(HorizontalSizing.LEFT_RIGHT)
										.rowHeight(26)
										.rowEditable(true)
										.transparentBackground(false)
										.rowpositionChanged((rowposition, values) -> {
											FakeAnimation fakeAnimation = this.fakeAnimations.get(this.animationGrid.getRowposition());
											ListTag phases = fakeAnimation.getParameter("phases");
											CompoundTag tag = phases.getCompound(rowposition);
											
											antic.setValue(tag.contains("antic") ? ParseUtil.valueOfOmittingType(tag.get("antic").getAsString()) : "");
											preDelay.setValue(tag.contains("preDelay") ? ParseUtil.valueOfOmittingType(tag.get("preDelay").getAsString()) : "");
											contact.setValue(tag.contains("contact") ? ParseUtil.valueOfOmittingType(tag.get("contact").getAsString()) : "");
											recovery.setValue(tag.contains("recovery") ? ParseUtil.valueOfOmittingType(tag.get("recovery").getAsString()) : "");
											
											if (tag.contains("joint")) {
												String armature$joint = tag.getString("joint");
												String joinName = armature$joint.substring(armature$joint.lastIndexOf('.') + 1);
												colliderJoint.setValue(this.animationModelPlayer.getArmature().searchJointByName(joinName));
											} else {
												colliderJoint.setValue(null);
											}
											
											interactionHand.setValue(tag.contains("hand") ? InteractionHand.valueOf(tag.getString("hand")) : null);
											
											if (tag.contains("collider")) {
												CompoundTag colliderTag = tag.getCompound("collider");
												colliderCount.setValue(ParseUtil.valueOfOmittingType(colliderTag.get("number")));
												colliderCenterX.setValue(ParseUtil.valueOfOmittingType(colliderTag.getList("center", Tag.TAG_DOUBLE).get(0)));
												colliderCenterY.setValue(ParseUtil.valueOfOmittingType(colliderTag.getList("center", Tag.TAG_DOUBLE).get(1)));
												colliderCenterZ.setValue(ParseUtil.valueOfOmittingType(colliderTag.getList("center", Tag.TAG_DOUBLE).get(2)));
												colliderSizeX.setValue(ParseUtil.valueOfOmittingType(colliderTag.getList("size", Tag.TAG_DOUBLE).get(0)));
												colliderSizeY.setValue(ParseUtil.valueOfOmittingType(colliderTag.getList("size", Tag.TAG_DOUBLE).get(1)));
												colliderSizeZ.setValue(ParseUtil.valueOfOmittingType(colliderTag.getList("size", Tag.TAG_DOUBLE).get(2)));
											} else {
												colliderPopup.setValue(null);
												colliderCount.setValue("");
												colliderCenterX.setValue("");
												colliderCenterY.setValue("");
												colliderCenterZ.setValue("");
												colliderSizeX.setValue("");
												colliderSizeY.setValue("");
												colliderSizeZ.setValue("");
											}
											
											if (rowposition > -1) {
												antic._setActive(true);
												preDelay._setActive(true);
												contact._setActive(true);
												recovery._setActive(true);
												colliderCount._setActive(true);
												colliderCenterX._setActive(true);
												colliderCenterY._setActive(true);
												colliderCenterZ._setActive(true);
												colliderSizeX._setActive(true);
												colliderSizeY._setActive(true);
												colliderSizeZ._setActive(true);
												colliderJoint._setActive(true);
												interactionHand._setActive(true);
												colliderPopup._setActive(true);
											}
										})
										.addColumn(Grid.editbox("phase")
														.editable(false)
														.width(200))
										.pressAdd((grid, button) -> {
											FakeAnimation fakeAnimation = this.fakeAnimations.get(this.animationGrid.getRowposition());
											ListTag phases = fakeAnimation.getParameter("phases");
											
											if (phases.isEmpty()) {
												antic._setActive(true);
												preDelay._setActive(true);
												contact._setActive(true);
												recovery._setActive(true);
												colliderCount._setActive(true);
												colliderCenterX._setActive(true);
												colliderCenterY._setActive(true);
												colliderCenterZ._setActive(true);
												colliderSizeX._setActive(true);
												colliderSizeY._setActive(true);
												colliderSizeZ._setActive(true);
												colliderJoint._setActive(true);
												interactionHand._setActive(true);
												colliderPopup._setActive(true);
											}
											
											phases.add(new CompoundTag());
											int rowposition = grid.addRowWithDefaultValues("phase", String.format("Phase%d", grid.children().size() + 1));
											grid.setGridFocus(rowposition, "phase");
										})
										.pressRemove((grid, button) -> {
											grid.removeRow((removedRow) -> {
												ListTag phases = this.fakeAnimations.get(this.animationGrid.getRowposition()).getParameter("phases");
												phases.remove(removedRow);
												
												if (phases.isEmpty()) {
													antic._setActive(false);
													preDelay._setActive(false);
													contact._setActive(false);
													recovery._setActive(false);
													colliderCount._setActive(false);
													colliderCenterX._setActive(false);
													colliderCenterY._setActive(false);
													colliderCenterZ._setActive(false);
													colliderSizeX._setActive(false);
													colliderSizeY._setActive(false);
													colliderSizeZ._setActive(false);
													colliderJoint._setActive(false);
													interactionHand._setActive(false);
													colliderPopup._setActive(false);
												}
											});
										})
										.build();
			
			antic.setResponder((input) -> {
				if (!StringUtil.isNullOrEmpty(input)) {
					ListTag phases = this.fakeAnimations.get(this.animationGrid.getRowposition()).getParameter("phases");
					CompoundTag phaseTag = phases.getCompound(phasesGrid.getRowposition());
					phaseTag.putFloat("antic", Float.valueOf(input));
				}
			});
			
			preDelay.setResponder((input) -> {
				if (!StringUtil.isNullOrEmpty(input)) {
					ListTag phases = this.fakeAnimations.get(this.animationGrid.getRowposition()).getParameter("phases");
					CompoundTag phaseTag = phases.getCompound(phasesGrid.getRowposition());
					phaseTag.putFloat("preDelay", Float.valueOf(input));
				}
			});
			
			contact.setResponder((input) -> {
				if (!StringUtil.isNullOrEmpty(input)) {
					ListTag phases = this.fakeAnimations.get(this.animationGrid.getRowposition()).getParameter("phases");
					CompoundTag phaseTag = phases.getCompound(phasesGrid.getRowposition());
					phaseTag.putFloat("contact", Float.valueOf(input));
				}
			});
			
			recovery.setResponder((input) -> {
				if (!StringUtil.isNullOrEmpty(input)) {
					ListTag phases = this.fakeAnimations.get(this.animationGrid.getRowposition()).getParameter("phases");
					CompoundTag phaseTag = phases.getCompound(phasesGrid.getRowposition());
					phaseTag.putFloat("recovery", Float.valueOf(input));
				}
			});
			
			antic.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsable(context, Float::parseFloat));
			preDelay.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsable(context, Float::parseFloat));
			contact.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsable(context, Float::parseFloat));
			recovery.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsable(context, Float::parseFloat));
			
			final Runnable setCollider = () -> {
				CompoundTag tag = new CompoundTag();
				
				if (ParseUtil.isParsable(colliderCount.getValue(), Integer::parseInt)) {
					tag.putInt("number", Integer.parseInt(colliderCount.getValue()));
				}
				
				ListTag center = new ListTag();
				
				if (ParseUtil.isParsable(colliderCenterX.getValue(), Double::parseDouble)) {
					center.add(DoubleTag.valueOf(Double.parseDouble(colliderCenterX.getValue())));
				}
				if (ParseUtil.isParsable(colliderCenterY.getValue(), Double::parseDouble)) {
					center.add(DoubleTag.valueOf(Double.parseDouble(colliderCenterY.getValue())));
				}
				if (ParseUtil.isParsable(colliderCenterZ.getValue(), Double::parseDouble)) {
					center.add(DoubleTag.valueOf(Double.parseDouble(colliderCenterZ.getValue())));
				}
				
				tag.put("center", center);
				
				ListTag size = new ListTag();
				
				if (ParseUtil.isParsable(colliderSizeX.getValue(), Double::parseDouble)) {
					size.add(DoubleTag.valueOf(Double.parseDouble(colliderSizeX.getValue())));
				}
				if (ParseUtil.isParsable(colliderSizeY.getValue(), Double::parseDouble)) {
					size.add(DoubleTag.valueOf(Double.parseDouble(colliderSizeY.getValue())));
				}
				if (ParseUtil.isParsable(colliderSizeZ.getValue(), Double::parseDouble)) {
					size.add(DoubleTag.valueOf(Double.parseDouble(colliderSizeZ.getValue())));
				}
				
				tag.put("size", size);
				FakeAnimation fakeAnimation = this.fakeAnimations.get(this.animationGrid.getRowposition());
				ListTag phases = fakeAnimation.getParameter("phases");
				CompoundTag phaseTag = phases.getCompound(phasesGrid.getRowposition());
				
				try {
					Collider collider = ColliderPreset.deserializeSimpleCollider(tag);
					phaseTag.put("collider", tag);
					this.animationModelPlayer.setCollider(collider);
				} catch (Exception e) {
					phaseTag.remove("collider");
					this.animationModelPlayer.setCollider(ColliderPreset.FIST);
				}
			};
			
			colliderCount.setResponder((input) -> {
				setCollider.run();
			});
			
			colliderCenterX.setResponder((input) -> {
				setCollider.run();
			});
			
			colliderCenterY.setResponder((input) -> {
				setCollider.run();
			});
			
			colliderCenterZ.setResponder((input) -> {
				setCollider.run();
			});
			
			colliderSizeX.setResponder((input) -> {
				setCollider.run();
			});
			
			colliderSizeY.setResponder((input) -> {
				setCollider.run();
			});
			
			colliderSizeZ.setResponder((input) -> {
				setCollider.run();
			});
			
			colliderCount.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsable(context, Integer::parseInt));
			colliderCenterX.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsableAllowingMinus(context, Double::parseDouble));
			colliderCenterY.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsableAllowingMinus(context, Double::parseDouble));
			colliderCenterZ.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsableAllowingMinus(context, Double::parseDouble));
			colliderSizeX.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsable(context, Double::parseDouble));
			colliderSizeY.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsable(context, Double::parseDouble));
			colliderSizeZ.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsable(context, Double::parseDouble));
			
			colliderJoint.setResponder((joint) -> {
				if (joint != null) {
					ListTag phases = this.fakeAnimations.get(this.animationGrid.getRowposition()).getParameter("phases");
					CompoundTag phaseTag = phases.getCompound(phasesGrid.getRowposition());
					phaseTag.putString("joint", this.animationModelPlayer.getArmature().toString() + "." + joint.getName());
					
					this.animationModelPlayer.setColliderJoint(joint);
				}
			});
			
			interactionHand.setResponder((hand) -> {
				if (hand != null) {
					ListTag phases = this.fakeAnimations.get(this.animationGrid.getRowposition()).getParameter("phases");
					CompoundTag phaseTag = phases.getCompound(phasesGrid.getRowposition());
					phaseTag.putString("hand", hand.toString());
				}
			});
			
			colliderPopup.setResponder((collider) -> {
				if (collider != null) {
					ListTag phases = this.fakeAnimations.get(this.animationGrid.getRowposition()).getParameter("phases");
					CompoundTag phaseTag = phases.getCompound(phasesGrid.getRowposition());
					CompoundTag colliderTag = new CompoundTag();
					collider.serialize(colliderTag);
					
					colliderCount.setValue(String.valueOf(colliderTag.getInt("number")));
					
					ListTag centerVec = colliderTag.getList("center", Tag.TAG_DOUBLE);
					colliderCenterX.setValue(String.valueOf(centerVec.getDouble(0)));
					colliderCenterY.setValue(String.valueOf(centerVec.getDouble(1)));
					colliderCenterZ.setValue(String.valueOf(centerVec.getDouble(2)));
					
					ListTag sizeVec = colliderTag.getList("size", Tag.TAG_DOUBLE);
					colliderSizeX.setValue(String.valueOf(sizeVec.getDouble(0)));
					colliderSizeY.setValue(String.valueOf(sizeVec.getDouble(1)));
					colliderSizeZ.setValue(String.valueOf(sizeVec.getDouble(2)));
					
					phaseTag.put("collider", colliderTag);
					
					this.animationModelPlayer.setCollider(collider, colliderJoint.getValue());
				} else {
					this.animationModelPlayer.setCollider(ColliderPreset.FIST);
				}
			});
			
			colliderPopup.applyFilter((collider) -> collider instanceof OBBCollider || collider instanceof MultiOBBCollider);
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 85, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.import_animation.convert_time")));
			this.inputComponentsList.addComponentCurrentRow(convertTime.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new RowSpliter(this.inputComponentsList.nextStart(5), 10, 60, 15, HorizontalSizing.LEFT_RIGHT, null));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 85, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.import_animation.phases")));
			this.inputComponentsList.newRow();
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(phasesGrid);
			this.inputComponentsList.newRow();
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 85, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.import_animation.antic")));
			this.inputComponentsList.addComponentCurrentRow(antic.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 85, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.import_animation.pre_delay")));
			this.inputComponentsList.addComponentCurrentRow(preDelay.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 85, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.import_animation.contact")));
			this.inputComponentsList.addComponentCurrentRow(contact.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 85, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.import_animation.recovery")));
			this.inputComponentsList.addComponentCurrentRow(recovery.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 85, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.import_animation.hand")));
			this.inputComponentsList.addComponentCurrentRow(interactionHand.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 85, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.collider")));
			this.inputComponentsList.addComponentCurrentRow(colliderPopup.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(20), 40, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.collider.count")));
			this.inputComponentsList.addComponentCurrentRow(colliderCount.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(20), 40, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.collider.center")));
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(5), 8, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("X: ")));
			this.inputComponentsList.addComponentCurrentRow(colliderCenterX.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(8), 8, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("Y: ")));
			this.inputComponentsList.addComponentCurrentRow(colliderCenterY.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(8), 8, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("Z: ")));
			this.inputComponentsList.addComponentCurrentRow(colliderCenterZ.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(20), 40, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.collider.size")));
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(5), 8, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("X: ")));
			this.inputComponentsList.addComponentCurrentRow(colliderSizeX.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(8), 8, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("Y: ")));
			this.inputComponentsList.addComponentCurrentRow(colliderSizeY.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(8), 8, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("Z: ")));
			this.inputComponentsList.addComponentCurrentRow(colliderSizeZ.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 85, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.import_animation.joint")));
			this.inputComponentsList.addComponentCurrentRow(colliderJoint.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new RowSpliter(this.inputComponentsList.nextStart(5), 10, 60, 15, HorizontalSizing.LEFT_RIGHT, null));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 85, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.import_animation.client_data")));
			this.inputComponentsList.addComponentCurrentRow(SubScreenOpenButton.builder().subScreen(() -> {
				return new AttackAnimationPropertyScreen(this, this.fakeAnimations.get(this.animationGrid.getRowposition()), this.animationModelPlayer.getArmature().rootJoint.getAllJoints(), this.animationModelPlayer);
			}).bounds(this.inputComponentsList.nextStart(4), 0, 15, 15).build());
		}
		break;
		default:
		}
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
		
		this.animationGrid.updateSize(split - 10, screenRect.height() - 21, screenRect.top() + 14, screenRect.bottom() - 36);
		this.animationGrid.setLeftPos(8);
		this.animationGrid.resize(screenRect);
		this.inputComponentsList.updateSize(screenRect.width() - (split + 8), screenRect.height() - 21, screenRect.top() + 14, screenRect.bottom() - 36);
		this.inputComponentsList.setLeftPos(split + 2);
		
		this.addRenderableWidget(this.animationGrid);
		this.addRenderableWidget(this.inputComponentsList);
		
		this.addRenderableWidget(Button.builder(CommonComponents.GUI_OK, (button) -> {
			String errorMessage = createRealAnimations(this.fakeAnimations);
			
			if (errorMessage != null) {
				this.minecraft.setScreen(new MessageScreen<>("Failed to import the animations", errorMessage, this, (button2) -> this.minecraft.setScreen(this), 300, 70).autoCalculateHeight());
			} else {
				this.onClose();
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
	public void tick() {
		this.animationGrid._tick();
		this.inputComponentsList.tick();
	}
	
	@Override
	public void onClose() {
		this.animationModelPlayer.onDestroy();
		this.caller.refreshAnimationList();
		this.minecraft.setScreen(this.caller);
	}
	
	@Override
	public void onFilesDrop(List<Path> paths) {
		this.minecraft.setScreen(new MessageScreen<>("", "Enter the mod id", this,
			(modid) -> {
				this.animationGrid.setValueChangeEnabled(false);
				
				for (Path path : paths) {
					InputStream stream = null;
					
					try {
						File file = path.toFile();
						stream = new FileInputStream(file);
						JsonModelLoader jsonLoader = new JsonModelLoader(stream);
						String armatureName = this.animationModelPlayer.getArmature().toString();
						String animationPath = modid + ":" + armatureName.substring(armatureName.lastIndexOf("/") + 1) + "/" + file.getName().replace(".json", "");
						FakeAnimation animation = new FakeAnimation(animationPath, this.animationModelPlayer.getArmature(), jsonLoader.loadAnimationClip(this.animationModelPlayer.getArmature()), jsonLoader.getRootJson().getAsJsonArray("animation"));
						
						this.fakeAnimations.add(animation);
						this.animationGrid.addRowWithDefaultValues("animation_name", animationPath);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						try {
							stream.close();
						} catch (IOException e) {
						}
					}
				}
				
				this.animationGrid.setValueChangeEnabled(true);
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
	
	private static final Map<ResourceLocation, PackEntry<FakeAnimation, ClipHoldingAnimation>> USER_ANIMATIONS = Maps.newLinkedHashMap();
	
	private static String createRealAnimations(List<FakeAnimation> fakeAnimations) {
		USER_ANIMATIONS.clear();
		
		StringBuilder sb = new StringBuilder();
		boolean hasException = false;
		
		List<Object> uniquepaths = fakeAnimations.stream().map((fakeAnim) -> fakeAnim.getParameter("path")).distinct().toList();
		
		if (uniquepaths.size() != fakeAnimations.size()) {
			hasException = true;
			sb.append("Duplicated animation path.");
		} else {
			for (FakeAnimation fakeAnimation : fakeAnimations) {
				try {
					USER_ANIMATIONS.put(fakeAnimation.getRegistryName(), PackEntry.ofValue(fakeAnimation, fakeAnimation.createAnimation()));
				} catch (Exception e) {
					hasException = true;
					sb.append(String.format("%s : %s\n", fakeAnimation.getParameter("path"), e.getMessage()));
					e.printStackTrace();
				}
			}
		}
		
		if (hasException) {
			return sb.toString();
		} else {
			return null;
		}
	}
	
	public static void clearUserAnimation() {
		USER_ANIMATIONS.clear();
	}
	
	public static StaticAnimation byKey(String path) {
		ResourceLocation rl = new ResourceLocation(path);
		StaticAnimation animation = AnimationManager.getInstance().byKey(rl);
		
		if (animation == null && USER_ANIMATIONS.containsKey(rl)) {
			animation = USER_ANIMATIONS.get(rl).getPackValue().cast();
		}
		
		return animation;
	}
	
	public static List<StaticAnimation> getUserAnimations(Predicate<StaticAnimation> filter) {
		return USER_ANIMATIONS.values().stream().map((entry) -> entry.getPackValue().cast()).filter(filter).toList();
	}
	
	public static void importAnimations(PackResources packResources) {
		packResources.getNamespaces(PackType.CLIENT_RESOURCES).stream().distinct().forEach((namespace) -> {
			packResources.listResources(PackType.CLIENT_RESOURCES, namespace, "animmodels/animations", (resourceLocation, stream) -> {
				if (resourceLocation.getPath().contains("/data/")) {
					return;
				}
				
				try {
					JsonReader jsonReader = new JsonReader(new InputStreamReader(stream.get(), StandardCharsets.UTF_8));
					jsonReader.setLenient(true);
					
					JsonObject jsonObject = Streams.parse(jsonReader).getAsJsonObject();
					ResourceLocation rl = new ResourceLocation(resourceLocation.getNamespace(), resourceLocation.getPath().replaceAll("animmodels/animations", "").replaceAll(".json", ""));
					ResourceLocation datapath = AnimationManager.getAnimationDataFileLocation(resourceLocation);
					
					readAnimation(rl, jsonObject, packResources.getResource(PackType.CLIENT_RESOURCES, datapath).get());
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		});
	}
	
	@SuppressWarnings({ "unchecked" })
	private static void readAnimation(ResourceLocation rl, JsonObject json, InputStream dataReader) throws Exception {
		JsonElement constructorElement = json.getAsJsonObject().get("constructor");
		
		if (constructorElement == null) {
			throw new IllegalStateException(String.format("No constructor information has provided in User animation %s", rl));
		}
		
		JsonObject constructorObject = constructorElement.getAsJsonObject();
		String invocationCommand = constructorObject.get("invocation_command").getAsString();
		
		if (invocationCommand.lastIndexOf('#') == -1) {
			throw new IllegalStateException(String.format("Invocation command exception: Missing separator %s in animation %s", invocationCommand, rl));
		}
		
		String className = invocationCommand.substring(invocationCommand.lastIndexOf('#') + 1);
		Class<? extends ClipHoldingAnimation> animationClass = FakeAnimation.switchType((Class<? extends StaticAnimation>)Class.forName(className));
		ClipHoldingAnimation animation = InstantiateInvoker.invoke(invocationCommand, animationClass).getResult();
		
		if (dataReader != null) {
			ClientAnimationDataReader.readAndApply(animation.cast(), dataReader);
		}
		
		JsonModelLoader modelLoader = new JsonModelLoader(json.getAsJsonObject());
		animation.setAnimationClip(modelLoader.loadAnimationClip(animation.cast().getArmature()));
		
		USER_ANIMATIONS.put(rl, PackEntry.ofValue(animation.buildAnimation(modelLoader.getRootJson().get("animation").getAsJsonArray()), animation));
	}
	
	public static void export(ZipOutputStream out) throws Exception {
		for (Map.Entry<ResourceLocation, PackEntry<FakeAnimation, ClipHoldingAnimation>> entry : USER_ANIMATIONS.entrySet()) {
			String exportPath = String.format("assets/%s/animmodels/animations/%s.json", entry.getKey().getNamespace(), entry.getKey().getPath());
			ResourceLocation clientData = AnimationManager.getAnimationDataFileLocation(new ResourceLocation(entry.getKey().getNamespace(), exportPath));
			
			ZipEntry asResource = new ZipEntry(exportPath);
			ZipEntry asResourceClientData = new ZipEntry(clientData.getPath());
			ZipEntry asData = new ZipEntry(String.format("data/%s/animmodels/animations/%s.json", entry.getKey().getNamespace(), entry.getKey().getPath()));
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			FakeAnimation animation = entry.getValue().getPackValue().getCreator();
			
			JsonObject root = new JsonObject();
			JsonObject constructorInfo = new JsonObject();
			constructorInfo.addProperty("invocation_command", animation.getInvocationCommand());
			
			root.add("constructor", constructorInfo);
			root.add("animation", animation.getRawAnimationJson());
			
			out.putNextEntry(asResource);
			out.write(gson.toJson(root).getBytes());
			out.closeEntry();
			
			if (animation.getPropertiesJson().size() > 0) {
				out.putNextEntry(asResourceClientData);
				out.write(gson.toJson(animation.getPropertiesJson()).getBytes());
				out.closeEntry();
			}
			
			out.putNextEntry(asData);
			out.write(gson.toJson(root).getBytes());
			out.closeEntry();
		}
	}
}