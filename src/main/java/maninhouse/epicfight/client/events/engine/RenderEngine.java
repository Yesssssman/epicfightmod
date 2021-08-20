package maninhouse.epicfight.client.events.engine;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;

import maninhouse.epicfight.capabilities.ModCapabilities;
import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.capabilities.entity.mob.EvokerData;
import maninhouse.epicfight.capabilities.entity.mob.HoglinData;
import maninhouse.epicfight.capabilities.entity.mob.PiglinBruteData;
import maninhouse.epicfight.capabilities.entity.mob.PiglinData;
import maninhouse.epicfight.capabilities.entity.mob.PillagerData;
import maninhouse.epicfight.capabilities.entity.mob.SkeletonData;
import maninhouse.epicfight.capabilities.entity.mob.VindicatorData;
import maninhouse.epicfight.capabilities.entity.mob.WitchData;
import maninhouse.epicfight.capabilities.entity.mob.WitherSkeletonData;
import maninhouse.epicfight.capabilities.entity.mob.ZoglinData;
import maninhouse.epicfight.capabilities.entity.mob.ZombieData;
import maninhouse.epicfight.capabilities.item.CapabilityItem;
import maninhouse.epicfight.client.ClientEngine;
import maninhouse.epicfight.client.capabilites.entity.ClientPlayerData;
import maninhouse.epicfight.client.gui.BattleModeGui;
import maninhouse.epicfight.client.gui.EntityIndicator;
import maninhouse.epicfight.client.input.ModKeys;
import maninhouse.epicfight.client.renderer.AimHelperRenderer;
import maninhouse.epicfight.client.renderer.FirstPersonRenderer;
import maninhouse.epicfight.client.renderer.ModRenderTypes;
import maninhouse.epicfight.client.renderer.entity.ArmatureRenderer;
import maninhouse.epicfight.client.renderer.entity.CaveSpiderRenderer;
import maninhouse.epicfight.client.renderer.entity.ClientPlayerRenderer;
import maninhouse.epicfight.client.renderer.entity.CreeperRenderer;
import maninhouse.epicfight.client.renderer.entity.DrownedRenderer;
import maninhouse.epicfight.client.renderer.entity.EndermanRenderer;
import maninhouse.epicfight.client.renderer.entity.HoglinRenderer;
import maninhouse.epicfight.client.renderer.entity.IronGolemRenderer;
import maninhouse.epicfight.client.renderer.entity.RavagerRenderer;
import maninhouse.epicfight.client.renderer.entity.SimpleTextureBipedRenderer;
import maninhouse.epicfight.client.renderer.entity.SpiderRenderer;
import maninhouse.epicfight.client.renderer.entity.VexRenderer;
import maninhouse.epicfight.client.renderer.entity.ZombieVillagerRenderer;
import maninhouse.epicfight.client.renderer.item.RenderBow;
import maninhouse.epicfight.client.renderer.item.RenderCrossbow;
import maninhouse.epicfight.client.renderer.item.RenderElytra;
import maninhouse.epicfight.client.renderer.item.RenderHat;
import maninhouse.epicfight.client.renderer.item.RenderItemBase;
import maninhouse.epicfight.client.renderer.item.RenderKatana;
import maninhouse.epicfight.client.renderer.item.RenderShield;
import maninhouse.epicfight.client.renderer.item.RenderTrident;
import maninhouse.epicfight.config.CapabilityConfig;
import maninhouse.epicfight.config.CapabilityConfig.CustomEntityConfig;
import maninhouse.epicfight.item.ModItems;
import maninhouse.epicfight.main.EpicFightMod;
import maninhouse.epicfight.skill.SkillContainer;
import maninhouse.epicfight.utils.math.OpenMatrix4f;
import maninhouse.epicfight.utils.math.Vec3f;
import maninhouse.epicfight.utils.math.Vec4f;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.EvokerEntity;
import net.minecraft.entity.monster.HoglinEntity;
import net.minecraft.entity.monster.HuskEntity;
import net.minecraft.entity.monster.PillagerEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.StrayEntity;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.monster.ZoglinEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.monster.ZombifiedPiglinEntity;
import net.minecraft.entity.monster.piglin.PiglinBruteEntity;
import net.minecraft.entity.monster.piglin.PiglinEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.TridentItem;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.client.event.EntityViewRenderEvent.RenderFogEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@OnlyIn(Dist.CLIENT)
public class RenderEngine {
	private static final Vec3f AIMING_CORRECTION = new Vec3f(-1.5F, 0.0F, 1.25F);
	public static final ResourceLocation NULL_TEXTURE = new ResourceLocation(EpicFightMod.MODID, "textures/gui/null.png");
	public AimHelperRenderer aimHelper;
	public BattleModeGui guiSkillBar = new BattleModeGui();
	private Minecraft minecraft;
	private EntityRendererManager entityRendererManager;
	private OpenMatrix4f projectionMatrix;
	private Map<EntityType<?>, ArmatureRenderer> entityRendererMap;
	private Map<Item, RenderItemBase> itemRendererMapByInstance;
	private Map<Class<? extends Item>, RenderItemBase> itemRendererMapByClass;
	private FirstPersonRenderer firstPersonRenderer;
	private boolean aiming;
	private int zoomOutTimer = 0;
	private int zoomCount;
	private int zoomMaxCount = 20;
	
