package yesman.epicfight.events;

import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Items;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
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
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.ExtendedDamageSource;
import yesman.epicfight.api.utils.ExtendedDamageSource.StunType;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.client.CPPlayAnimation;
import yesman.epicfight.network.server.SPPotion;
import yesman.epicfight.network.server.SPPotion.Action;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.mob.EndermanPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.projectile.ProjectilePatch;
import yesman.epicfight.world.effect.EpicFightMobEffects;
import yesman.epicfight.world.entity.eventlistener.HurtEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.world.entity.eventlistener.ProjectileHitEvent;
import yesman.epicfight.world.gamerule.EpicFightGamerules;

@Mod.EventBusSubscriber(modid=EpicFightMod.MODID)
public class EntityEvents {
	@SuppressWarnings("unchecked")
	@SubscribeEvent
	public static void spawnEvent(EntityJoinWorldEvent event) {
		EntityPatch<Entity> entitypatch = event.getEntity().getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		
		if (entitypatch != null && !entitypatch.isInitialized()) {
			entitypatch.onJoinWorld(event.getEntity(), event);
		}
		
		if (event.getEntity() instanceof ProjectileEntity) {
			ProjectileEntity projectileentity = (ProjectileEntity)event.getEntity();
			ProjectilePatch<ProjectileEntity> projectilePatch = event.getEntity().getCapability(EpicFightCapabilities.CAPABILITY_PROJECTILE, null).orElse(null);
			
			if (projectilePatch != null) {
				projectilePatch.onJoinWorld(projectileentity, event);
			}
		}
	}
	
	@SubscribeEvent
	public static void updateEvent(LivingUpdateEvent event) {
		LivingEntityPatch<?> entitypatch = (LivingEntityPatch<?>) event.getEntityLiving().getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		
		if (entitypatch != null && entitypatch.getOriginal() != null) {
			entitypatch.tick(event);
		}
	}
	
