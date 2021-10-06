package yesman.epicfight.skill;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.animation.types.StaticAnimation;
import yesman.epicfight.capabilities.entity.player.PlayerData;
import yesman.epicfight.capabilities.item.CapabilityItem;
import yesman.epicfight.capabilities.item.CapabilityItem.Style;
import yesman.epicfight.capabilities.item.CapabilityItem.WeaponCategory;
import yesman.epicfight.entity.eventlistener.HitEvent;
import yesman.epicfight.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.gamedata.Animations;
import yesman.epicfight.gamedata.Skills;
import yesman.epicfight.gamedata.Sounds;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.particle.Particles;
import yesman.epicfight.skill.SkillDataManager.SkillDataKey;

public class ActiveGuardSkill extends GuardSkill {
	private static final SkillDataKey<Integer> LAST_ACTIVE = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	private static final Map<WeaponCategory, BiFunction<CapabilityItem, PlayerData<?>, StaticAnimation>> AVAILABLE_WEAPON_TYPES = 
			Maps.<WeaponCategory, BiFunction<CapabilityItem, PlayerData<?>, StaticAnimation>>newLinkedHashMap();
	
	static {
		AVAILABLE_WEAPON_TYPES.put(WeaponCategory.KATANA, (item, player) -> Animations.KATANA_GUARD_HIT);
		AVAILABLE_WEAPON_TYPES.put(WeaponCategory.LONGSWORD, (item, player) -> Animations.LONGSWORD_GUARD_HIT);
		AVAILABLE_WEAPON_TYPES.put(WeaponCategory.SWORD, (item, player) -> item.getStyle(player) == Style.ONE_HAND ? Animations.SWORD_GUARD_HIT : Animations.SWORD_DUAL_GUARD_HIT);
		AVAILABLE_WEAPON_TYPES.put(WeaponCategory.TACHI, (item, player) -> Animations.LONGSWORD_GUARD_HIT);
	}
	
	public ActiveGuardSkill() {
		super("active_guard");
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		super.onInitiate(container);
		container.getDataManager().registerData(LAST_ACTIVE);
		container.executer.getEventListener().addEventListener(EventType.SERVER_ITEM_USE_EVENT, EVENT_UUID, (event) -> {
			if (this.isExecutableState(event.getPlayerData())) {
				event.getPlayerData().getOriginalEntity().setActiveHand(Hand.MAIN_HAND);
			}
			container.getDataManager().setData(LAST_ACTIVE, event.getPlayerData().getOriginalEntity().ticksExisted);
			return false;
		});
	}
	
	@Override
	public boolean guard(SkillContainer container, CapabilityItem itemCapability, HitEvent event, float knockback, float impact, boolean reinforced) {
		if (this.getAvailableWeaponTypes(2).getOrDefault(itemCapability.getWeaponCategory(), (a, b) -> null).apply(itemCapability, event.getPlayerData()) != null) {
			DamageSource damageSource = event.getForgeEvent().getSource();
			if (this.isBlockableSource(damageSource, true)) {
				ServerPlayerEntity playerentity = event.getPlayerData().getOriginalEntity();
				boolean successParry = playerentity.ticksExisted - container.getDataManager().getDataValue(LAST_ACTIVE) < 8;
				float penalty = container.getDataManager().getDataValue(PENALTY);
				
				event.getPlayerData().playSound(Sounds.CLASH, -0.05F, 0.1F);
				Particles.HIT_BLUNT.get().spawnParticleWithArgument(((ServerWorld)playerentity.world), HitParticleType.POSITION_MIDDLE_OF_EACH_ENTITY,
						HitParticleType.ARGUMENT_ZERO, playerentity, damageSource.getImmediateSource());
				
				if (successParry) {
					penalty = 0;
					knockback *= 0.4F;
				} else {
					penalty += this.getPenaltyStamina(itemCapability);
					container.getDataManager().setDataSync(PENALTY, penalty, playerentity);
				}
				
				if (damageSource.getImmediateSource() instanceof LivingEntity) {
					knockback += EnchantmentHelper.getKnockbackModifier((LivingEntity)damageSource.getImmediateSource()) * 0.1F;
				}
				
				event.getPlayerData().knockBackEntity(damageSource.getImmediateSource(), knockback);
				float stamina = event.getPlayerData().getStamina() - penalty * impact;
				event.getPlayerData().setStamina(stamina);
				
				StaticAnimation animation = this.getAvailableWeaponTypes(successParry ? 2 : stamina >= 0.0F ? 1 : 0).get(itemCapability.getWeaponCategory())
						.apply(itemCapability, container.executer);
				
				if (animation != null) {
					event.getPlayerData().playAnimationSynchronize(animation, 0);
				}
				
				return stamina >= 0.0F;
			}
		}
		
		return super.guard(container, itemCapability, event, knockback, impact, false);
	}
	
	@Override
	protected boolean isBlockableSource(DamageSource damageSource, boolean specialSourceBlockCondition) {
		return (damageSource.isProjectile() && specialSourceBlockCondition) || super.isBlockableSource(damageSource, false);
	}
	
	@Override
	public Map<WeaponCategory, BiFunction<CapabilityItem, PlayerData<?>, StaticAnimation>> getAvailableWeaponTypes(int meta) {
		if (meta == 2) {
			return AVAILABLE_WEAPON_TYPES;
		} else {
			return super.getAvailableWeaponTypes(meta);
		}
	}
	
	@Override
	public Skill getPriorSkill() {
		return Skills.GUARD;
	}
	
	@Override
	protected boolean isHighTierGuard() {
		return true;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public List<Object> getTooltipArgs() {
		List<Object> list = Lists.<Object>newArrayList();
		list.add(String.format("%s, %s, %s, %s", WeaponCategory.KATANA, WeaponCategory.LONGSWORD, WeaponCategory.SWORD, WeaponCategory.TACHI).toLowerCase());
		return list;
	}
}