	//public static Collider wanttodraw;
	//public static VisibleMatrix4f matrix;
	
	public RenderEngine() {
		Events.renderEngine = this;
		RenderItemBase.renderEngine = this;
		EntityIndicator.init();
		this.minecraft = Minecraft.getInstance();
		this.entityRendererManager = minecraft.getRenderManager();
		this.entityRendererMap = new HashMap<EntityType<?>, ArmatureRenderer>();
		this.itemRendererMapByInstance = new HashMap<Item, RenderItemBase>();
		this.itemRendererMapByClass = new HashMap<Class<? extends Item>, RenderItemBase>();
		this.projectionMatrix = new OpenMatrix4f();
		this.firstPersonRenderer = new FirstPersonRenderer();
		this.minecraft.getRenderTypeBuffers().fixedBuffers.put(ModRenderTypes.getEnchantedArmor(), new BufferBuilder(ModRenderTypes.getEnchantedArmor().getBufferSize()));
	}
	
	public void buildRenderer() {
		this.entityRendererMap.put(EntityType.CREEPER, new CreeperRenderer());
		this.entityRendererMap.put(EntityType.ENDERMAN, new EndermanRenderer());
		this.entityRendererMap.put(EntityType.ZOMBIE, new SimpleTextureBipedRenderer<ZombieEntity, ZombieData<ZombieEntity>>("textures/entity/zombie/zombie.png"));
		this.entityRendererMap.put(EntityType.ZOMBIE_VILLAGER, new ZombieVillagerRenderer());
		this.entityRendererMap.put(EntityType.ZOMBIFIED_PIGLIN, new SimpleTextureBipedRenderer<ZombifiedPiglinEntity, ZombieData<ZombifiedPiglinEntity>>(EpicFightMod.MODID + ":textures/entity/zombified_piglin.png"));
		this.entityRendererMap.put(EntityType.HUSK, new SimpleTextureBipedRenderer<HuskEntity, ZombieData<HuskEntity>>("textures/entity/zombie/husk.png"));
		this.entityRendererMap.put(EntityType.SKELETON, new SimpleTextureBipedRenderer<SkeletonEntity, SkeletonData<SkeletonEntity>>("textures/entity/skeleton/skeleton.png"));
		this.entityRendererMap.put(EntityType.WITHER_SKELETON, new SimpleTextureBipedRenderer<WitherSkeletonEntity, WitherSkeletonData>("textures/entity/skeleton/wither_skeleton.png"));
		this.entityRendererMap.put(EntityType.STRAY, new SimpleTextureBipedRenderer<StrayEntity, SkeletonData<StrayEntity>>("textures/entity/skeleton/stray.png"));
		this.entityRendererMap.put(EntityType.PLAYER, new ClientPlayerRenderer());
		this.entityRendererMap.put(EntityType.SPIDER, new SpiderRenderer());
		this.entityRendererMap.put(EntityType.CAVE_SPIDER, new CaveSpiderRenderer());
		this.entityRendererMap.put(EntityType.IRON_GOLEM, new IronGolemRenderer());
		this.entityRendererMap.put(EntityType.VINDICATOR, new SimpleTextureBipedRenderer<MobEntity, VindicatorData>("textures/entity/illager/vindicator.png", new EquipmentSlotType[0]));
		this.entityRendererMap.put(EntityType.EVOKER, new SimpleTextureBipedRenderer<EvokerEntity, EvokerData>("textures/entity/illager/evoker.png"));
		this.entityRendererMap.put(EntityType.WITCH, new SimpleTextureBipedRenderer<WitchEntity, WitchData>(EpicFightMod.MODID + ":textures/entity/witch.png"));
		this.entityRendererMap.put(EntityType.DROWNED, new DrownedRenderer());
		this.entityRendererMap.put(EntityType.PILLAGER, new SimpleTextureBipedRenderer<PillagerEntity, PillagerData>("textures/entity/illager/pillager.png"));
		this.entityRendererMap.put(EntityType.RAVAGER, new RavagerRenderer());
		this.entityRendererMap.put(EntityType.VEX, new VexRenderer());
		this.entityRendererMap.put(EntityType.PIGLIN, new SimpleTextureBipedRenderer<PiglinEntity, PiglinData>("textures/entity/piglin/piglin.png"));
		this.entityRendererMap.put(EntityType.field_242287_aj, new SimpleTextureBipedRenderer<PiglinBruteEntity, PiglinBruteData>(EpicFightMod.MODID + ":textures/entity/piglin_brute.png"));
		this.entityRendererMap.put(EntityType.HOGLIN, new HoglinRenderer<HoglinEntity, HoglinData>("textures/entity/hoglin/hoglin.png"));
		this.entityRendererMap.put(EntityType.ZOGLIN, new HoglinRenderer<ZoglinEntity, ZoglinData>("textures/entity/hoglin/zoglin.png"));
		
		for (Map.Entry<ResourceLocation, CustomEntityConfig> config : CapabilityConfig.CUSTOM_ENTITY_MAP.entrySet()) {
			EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(config.getKey());
			if (entityType != null) {
				switch (config.getValue().getEntityAIType()) {
				case ZOMBIE:
					this.entityRendererMap.put(entityType, new SimpleTextureBipedRenderer<MobEntity, ZombieData<MobEntity>>(config.getValue().getEntityTextureLocation()));
					break;
				case SKELETON:
					this.entityRendererMap.put(entityType, new SimpleTextureBipedRenderer<MobEntity, SkeletonData<MobEntity>>(config.getValue().getEntityTextureLocation()));
					break;
				case CREEPER:
					this.entityRendererMap.put(entityType, new CreeperRenderer(new ResourceLocation(config.getValue().getEntityTextureLocation())));
					break;
				case SPIDER:
					this.entityRendererMap.put(entityType, new SpiderRenderer(new ResourceLocation(config.getValue().getEntityTextureLocation())));
					break;
				case VINDICATOR:
					this.entityRendererMap.put(entityType, new SimpleTextureBipedRenderer<MobEntity, VindicatorData>(config.getValue().getEntityTextureLocation(), new EquipmentSlotType[0]));
					break;
				default:
					
				}
			}
		}
		
		RenderBow bowRenderer = new RenderBow();
		RenderCrossbow crossbowRenderer = new RenderCrossbow();
		RenderElytra elytraRenderer = new RenderElytra();
		RenderHat hatRenderer = new RenderHat();
		RenderKatana katanaRenderer = new RenderKatana();
		RenderShield shieldRenderer = new RenderShield();
		RenderTrident tridentRenderer = new RenderTrident();
		
		this.itemRendererMapByInstance.put(Items.AIR, new RenderItemBase());
		this.itemRendererMapByInstance.put(Items.BOW, bowRenderer);
		this.itemRendererMapByInstance.put(Items.SHIELD, shieldRenderer);
		this.itemRendererMapByInstance.put(Items.ELYTRA, elytraRenderer);
		this.itemRendererMapByInstance.put(Items.CREEPER_HEAD, hatRenderer);
		this.itemRendererMapByInstance.put(Items.DRAGON_HEAD, hatRenderer);
		this.itemRendererMapByInstance.put(Items.PLAYER_HEAD, hatRenderer);
		this.itemRendererMapByInstance.put(Items.ZOMBIE_HEAD, hatRenderer);
		this.itemRendererMapByInstance.put(Items.SKELETON_SKULL, hatRenderer);
		this.itemRendererMapByInstance.put(Items.WITHER_SKELETON_SKULL, hatRenderer);
		this.itemRendererMapByInstance.put(Items.CARVED_PUMPKIN, hatRenderer);
		this.itemRendererMapByInstance.put(Items.CROSSBOW, crossbowRenderer);
		this.itemRendererMapByInstance.put(Items.TRIDENT, tridentRenderer);
		this.itemRendererMapByInstance.put(ModItems.KATANA.get(), katanaRenderer);
		this.itemRendererMapByClass.put(BlockItem.class, hatRenderer);
		this.itemRendererMapByClass.put(BowItem.class, bowRenderer);
		this.itemRendererMapByClass.put(CrossbowItem.class, crossbowRenderer);
		this.itemRendererMapByClass.put(ElytraItem.class, elytraRenderer);
		this.itemRendererMapByClass.put(ShieldItem.class, shieldRenderer);
		this.itemRendererMapByClass.put(TridentItem.class, tridentRenderer);
		this.aimHelper = new AimHelperRenderer();
	}
	
