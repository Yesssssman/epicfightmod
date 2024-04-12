package yesman.epicfight.client.gui.datapack.screen;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

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
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.MovementAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.animation.types.datapack.FakeAnimation;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.collider.MultiOBBCollider;
import yesman.epicfight.api.collider.OBBCollider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.model.JsonModelLoader;
import yesman.epicfight.api.utils.ParseUtil;
import yesman.epicfight.client.gui.datapack.widgets.AnimatedModelPlayer;
import yesman.epicfight.client.gui.datapack.widgets.CheckBox;
import yesman.epicfight.client.gui.datapack.widgets.ComboBox;
import yesman.epicfight.client.gui.datapack.widgets.Grid;
import yesman.epicfight.client.gui.datapack.widgets.InputComponentList;
import yesman.epicfight.client.gui.datapack.widgets.PopupBox;
import yesman.epicfight.client.gui.datapack.widgets.ResizableComponent.HorizontalSizing;
import yesman.epicfight.client.gui.datapack.widgets.ResizableEditBox;
import yesman.epicfight.client.gui.datapack.widgets.Static;
import yesman.epicfight.gameasset.ColliderPreset;

@OnlyIn(Dist.CLIENT)
public class ImportAnimationsScreen extends Screen {
	private static final List<PackEntry<FakeAnimation, StaticAnimation>> USER_ANIMATIONS = Lists.newArrayList();
	
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
					USER_ANIMATIONS.add(PackEntry.ofValue(fakeAnimation, fakeAnimation.createAnimation()));
				} catch (Exception e) {
					hasException = true;
					USER_ANIMATIONS.add(PackEntry.ofValue(fakeAnimation, null));
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
	
	public static List<StaticAnimation> getUserAnimations(Predicate<StaticAnimation> filter) {
		return USER_ANIMATIONS.stream().map((entry) -> entry.getPackValue()).filter(filter).toList();
	}
	
	private final SelectAnimationScreen caller;
	private final Grid grid;
	private final AnimatedModelPlayer animationModelPlayer;
	private final List<FakeAnimation> tempAnimations = Lists.newArrayList();
	
	private InputComponentList<FakeAnimation> inputComponentsList;
	private ComboBox<Class<? extends StaticAnimation>> animationType;
	private Consumer<Class<? extends StaticAnimation>> responder;
	
	public ImportAnimationsScreen(SelectAnimationScreen caller, Armature armature, AnimatedMesh mesh) {
		super(Component.literal("register_animation_screen"));
		
		this.tempAnimations.addAll(USER_ANIMATIONS.stream().map(PackEntry::getPackKey).map(FakeAnimation::deepCopy).toList());
		
		this.caller = caller;
		this.animationModelPlayer = new AnimatedModelPlayer(10, 15, 0, 140, HorizontalSizing.LEFT_RIGHT, null);
		this.animationModelPlayer.setArmature(armature);
		this.animationModelPlayer.setMesh(mesh);
		this.animationModelPlayer.setCollider(ColliderPreset.FIST);
		
		this.minecraft = caller.getMinecraft();
		this.font = caller.getMinecraft().font;
		
		ScreenRectangle screenRect = caller.getRectangle();
		int split = screenRect.width() / 2 - 60;
		
		this.grid = Grid.builder(this, caller.getMinecraft())
						.xy1(8, screenRect.top() + 14)
						.xy2(split - 10, screenRect.height() - 21)
						.rowHeight(26)
						.rowEditable(false)
						.transparentBackground(true)
						.rowpositionChanged((rowposition, values) -> this.inputComponentsList.importTag(this.tempAnimations.get(rowposition)))
						.addColumn(Grid.editbox("animation_name")
										.editWidgetCreated((editbox) -> editbox.setFilter(ResourceLocation::isValidResourceLocation))
										.editable(true)
										.valueChanged((event) -> this.tempAnimations.get(event.rowposition).setParameter("path", event.postValue))
										.width(180))
						.build();
		
		this.inputComponentsList = new InputComponentList<>(this, 0, 0, 0, 0, 30) {
			@Override
			public void importTag(FakeAnimation fakeAnim) {
				this.clearComponents();
				
				ImportAnimationsScreen.this.rearrangeComponents(fakeAnim.getAnimationClass());
				this.setComponentsActive(true);
				
				if (fakeAnim.getAnimationClass() == StaticAnimation.class || fakeAnim.getAnimationClass() == MovementAnimation.class) {
					ImportAnimationsScreen.this.animationType.setResponder(null);
					
					this.setDataBindingComponenets(new Object[] {
						fakeAnim.getAnimationClass(),
						ParseUtil.nullParam(fakeAnim.getParameter("convertTime")),
						fakeAnim.getParameter("isRepeat")
					});
					
					ImportAnimationsScreen.this.animationType.setResponder(ImportAnimationsScreen.this.responder);
				} else if (fakeAnim.getAnimationClass() == AttackAnimation.class) {
					CompoundTag colliderTag = new CompoundTag();
					Collider collider = (Collider)fakeAnim.getParameter("collider");
					
					if (collider != null) {
						collider.serialize(colliderTag);
					}
					
					ImportAnimationsScreen.this.animationType.setResponder(null);
					
					this.setDataBindingComponenets(new Object[] {
						fakeAnim.getAnimationClass(),
						ParseUtil.nullParam(fakeAnim.getParameter("convertTime")),
						ParseUtil.nullParam(fakeAnim.getParameter("antic")),
						ParseUtil.nullParam(fakeAnim.getParameter("preDelay")),
						ParseUtil.nullParam(fakeAnim.getParameter("contact")),
						ParseUtil.nullParam(fakeAnim.getParameter("recovery")),
						fakeAnim.getParameter("hand"),
						null,
						ParseUtil.nullParam(colliderTag.get("number")),
						colliderTag.contains("center") ? ParseUtil.nullParam(ParseUtil.nullOrApply(colliderTag.getList("center", Tag.TAG_DOUBLE).get(0), Tag::getAsString)) : "",
						colliderTag.contains("center") ? ParseUtil.nullParam(ParseUtil.nullOrApply(colliderTag.getList("center", Tag.TAG_DOUBLE).get(1), Tag::getAsString)) : "",
						colliderTag.contains("center") ? ParseUtil.nullParam(ParseUtil.nullOrApply(colliderTag.getList("center", Tag.TAG_DOUBLE).get(2), Tag::getAsString)) : "",
						colliderTag.contains("size") ? ParseUtil.nullParam(ParseUtil.nullOrApply(colliderTag.getList("size", Tag.TAG_DOUBLE).get(0), Tag::getAsString)) : "",
						colliderTag.contains("size") ? ParseUtil.nullParam(ParseUtil.nullOrApply(colliderTag.getList("size", Tag.TAG_DOUBLE).get(1), Tag::getAsString)) : "",
						colliderTag.contains("size") ? ParseUtil.nullParam(ParseUtil.nullOrApply(colliderTag.getList("size", Tag.TAG_DOUBLE).get(2), Tag::getAsString)) : "",
						fakeAnim.getParameter("colliderJoint")
					});
					
					ImportAnimationsScreen.this.animationType.setResponder(ImportAnimationsScreen.this.responder);
				} else {
					ImportAnimationsScreen.this.animationType.setValue(null);
				}
			}
		};
		
		if (this.tempAnimations.isEmpty()) {
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 5, 60, 15, HorizontalSizing.LEFT_RIGHT, null, Component.translatable("datapack_edit.import_animation.place_tooltip")));
		}
		
		this.responder = (clz) -> {
			if (clz != null) {
				FakeAnimation fakeAnim = this.tempAnimations.get(this.grid.getRowposition());
				fakeAnim.setAnimationClass(clz);
				this.rearrangeComponents(clz);
			}
		};
		
		this.animationType = new ComboBox<>(this, this.font, 0, 124, 100, 15, HorizontalSizing.LEFT_WIDTH, null, 8, Component.translatable("datapack_edit.import_animation.type"),
											List.of(StaticAnimation.class, MovementAnimation.class, AttackAnimation.class), (clz) -> clz.getSimpleName(), this.responder);
		
		for (PackEntry<FakeAnimation, StaticAnimation> entry : USER_ANIMATIONS) {
			this.grid.addRowWithDefaultValues("animation_name", entry.getPackKey().getParameter("path"));
		}
	}
	
	public void rearrangeComponents(Class<? extends StaticAnimation> animationClass) {
		ScreenRectangle screenRect = this.getRectangle();
		
		this.inputComponentsList.children().clear();
		this.inputComponentsList.newRow();
		
		this.animationModelPlayer.clearAnimations();
		this.animationModelPlayer.addAnimationToPlay(this.tempAnimations.get(this.grid.getRowposition()));
		
		this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 60, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.import_animation.type")));
		this.inputComponentsList.addComponentCurrentRow(this.animationType.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
		
		if (animationClass == StaticAnimation.class || animationClass == MovementAnimation.class) {
			final ResizableEditBox convertTime = new ResizableEditBox(this.font, 0, 0, 35, 15, Component.translatable("datapack_edit.import_animation.convert_time"), HorizontalSizing.LEFT_WIDTH, null);
			final CheckBox repeat = new CheckBox(this.font, 0, 60, 0, 10, HorizontalSizing.LEFT_WIDTH, null, false, Component.literal(""), (value) -> {});
			
			convertTime.setResponder((input) -> {
				Object f = StringUtil.isNullOrEmpty(input) ? null : Float.valueOf(input);
				this.tempAnimations.get(this.grid.getRowposition()).setParameter("convertTime", f);
			});
			
			convertTime.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsable(context, Float::parseFloat));
			
			repeat.setResponder((value) -> this.tempAnimations.get(this.grid.getRowposition()).setParameter("isRepeat", value));
			
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
				Object f = StringUtil.isNullOrEmpty(input) ? null : Float.valueOf(input);
				this.tempAnimations.get(this.grid.getRowposition()).setParameter("convertTime", f);
			});
			
			antic.setResponder((input) -> {
				Object f = StringUtil.isNullOrEmpty(input) ? null : Float.valueOf(input);
				this.tempAnimations.get(this.grid.getRowposition()).setParameter("antic", f);
			});
			
			preDelay.setResponder((input) -> {
				Object f = StringUtil.isNullOrEmpty(input) ? null : Float.valueOf(input);
				this.tempAnimations.get(this.grid.getRowposition()).setParameter("preDelay", f);
			});
			
			contact.setResponder((input) -> {
				Object f = StringUtil.isNullOrEmpty(input) ? null : Float.valueOf(input);
				this.tempAnimations.get(this.grid.getRowposition()).setParameter("contact", f);
			});
			
			recovery.setResponder((input) -> {
				Object f = StringUtil.isNullOrEmpty(input) ? null : Float.valueOf(input);
				this.tempAnimations.get(this.grid.getRowposition()).setParameter("recovery", f);
			});
			
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
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 60, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.import_animation.hand")));
			this.inputComponentsList.addComponentCurrentRow(new ComboBox<>(this, this.font, this.inputComponentsList.nextStart(5), 124, 100, 15, HorizontalSizing.LEFT_WIDTH, null, 8,
																			Component.translatable("datapack_edit.import_animation.hand"), List.of(InteractionHand.MAIN_HAND, InteractionHand.OFF_HAND),
																			ParseUtil::snakeToSpacedCamel, (hand) -> {
																				FakeAnimation fakeAnimation = this.tempAnimations.get(this.grid.getRowposition());
																				fakeAnimation.setParameter("hand", hand);
																			}));
			
			final ResizableEditBox colliderCount = new ResizableEditBox(this.font, 0, 0, 40, 15, Component.translatable("datapack_edit.weapon_type.collider.count"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox colliderCenterX = new ResizableEditBox(this.font, 0, 0, 35, 15, Component.translatable("datapack_edit.weapon_type.collider.center.x"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox colliderCenterY = new ResizableEditBox(this.font, 0, 0, 35, 15, Component.translatable("datapack_edit.weapon_type.collider.center.y"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox colliderCenterZ = new ResizableEditBox(this.font, 0, 0, 35, 15, Component.translatable("datapack_edit.weapon_type.collider.center.z"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox colliderSizeX = new ResizableEditBox(this.font, 0, 0, 35, 15, Component.translatable("datapack_edit.weapon_type.collider.size.x"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox colliderSizeY = new ResizableEditBox(this.font, 0, 0, 35, 15, Component.translatable("datapack_edit.weapon_type.collider.size.y"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox colliderSizeZ = new ResizableEditBox(this.font, 0, 0, 35, 15, Component.translatable("datapack_edit.weapon_type.collider.size.z"), HorizontalSizing.LEFT_WIDTH, null);
			
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
				FakeAnimation fakeAnimation = this.tempAnimations.get(this.grid.getRowposition());
				
				try {
					Collider collider = ColliderPreset.deserializeSimpleCollider(tag);
					fakeAnimation.setParameter("collider", collider);
					this.animationModelPlayer.setCollider(ColliderPreset.FIST);
				} catch (Exception e) {
					fakeAnimation.setParameter("collider", null);
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
			colliderCenterX.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsableAllowMinus(context, Double::parseDouble));
			colliderCenterY.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsableAllowMinus(context, Double::parseDouble));
			colliderCenterZ.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsableAllowMinus(context, Double::parseDouble));
			colliderSizeX.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsable(context, Double::parseDouble));
			colliderSizeY.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsable(context, Double::parseDouble));
			colliderSizeZ.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsable(context, Double::parseDouble));
			
			final ComboBox<Joint> jointCombo = new ComboBox<>(this, this.font, 0, 124, 100, 15, HorizontalSizing.LEFT_WIDTH, null, 8,
														Component.translatable("datapack_edit.import_animation.joint"), this.animationModelPlayer.getArmature().getRootJoint().getAllJoints(),
														Joint::getName, (joint) -> {
															FakeAnimation fakeAnimation = this.tempAnimations.get(this.grid.getRowposition());
															fakeAnimation.setParameter("colliderJoint", joint);
															
															this.animationModelPlayer.setColliderJoint(joint);
														});
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 60, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.weapon_type.collider")));
			this.inputComponentsList.addComponentCurrentRow(new PopupBox.ColliderPopupBox(this, font, this.inputComponentsList.nextStart(5), 130, 30, 15, HorizontalSizing.LEFT_RIGHT, null, Component.translatable("datapack_edit.weapon_type.collider"),
																							(collider) -> {
																								if (collider != null) {
																									FakeAnimation fakeAnimation = this.tempAnimations.get(this.grid.getRowposition());
																									fakeAnimation.setParameter("collider", collider);
																									
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
																									
																									this.animationModelPlayer.setCollider(collider, jointCombo.getValue());
																								}
																							}).applyFilter((collider) -> collider instanceof OBBCollider || collider instanceof MultiOBBCollider));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(20), 40, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.weapon_type.collider.count")));
			this.inputComponentsList.addComponentCurrentRow(colliderCount.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(20), 40, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.weapon_type.collider.center")));
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(5), 8, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("X: ")));
			this.inputComponentsList.addComponentCurrentRow(colliderCenterX.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(8), 8, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("Y: ")));
			this.inputComponentsList.addComponentCurrentRow(colliderCenterY.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(8), 8, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("Z: ")));
			this.inputComponentsList.addComponentCurrentRow(colliderCenterZ.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(20), 40, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.weapon_type.collider.size")));
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(5), 8, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("X: ")));
			this.inputComponentsList.addComponentCurrentRow(colliderSizeX.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(8), 8, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("Y: ")));
			this.inputComponentsList.addComponentCurrentRow(colliderSizeY.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(8), 8, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("Z: ")));
			this.inputComponentsList.addComponentCurrentRow(colliderSizeZ.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(this.font, this.inputComponentsList.nextStart(4), 70, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.import_animation.joint")));
			this.inputComponentsList.addComponentCurrentRow(jointCombo.relocateX(screenRect, this.inputComponentsList.nextStart(5)));
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
			String errorMessage = createRealAnimations(this.tempAnimations);
			
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
		this.grid.tick();
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
				this.grid.setValueChangeEnabled(false);
				
				for (Path path : paths) {
					try {
						File file = path.toFile();
						InputStream stream = new FileInputStream(file);
						JsonModelLoader jsonLoader = new JsonModelLoader(stream);
						String animationPath = modid + ":" + this.animationModelPlayer.getArmature().toString() + "/" + file.getName().replace(".json", "");
						FakeAnimation animation = new FakeAnimation(file, animationPath, this.animationModelPlayer.getArmature(), jsonLoader.loadAnimationClip(this.animationModelPlayer.getArmature()));
						
						this.tempAnimations.add(animation);
						this.grid.addRowWithDefaultValues("animation_name", animationPath);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				this.grid.setValueChangeEnabled(true);
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