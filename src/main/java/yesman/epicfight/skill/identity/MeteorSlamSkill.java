package yesman.epicfight.skill.identity;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

import com.google.common.collect.Maps;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.LevelUtil;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKeys;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;
import yesman.epicfight.world.capabilities.item.WeaponCategory;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class MeteorSlamSkill extends Skill {
	private static final UUID EVENT_UUID = UUID.fromString("03181ad0-e750-11ed-a05b-0242ac120003");
	
	public static class Builder extends Skill.Builder<MeteorSlamSkill> {
		protected final Map<WeaponCategory, BiFunction<CapabilityItem, PlayerPatch<?>, StaticAnimation>> slamMotions = Maps.newHashMap();
		
		public Builder addSlamMotion(WeaponCategory weaponCategory, BiFunction<CapabilityItem, PlayerPatch<?>, StaticAnimation> function) {
			this.slamMotions.put(weaponCategory, function);
			return this;
		}
		
		@Override
		public Builder setCategory(SkillCategory category) {
			this.category = category;
			return this;
		}
		
		@Override
		public Builder setActivateType(ActivateType activateType) {
			this.activateType = activateType;
			return this;
		}
		
		@Override
		public Builder setResource(Resource resource) {
			this.resource = resource;
			return this;
		}
		
		@Override
		public Builder setCreativeTab(CreativeModeTab tab) {
			this.tab = tab;
			return this;
		}
	}
	
	public static float getFallDistance(SkillContainer skillContainer) {
		return skillContainer.getDataManager().getDataValue(SkillDataKeys.FALL_DISTANCE.get());
	}
	
	public static MeteorSlamSkill.Builder createMeteorSlamBuilder() {
		return (new MeteorSlamSkill.Builder())
				    .setCategory(SkillCategories.IDENTITY)
				    .setResource(Resource.NONE)
				    .addSlamMotion(WeaponCategories.SPEAR, (item, player) -> Animations.METEOR_SLAM)
				    .addSlamMotion(WeaponCategories.GREATSWORD, (item, player) -> Animations.METEOR_SLAM)
				    .addSlamMotion(WeaponCategories.TACHI, (item, player) -> Animations.METEOR_SLAM)
				    .addSlamMotion(WeaponCategories.LONGSWORD, (item, player) -> Animations.METEOR_SLAM);
	}
	
	protected final Map<WeaponCategory, BiFunction<CapabilityItem, PlayerPatch<?>, StaticAnimation>> slamMotions;
	private final double minDistance = 6.0D;
	
	public MeteorSlamSkill(Builder builder) {
		super(builder);
		
		this.slamMotions = builder.slamMotions;
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		PlayerEventListener listener = container.getExecuter().getEventListener();
		
		listener.addEventListener(EventType.SKILL_EXECUTE_EVENT, EVENT_UUID, (event) -> {
			if (container.getExecuter() instanceof ServerPlayerPatch serverPlayerPatch) {
				Skill skill = event.getSkillContainer().getSkill();
				
				if (skill.getCategory() != SkillCategories.BASIC_ATTACK && skill.getCategory() != SkillCategories.AIR_ATTACK) {
					return;
				}
				
				if (container.getExecuter().getOriginal().onGround() || container.getExecuter().getOriginal().getXRot() < 40.0F) {
					return;
				}
				
				CapabilityItem holdingItem = container.getExecuter().getHoldingItemCapability(InteractionHand.MAIN_HAND);
				
				if (!this.slamMotions.containsKey(holdingItem.getWeaponCategory())) {
					return;
				}
				
				StaticAnimation slamAnimation = this.slamMotions.get(holdingItem.getWeaponCategory()).apply(holdingItem, container.getExecuter());
				
				if (slamAnimation == null) {
					return;
				}
				
				Vec3 vec3 = container.getExecuter().getOriginal().getEyePosition(1.0F);
				Vec3 vec31 = container.getExecuter().getOriginal().getViewVector(1.0F);
				Vec3 vec32 = vec3.add(vec31.x * 50.0D, vec31.y * 50.0D, vec31.z * 50.0D);
				HitResult hitResult = container.getExecuter().getOriginal().level().clip(new ClipContext(vec3, vec32, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, container.getExecuter().getOriginal()));
				
				if (hitResult.getType() != HitResult.Type.MISS) {
					Vec3 to = hitResult.getLocation();
					Vec3 from = container.getExecuter().getOriginal().position();
					double distance = to.distanceTo(from);
					
					if (distance > this.minDistance) {
						container.getExecuter().playAnimationSynchronized(slamAnimation, 0.0F);
						container.getDataManager().setDataSync(SkillDataKeys.FALL_DISTANCE.get(), (float)distance, serverPlayerPatch.getOriginal());
						container.getDataManager().setData(SkillDataKeys.PROTECT_NEXT_FALL.get(), true);
						event.setCanceled(true);
					}
				}
			}
		});
		
		listener.addEventListener(EventType.HURT_EVENT_PRE, EVENT_UUID, (event) -> {
			if (event.getDamageSource().is(DamageTypeTags.IS_FALL) && container.getDataManager().getDataValue(SkillDataKeys.PROTECT_NEXT_FALL.get())) {
				float stamina = container.getExecuter().getStamina();
				float damage = event.getAmount();
				event.setAmount(damage - stamina);
				event.setCanceled(true);
				container.getExecuter().setStamina(stamina - damage);
				container.getDataManager().setData(SkillDataKeys.PROTECT_NEXT_FALL.get(), false);
			}
		});
		
		listener.addEventListener(EventType.FALL_EVENT, EVENT_UUID, (event) -> {
			if (LevelUtil.calculateLivingEntityFallDamage(event.getForgeEvent().getEntity(), event.getForgeEvent().getDamageMultiplier(), event.getForgeEvent().getDistance()) == 0) {
				container.getDataManager().setData(SkillDataKeys.PROTECT_NEXT_FALL.get(), false);
			}
		});
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		super.onRemoved(container);
		container.getExecuter().getEventListener().removeListener(EventType.FALL_EVENT, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.HURT_EVENT_PRE, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.SKILL_EXECUTE_EVENT, EVENT_UUID);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public List<Object> getTooltipArgsOfScreen(List<Object> list) {
		StringBuilder sb = new StringBuilder();
		Iterator<WeaponCategory> iter = this.slamMotions.keySet().iterator();
		
        while (iter.hasNext()) {
            sb.append(WeaponCategory.ENUM_MANAGER.toTranslated(iter.next()));
            
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }
        
        list.add(sb.toString());
		
		return list;
	}
}