	public RenderItemBase getItemRenderer(Item item) {
		RenderItemBase renderItem = itemRendererMapByInstance.get(item);
		if (renderItem == null) {
			renderItem = this.findMatchingRendererByClass(item.getClass());
			if (renderItem == null) {
				renderItem = itemRendererMapByInstance.get(Items.AIR);
			}
			this.itemRendererMapByInstance.put(item, renderItem);
		}
		
		return renderItem;
	}

	private RenderItemBase findMatchingRendererByClass(Class<?> clazz) {
		RenderItemBase renderer = null;
		for (; clazz != null && renderer == null; clazz = clazz.getSuperclass())
			renderer = itemRendererMapByClass.getOrDefault(clazz, null);
		
		return renderer;
	}
	
	@SuppressWarnings("unchecked")
	public void renderEntityArmatureModel(LivingEntity livingEntity, LivingData<?> entitydata, EntityRenderer<? extends Entity> renderer, IRenderTypeBuffer buffer, MatrixStack matStack, int packedLightIn, float partialTicks) {
		this.entityRendererMap.get(livingEntity.getType()).render(livingEntity, entitydata, renderer, buffer, matStack, packedLightIn, partialTicks);
	}
	
	public boolean isEntityContained(Entity entity) {
		return this.entityRendererMap.containsKey(entity.getType());
	}
	
