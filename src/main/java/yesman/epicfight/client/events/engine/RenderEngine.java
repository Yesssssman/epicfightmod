package yesman.epicfight.client.events.engine;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.api.client.forgeevent.PatchedRenderersEvent;
import yesman.epicfight.api.client.forgeevent.RenderEnderDragonEvent;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.client.gui.BetaWarningMessage;
import yesman.epicfight.client.gui.EntityIndicator;
import yesman.epicfight.client.gui.screen.UISetupScreen;
import yesman.epicfight.client.gui.screen.overlay.OverlayManager;
import yesman.epicfight.client.input.EpicFightKeyMappings;
import yesman.epicfight.client.renderer.AimHelperRenderer;
import yesman.epicfight.client.renderer.FirstPersonRenderer;
import yesman.epicfight.client.renderer.patched.entity.PCreeperRenderer;
import yesman.epicfight.client.renderer.patched.entity.PDrownedRenderer;
import yesman.epicfight.client.renderer.patched.entity.PEnderDragonRenderer;
import yesman.epicfight.client.renderer.patched.entity.PEndermanRenderer;
import yesman.epicfight.client.renderer.patched.entity.PHoglinRenderer;
import yesman.epicfight.client.renderer.patched.entity.PHumanoidRenderer;
import yesman.epicfight.client.renderer.patched.entity.PIllagerRenderer;
import yesman.epicfight.client.renderer.patched.entity.PIronGolemRenderer;
import yesman.epicfight.client.renderer.patched.entity.PPlayerRenderer;
import yesman.epicfight.client.renderer.patched.entity.PRavagerRenderer;
import yesman.epicfight.client.renderer.patched.entity.PSpiderRenderer;
import yesman.epicfight.client.renderer.patched.entity.PStrayRenderer;
import yesman.epicfight.client.renderer.patched.entity.PVexRenderer;
import yesman.epicfight.client.renderer.patched.entity.PVindicatorRenderer;
import yesman.epicfight.client.renderer.patched.entity.PWitchRenderer;
import yesman.epicfight.client.renderer.patched.entity.PWitherRenderer;
import yesman.epicfight.client.renderer.patched.entity.PWitherSkeletonMinionRenderer;
import yesman.epicfight.client.renderer.patched.entity.PZombieVillagerRenderer;
import yesman.epicfight.client.renderer.patched.entity.PatchedEntityRenderer;
import yesman.epicfight.client.renderer.patched.entity.WitherGhostCloneRenderer;
import yesman.epicfight.client.renderer.patched.item.RenderBow;
import yesman.epicfight.client.renderer.patched.item.RenderCrossbow;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.client.renderer.patched.item.RenderKatana;
import yesman.epicfight.client.renderer.patched.item.RenderMap;
import yesman.epicfight.client.renderer.patched.item.RenderTrident;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.boss.enderdragon.EnderDragonPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.entity.EpicFightEntities;
import yesman.epicfight.world.gamerule.EpicFightGamerules;
import yesman.epicfight.world.item.EpicFightItems;

@SuppressWarnings("rawtypes")
@OnlyIn(Dist.CLIENT)
public class RenderEngine {
	private static final Vec3f AIMING_CORRECTION = new Vec3f(-1.5F, 0.0F, 1.25F);
	
	public AimHelperRenderer aimHelper;
	public final BattleModeGui battleModeUI = new BattleModeGui(Minecraft.getInstance());
	public final BetaWarningMessage betaWarningMessage = new BetaWarningMessage(Minecraft.getInstance());
	public final Minecraft minecraft;
	private final Map<EntityType<?>, Supplier<PatchedEntityRenderer>> entityRendererProvider;
	private final Map<EntityType<?>, PatchedEntityRenderer> entityRendererCache;
	private final Map<Item, RenderItemBase> itemRendererMapByInstance;
	private final Map<Class<? extends Item>, RenderItemBase> itemRendererMapByClass;
	private FirstPersonRenderer firstPersonRenderer;
	private PHumanoidRenderer<?, ?, ?, ?> basicHumanoidRenderer;
	private final OverlayManager overlayManager;
	private boolean aiming;
	private int zoomOutTimer = 0;
	private int zoomCount;
	private final int zoomMaxCount = 20;
	private float cameraXRot;
	private float cameraYRot;
	private boolean isPlayerRotationLocked;
	
