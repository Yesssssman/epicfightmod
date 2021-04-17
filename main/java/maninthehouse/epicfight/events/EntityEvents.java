package maninthehouse.epicfight.events;

import java.util.List;

import com.google.common.collect.Lists;

import maninthehouse.epicfight.animation.types.StaticAnimation;
import maninthehouse.epicfight.capabilities.ModCapabilities;
import maninthehouse.epicfight.capabilities.entity.CapabilityEntity;
import maninthehouse.epicfight.capabilities.entity.IRangedAttackMobCapability;
import maninthehouse.epicfight.capabilities.entity.LivingData;
import maninthehouse.epicfight.capabilities.entity.mob.BipedMobData;
import maninthehouse.epicfight.capabilities.entity.mob.EndermanData;
import maninthehouse.epicfight.capabilities.entity.player.ServerPlayerData;
import maninthehouse.epicfight.capabilities.item.CapabilityItem;
import maninthehouse.epicfight.effects.ModEffects;
import maninthehouse.epicfight.gamedata.Animations;
import maninthehouse.epicfight.main.EpicFightMod;
import maninthehouse.epicfight.network.ModNetworkManager;
import maninthehouse.epicfight.network.client.CTSPlayAnimation;
import maninthehouse.epicfight.network.server.STCPlayAnimation;
import maninthehouse.epicfight.network.server.STCPotion;
import maninthehouse.epicfight.network.server.STCPotion.Action;
import maninthehouse.epicfight.utils.game.IExtendedDamageSource;
import maninthehouse.epicfight.utils.game.IndirectDamageSourceExtended;
import maninthehouse.epicfight.utils.game.IExtendedDamageSource.StunType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.CombatRules;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionAddedEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionExpiryEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionRemoveEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber(modid=EpicFightMod.MODID)
public class EntityEvents {
	private static List<CapabilityEntity<?>> unInitializedEntitiesClient = Lists.<CapabilityEntity<?>>newArrayList();
	private static List<CapabilityEntity<?>> unInitializedEntitiesServer = Lists.<CapabilityEntity<?>>newArrayList();
	
	@SubscribeEvent
	public static void spawnEvent(EntityJoinWorldEvent event) {
		CapabilityEntity entitydata = event.getEntity().getCapability(ModCapabilities.CAPABILITY_ENTITY, null);
		if(entitydata != null && event.getEntity().ticksExisted == 0) {
			entitydata.onEntityJoinWorld(event.getEntity());
			if(entitydata.isRemote()) {
				unInitializedEntitiesClient.add(entitydata);
			} else {
				unInitializedEntitiesServer.add(entitydata);
			}
		}
	}
	
	@SubscribeEvent
	public static void updateEvent(LivingUpdateEvent event) {
		LivingData<?> entitydata = (LivingData<?>) event.getEntityLiving().getCapability(ModCapabilities.CAPABILITY_ENTITY, null);
		
		if(entitydata != null && entitydata.getOriginalEntity() != null) {
			entitydata.update();
		}
	}
	
