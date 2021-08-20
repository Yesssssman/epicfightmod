package maninhouse.epicfight.capabilities.item;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.capabilities.entity.player.PlayerData;
import maninhouse.epicfight.gamedata.Colliders;
import maninhouse.epicfight.gamedata.Sounds;
import maninhouse.epicfight.particle.HitParticleType;
import maninhouse.epicfight.particle.Particles;
import maninhouse.epicfight.physics.Collider;
import maninhouse.epicfight.skill.Skill;
import net.minecraft.item.UseAction;
import net.minecraft.util.SoundEvent;

public class ModWeaponCapability extends CapabilityItem {
	protected final Function<LivingData<?>, HoldStyle> stylegetter;
	protected final Skill passiveSkill;
	protected final SoundEvent smashingSound;
	protected final SoundEvent hitSound;
	protected final Collider weaponCollider;
	protected final HoldOption holdOption;
	protected final Map<HoldStyle, List<StaticAnimation>> autoAttackMotions;
	protected final Map<HoldStyle, Skill> specialAttacks;
	protected final Map<HoldStyle, Map<LivingMotion, StaticAnimation>> livingMotionModifiers;
	
	public ModWeaponCapability(ModWeaponCapability.Builder builder) {
		super(builder.category);
		this.autoAttackMotions = builder.autoAttackMotionMap;
		this.specialAttacks = builder.specialAttackMap;
		this.livingMotionModifiers = builder.livingMotionModifiers;
		this.stylegetter = builder.stylegetter;
		this.passiveSkill = builder.passiveSkill;
		this.smashingSound = builder.smashingSound;
		this.hitSound = builder.hitSound;
		this.holdOption = builder.holdOption;
		this.weaponCollider = builder.weaponCollider;
	}
	
	@Override
	public final List<StaticAnimation> getAutoAttckMotion(PlayerData<?> playerdata) {
		return this.autoAttackMotions.get(this.getStyle(playerdata));
	}
	
	@Override
	public final Skill getSpecialAttack(PlayerData<?> playerdata) {
		return this.specialAttacks.get(this.getStyle(playerdata));
	}
	
	@Override
	public Skill getPassiveSkill() {
		return this.passiveSkill;
	}
	
	@Override
	public final List<StaticAnimation> getMountAttackMotion() {
		return this.autoAttackMotions.get(HoldStyle.MOUNT);
	}
	
	@Override
	public HoldStyle getStyle(LivingData<?> entitydata) {
		return this.stylegetter.apply(entitydata);
	}
	
	@Override
	public SoundEvent getSmashingSound() {
		return this.smashingSound;
	}
	
	@Override
	public SoundEvent getHitSound() {
		return this.hitSound;
	}
	
	@Override
	public HitParticleType getHitParticle() {
		return Particles.HIT_BLADE.get();
	}
	
	@Override
	public Collider getWeaponCollider() {
		return this.weaponCollider != null ? this.weaponCollider : super.getWeaponCollider();
	}
	
	@Override
	public HoldOption getHoldOption() {
		return this.holdOption;
	}
	
	@Override
	public Map<LivingMotion, StaticAnimation> getLivingMotionChanges(PlayerData<?> player) {
		return this.livingMotionModifiers == null ? super.getLivingMotionChanges(player) : this.livingMotionModifiers.get(this.getStyle(player));
	}
	
	@Override
	public UseAction getUseAction(PlayerData<?> playerdata) {
		if (this.livingMotionModifiers != null) {
			HoldStyle style = this.getStyle(playerdata);
			if (this.livingMotionModifiers.containsKey(style)) {
				if (this.livingMotionModifiers.get(style).containsKey(LivingMotion.BLOCK)) {
					return UseAction.BLOCK;
				}
			}
		}
		return UseAction.NONE;
	}
	
	@Override
	public boolean canUsedOffhandAlone() {
		return false;
	}
	
	public static class Builder {
		WeaponCategory category;
		Function<LivingData<?>, HoldStyle> stylegetter;
		Skill passiveSkill;
		SoundEvent smashingSound;
		SoundEvent hitSound;
		Collider weaponCollider;
		HoldOption holdOption;
		Map<HoldStyle, List<StaticAnimation>> autoAttackMotionMap;
		Map<HoldStyle, Skill> specialAttackMap;
		Map<HoldStyle, Map<LivingMotion, StaticAnimation>> livingMotionModifiers;
		
		public Builder() {
			this.category = WeaponCategory.NOT_WEAON;
			this.stylegetter = (entitydata) -> HoldStyle.ONE_HAND;
			this.passiveSkill = null;
			this.smashingSound = Sounds.WHOOSH;
			this.hitSound = Sounds.BLUNT_HIT;
			this.weaponCollider = Colliders.fist;
			this.holdOption = HoldOption.GENERAL;
			this.autoAttackMotionMap = Maps.<HoldStyle, List<StaticAnimation>>newHashMap();
			this.specialAttackMap = Maps.<HoldStyle, Skill>newHashMap();
			this.livingMotionModifiers = null;
		}
		
		public Builder setCategory(WeaponCategory category) {
			this.category = category;
			return this;
		}
		
		public Builder setStyleGetter(Function<LivingData<?>, HoldStyle> stylegetter) {
			this.stylegetter = stylegetter;
			return this;
		}
		
		public Builder setPassiveSkill(Skill passiveSkill) {
			this.passiveSkill = passiveSkill;
			return this;
		}
		
		public Builder setSmashingSound(SoundEvent smashingSound) {
			this.smashingSound = smashingSound;
			return this;
		}
		
		public Builder setHitSound(SoundEvent hitSound) {
			this.hitSound = hitSound;
			return this;
		}
		
		public Builder setWeaponCollider(Collider weaponCollider) {
			this.weaponCollider = weaponCollider;
			return this;
		}
		
		public Builder setHoldOption(HoldOption holdOption) {
			this.holdOption = holdOption;
			return this;
		}
		
		public Builder addLivingMotionModifier(HoldStyle wieldStyle, LivingMotion livingMotion, StaticAnimation animation) {
			if (this.livingMotionModifiers == null) {
				this.livingMotionModifiers = Maps.<HoldStyle, Map<LivingMotion, StaticAnimation>>newHashMap();
			}
			if (!this.livingMotionModifiers.containsKey(wieldStyle)) {
				this.livingMotionModifiers.put(wieldStyle, Maps.<LivingMotion, StaticAnimation>newHashMap());
			}
			this.livingMotionModifiers.get(wieldStyle).put(livingMotion, animation);
			return this;
		}
		
		public Builder addStyleCombo(HoldStyle style, StaticAnimation... animation) {
			this.autoAttackMotionMap.put(style, Lists.newArrayList(animation));
			return this;
		}
		
		public Builder addStyleSpecialAttack(HoldStyle style, Skill specialAttack) {
			this.specialAttackMap.put(style, specialAttack);
			return this;
		}
	}
}