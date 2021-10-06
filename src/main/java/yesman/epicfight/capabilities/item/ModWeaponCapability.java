package yesman.epicfight.capabilities.item;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.SoundEvent;
import yesman.epicfight.animation.LivingMotion;
import yesman.epicfight.animation.types.StaticAnimation;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.capabilities.entity.player.PlayerData;
import yesman.epicfight.gamedata.Colliders;
import yesman.epicfight.gamedata.Sounds;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.particle.Particles;
import yesman.epicfight.physics.Collider;
import yesman.epicfight.skill.Skill;

public class ModWeaponCapability extends CapabilityItem {
	protected final Function<LivingData<?>, Style> stylegetter;
	protected final Function<ItemStack, Boolean> offhandCompatiblePredicator;
	protected final Skill passiveSkill;
	protected final SoundEvent smashingSound;
	protected final SoundEvent hitSound;
	protected final Collider weaponCollider;
	protected final HoldOption holdOption;
	protected final Map<Style, List<StaticAnimation>> autoAttackMotions;
	protected final Map<Style, Skill> specialAttacks;
	protected final Map<Style, Map<LivingMotion, StaticAnimation>> livingMotionModifiers;
	
	public ModWeaponCapability(ModWeaponCapability.Builder builder) {
		super(builder.category);
		this.autoAttackMotions = builder.autoAttackMotionMap;
		this.specialAttacks = builder.specialAttackMap;
		this.livingMotionModifiers = builder.livingMotionModifiers;
		this.stylegetter = builder.styleGetter;
		this.offhandCompatiblePredicator = builder.offhandCompatiblePredicator;
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
		return this.autoAttackMotions.get(Style.MOUNT);
	}
	
	@Override
	public Style getStyle(LivingData<?> entitydata) {
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
	public Map<LivingMotion, StaticAnimation> getLivingMotionModifier(PlayerData<?> player) {
		return this.livingMotionModifiers == null ? super.getLivingMotionModifier(player) : this.livingMotionModifiers.get(this.getStyle(player));
	}
	
	@Override
	public UseAction getUseAction(PlayerData<?> playerdata) {
		if (this.livingMotionModifiers != null) {
			Style style = this.getStyle(playerdata);
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
	
	@Override
	public boolean isValidOffhandItem(ItemStack itemstack) {
		return super.isValidOffhandItem(itemstack) || this.offhandCompatiblePredicator.apply(itemstack);
	}
	
	public static ModWeaponCapability.Builder builder() {
		return new ModWeaponCapability.Builder();
	}
	
	public static class Builder {
		WeaponCategory category;
		Function<LivingData<?>, Style> styleGetter;
		Function<ItemStack, Boolean> offhandCompatiblePredicator;
		Skill passiveSkill;
		SoundEvent smashingSound;
		SoundEvent hitSound;
		Collider weaponCollider;
		HoldOption holdOption;
		Map<Style, List<StaticAnimation>> autoAttackMotionMap;
		Map<Style, Skill> specialAttackMap;
		Map<Style, Map<LivingMotion, StaticAnimation>> livingMotionModifiers;
		
		public Builder() {
			this.category = WeaponCategory.NOT_WEAON;
			this.styleGetter = (entitydata) -> Style.ONE_HAND;
			this.offhandCompatiblePredicator = (entitydata) -> false;
			this.passiveSkill = null;
			this.smashingSound = Sounds.WHOOSH;
			this.hitSound = Sounds.BLUNT_HIT;
			this.weaponCollider = Colliders.fist;
			this.holdOption = HoldOption.GENERAL;
			this.autoAttackMotionMap = Maps.<Style, List<StaticAnimation>>newHashMap();
			this.specialAttackMap = Maps.<Style, Skill>newHashMap();
			this.livingMotionModifiers = null;
		}
		
		public Builder setCategory(WeaponCategory category) {
			this.category = category;
			return this;
		}
		
		public Builder setStyleGetter(Function<LivingData<?>, Style> stylegetter) {
			this.styleGetter = stylegetter;
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
		
		public Builder addLivingMotionModifier(Style wieldStyle, LivingMotion livingMotion, StaticAnimation animation) {
			if (this.livingMotionModifiers == null) {
				this.livingMotionModifiers = Maps.<Style, Map<LivingMotion, StaticAnimation>>newHashMap();
			}
			if (!this.livingMotionModifiers.containsKey(wieldStyle)) {
				this.livingMotionModifiers.put(wieldStyle, Maps.<LivingMotion, StaticAnimation>newHashMap());
			}
			this.livingMotionModifiers.get(wieldStyle).put(livingMotion, animation);
			return this;
		}
		
		public Builder addStyleCombo(Style style, StaticAnimation... animation) {
			this.autoAttackMotionMap.put(style, Lists.newArrayList(animation));
			return this;
		}
		
		public Builder addOffhandPredicator(Function<ItemStack, Boolean> predicator) {
			this.offhandCompatiblePredicator = predicator;
			return this;
		}
		
		public Builder addStyleSpecialAttack(Style style, Skill specialAttack) {
			this.specialAttackMap.put(style, specialAttack);
			return this;
		}
	}
}