package yesman.epicfight.world.capabilities.item;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class ModWeaponCapability extends CapabilityItem {
	protected final Function<LivingEntityPatch<?>, Style> stylegetter;
	protected final Function<ItemStack, Boolean> offhandPredicator;
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
		this.offhandPredicator = builder.offhandPredicator;
		this.passiveSkill = builder.passiveSkill;
		this.smashingSound = builder.smashingSound;
		this.hitSound = builder.hitSound;
		this.holdOption = builder.holdOption;
		this.weaponCollider = builder.weaponCollider;
	}
	
	@Override
	public final List<StaticAnimation> getAutoAttckMotion(PlayerPatch<?> playerpatch) {
		return this.autoAttackMotions.get(this.getStyle(playerpatch));
	}
	
	@Override
	public final Skill getSpecialAttack(PlayerPatch<?> playerpatch) {
		return this.specialAttacks.get(this.getStyle(playerpatch));
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
	public Style getStyle(LivingEntityPatch<?> entitypatch) {
		return this.stylegetter.apply(entitypatch);
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
		return EpicFightParticles.HIT_BLADE.get();
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
	public Map<LivingMotion, StaticAnimation> getLivingMotionModifier(PlayerPatch<?> player, InteractionHand hand) {
		return (this.livingMotionModifiers == null || hand == InteractionHand.OFF_HAND) ? super.getLivingMotionModifier(player, hand) : this.livingMotionModifiers.get(this.getStyle(player));
	}
	
	@Override
	public UseAnim getUseAnimation(PlayerPatch<?> playerpatch) {
		if (this.livingMotionModifiers != null) {
			Style style = this.getStyle(playerpatch);
			if (this.livingMotionModifiers.containsKey(style)) {
				if (this.livingMotionModifiers.get(style).containsKey(LivingMotion.BLOCK)) {
					return UseAnim.BLOCK;
				}
			}
		}
		return UseAnim.NONE;
	}
	
	@Override
	public boolean canUsedInOffhandAlone() {
		return false;
	}
	
	@Override
	public boolean isValidOffhandItem(ItemStack itemstack) {
		return super.isValidOffhandItem(itemstack) || this.offhandPredicator.apply(itemstack);
	}
	
	public static ModWeaponCapability.Builder builder() {
		return new ModWeaponCapability.Builder();
	}
	
	public static class Builder {
		WeaponCategory category;
		Function<LivingEntityPatch<?>, Style> styleGetter;
		Function<ItemStack, Boolean> offhandPredicator;
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
			this.styleGetter = (entitypatch) -> Style.ONE_HAND;
			this.offhandPredicator = (entitypatch) -> false;
			this.passiveSkill = null;
			this.smashingSound = EpicFightSounds.WHOOSH;
			this.hitSound = EpicFightSounds.BLUNT_HIT;
			this.weaponCollider = ColliderPreset.FIST;
			this.holdOption = HoldOption.GENERAL;
			this.autoAttackMotionMap = Maps.<Style, List<StaticAnimation>>newHashMap();
			this.specialAttackMap = Maps.<Style, Skill>newHashMap();
			this.livingMotionModifiers = null;
		}
		
		public Builder setCategory(WeaponCategory category) {
			this.category = category;
			return this;
		}
		
		public Builder setStyleGetter(Function<LivingEntityPatch<?>, Style> stylegetter) {
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
			this.offhandPredicator = predicator;
			return this;
		}
		
		public Builder addStyleSpecialAttack(Style style, Skill specialAttack) {
			this.specialAttackMap.put(style, specialAttack);
			return this;
		}
	}
}