	@SubscribeEvent
	public static void knockBackEvent(LivingKnockBackEvent event) {
		CapabilityEntity<?> cap = event.getEntityLiving().getCapability(ModCapabilities.CAPABILITY_ENTITY, null);
		if (cap != null) {
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public static void hurtEvent(LivingHurtEvent event) {
		IExtendedDamageSource extSource = null;
		Entity trueSource = event.getSource().getTrueSource();
		
		if(trueSource != null) {
			if(event.getSource() instanceof IExtendedDamageSource) {
				extSource = (IExtendedDamageSource) event.getSource();
			}
			else if(event.getSource() instanceof EntityDamageSourceIndirect) {
				CapabilityEntity<?> attackerdata = trueSource.getCapability(ModCapabilities.CAPABILITY_ENTITY, null);
				
				if(attackerdata != null) {
					if(attackerdata instanceof IRangedAttackMobCapability) {
						extSource = ((IRangedAttackMobCapability)attackerdata).getRangedDamageSource(event.getSource().getImmediateSource());
					} else if(event.getSource().damageType.equals("arrow")) {
						extSource = new IndirectDamageSourceExtended("arrow", trueSource, event.getSource().getImmediateSource(), StunType.SHORT);
						extSource.setImpact(1.0F);
					}
				}
			}
			
			if(extSource != null) {
				float totalDamage = event.getAmount();
				float ignoreDamage = event.getAmount() * extSource.getArmorIgnoreRatio();
				float calculatedDamage = ignoreDamage;
				EntityLivingBase hitEntity = event.getEntityLiving();
				
			    if(hitEntity.isPotionActive(MobEffects.RESISTANCE)) {
			    	int i = (hitEntity.getActivePotionEffect(MobEffects.RESISTANCE).getAmplifier() + 1) * 5;
			        int j = 25 - i;
			        float f = calculatedDamage * (float)j;
			        calculatedDamage = Math.max(f / 25.0F, 0.0F);
			    }
			    
			    if(calculatedDamage > 0.0F) {
			    	int k = EnchantmentHelper.getEnchantmentModifierDamage(hitEntity.getArmorInventoryList(), event.getSource());
			        if(k > 0) {
			        	calculatedDamage = CombatRules.getDamageAfterMagicAbsorb(calculatedDamage, (float)k);
			        }
			    }
			    
			    float absorpAmount = hitEntity.getAbsorptionAmount() - calculatedDamage;
			    hitEntity.setAbsorptionAmount(Math.max(absorpAmount, 0.0F));
		        
		        if(absorpAmount < 0.0F) {
		        	hitEntity.setHealth(hitEntity.getHealth() + absorpAmount);
		        	LivingData<?> attacker = (LivingData<?>)trueSource.getCapability(ModCapabilities.CAPABILITY_ENTITY, null);
					if(attacker != null)
						attacker.gatherDamageDealt((IExtendedDamageSource)event.getSource(), calculatedDamage);
		        }
		        
				event.setAmount(totalDamage - ignoreDamage);
				
				if(event.getAmount() + ignoreDamage > 0.0F) {
					LivingData<?> hitEntityData = (LivingData<?>)hitEntity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null);
					
					if(hitEntityData != null) {
						StaticAnimation hitAnimation = null;
						float extendStunTime = 0;
						float knockBackAmount = 0;
						float weightReduction = 40.0F / (float)hitEntityData.getWeight();
						
						float currentStunResistance = hitEntityData.getStunArmor();
						if(currentStunResistance > 0) {
							float impact = extSource.getImpact();
							hitEntityData.setStunArmor(currentStunResistance - impact);
						}
						
						switch(extSource.getStunType()) {
						case SHORT:
							if(!hitEntity.isPotionActive(ModEffects.STUN_IMMUNITY) && (hitEntityData.getStunArmor() == 0)) {
								int i = EnchantmentHelper.getKnockbackModifier((EntityLivingBase)trueSource);
								float totalStunTime = (float) ((0.25F + extSource.getImpact() * 0.1F + 0.1F * i) * weightReduction);
								totalStunTime *= (1.0F - hitEntityData.getStunTimeTimeReduction());
								
								if(totalStunTime >= 0.1F) {
									extendStunTime = totalStunTime - 0.1F;
									boolean flag = totalStunTime >= 0.83F;
									StunType stunType = flag ? StunType.LONG : StunType.SHORT;
									extendStunTime = flag ? 0 : extendStunTime;
									hitAnimation = hitEntityData.getHitAnimation(stunType);
									knockBackAmount = totalStunTime;
								}
							}
							break;
						case LONG:
							hitAnimation = hitEntity.isPotionActive(ModEffects.STUN_IMMUNITY) ? null : hitEntityData.getHitAnimation(StunType.LONG);
							knockBackAmount = (extSource.getImpact() * 0.25F) * weightReduction;
							break;
						case HOLD:
							hitAnimation = hitEntityData.getHitAnimation(StunType.SHORT);
							extendStunTime = extSource.getImpact() * 0.1F;
							break;
						}
						
						if(hitAnimation != null) {
							if(!(hitEntity instanceof EntityPlayer)) {
								hitEntityData.lookAttacker(trueSource);
							}
							hitEntityData.setStunTimeReduction();
							hitEntityData.getAnimator().playAnimation(hitAnimation, extendStunTime);
							ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimation(hitAnimation.getId(), hitEntity.getEntityId(), extendStunTime), hitEntity);
							if(hitEntity instanceof EntityPlayerMP) {
								ModNetworkManager.sendToPlayer(new STCPlayAnimation(hitAnimation.getId(), hitEntity.getEntityId(), extendStunTime), (EntityPlayerMP)hitEntity);
							}
						}
						
						hitEntityData.knockBackEntity(trueSource, knockBackAmount);
					}
				}
			}
		}
		
		if(event.getEntityLiving().isHandActive() && event.getEntityLiving().getActiveItemStack().getItem() == Items.SHIELD) {
			if(event.getEntityLiving() instanceof EntityPlayer) {
				event.getEntityLiving().world.playSound((EntityPlayer)event.getEntityLiving(), event.getEntityLiving().getPosition(), SoundEvents.ITEM_SHIELD_BLOCK,
						event.getEntityLiving().getSoundCategory(), 1.0F, 0.8F + event.getEntityLiving().getRNG().nextFloat() * 0.4F);
			}
		}
	}
	