	public void zoomIn() {
		aiming = true;
		zoomCount = zoomCount == 0 ? 1 : zoomCount;
		zoomOutTimer = 0;
	}

	public void zoomOut(int timer) {
		aiming = false;
		zoomOutTimer = timer;
	}
	
	private void updateCameraInfo(CameraSetup event, PointOfView pov, double partialTicks) {
		if (ClientEngine.INSTANCE.getPlayerData() == null) {
			return;
		}
		
		ActiveRenderInfo info = event.getInfo();
		Entity entity = minecraft.getRenderViewEntity();
		Vector3d vector = info.getProjectedView();
		double totalX = vector.getX();
		double totalY = vector.getY();
		double totalZ = vector.getZ();
		if (pov == PointOfView.THIRD_PERSON_BACK && zoomCount > 0) {
			double posX = info.getProjectedView().x;
			double posY = info.getProjectedView().y;
			double posZ = info.getProjectedView().z;
			double entityPosX = entity.lastTickPosX + (entity.getPosX() - entity.lastTickPosX) * partialTicks;
			double entityPosY = entity.lastTickPosY + (entity.getPosY() - entity.lastTickPosY) * partialTicks + entity.getEyeHeight();
			double entityPosZ = entity.lastTickPosZ + (entity.getPosZ() - entity.lastTickPosZ) * partialTicks;
			float intpol = pov == PointOfView.THIRD_PERSON_BACK ? ((float) zoomCount / (float) zoomMaxCount) : 0;
			Vec3f interpolatedCorrection = new Vec3f(AIMING_CORRECTION.x * intpol, AIMING_CORRECTION.y * intpol, AIMING_CORRECTION.z * intpol);
			OpenMatrix4f rotationMatrix = ClientEngine.INSTANCE.getPlayerData().getMatrix((float)partialTicks);
			Vec4f rotateVec = OpenMatrix4f.transform(rotationMatrix, new Vec4f(interpolatedCorrection.x, interpolatedCorrection.y, interpolatedCorrection.z, 1.0F), null);
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
				RayTraceResult raytraceresult = minecraft.world.rayTraceBlocks(new RayTraceContext(new Vector3d(entityPosX + f, entityPosY + f1, entityPosZ + f2),
							new Vector3d(d00 + f + f2, d11 + f1, d22 + f2), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, entity));
				if (raytraceresult != null) {
					double d7 = raytraceresult.getHitVec().distanceTo(new Vector3d(entityPosX, entityPosY, entityPosZ));
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
		
		info.setPosition(totalX, totalY, totalZ);
		FloatBuffer fb = GLAllocation.createDirectFloatBuffer(16);
		GL11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, fb);
		this.projectionMatrix.load(fb.asReadOnlyBuffer());
	}

	public OpenMatrix4f getCurrentProjectionMatrix() {
		return this.projectionMatrix;
	}

	@Mod.EventBusSubscriber(modid = EpicFightMod.MODID, value = Dist.CLIENT)
	public static class Events {
		static RenderEngine renderEngine;
		@SubscribeEvent
		public static void renderLivingEvent(RenderLivingEvent.Pre<? extends LivingEntity, ? extends EntityModel<? extends LivingEntity>> event) {
			LivingEntity livingentity = event.getEntity();
			if (renderEngine.isEntityContained(livingentity)) {
				if (livingentity instanceof ClientPlayerEntity && event.getPartialRenderTick() == 1.0F) {
					return;
				}
				
				LivingData<?> entitydata = (LivingData<?>) livingentity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				
				if (entitydata != null && !entitydata.shouldSkipRender()) {
					event.setCanceled(true);
					renderEngine.renderEntityArmatureModel(livingentity, entitydata, event.getRenderer(), event.getBuffers(), event.getMatrixStack(), event.getLight(), event.getPartialRenderTick());
				}
			}
			
			if (!Minecraft.getInstance().gameSettings.hideGUI) {
				for (EntityIndicator entityIndicator : EntityIndicator.ENTITY_INDICATOR_RENDERERS) {
					if (entityIndicator.shouldDraw(event.getEntity())) {
						entityIndicator.drawIndicator(event.getEntity(), event.getMatrixStack(), event.getBuffers(), event.getPartialRenderTick());
					}
				}
			}
		}

		@SubscribeEvent
		public static void itemTooltip(ItemTooltipEvent event) {
			if (event.getPlayer() != null) {
				CapabilityItem cap = ModCapabilities.getItemStackCapability(event.getItemStack());
				ClientPlayerData playerdata = (ClientPlayerData) event.getPlayer().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				
				if (cap != null && playerdata != null) {
					if (ClientEngine.INSTANCE.inputController.isKeyDown(ModKeys.SPECIAL_SKILL_TOOLTIP)) {
						if (cap.getSpecialAttack(playerdata) != null) {
							event.getToolTip().clear();
							List<ITextComponent> skilltooltip = cap.getSpecialAttack(playerdata).getTooltipOnItem(event.getItemStack(), cap, playerdata);
							for (ITextComponent s : skilltooltip) {
								event.getToolTip().add(s);
							}
						}
					} else {
						List<ITextComponent> tooltip = event.getToolTip();
						cap.modifyItemTooltip(event.getToolTip(), playerdata);
						
						for (int i = 0; i < tooltip.size(); i++) {
							ITextComponent textComp = tooltip.get(i);
							if (textComp.getSiblings().size() > 0) {
								ITextComponent sibling = textComp.getSiblings().get(0);
								if (sibling instanceof TranslationTextComponent) {
									TranslationTextComponent translationComponent = (TranslationTextComponent)sibling;
									if (translationComponent.getFormatArgs().length > 1 &&
											translationComponent.getFormatArgs()[1] instanceof TranslationTextComponent) {
										if (((TranslationTextComponent)translationComponent.getFormatArgs()[1]).getKey().equals(Attributes.ATTACK_SPEED.getAttributeName())) {
											float weaponSpeed = (float)playerdata.getOriginalEntity().getAttribute(Attributes.ATTACK_SPEED).getBaseValue();
											
											for (AttributeModifier modifier : event.getItemStack().getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_SPEED)) {
												weaponSpeed += modifier.getAmount();
											}
											
											tooltip.remove(i);
											tooltip.add(i, new StringTextComponent(String.format(" %.2f ", playerdata.getAttackSpeed(cap, weaponSpeed)))
													.append(new TranslationTextComponent(Attributes.ATTACK_SPEED.getAttributeName())));
										} else if (((TranslationTextComponent)translationComponent.getFormatArgs()[1]).getKey().equals(Attributes.ATTACK_DAMAGE.getAttributeName())) {
											float weaponDamage = (float)playerdata.getOriginalEntity().getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue();
											weaponDamage += EnchantmentHelper.getModifierForCreature(event.getItemStack(), CreatureAttribute.UNDEFINED);
											
											for (AttributeModifier modifier : event.getItemStack().getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_DAMAGE)) {
												weaponDamage += modifier.getAmount();
											}
											
											tooltip.remove(i);
											tooltip.add(i, new StringTextComponent(String.format(" %.0f ", playerdata.getDamageToEntity(null, null, weaponDamage)))
													.append(new TranslationTextComponent(Attributes.ATTACK_DAMAGE.getAttributeName())).mergeStyle(TextFormatting.DARK_GREEN));
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
		public static void cameraSetupEvent(CameraSetup event) {
			renderEngine.updateCameraInfo(event, Minecraft.getInstance().gameSettings.getPointOfView(), event.getRenderPartialTicks());
			if (renderEngine.zoomCount > 0) {
				if (renderEngine.zoomOutTimer > 0) {
					renderEngine.zoomOutTimer--;
				} else {
					renderEngine.zoomCount = renderEngine.aiming ? renderEngine.zoomCount + 1 : renderEngine.zoomCount - 1;
				}
				renderEngine.zoomCount = Math.min(renderEngine.zoomMaxCount, renderEngine.zoomCount);
			}
		}
		
		@SubscribeEvent
		public static void fogEvent(RenderFogEvent event) {
			/**
			 * GlStateManager.fogMode(FogMode.LINEAR); GlStateManager.fogStart(0.0F);
			 * GlStateManager.fogEnd(75.0F); GlStateManager.fogDensity(0.1F);
			 ***/
		}
		
		@SubscribeEvent
		public static void renderGameOverlay(RenderGameOverlayEvent.Pre event) {
			if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
		        MainWindow sr = Minecraft.getInstance().getMainWindow();
				ClientPlayerData playerdata = ClientEngine.INSTANCE.getPlayerData();
				
				if (playerdata != null) {
					for (SkillContainer skillContainer : playerdata.getSkillCapability().skills) {
						if (skillContainer.getContaining() != null) {
							skillContainer.getContaining().onScreen(playerdata, sr.getScaledWidth(), sr.getScaledHeight());
						}
					}
					
					if (Minecraft.isGuiEnabled()) {
						renderEngine.guiSkillBar.renderGui(playerdata, event.getPartialTicks());
					}
				}
			}
		}
		
		@SubscribeEvent
		public static void renderHand(RenderHandEvent event) {
			boolean isBattleMode = ClientEngine.INSTANCE.isBattleMode();
			if (isBattleMode) {
				if (event.getHand() == Hand.MAIN_HAND) {
					if (ClientEngine.INSTANCE.getPlayerData() != null) {
						renderEngine.firstPersonRenderer.render(Minecraft.getInstance().player, ClientEngine.INSTANCE.getPlayerData(), null, event.getBuffers(),
								event.getMatrixStack(), event.getLight(), event.getPartialTicks());
					}
				}
				event.setCanceled(true);
			}
		}
		
		@SubscribeEvent
		public static void renderWorldLast(RenderWorldLastEvent event) {
			if (renderEngine.zoomCount > 0 && renderEngine.minecraft.gameSettings.getPointOfView() == PointOfView.THIRD_PERSON_BACK) {
				renderEngine.aimHelper.doRender(event.getMatrixStack(), event.getPartialTicks());
			}
		}
	}
}