package yesman.epicfight.events;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.command.arguments.EntityAnchorArgument.Type;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Items;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.CombatRules;
import net.minecraft.util.Hand;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.EntityTeleportEvent;
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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.animation.types.StaticAnimation;
import yesman.epicfight.capabilities.ModCapabilities;
import yesman.epicfight.capabilities.entity.CapabilityEntity;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.capabilities.entity.mob.BipedMobData;
import yesman.epicfight.capabilities.entity.mob.EndermanData;
import yesman.epicfight.capabilities.entity.player.ServerPlayerData;
import yesman.epicfight.capabilities.entity.projectile.CapabilityProjectile;
import yesman.epicfight.capabilities.item.CapabilityItem;
import yesman.epicfight.effects.ModEffects;
import yesman.epicfight.entity.eventlistener.TakeDamageEvent;
import yesman.epicfight.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.gamedata.Animations;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.ModNetworkManager;
import yesman.epicfight.network.client.CTSPlayAnimation;
import yesman.epicfight.network.server.STCPotion;
import yesman.epicfight.network.server.STCPotion.Action;
import yesman.epicfight.utils.game.IExtendedDamageSource;
import yesman.epicfight.utils.game.IndirectDamageSourceExtended;
import yesman.epicfight.utils.game.IExtendedDamageSource.StunType;
import yesman.epicfight.world.ModGamerules;

@Mod.EventBusSubscriber(modid=EpicFightMod.MODID)
public class EntityEvents {
	private static List<CapabilityEntity<?>> unInitializedEntitiesClient = Lists.<CapabilityEntity<?>>newArrayList();
	private static List<CapabilityEntity<?>> unInitializedEntitiesServer = Lists.<CapabilityEntity<?>>newArrayList();
	
	@SuppressWarnings("unchecked")
	@SubscribeEvent
	public static void spawnEvent(EntityJoinWorldEvent event) {
		CapabilityEntity<Entity> entitydata = event.getEntity().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		if (entitydata != null && event.getEntity().ticksExisted == 0) {
			entitydata.onEntityJoinWorld(event.getEntity());
			if (entitydata.isRemote()) {
				unInitializedEntitiesClient.add(entitydata);
			} else {
				unInitializedEntitiesServer.add(entitydata);
			}
		}
		
		if (event.getEntity() instanceof ProjectileEntity) {
			ProjectileEntity projectileentity = (ProjectileEntity)event.getEntity();
			CapabilityProjectile<ProjectileEntity> projectileData = event.getEntity().getCapability(ModCapabilities.CAPABILITY_PROJECTILE, null).orElse(null);
			if (projectileData != null && event.getEntity().ticksExisted == 0) {
				projectileData.onJoinWorld(projectileentity);
			}
		}
	}
	
	@SubscribeEvent
	public static void updateEvent(LivingUpdateEvent event) {
		LivingData<?> entitydata = (LivingData<?>) event.getEntityLiving().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		if (entitydata != null && entitydata.getOriginalEntity() != null) {
			entitydata.update();
		}
	}
	