	public RenderEngine() {
		Events.renderEngine = this;
		RenderItemBase.renderEngine = this;
		EntityIndicator.init();
		this.minecraft = Minecraft.getInstance();
		this.entityRendererProvider = Maps.newHashMap();
		this.entityRendererCache = Maps.newHashMap();
		this.itemRendererMapByInstance = Maps.newHashMap();
		this.itemRendererMapByClass = Maps.newHashMap();
		this.overlayManager = new OverlayManager();
	}
	
	public void registerRenderer() {
		this.firstPersonRenderer = new FirstPersonRenderer();
		this.basicHumanoidRenderer = new PHumanoidRenderer<>(Meshes.BIPED);
		
		this.entityRendererProvider.put(EntityType.CREEPER, PCreeperRenderer::new);
		this.entityRendererProvider.put(EntityType.ENDERMAN, PEndermanRenderer::new);
		this.entityRendererProvider.put(EntityType.ZOMBIE, () -> new PHumanoidRenderer<>(Meshes.BIPED_OLD_TEX));
		this.entityRendererProvider.put(EntityType.ZOMBIE_VILLAGER, PZombieVillagerRenderer::new);
		this.entityRendererProvider.put(EntityType.ZOMBIFIED_PIGLIN, () -> new PHumanoidRenderer<>(Meshes.PIGLIN));
		this.entityRendererProvider.put(EntityType.HUSK, () -> new PHumanoidRenderer<>(Meshes.BIPED_OLD_TEX));
		this.entityRendererProvider.put(EntityType.SKELETON, () -> new PHumanoidRenderer<>(Meshes.SKELETON));
		this.entityRendererProvider.put(EntityType.WITHER_SKELETON, () -> new PHumanoidRenderer<>(Meshes.SKELETON));
		this.entityRendererProvider.put(EntityType.STRAY, PStrayRenderer::new);
		this.entityRendererProvider.put(EntityType.PLAYER, PPlayerRenderer::new);
		this.entityRendererProvider.put(EntityType.SPIDER, PSpiderRenderer::new);
		this.entityRendererProvider.put(EntityType.CAVE_SPIDER, PSpiderRenderer::new);
		this.entityRendererProvider.put(EntityType.IRON_GOLEM, PIronGolemRenderer::new);
		this.entityRendererProvider.put(EntityType.VINDICATOR, PVindicatorRenderer::new);
		this.entityRendererProvider.put(EntityType.EVOKER, PIllagerRenderer::new);
		this.entityRendererProvider.put(EntityType.WITCH, PWitchRenderer::new);
		this.entityRendererProvider.put(EntityType.DROWNED, PDrownedRenderer::new);
		this.entityRendererProvider.put(EntityType.PILLAGER, PIllagerRenderer::new);
		this.entityRendererProvider.put(EntityType.RAVAGER, PRavagerRenderer::new);
		this.entityRendererProvider.put(EntityType.VEX, PVexRenderer::new);
		this.entityRendererProvider.put(EntityType.PIGLIN, () -> new PHumanoidRenderer<>(Meshes.PIGLIN));
		this.entityRendererProvider.put(EntityType.PIGLIN_BRUTE, () -> new PHumanoidRenderer<>(Meshes.PIGLIN));
		this.entityRendererProvider.put(EntityType.HOGLIN, PHoglinRenderer::new);
		this.entityRendererProvider.put(EntityType.ZOGLIN, PHoglinRenderer::new);
		this.entityRendererProvider.put(EntityType.ENDER_DRAGON, PEnderDragonRenderer::new);
		this.entityRendererProvider.put(EntityType.WITHER, PWitherRenderer::new);
		this.entityRendererProvider.put(EpicFightEntities.WITHER_SKELETON_MINION.get(), PWitherSkeletonMinionRenderer::new);
		this.entityRendererProvider.put(EpicFightEntities.WITHER_GHOST_CLONE.get(), WitherGhostCloneRenderer::new);
		
		RenderItemBase baseRenderer = new RenderItemBase();
		RenderBow bowRenderer = new RenderBow();
		RenderCrossbow crossbowRenderer = new RenderCrossbow();
		RenderTrident tridentRenderer = new RenderTrident();
		RenderMap mapRenderer = new RenderMap();
		
		this.itemRendererMapByInstance.clear();
		this.itemRendererMapByInstance.put(Items.AIR, baseRenderer);
		this.itemRendererMapByInstance.put(Items.BOW, bowRenderer);
		this.itemRendererMapByInstance.put(Items.SHIELD, baseRenderer);
		this.itemRendererMapByInstance.put(Items.CROSSBOW, crossbowRenderer);
		this.itemRendererMapByInstance.put(Items.TRIDENT, tridentRenderer);
		this.itemRendererMapByInstance.put(Items.FILLED_MAP, mapRenderer);
		this.itemRendererMapByInstance.put(EpicFightItems.UCHIGATANA.get(), new RenderKatana());
		this.itemRendererMapByClass.put(BowItem.class, bowRenderer);
		this.itemRendererMapByClass.put(CrossbowItem.class, crossbowRenderer);
		this.itemRendererMapByClass.put(ShieldItem.class, baseRenderer);
		this.itemRendererMapByClass.put(TridentItem.class, tridentRenderer);
		this.aimHelper = new AimHelperRenderer();
		
		ModLoader.get().postEvent(new PatchedRenderersEvent.Add(this.entityRendererProvider, this.itemRendererMapByInstance));
		
		for (Map.Entry<EntityType<?>, Supplier<PatchedEntityRenderer>> entry : this.entityRendererProvider.entrySet()) {
			this.entityRendererCache.put(entry.getKey(), entry.getValue().get());
		}
		
		ModLoader.get().postEvent(new PatchedRenderersEvent.Modify(this.entityRendererCache));
	}
	