	@SubscribeEvent
	public static void knockBackEvent(LivingKnockBackEvent event) {
		EntityPatch<?> cap = event.getEntityLiving().getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
		
		if (cap != null) {
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public static void hurtEvent(LivingHurtEvent event) {
		ExtendedDamageSource extendedDamageSource = null;
		Entity trueSource = event.getSource().getEntity();
		
		if (trueSource != null) {
			if (event.getSource() instanceof ExtendedDamageSource) {
				extendedDamageSource = (ExtendedDamageSource) event.getSource();
			} else if (event.getSource() instanceof IndirectEntityDamageSource && event.getSource().getDirectEntity() != null) {
				ProjectilePatch<?> projectileCap = event.getSource().getDirectEntity().getCapability(EpicFightCapabilities.CAPABILITY_PROJECTILE, null).orElse(null);
				
				if (projectileCap != null) {
					extendedDamageSource = projectileCap.getEpicFightDamageSource(event.getSource());
				}
			}
			
			if (extendedDamageSource != null) {
				LivingEntity hitEntity = event.getEntityLiving();
				float totalDamage = event.getAmount();
				
				if (hitEntity instanceof ServerPlayerEntity) {
					ServerPlayerPatch playerpatch = (ServerPlayerPatch)hitEntity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
					HurtEvent.Post hurtEvent = new HurtEvent.Post(playerpatch, extendedDamageSource, totalDamage);
					playerpatch.getEventListener().triggerEvents(EventType.HURT_EVENT_POST, hurtEvent);
					totalDamage = hurtEvent.getAmount();
				}
				
				float ignoreDamage = totalDamage * extendedDamageSource.getArmorNegation() * 0.01F;
				float calculatedDamage = ignoreDamage;
				
			    if (hitEntity.hasEffect(Effects.DAMAGE_RESISTANCE)) {
			    	int i = (hitEntity.getEffect(Effects.DAMAGE_RESISTANCE).getAmplifier() + 1) * 5;
			        int j = 25 - i;
			        float f = calculatedDamage * (float)j;
			        float f1 = calculatedDamage;
			        calculatedDamage = Math.max(f / 25.0F, 0.0F);
			        float f2 = f1 - calculatedDamage;
			        
					if (f2 > 0.0F && f2 < 3.4028235E37F) {
			        	if (hitEntity instanceof ServerPlayerEntity) {
			        		((ServerPlayerEntity)hitEntity).awardStat(Stats.DAMAGE_RESISTED, Math.round(f2 * 10.0F));
			        	} else if (event.getSource().getEntity() instanceof ServerPlayerEntity) {
			                ((ServerPlayerEntity)event.getSource().getEntity()).awardStat(Stats.DAMAGE_DEALT_RESISTED, Math.round(f2 * 10.0F));
			        	}
			        }
			    }

				if (calculatedDamage > 0.0F) {
					int k = EnchantmentHelper.getDamageProtection(hitEntity.getArmorSlots(), event.getSource());
					if (k > 0) {
			        	calculatedDamage = CombatRules.getDamageAfterMagicAbsorb(calculatedDamage, (float)k);
			        }
			    }
			    
			    float absorpAmount = hitEntity.getAbsorptionAmount() - calculatedDamage;
			    hitEntity.setAbsorptionAmount(Math.max(absorpAmount, 0.0F));
		        float realHealthDamage = Math.max(-absorpAmount, 0.0F);
		        
		        if (realHealthDamage > 0.0F && realHealthDamage < 3.4028235E37F && event.getSource().getEntity() instanceof ServerPlayerEntity) {
		        	((ServerPlayerEntity)event.getSource().getEntity()).awardStat(Stats.DAMAGE_DEALT_ABSORBED, Math.round(realHealthDamage * 10.0F));
		        }
		        
				if (absorpAmount < 0.0F) {
					hitEntity.setHealth(hitEntity.getHealth() + absorpAmount);
		        	LivingEntityPatch<?> attacker = (LivingEntityPatch<?>)trueSource.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		        	
					if (attacker != null) {
						attacker.gatherDamageDealt(extendedDamageSource, calculatedDamage);
					}
		        }
		        
				event.setAmount(totalDamage - ignoreDamage);
				if (event.getAmount() + ignoreDamage > 0.0F) {
					LivingEntityPatch<?> hitentitypatch = (LivingEntityPatch<?>)hitEntity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
					
					if (hitentitypatch != null) {
						StaticAnimation hitAnimation = null;
						float extendStunTime = 0.0F;
						float knockBackAmount = 0.0F;
						float weightReduction = 40.0F / (float)hitentitypatch.getWeight();
						float stunShield = hitentitypatch.getStunShield();
						
						if (stunShield > 0.0F) {
							hitentitypatch.setStunShield(stunShield - extendedDamageSource.getImpact());
						}
						
						switch (extendedDamageSource.getStunType()) {
						case SHORT:
							if (!hitEntity.hasEffect(EpicFightMobEffects.STUN_IMMUNITY.get()) && (hitentitypatch.getStunShield() == 0.0F)) {
								float totalStunTime = (float) ((0.25F + (extendedDamageSource.getImpact()) * 0.1F) * weightReduction);
								totalStunTime *= (1.0F - hitentitypatch.getStunTimeTimeReduction());
								
								if (totalStunTime >= 0.1F) {
									extendStunTime = totalStunTime - 0.1F;
									boolean flag = totalStunTime >= 0.83F;
									StunType stunType = flag ? StunType.LONG : StunType.SHORT;
									extendStunTime = flag ? 0.0F : extendStunTime;
									hitAnimation = hitentitypatch.getHitAnimation(stunType);
									knockBackAmount = Math.min(flag ? extendedDamageSource.getImpact() * 0.05F : totalStunTime, 2.0F);
								}
							}
							break;
						case LONG:
							hitAnimation = hitEntity.hasEffect(EpicFightMobEffects.STUN_IMMUNITY.get()) ? null : hitentitypatch.getHitAnimation(StunType.LONG);
							knockBackAmount = Math.min(extendedDamageSource.getImpact() * 0.25F * weightReduction, 5.0F);
							break;
						case HOLD:
							hitAnimation = hitentitypatch.getHitAnimation(StunType.SHORT);
							extendStunTime = extendedDamageSource.getImpact() * 0.1F;
							break;
						case KNOCKDOWN:
							hitAnimation = hitEntity.hasEffect(EpicFightMobEffects.STUN_IMMUNITY.get()) ? null : hitentitypatch.getHitAnimation(StunType.KNOCKDOWN);
							knockBackAmount = Math.min(extendedDamageSource.getImpact() * 0.05F, 5.0F);
							break;
						case FALL:
							break;
						case NONE:
							break;
						}
						
						Vector3d sourcePosition = ((DamageSource)extendedDamageSource).getSourcePosition();
						
						if (sourcePosition != null) {
							if (hitAnimation != null) {
								if (!(hitEntity instanceof PlayerEntity)) {
									hitEntity.lookAt(EntityAnchorArgument.Type.FEET, sourcePosition);
								}
								
								hitentitypatch.setStunReductionOnHit();
								hitentitypatch.playAnimationSynchronized(hitAnimation, extendStunTime);
							}
							
							if (knockBackAmount != 0.0F) {
								hitentitypatch.knockBackEntity(((DamageSource)extendedDamageSource).getSourcePosition(), knockBackAmount);
							}
						}
					}
				}
			}
		}
		
		if (event.getEntityLiving().isUsingItem() && event.getEntityLiving().getUseItem().getItem() == Items.SHIELD) {
			if (event.getEntityLiving() instanceof PlayerEntity) {
				event.getEntityLiving().level.playSound((PlayerEntity)event.getEntityLiving(), event.getEntityLiving().blockPosition(), SoundEvents.SHIELD_BLOCK, event.getEntityLiving().getSoundSource(), 1.0F, 0.8F + event.getEntityLiving().getRandom().nextFloat() * 0.4F);
			}
		}
	}
	
	@SubscribeEvent
	public static void damageEvent(LivingDamageEvent event) {
		Entity trueSource = event.getSource().getEntity();
		
		if (event.getSource() instanceof ExtendedDamageSource) {
			if (trueSource != null) {
				LivingEntityPatch<?> attacker = (LivingEntityPatch<?>) trueSource.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				
				if (attacker != null) {
					attacker.gatherDamageDealt((ExtendedDamageSource) event.getSource(), event.getAmount());
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void attackEvent(LivingAttackEvent event) {
		if (event.getEntity().level.isClientSide()) {
			return;
		}
		
		LivingEntityPatch<?> entitypatch = (LivingEntityPatch<?>) event.getEntity().getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
		DamageSource damageSource = null;
		
		if (entitypatch != null && event.getEntityLiving().getHealth() > 0.0F) {
			if (damageSource == DamageSource.FALL && event.getEntity().level.getGameRules().getBoolean(EpicFightGamerules.HAS_FALL_ANIMATION)) {
				if (!entitypatch.getEntityState().inaction()) {
					float damage = event.getAmount();
					
					if (damage > 5.0F) {
						StaticAnimation fallAnimation = entitypatch.getHitAnimation(StunType.FALL);
						
						if (fallAnimation != null) {
							entitypatch.playAnimationSynchronized(fallAnimation, 0);
						}
					}
				}
				
				return;
			}
			
			if (event.getSource() instanceof IndirectEntityDamageSource && event.getSource().getDirectEntity() != null) {
				ProjectilePatch<?> projectilepatch = event.getSource().getDirectEntity().getCapability(EpicFightCapabilities.CAPABILITY_PROJECTILE).orElse(null);
				
				if (projectilepatch != null) {
					damageSource = projectilepatch.getEpicFightDamageSource(event.getSource());
				}
			}
			
			if (damageSource == null) {
				damageSource = event.getSource();
			}
			
			AttackResult result = entitypatch.tryHurt(damageSource, event.getAmount());
			
			if (!result.resultType.dealtDamage()) {
				event.setCanceled(true);
			} else if (event.getAmount() != result.damage) {
				event.setCanceled(true);
				
				DamageSource damagesource = new DamageSource( event.getSource().getMsgId() );
				damagesource.bypassInvul();
				
				event.getEntity().hurt(damagesource, result.damage);
			}
		}
	}
	
	@SubscribeEvent
	public static void dropEvent(LivingDropsEvent event) {
		LivingEntityPatch<?> entitypatch = (LivingEntityPatch<?>)event.getEntityLiving().getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
		
		if (entitypatch != null) {
			if (entitypatch.onDrop(event)) {
				event.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent
	public static void projectileImpactEvent(ProjectileImpactEvent event) {
		Entity eProjectile = event.getEntity();
		
		if (!(eProjectile instanceof ProjectileEntity)) {
			return;
		}
		
		ProjectileEntity projectile =(ProjectileEntity)eProjectile; 
		ProjectilePatch<?> projectilepatch = projectile.getCapability(EpicFightCapabilities.CAPABILITY_PROJECTILE, null).orElse(null);
		
		if (!projectile.level.isClientSide() && projectilepatch != null) {
			if (projectilepatch.onProjectileImpact(event)) {
				event.setCanceled(true);
				return;
			}
		}
		
		if (event.getRayTraceResult() instanceof EntityRayTraceResult) {
			EntityRayTraceResult rayresult = ((EntityRayTraceResult)event.getRayTraceResult());
			
			if (rayresult.getEntity() != null) {
				if (rayresult.getEntity() instanceof ServerPlayerEntity) {
					ServerPlayerPatch playerpatch = (ServerPlayerPatch) rayresult.getEntity().getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
					boolean canceled = playerpatch.getEventListener().triggerEvents(EventType.PROJECTILE_HIT_EVENT, new ProjectileHitEvent(playerpatch, event));
					
					if (canceled) {
						event.setCanceled(true);
					}
				}
				
				if (projectile.getOwner() != null) {
					if (rayresult.getEntity().equals(projectile.getOwner().getVehicle())) {
						event.setCanceled(true);
					}
					
					if (rayresult.getEntity() instanceof PartEntity) {
						Entity parent = ((PartEntity<?>)rayresult.getEntity()).getParent();
						
						if (projectile.getOwner().is(parent)) {
							event.setCanceled(true);
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void equipChangeEvent(LivingEquipmentChangeEvent event) {
		if (event.getFrom().getItem() == event.getTo().getItem()) {
			return;
		}
		
		LivingEntityPatch<?> entitypatch = (LivingEntityPatch<?>) event.getEntity().getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		CapabilityItem fromCap = EpicFightCapabilities.getItemStackCapability(event.getFrom());
		CapabilityItem toCap = EpicFightCapabilities.getItemStackCapability(event.getTo());
		
		if (event.getSlot() != EquipmentSlotType.OFFHAND) {
			if (fromCap != null) {
				event.getEntityLiving().getAttributes().removeAttributeModifiers(fromCap.getAttributeModifiers(event.getSlot(), entitypatch));
			}
			
			if (toCap != null) {
				event.getEntityLiving().getAttributes().addTransientAttributeModifiers(toCap.getAttributeModifiers(event.getSlot(), entitypatch));
			}
		}
		
		if (entitypatch != null && entitypatch.getOriginal() != null) {
			if (event.getSlot().getType() == EquipmentSlotType.Group.HAND) {
				Hand hand = event.getSlot() == EquipmentSlotType.MAINHAND ? Hand.MAIN_HAND : Hand.OFF_HAND;
				entitypatch.updateHeldItem(fromCap, toCap, event.getFrom(), event.getTo(), hand);
			} else if (event.getSlot().getType() == EquipmentSlotType.Group.ARMOR) {
				entitypatch.updateArmor(fromCap, toCap, event.getSlot());
			}
		}
	}
	
	@SubscribeEvent
	public static void sizingEvent(EntityEvent.Size event) {
		if (event.getEntity() instanceof EnderDragonEntity) {
			event.setNewSize(EntitySize.scalable(5.0F, 3.0F));
		}
	}
	
	
	@SubscribeEvent
	public static void effectAddEvent(PotionAddedEvent event) {
		if (!event.getEntity().level.isClientSide()) {
			EpicFightNetworkManager.sendToAll(new SPPotion(event.getPotionEffect().getEffect(), Action.ACTIVATE, event.getEntity().getId()));
		}
	}
	
	@SubscribeEvent
	public static void effectRemoveEvent(PotionRemoveEvent event) {
		if (!event.getEntity().level.isClientSide() && event.getPotionEffect() != null) {
			EpicFightNetworkManager.sendToAll(new SPPotion(event.getPotionEffect().getEffect(), Action.REMOVE, event.getEntity().getId()));
		}
	}
	
	@SubscribeEvent
	public static void effectExpiryEvent(PotionExpiryEvent event) {
		if (!event.getEntity().level.isClientSide()) {
			EpicFightNetworkManager.sendToAll(new SPPotion(event.getPotionEffect().getEffect(), Action.REMOVE, event.getEntity().getId()));
		}
	}
	
	@SubscribeEvent
	public static void mountEvent(EntityMountEvent event) {
		EntityPatch<?> mountEntity = event.getEntityMounting().getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		
		if (!event.getWorldObj().isClientSide() && mountEntity instanceof HumanoidMobPatch && mountEntity.getOriginal() != null) {
			if (event.getEntityBeingMounted() instanceof MobEntity) {
				((HumanoidMobPatch<?>)mountEntity).onMount(event.isMounting(), event.getEntityBeingMounted());
			}
		}
	}
	
	@SubscribeEvent
	public static void tpEvent(EntityTeleportEvent.EnderEntity event) {
		LivingEntity entity = event.getEntityLiving();
		
		if (event.getEntityLiving() instanceof EndermanEntity) {
			EndermanEntity enderman = (EndermanEntity)entity;
			EndermanPatch endermanpatch = (EndermanPatch) enderman.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			
			if (endermanpatch != null) {
				if (endermanpatch.getEntityState().inaction()) {
					for (Entity collideEntity : enderman.level.getEntitiesOfClass(Entity.class, enderman.getBoundingBox().inflate(0.2D, 0.2D, 0.2D))) {
						if (collideEntity instanceof ProjectileEntity) {
	                    	return;
	                    }
	                }
					
					event.setCanceled(true);
				} else if (endermanpatch.isRaging()) {
					event.setCanceled(true);
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void jumpEvent(LivingJumpEvent event) {
		LivingEntityPatch<?> entitypatch = (LivingEntityPatch<?>) event.getEntity().getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		
		if (entitypatch != null && entitypatch.isLogicalClient()) {
			if (!entitypatch.getEntityState().inaction() && !event.getEntity().isInWater()) {
				StaticAnimation jumpAnimation = entitypatch.getClientAnimator().getJumpAnimation();
				entitypatch.getAnimator().playAnimation(jumpAnimation, 0);
				EpicFightNetworkManager.sendToServer(new CPPlayAnimation(jumpAnimation.getNamespaceId(), jumpAnimation.getId(), 0, true, false));
			}
		}
	}
	
	@SubscribeEvent
	public static void deathEvent(LivingDeathEvent event) {
		LivingEntityPatch<?> entitypatch = (LivingEntityPatch<?>)event.getEntityLiving().getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		
		if (entitypatch != null) {
			entitypatch.onDeath();
		}
	}
	
	@SubscribeEvent
	public static void fallEvent(LivingFallEvent event) {/**
		if (event.getEntity().level.getGameRules().getBoolean(EpicFightGamerules.HAS_FALL_ANIMATION) && !event.getEntity().level.isClientSide() && event.getDamageMultiplier() > 0.0F) {
			LivingEntityPatch<?> entitypatch = (LivingEntityPatch<?>) event.getEntity().getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			
			if (entitypatch != null && !entitypatch.getEntityState().inaction()) {
				float distance = event.getDistance();
				
				if (distance > 5.0F) {
					StaticAnimation fallAnimation = entitypatch.getHitAnimation(StunType.FALL);
					
					if (fallAnimation != null) {
						entitypatch.playAnimationSynchronized(fallAnimation, 0);
					}
				}
			}
		}**/
	}
}