	@SubscribeEvent
	public static void knockBackEvent(LivingKnockBackEvent event) {
		CapabilityEntity<?> cap = event.getEntityLiving().getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
		if (cap != null) {
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public static void hurtEvent(LivingHurtEvent event) {
		IExtendedDamageSource extSource = null;
		Entity trueSource = event.getSource().getTrueSource();
		
		if (trueSource != null) {
			if (event.getSource() instanceof IExtendedDamageSource) {
				extSource = (IExtendedDamageSource) event.getSource();
			} else if (event.getSource() instanceof IndirectEntityDamageSource && event.getSource().getImmediateSource() != null) {
				CapabilityProjectile<?> projectileCap = event.getSource().getImmediateSource().getCapability(ModCapabilities.CAPABILITY_PROJECTILE, null).orElse(null);
				
				if (projectileCap != null) {
					extSource = new IndirectDamageSourceExtended(event.getSource().damageType, trueSource, event.getSource().getImmediateSource(), StunType.SHORT);
					extSource.setArmorNegation(projectileCap.getArmorNegation());
					extSource.setImpact(projectileCap.getImpact());
				}
			}
			
			if (extSource != null) {
				float totalDamage = event.getAmount();
				float ignoreDamage = event.getAmount() * extSource.getArmorNegation() * 0.01F;
				float calculatedDamage = ignoreDamage;
				LivingEntity hitEntity = event.getEntityLiving();
				
			    if (hitEntity.isPotionActive(Effects.RESISTANCE)) {
			    	int i = (hitEntity.getActivePotionEffect(Effects.RESISTANCE).getAmplifier() + 1) * 5;
			        int j = 25 - i;
			        float f = calculatedDamage * (float)j;
			        float f1 = calculatedDamage;
			        calculatedDamage = Math.max(f / 25.0F, 0.0F);
			        float f2 = f1 - calculatedDamage;
					if (f2 > 0.0F && f2 < 3.4028235E37F) {
			        	if (hitEntity instanceof ServerPlayerEntity) {
			        		((ServerPlayerEntity)hitEntity).addStat(Stats.DAMAGE_RESISTED, Math.round(f2 * 10.0F));
			        	} else if(event.getSource().getTrueSource() instanceof ServerPlayerEntity) {
			                ((ServerPlayerEntity)event.getSource().getTrueSource()).addStat(Stats.DAMAGE_DEALT_RESISTED, Math.round(f2 * 10.0F));
			        	}
			        }
			    }

				if (calculatedDamage > 0.0F) {
					int k = EnchantmentHelper.getEnchantmentModifierDamage(hitEntity.getArmorInventoryList(), event.getSource());
					if (k > 0) {
			        	calculatedDamage = CombatRules.getDamageAfterMagicAbsorb(calculatedDamage, (float)k);
			        }
			    }
			    
			    float absorpAmount = hitEntity.getAbsorptionAmount() - calculatedDamage;
			    hitEntity.setAbsorptionAmount(Math.max(absorpAmount, 0.0F));
		        float realHealthDamage = Math.max(-absorpAmount, 0.0F);
		        if (realHealthDamage > 0.0F && realHealthDamage < 3.4028235E37F && event.getSource().getTrueSource() instanceof ServerPlayerEntity) {
		        	((ServerPlayerEntity)event.getSource().getTrueSource()).addStat(Stats.DAMAGE_DEALT_ABSORBED, Math.round(realHealthDamage * 10.0F));
		        }
		        
				if (absorpAmount < 0.0F) {
					hitEntity.setHealth(hitEntity.getHealth() + absorpAmount);
		        	LivingData<?> attacker = (LivingData<?>)trueSource.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
					if (attacker != null) {
						attacker.gatherDamageDealt(extSource, calculatedDamage);
					}
		        }
		        
				event.setAmount(totalDamage - ignoreDamage);
				if (event.getAmount() + ignoreDamage > 0.0F) {
					LivingData<?> hitEntityData = (LivingData<?>)hitEntity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
					
					if (hitEntityData != null) {
						StaticAnimation hitAnimation = null;
						float extendStunTime = 0;
						float knockBackAmount = 0;
						float weightReduction = 40.0F / (float)hitEntityData.getWeight();
						float stunShield = hitEntityData.getStunShield();
						
						if (stunShield > 0) {
							float impact = extSource.getImpact();
							hitEntityData.setStunShield(stunShield - impact);
						}
						
						switch (extSource.getStunType()) {
						case SHORT:
							if (!hitEntity.isPotionActive(ModEffects.STUN_IMMUNITY.get()) && (hitEntityData.getStunShield() == 0)) {
								int i = trueSource instanceof LivingEntity ? EnchantmentHelper.getKnockbackModifier((LivingEntity)trueSource) : 0;
								float totalStunTime = (float) ((0.25F + (extSource.getImpact() + i) * 0.1F) * weightReduction);
								totalStunTime *= (1.0F - hitEntityData.getStunTimeTimeReduction());
								
								if (totalStunTime >= 0.1F) {
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
							hitAnimation = hitEntity.isPotionActive(ModEffects.STUN_IMMUNITY.get()) ? null : hitEntityData.getHitAnimation(StunType.LONG);
							knockBackAmount = (extSource.getImpact() * 0.25F) * weightReduction;
							break;
						case HOLD:
							hitAnimation = hitEntityData.getHitAnimation(StunType.SHORT);
							extendStunTime = extSource.getImpact() * 0.1F;
							break;
						}
						
						if (hitAnimation != null) {
							if (!(hitEntity instanceof PlayerEntity)) {
								hitEntity.lookAt(Type.FEET, trueSource.getPositionVec());
							}
							hitEntityData.setStunReductionOnHit();
							hitEntityData.playAnimationSynchronize(hitAnimation, extendStunTime);
						}
						hitEntityData.knockBackEntity(trueSource, knockBackAmount);
					}
				}
			}
		}
		
		if (event.getEntityLiving().isHandActive() && event.getEntityLiving().getActiveItemStack().getItem() == Items.SHIELD) {
			if (event.getEntityLiving() instanceof PlayerEntity) {
				event.getEntityLiving().world.playSound((PlayerEntity)event.getEntityLiving(), event.getEntityLiving().getPosition(), SoundEvents.ITEM_SHIELD_BLOCK,
						event.getEntityLiving().getSoundCategory(), 1.0F, 0.8F + event.getEntityLiving().getRNG().nextFloat() * 0.4F);
			}
		}
	}
	
	@SubscribeEvent
	public static void damageEvent(LivingDamageEvent event) {
		Entity trueSource = event.getSource().getTrueSource();
		
		if (event.getEntity() instanceof ServerPlayerEntity) {
			ServerPlayerData serverplayerdata = (ServerPlayerData) event.getEntity().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			serverplayerdata.getEventListener().activateEvents(EventType.TAKE_DAMAGE_EVENT, new TakeDamageEvent(serverplayerdata, event));
		}
		
		if (event.getSource() instanceof IExtendedDamageSource) {
			if (trueSource != null) {
				LivingData<?> attacker = (LivingData<?>) trueSource.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				if (attacker != null) {
					attacker.gatherDamageDealt((IExtendedDamageSource) event.getSource(), event.getAmount());
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void livingAttackEvent(LivingAttackEvent event) {
		LivingData<?> entitydata = (LivingData<?>) event.getEntity().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		if (entitydata != null && event.getEntityLiving().getHealth() > 0.0F) {
			if (!entitydata.hurtBy(event)) {
				event.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent
	public static void arrowHitEvent(ProjectileImpactEvent.Arrow event) {
		if (event.getRayTraceResult() instanceof EntityRayTraceResult) {
			EntityRayTraceResult rayresult = ((EntityRayTraceResult) event.getRayTraceResult());
			if (rayresult.getEntity() != null && event.getArrow().getShooter() != null) {
				if (rayresult.getEntity().equals(event.getArrow().getShooter().getRidingEntity())) {
					event.setCanceled(true);
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void equipChangeEvent(LivingEquipmentChangeEvent event) {
		if (event.getFrom().getItem() == event.getTo().getItem()) {
			return;
		}
		
		LivingData<?> entitycap = (LivingData<?>) event.getEntity().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		
		if (entitycap != null && entitycap.getOriginalEntity() != null) {
			if (event.getSlot() == EquipmentSlotType.MAINHAND) {
				CapabilityItem fromCap = ModCapabilities.getItemStackCapability(event.getFrom());
				CapabilityItem toCap = ModCapabilities.getItemStackCapability(event.getTo());
				entitycap.cancelUsingItem();
				
				if (fromCap != null) {
					event.getEntityLiving().getAttributeManager().removeModifiers(fromCap.getAttributeModifiers(event.getSlot(), entitycap));
				}
				
				if (toCap != null) {
					event.getEntityLiving().getAttributeManager().reapplyModifiers(toCap.getAttributeModifiers(event.getSlot(), entitycap));
				}
				
				if (entitycap instanceof ServerPlayerData) {
					ServerPlayerData playercap = (ServerPlayerData)entitycap;
					playercap.updateHeldItem(toCap, event.getTo(), Hand.MAIN_HAND);
				}
			} else if (event.getSlot() == EquipmentSlotType.OFFHAND) {
				entitycap.cancelUsingItem();
				
				if (entitycap instanceof ServerPlayerData) {
					ServerPlayerData playercap = (ServerPlayerData)entitycap;
					playercap.updateHeldItem(entitycap.getHeldItemCapability(Hand.MAIN_HAND), event.getTo(), Hand.OFF_HAND);
				}
			} else if (event.getSlot().getSlotType() == EquipmentSlotType.Group.ARMOR) {
				CapabilityItem fromCap = ModCapabilities.getItemStackCapability(event.getFrom());
				CapabilityItem toCap = ModCapabilities.getItemStackCapability(event.getTo());
				
				if (fromCap != null) {
					event.getEntityLiving().getAttributeManager().removeModifiers(fromCap.getAttributeModifiers(event.getSlot(), entitycap));
				}
				
				if (toCap != null) {
					event.getEntityLiving().getAttributeManager().reapplyModifiers(toCap.getAttributeModifiers(event.getSlot(), entitycap));
				}
				
				LivingData<?> entitydata = (LivingData<?>) event.getEntity().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				entitydata.updateArmor(fromCap, toCap, event.getSlot());
			}
		}
	}
	
	@SubscribeEvent
	public static void effectAddEvent(PotionAddedEvent event) {
		if (!event.getEntity().world.isRemote) {
			ModNetworkManager.sendToAll(new STCPotion(event.getPotionEffect().getPotion(), Action.Active, event.getEntity().getEntityId()));
		}
	}
	
	@SubscribeEvent
	public static void effectRemoveEvent(PotionRemoveEvent event) {
		if (!event.getEntity().world.isRemote && event.getPotionEffect() != null) {
			ModNetworkManager.sendToAll(new STCPotion(event.getPotionEffect().getPotion(), Action.Remove, event.getEntity().getEntityId()));
		}
	}
	
	@SubscribeEvent
	public static void effectExpiryEvent(PotionExpiryEvent event) {
		if (!event.getEntity().world.isRemote) {
			ModNetworkManager.sendToAll(new STCPotion(event.getPotionEffect().getPotion(), Action.Remove, event.getEntity().getEntityId()));
		}
	}
	
	@SubscribeEvent
	public static void mountEvent(EntityMountEvent event) {
		CapabilityEntity<?> mountEntity = event.getEntityMounting().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);

		if (!event.getWorldObj().isRemote && mountEntity instanceof BipedMobData && mountEntity.getOriginalEntity() != null) {
			if (event.getEntityBeingMounted() instanceof MobEntity) {
				((BipedMobData<?>) mountEntity).onMount(event.isMounting(), event.getEntityBeingMounted());
			}
		}
	}
	
	@SubscribeEvent
	public static void tpEvent(EntityTeleportEvent.EnderEntity event) {
		LivingEntity entity = event.getEntityLiving();
		if (event.getEntityLiving() instanceof EndermanEntity) {
			EndermanEntity enderman = (EndermanEntity)entity;
			EndermanData endermandata = (EndermanData) enderman.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			
			if (endermandata != null) {
				if (endermandata.getEntityState().isInaction()) {
					for (Entity collideEntity : enderman.world.getEntitiesWithinAABB(Entity.class, enderman.getBoundingBox().grow(0.2D, 0.2D, 0.2D))) {
						if (collideEntity instanceof ProjectileEntity) {
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
		LivingData<?> entitydata = (LivingData<?>) event.getEntity().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		
		if (entitydata != null && entitydata.isRemote()) {
			if (!entitydata.getEntityState().isInaction() && !event.getEntity().isInWater()) {
				StaticAnimation jumpAnimation = entitydata.getClientAnimator().getJumpAnimation();
				entitydata.getAnimator().playAnimation(jumpAnimation, 0);
				ModNetworkManager.sendToServer(new CTSPlayAnimation(jumpAnimation.getNamespaceId(), jumpAnimation.getId(), 0, true, false));
			}
		}
	}
	
	@SubscribeEvent
	public static void deathEvent(LivingDeathEvent event) {
		LivingData<?> entitydata = (LivingData<?>)event.getEntityLiving().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		if (entitydata != null) {
			entitydata.getAnimator().playDeathAnimation();
		}
	}
	
	@SubscribeEvent
	public static void fallEvent(LivingFallEvent event) {
		if (event.getEntity().world.getGameRules().getBoolean(ModGamerules.HAS_FALL_ANIMATION) && !event.getEntity().world.isRemote && event.getDamageMultiplier() > 0.0F) {
			LivingData<?> entitydata = (LivingData<?>) event.getEntity().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (entitydata != null && !entitydata.getEntityState().isInaction()) {
				float distance = event.getDistance();
				if (distance > 5.0F) {
					entitydata.playAnimationSynchronize(Animations.BIPED_LANDING, 0);
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