	@SubscribeEvent
	public static void damageEvent(LivingDamageEvent event)
	{
		Entity trueSource = event.getSource().getTrueSource();
		if(event.getSource() instanceof IExtendedDamageSource)
		{
			if(trueSource != null)
			{
				LivingData<?> attacker = (LivingData<?>)trueSource.getCapability(ModCapabilities.CAPABILITY_ENTITY, null);
				if(attacker!=null)
					attacker.gatherDamageDealt((IExtendedDamageSource)event.getSource(), event.getAmount());
			}
		}
	}
	
	@SubscribeEvent
	public static void attackEvent(LivingAttackEvent event)
	{
		LivingData<?> entitydata = (LivingData<?>) event.getEntity().getCapability(ModCapabilities.CAPABILITY_ENTITY, null);
		
		if(entitydata != null && !event.getEntity().world.isRemote && event.getEntityLiving().getHealth() > 0.0F)
		{
			if(!entitydata.attackEntityFrom(event.getSource(), event.getAmount()))
			{
				event.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent
	public static void arrowHitEvent(ProjectileImpactEvent.Arrow event) {
		if (event.getRayTraceResult().entityHit != null && event.getArrow() != null && event.getArrow().shootingEntity != null) {
			if (event.getRayTraceResult().entityHit.equals(event.getArrow().shootingEntity.getRidingEntity())) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void equipChangeEvent(LivingEquipmentChangeEvent event)
	{
		if(event.getFrom().getItem() == event.getTo().getItem()) {
			return;
		}
		
		LivingData<?> entitycap = (LivingData<?>) event.getEntity().getCapability(ModCapabilities.CAPABILITY_ENTITY, null);
		
		if(entitycap != null && entitycap.getOriginalEntity() != null)
		{
			if(event.getSlot() == EntityEquipmentSlot.MAINHAND)
			{
				CapabilityItem fromCap = ModCapabilities.stackCapabilityGetter(event.getFrom());
				CapabilityItem toCap = ModCapabilities.stackCapabilityGetter(event.getTo());
				entitycap.cancelUsingItem();
				
				if(fromCap != null)
					event.getEntityLiving().getAttributeMap().removeAttributeModifiers(fromCap.getAttributeModifiers(event.getSlot(), entitycap));
				if(toCap != null)
					event.getEntityLiving().getAttributeMap().applyAttributeModifiers(toCap.getAttributeModifiers(event.getSlot(), entitycap));
				
				if(entitycap instanceof ServerPlayerData)
				{
					ServerPlayerData playercap = (ServerPlayerData)entitycap;
					playercap.onHeldItemChange(toCap, event.getTo(), EnumHand.MAIN_HAND);
				}
			}
			else if(event.getSlot() == EntityEquipmentSlot.OFFHAND)
			{
				entitycap.cancelUsingItem();
				
				if(entitycap instanceof ServerPlayerData)
				{
					ServerPlayerData playercap = (ServerPlayerData)entitycap;
					CapabilityItem toCap = event.getTo().isEmpty() ? null : entitycap.getHeldItemCapability(EnumHand.MAIN_HAND);
					playercap.onHeldItemChange(toCap, event.getTo(), EnumHand.OFF_HAND);
				}
			}
			else if(event.getSlot().getSlotType() == EntityEquipmentSlot.Type.ARMOR)
			{
				CapabilityItem fromCap = ModCapabilities.stackCapabilityGetter(event.getFrom());
				CapabilityItem toCap = ModCapabilities.stackCapabilityGetter(event.getTo());
				
				if(fromCap != null) {
					event.getEntityLiving().getAttributeMap().removeAttributeModifiers(fromCap.getAttributeModifiers(event.getSlot(), entitycap));
				}
				
				if(toCap != null) {
					event.getEntityLiving().getAttributeMap().applyAttributeModifiers(toCap.getAttributeModifiers(event.getSlot(), entitycap));
				}
				
				LivingData<?> entitydata = (LivingData<?>) event.getEntity().getCapability(ModCapabilities.CAPABILITY_ENTITY, null);
				entitydata.onArmorSlotChanged(fromCap, toCap, event.getSlot());
			}
		}
	}
	
	@SubscribeEvent
	public static void effectAddEvent(PotionAddedEvent event) {
		if(!event.getEntity().world.isRemote) {
			ModNetworkManager.sendToAll(new STCPotion(event.getPotionEffect().getPotion(), Action.Active, event.getEntity().getEntityId()));
		}
	}
	
	@SubscribeEvent
	public static void effectRemoveEvent(PotionRemoveEvent event) {
		if(!event.getEntity().world.isRemote && event.getPotionEffect() != null) {
			ModNetworkManager.sendToAll(new STCPotion(event.getPotionEffect().getPotion(), Action.Remove, event.getEntity().getEntityId()));
		}
	}
	
	@SubscribeEvent
	public static void effectExpiryEvent(PotionExpiryEvent event) {
		if(!event.getEntity().world.isRemote) {
			ModNetworkManager.sendToAll(new STCPotion(event.getPotionEffect().getPotion(), Action.Remove, event.getEntity().getEntityId()));
		}
	}
	
	@SubscribeEvent
	public static void mountEvent(EntityMountEvent event) {
		CapabilityEntity<?> mountEntity = event.getEntityMounting().getCapability(ModCapabilities.CAPABILITY_ENTITY, null);

		if (!event.getWorldObj().isRemote && mountEntity instanceof BipedMobData && mountEntity.getOriginalEntity() != null) {
			if (event.getEntityBeingMounted() instanceof EntityCreature) {
				((BipedMobData<?>) mountEntity).onMount(event.isMounting(), event.getEntityBeingMounted());
			}
		}
	}
	
	@SubscribeEvent
	public static void tpEvent(EnderTeleportEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (event.getEntityLiving() instanceof EntityEnderman) {
			EntityEnderman enderman = (EntityEnderman)entity;
			EndermanData endermandata = (EndermanData) enderman.getCapability(ModCapabilities.CAPABILITY_ENTITY, null);
			
			if (endermandata != null) {
				if (endermandata.isInaction()) {
					for (Entity collideEntity : enderman.world.getEntitiesWithinAABB(Entity.class, enderman.getEntityBoundingBox().grow(0.2D, 0.2D, 0.2D))) {
						if (collideEntity instanceof IProjectile) {
	                    	return;
	                    }
	                }
					
					event.setCanceled(true);
				} else if (endermandata.isRaging()) {
					event.setCanceled(true);
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void jumpEvent(LivingJumpEvent event) {
		LivingData<?> entitydata = (LivingData<?>) event.getEntity().getCapability(ModCapabilities.CAPABILITY_ENTITY, null);
		
		if (entitydata != null && entitydata.isRemote()) {
			if (!entitydata.isInaction() && !event.getEntity().isInWater()) {
				StaticAnimation jumpAnimation = entitydata.getClientAnimator().getJumpAnimation();
				entitydata.getAnimator().playAnimation(jumpAnimation, 0);
				ModNetworkManager.sendToServer(new CTSPlayAnimation(jumpAnimation.getId(), 0, true, false));
			}
		}
	}
	
	@SubscribeEvent
	public static void deathEvent(LivingDeathEvent event) {
		LivingData<?> entitydata = (LivingData<?>)event.getEntityLiving().getCapability(ModCapabilities.CAPABILITY_ENTITY, null);
		
		if(entitydata != null) {
			entitydata.getAnimator().playDeathAnimation();
		}
	}
	
	@SubscribeEvent
	public static void fallEvent(LivingFallEvent event) {
		if (event.getEntity().world.getGameRules().getBoolean("hasFallAnimation")) {
			LivingData<?> entitydata = (LivingData<?>) event.getEntity().getCapability(ModCapabilities.CAPABILITY_ENTITY, null);
			
			if (entitydata != null && !entitydata.isInaction()) {
				float distance = event.getDistance();

				if (distance > 5.0F) {
					entitydata.getAnimator().playAnimation(Animations.BIPED_LAND_DAMAGE, 0);
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void tickClientEvent(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			for (CapabilityEntity<?> cap : unInitializedEntitiesClient) {
				cap.postInit();
			}
			unInitializedEntitiesClient.clear();
		}
	}
	
	@SubscribeEvent
	public static void tickServerEvent(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			for (CapabilityEntity<?> cap : unInitializedEntitiesServer) {
				cap.postInit();
			}
			unInitializedEntitiesServer.clear();
		}
	}
}