	public void registerCustomEntityRenderer(EntityType<?> entityType, String renderer) {
		if ("".equals(renderer)) {
			return;
		}
		
		if ("player".equals(renderer)) {
			this.entityRendererCache.put(entityType, this.basicHumanoidRenderer);
		} else {
			EntityType<?> presetEntityType = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(renderer));
			
			if (this.entityRendererProvider.containsKey(presetEntityType)) {
				this.entityRendererCache.put(entityType, this.entityRendererProvider.get(presetEntityType).get());
			} else {
				throw new IllegalArgumentException("Datapack Mob Patch Crash: Invalid Renderer type " + renderer);
			}
		}
	}
	
	public RenderItemBase getItemRenderer(Item item) {
		RenderItemBase renderItem = this.itemRendererMapByInstance.get(item);
		
		if (renderItem == null) {
			renderItem = this.findMatchingRendererByClass(item.getClass());
			
			if (renderItem == null) {
				renderItem = this.itemRendererMapByInstance.get(Items.AIR);
			}
			
			this.itemRendererMapByInstance.put(item, renderItem);
		}
		
		return renderItem;
	}

	private RenderItemBase findMatchingRendererByClass(Class<?> clazz) {
		RenderItemBase renderer = null;
		
		for (; clazz != null && renderer == null; clazz = clazz.getSuperclass()) {
			renderer = this.itemRendererMapByClass.getOrDefault(clazz, null);
		}
		
		return renderer;
	}
	
	@SuppressWarnings("unchecked")
	public void renderEntityArmatureModel(LivingEntity livingEntity, LivingEntityPatch<?> entitypatch, LivingEntityRenderer<? extends Entity, ?> renderer, MultiBufferSource buffer, PoseStack matStack, int packedLightIn, float partialTicks) {
		this.getEntityRenderer(livingEntity).render(livingEntity, entitypatch, renderer, buffer, matStack, packedLightIn, partialTicks);
	}
	
	public PatchedEntityRenderer getEntityRenderer(Entity entity) {
		return this.entityRendererCache.get(entity.getType());
	}
	
	public boolean hasRendererFor(Entity entity) {
		return this.entityRendererCache.computeIfAbsent(entity.getType(), (key) -> this.entityRendererProvider.containsKey(key) ? this.entityRendererProvider.get(entity.getType()).get() : null) != null;
	}
	
	public void clearCustomEntityRenerer() {
		this.entityRendererCache.clear();
	}
	
	public void zoomIn() {
		this.aiming = true;
		this.zoomCount = this.zoomCount == 0 ? 1 : this.zoomCount;
		this.zoomOutTimer = 0;
	}

	public void zoomOut(int timer) {
		this.aiming = false;
		this.zoomOutTimer = timer;
	}
	
	private void setRangedWeaponThirdPerson(ViewportEvent.ComputeCameraAngles event, CameraType pov, double partialTicks) {
		if (ClientEngine.getInstance().getPlayerPatch() == null) {
			return;
		}
		
		Camera camera = event.getCamera();
		Entity entity = minecraft.getCameraEntity();
		Vec3 vector = camera.getPosition();
		double totalX = vector.x();
		double totalY = vector.y();
		double totalZ = vector.z();
		
		if (pov == CameraType.THIRD_PERSON_BACK) {
			double posX = vector.x();
			double posY = vector.y();
			double posZ = vector.z();
			double entityPosX = entity.xOld + (entity.getX() - entity.xOld) * partialTicks;
			double entityPosY = entity.yOld + (entity.getY() - entity.yOld) * partialTicks + entity.getEyeHeight();
			double entityPosZ = entity.zOld + (entity.getZ() - entity.zOld) * partialTicks;
			float intpol = pov == CameraType.THIRD_PERSON_BACK ? ((float) zoomCount / (float) zoomMaxCount) : 0;
			Vec3f interpolatedCorrection = new Vec3f(AIMING_CORRECTION.x * intpol, AIMING_CORRECTION.y * intpol, AIMING_CORRECTION.z * intpol);
			OpenMatrix4f rotationMatrix = ClientEngine.getInstance().getPlayerPatch().getMatrix((float)partialTicks);
			Vec3f rotateVec = OpenMatrix4f.transform3v(rotationMatrix, interpolatedCorrection, null);
			double d3 = Math.sqrt((rotateVec.x * rotateVec.x) + (rotateVec.y * rotateVec.y) + (rotateVec.z * rotateVec.z));
			double smallest = d3;
			double d00 = posX + rotateVec.x;
			double d11 = posY - rotateVec.y;
			double d22 = posZ + rotateVec.z;
			
			for (int i = 0; i < 8; ++i) {
				float f = (float) ((i & 1) * 2 - 1);
				float f1 = (float) ((i >> 1 & 1) * 2 - 1);
				float f2 = (float) ((i >> 2 & 1) * 2 - 1);
				f = f * 0.1F;
				f1 = f1 * 0.1F;
				f2 = f2 * 0.1F;
				HitResult raytraceresult = minecraft.level.clip(new ClipContext(new Vec3(entityPosX + f, entityPosY + f1, entityPosZ + f2), new Vec3(d00 + f + f2, d11 + f1, d22 + f2), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));

				if (raytraceresult != null) {
					double d7 = raytraceresult.getLocation().distanceTo(new Vec3(entityPosX, entityPosY, entityPosZ));
					if (d7 < smallest) {
						smallest = d7;
					}
				}
			}
			
			float dist = d3 == 0 ? 0 : (float) (smallest / d3);
			totalX += rotateVec.x * dist;
			totalY -= rotateVec.y * dist;
			totalZ += rotateVec.z * dist;
		}
		
		camera.setPosition(totalX, totalY, totalZ);
	}
	
	public void rotateCameraByMouseInput(float dx, float dy) {
		float f = dx * 0.15F;
		float f1 = dy * 0.15F;
		
		if (!this.isPlayerRotationLocked) {
			this.cameraXRot = this.minecraft.player.getXRot();
			this.cameraYRot = this.minecraft.player.getYRot();
			this.isPlayerRotationLocked = true;
		}
		
		this.cameraXRot = Mth.clamp(this.cameraXRot + f, -90.0F, 90.0F);
		this.cameraYRot += f1;
	}
	
	public boolean isPlayerRotationLocked() {
		return this.isPlayerRotationLocked;
	}
	
	public float getCorrectedXRot() {
		return this.cameraXRot;
	}
	
	public float getCorrectedYRot() {
		return this.cameraYRot;
	}
	
	public void unlockRotation(Entity cameraEntity) {
		if (this.isPlayerRotationLocked) {
			cameraEntity.setXRot(this.cameraXRot);
			cameraEntity.setYRot(this.cameraYRot);
			
			this.isPlayerRotationLocked = false;
		}
	}
	
	public void correctCamera(ViewportEvent.ComputeCameraAngles event, float partialTicks) {
		LocalPlayerPatch localPlayerPatch = ClientEngine.getInstance().getPlayerPatch();
		Camera camera = event.getCamera();
		CameraType cameraType = this.minecraft.options.getCameraType();
		boolean hasAnyCorrection = false;
		
		if (localPlayerPatch != null) {
			if (localPlayerPatch.getTarget() != null && localPlayerPatch.isTargetLockedOn()) {
				this.cameraXRot = localPlayerPatch.getLerpedLockOnX(event.getPartialTick());
				this.cameraYRot = localPlayerPatch.getLerpedLockOnY(event.getPartialTick());
				hasAnyCorrection = true;
			} else if (this.isPlayerRotationLocked) {
				if (!localPlayerPatch.getEntityState().turningLocked()) {
					this.unlockRotation(this.minecraft.player);
				}
				
				hasAnyCorrection = true;
			}
		}
		
		if (hasAnyCorrection) {
			float xRot = this.cameraXRot;
			float yRot = this.cameraYRot;
			
			if (cameraType.isMirrored()) {
				yRot += 180.0F;
				xRot *= -1.0F;
			}
			
			camera.setRotation(yRot, xRot);
			event.setPitch(xRot);
			event.setYaw(yRot);
			
			if (!cameraType.isFirstPerson()) {
				Entity cameraEntity = this.minecraft.cameraEntity;
				
				camera.setPosition(Mth.lerp(partialTicks, cameraEntity.xo, cameraEntity.getX()),
								   Mth.lerp(partialTicks, cameraEntity.yo, cameraEntity.getY()) + Mth.lerp(partialTicks, camera.eyeHeightOld, camera.eyeHeight),
								   Mth.lerp(partialTicks, cameraEntity.zo, cameraEntity.getZ()));
				
				camera.move(-camera.getMaxZoom(4.0D), 0.0D, 0.0D);
			}
		}
	}
	
	public OverlayManager getOverlayManager() {
		return this.overlayManager;
	}
	
	public FirstPersonRenderer getFirstPersonRenderer() {
		return firstPersonRenderer;
	}
	
	public void upSlideSkillUI() {
		this.battleModeUI.slideUp();
	}
	
	public void downSlideSkillUI() {
		this.battleModeUI.slideDown();
	}
	
	@Mod.EventBusSubscriber(modid = EpicFightMod.MODID, value = Dist.CLIENT)
	public static class Events {
		static RenderEngine renderEngine;
		
		@SubscribeEvent
		public static void renderLivingEvent(RenderLivingEvent.Pre<? extends LivingEntity, ? extends EntityModel<? extends LivingEntity>> event) {
			LivingEntity livingentity = event.getEntity();
			
			if (livingentity.level() == null) {
				return;
			}
			
			if (renderEngine.hasRendererFor(livingentity)) {
				LivingEntityPatch<?> entitypatch = EpicFightCapabilities.getEntityPatch(livingentity, LivingEntityPatch.class);
				LocalPlayerPatch playerpatch = null;
				float bodyRotO = 0.0F;
				float bodyRot = 0.0F;

				if (event.getPartialTick() == 1.0F && entitypatch instanceof LocalPlayerPatch localPlayerPatch) {
					playerpatch = localPlayerPatch;
					bodyRotO = playerpatch.prevBodyYaw;
					bodyRot = playerpatch.getBodyYaw();
					playerpatch.prevBodyYaw = livingentity.getYRot();
					playerpatch.setYaw(livingentity.getYRot());

					event.getPoseStack().translate(0, 0.1D, 0);
				}

				if (entitypatch != null && entitypatch.overrideRender()) {
					event.setCanceled(true);
					renderEngine.renderEntityArmatureModel(livingentity, entitypatch, event.getRenderer(), event.getMultiBufferSource(), event.getPoseStack(), event.getPackedLight(), event.getPartialTick());
				}

				if (playerpatch != null) {
					playerpatch.prevBodyYaw = bodyRotO;
					playerpatch.setYaw(bodyRot);
				}
			}
			
			if (ClientEngine.getInstance().getPlayerPatch() != null && !renderEngine.minecraft.options.hideGui && !livingentity.level().getGameRules().getBoolean(EpicFightGamerules.DISABLE_ENTITY_UI)) {
				LivingEntityPatch<?> entitypatch = EpicFightCapabilities.getEntityPatch(livingentity, LivingEntityPatch.class);
				
				for (EntityIndicator entityIndicator : EntityIndicator.ENTITY_INDICATOR_RENDERERS) {
					if (entityIndicator.shouldDraw(livingentity, entitypatch, ClientEngine.getInstance().getPlayerPatch())) {
						entityIndicator.drawIndicator(livingentity, entitypatch, ClientEngine.getInstance().getPlayerPatch(), event.getPoseStack(), event.getMultiBufferSource(), event.getPartialTick());
					}
				}
			}
		}

		@SubscribeEvent
		public static void itemTooltip(ItemTooltipEvent event) {
			if (event.getEntity() != null && event.getEntity().level().isClientSide) {
				CapabilityItem cap = EpicFightCapabilities.getItemStackCapabilityOr(event.getItemStack(), null);
				LocalPlayerPatch playerpatch = EpicFightCapabilities.getEntityPatch(event.getEntity(), LocalPlayerPatch.class);
				
				if (cap != null && playerpatch != null) {
					if (ClientEngine.getInstance().controllEngine.isKeyDown(EpicFightKeyMappings.WEAPON_INNATE_SKILL_TOOLTIP)) {
						Skill weaponInnateSkill = cap.getInnateSkill(playerpatch, event.getItemStack());

						if (weaponInnateSkill != null) {
							event.getToolTip().clear();
							List<Component> skilltooltip = weaponInnateSkill.getTooltipOnItem(event.getItemStack(), cap, playerpatch);

							for (Component s : skilltooltip) {
								event.getToolTip().add(s);
							}
						}
					} else {
						List<Component> tooltip = event.getToolTip();
						cap.modifyItemTooltip(event.getItemStack(), event.getToolTip(), playerpatch);
						
						for (int i = 0; i < tooltip.size(); i++) {
							Component textComp = tooltip.get(i);
							
							if (textComp.getSiblings().size() > 0) {
								Component sibling = textComp.getSiblings().get(0);
								
								if (sibling instanceof MutableComponent mutableComponent && mutableComponent.getContents() instanceof TranslatableContents translatableContent) {
									if (translatableContent.getArgs().length > 1 && translatableContent.getArgs()[1] instanceof MutableComponent mutableComponent$2) {
										if (mutableComponent$2.getContents() instanceof TranslatableContents translatableContent$2) {
											if (translatableContent$2.getKey().equals(Attributes.ATTACK_SPEED.getDescriptionId())) {
												float weaponSpeed = (float)playerpatch.getWeaponAttribute(Attributes.ATTACK_SPEED, event.getItemStack());
												tooltip.remove(i);
												tooltip.add(i, Component.literal(String.format(" %.2f ", playerpatch.getModifiedAttackSpeed(cap, weaponSpeed)))
														.append(Component.translatable(Attributes.ATTACK_SPEED.getDescriptionId())));
												
											} else if (translatableContent$2.getKey().equals(Attributes.ATTACK_DAMAGE.getDescriptionId())) {
												
												float weaponDamage = (float)playerpatch.getWeaponAttribute(Attributes.ATTACK_DAMAGE, event.getItemStack());
												float damageBonus = EnchantmentHelper.getDamageBonus(event.getItemStack(), MobType.UNDEFINED);
												String damageFormat = ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(playerpatch.getModifiedBaseDamage(weaponDamage) + damageBonus);
												
												tooltip.remove(i);
												tooltip.add(i, Component.literal(String.format(" %s ", damageFormat))
																		.append(Component.translatable(Attributes.ATTACK_DAMAGE.getDescriptionId()))
																		.withStyle(ChatFormatting.DARK_GREEN));
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		@SubscribeEvent
		public static void cameraSetupEvent(ViewportEvent.ComputeCameraAngles event) {
			if (renderEngine.zoomCount > 0) {
				renderEngine.setRangedWeaponThirdPerson(event, renderEngine.minecraft.options.getCameraType(), event.getPartialTick());
				
				if (renderEngine.zoomOutTimer > 0) {
					renderEngine.zoomOutTimer--;
				} else {
					renderEngine.zoomCount = renderEngine.aiming ? renderEngine.zoomCount + 1 : renderEngine.zoomCount - 1;
				}
				
				renderEngine.zoomCount = Math.min(renderEngine.zoomMaxCount, renderEngine.zoomCount);
			}
			
			renderEngine.correctCamera(event, (float)event.getPartialTick());
		}
		
		@SubscribeEvent
		public static void fogEvent(ViewportEvent.RenderFog event) {
		}
		
		@SubscribeEvent
		public static void renderGui(RenderGuiEvent.Pre event) {
			Window window = Minecraft.getInstance().getWindow();
			LocalPlayerPatch playerpatch = ClientEngine.getInstance().getPlayerPatch();
			
			if (playerpatch != null) {
				for (SkillContainer skillContainer : playerpatch.getSkillCapability().skillContainers) {
					if (skillContainer.getSkill() != null) {
						skillContainer.getSkill().onScreen(playerpatch, window.getGuiScaledWidth(), window.getGuiScaledHeight());
					}
				}
				
				renderEngine.overlayManager.renderTick(window.getGuiScaledWidth(), window.getGuiScaledHeight());
				
				if (Minecraft.renderNames() && !(Minecraft.getInstance().screen instanceof UISetupScreen)) {
					renderEngine.battleModeUI.renderGui(playerpatch, event.getGuiGraphics(), event.getPartialTick());
				}
			}
		}
		
		@SubscribeEvent
		public static void renderGameOverlayPost(CustomizeGuiOverlayEvent.BossEventProgress event) {
			if (event.getBossEvent().getName().getString().equals("Ender Dragon")) {
				if (EnderDragonPatch.INSTANCE_CLIENT != null) {
					EnderDragonPatch dragonpatch = EnderDragonPatch.INSTANCE_CLIENT;
					float stunShield = dragonpatch.getStunShield();
					GuiGraphics guiGraphics = event.getGuiGraphics();
					
					if (stunShield > 0) {
						float progression = stunShield / dragonpatch.getMaxStunShield();
						int x = event.getX();
						int y = event.getY();
						
						RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
						guiGraphics.blit(BossHealthOverlay.GUI_BARS_LOCATION, x, y + 6, 183, 2, 0, 45.0F, 182, 6, 255, 255);
						guiGraphics.blit(BossHealthOverlay.GUI_BARS_LOCATION, x + (int)(183 * progression), y + 6, (int)(183 * (1.0F - progression)), 2, 0, 39.0F, 182, 6, 255, 255);
					}
				}
			}
		}
		
		@SuppressWarnings("unchecked")
		@SubscribeEvent
		public static void renderHand(RenderHandEvent event) {
			LocalPlayerPatch playerpatch = ClientEngine.getInstance().getPlayerPatch();
			
			if (playerpatch != null) {
				boolean isBattleMode = playerpatch.isBattleMode();
				
				if (isBattleMode || !EpicFightMod.CLIENT_CONFIGS.filterAnimation.getValue()) {
					if (event.getHand() == InteractionHand.MAIN_HAND) {
						renderEngine.firstPersonRenderer.render(playerpatch.getOriginal(), playerpatch, (LivingEntityRenderer)renderEngine.minecraft.getEntityRenderDispatcher().getRenderer(playerpatch.getOriginal()),
								event.getMultiBufferSource(), event.getPoseStack(), event.getPackedLight(), event.getPartialTick());
					}
					
					event.setCanceled(true);
				}
			}
		}

		@SubscribeEvent
		public static void renderWorldLast(RenderLevelStageEvent event) {
			if (renderEngine.zoomCount > 0 && renderEngine.minecraft.options.getCameraType() == CameraType.THIRD_PERSON_BACK &&
					event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
				renderEngine.aimHelper.doRender(event.getPoseStack(), event.getPartialTick());
			}
			/**
			if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_WEATHER) {
				renderEngine.betaWarningMessage.drawMessage(event.getPoseStack());
			}**/
		}
		
		@SuppressWarnings("unchecked")
		@SubscribeEvent
		public static void renderEnderDragonEvent(RenderEnderDragonEvent event) {
			EnderDragon livingentity = event.getEntity();
			
			if (renderEngine.hasRendererFor(livingentity)) {
				EnderDragonPatch entitypatch = EpicFightCapabilities.getEntityPatch(livingentity, EnderDragonPatch.class);
				
				if (entitypatch != null) {
					event.setCanceled(true);
					renderEngine.getEntityRenderer(livingentity).render(livingentity, entitypatch, event.getRenderer(), event.getBuffers(), event.getPoseStack(), event.getLight(), event.getPartialRenderTick());
				}
			}
		}
	}
}
