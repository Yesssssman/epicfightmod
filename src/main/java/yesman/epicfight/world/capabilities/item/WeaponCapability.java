package yesman.epicfight.world.capabilities.item;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class WeaponCapability extends CapabilityItem {
	protected final Function<LivingEntityPatch<?>, Style> stylegetter;
	protected final Function<LivingEntityPatch<?>, Boolean> weaponCombinationPredicator;
	protected final Skill passiveSkill;
	protected final SoundEvent smashingSound;
	protected final SoundEvent hitSound;
	protected final Collider weaponCollider;
	protected final HitParticleType hitParticle;
	protected final Map<Style, List<StaticAnimation>> autoAttackMotions;
	protected final Map<Style, Function<ItemStack, Skill>> innateSkill;
	protected final Map<Style, Map<LivingMotion, StaticAnimation>> livingMotionModifiers;
	protected final boolean canBePlacedOffhand;
	protected final Function<Style, Boolean> comboCancel;
	
	protected WeaponCapability(CapabilityItem.Builder builder) {
		super(builder);
		
		WeaponCapability.Builder weaponBuilder = (WeaponCapability.Builder)builder;
		this.autoAttackMotions = weaponBuilder.autoAttackMotionMap;
		this.innateSkill = weaponBuilder.innateSkillByStyle;
		this.livingMotionModifiers = weaponBuilder.livingMotionModifiers;
		this.stylegetter = weaponBuilder.styleProvider;
		this.weaponCombinationPredicator = weaponBuilder.weaponCombinationPredicator;
		this.passiveSkill = weaponBuilder.passiveSkill;
		this.smashingSound = weaponBuilder.swingSound;
		this.hitParticle = weaponBuilder.hitParticle;
		this.hitSound = weaponBuilder.hitSound;
		this.weaponCollider = weaponBuilder.collider;
		this.canBePlacedOffhand = weaponBuilder.canBePlacedOffhand;
		this.comboCancel = weaponBuilder.comboCancel;
		this.attributeMap.putAll(weaponBuilder.attributeMap);
	}
	
	@Override
	public final List<StaticAnimation> getAutoAttckMotion(PlayerPatch<?> playerpatch) {
		return this.autoAttackMotions.get(this.getStyle(playerpatch));
	}
	
	@Override
	public final Skill getInnateSkill(PlayerPatch<?> playerpatch, ItemStack itemstack) {
		return this.innateSkill.getOrDefault(this.getStyle(playerpatch), (s) -> null).apply(itemstack);
	}
	
	@Override
	public Skill getPassiveSkill() {
		return this.passiveSkill;
	}
	
	@Override
	public final List<StaticAnimation> getMountAttackMotion() {
		return this.autoAttackMotions.get(Styles.MOUNT);
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
		return this.hitParticle;
	}
	
	@Override
	public Collider getWeaponCollider() {
		return this.weaponCollider != null ? this.weaponCollider : super.getWeaponCollider();
	}
	
	@Override
	public boolean canBePlacedOffhand() {
		return this.canBePlacedOffhand;
	}
	
	@Override
	public boolean shouldCancelCombo(LivingEntityPatch<?> entitypatch) {
		return this.comboCancel.apply(this.getStyle(entitypatch));
	}
	
	@Override
	public Map<LivingMotion, StaticAnimation> getLivingMotionModifier(LivingEntityPatch<?> player, InteractionHand hand) {
		if (this.livingMotionModifiers == null || hand == InteractionHand.OFF_HAND) {
			return super.getLivingMotionModifier(player, hand);
		}
		
		Map<LivingMotion, StaticAnimation> motions = this.livingMotionModifiers.getOrDefault(this.getStyle(player), Maps.newHashMap());
		this.livingMotionModifiers.getOrDefault(Styles.COMMON, Maps.newHashMap()).forEach(motions::putIfAbsent);
		
		return motions;
	}
	
	@Override
	public UseAnim getUseAnimation(LivingEntityPatch<?> playerpatch) {
		if (this.livingMotionModifiers != null) {
			Style style = this.getStyle(playerpatch);
			
			if (this.livingMotionModifiers.containsKey(style)) {
				if (this.livingMotionModifiers.get(style).containsKey(LivingMotions.BLOCK)) {
					return UseAnim.BLOCK;
				}
			}
		}
		
		return UseAnim.NONE;
	}
	
	@Override
	public boolean canHoldInOffhandAlone() {
		return false;
	}
	
	@Override
	public boolean checkOffhandValid(LivingEntityPatch<?> entitypatch) {
		return super.checkOffhandValid(entitypatch) || this.weaponCombinationPredicator.apply(entitypatch);
	}
	
	@Override
	public boolean availableOnHorse() {
		return this.getMountAttackMotion() != null;
	}
	
	public static WeaponCapability.Builder builder() {
		return new WeaponCapability.Builder();
	}
	
	public static class Builder extends CapabilityItem.Builder {
		Function<LivingEntityPatch<?>, Style> styleProvider;
		Function<LivingEntityPatch<?>, Boolean> weaponCombinationPredicator;
		Skill passiveSkill;
		SoundEvent swingSound;
		SoundEvent hitSound;
		HitParticleType hitParticle;
		Collider collider;
		Map<Style, List<StaticAnimation>> autoAttackMotionMap;
		Map<Style, Function<ItemStack, Skill>> innateSkillByStyle;
		Map<Style, Map<LivingMotion, StaticAnimation>> livingMotionModifiers;
		Function<Style, Boolean> comboCancel;
		boolean canBePlacedOffhand;
		
		protected Builder() {
			this.constructor = WeaponCapability::new;
			this.styleProvider = (entitypatch) -> Styles.ONE_HAND;
			this.weaponCombinationPredicator = (entitypatch) -> false;
			this.passiveSkill = null;
			this.swingSound = EpicFightSounds.WHOOSH;
			this.hitSound = EpicFightSounds.BLUNT_HIT;
			this.hitParticle = EpicFightParticles.HIT_BLADE.get();
			this.collider = ColliderPreset.FIST;
			this.autoAttackMotionMap = Maps.newHashMap();
			this.innateSkillByStyle = Maps.newHashMap();
			this.livingMotionModifiers = null;
			this.canBePlacedOffhand = true;
			this.comboCancel = (style) -> true;
		}
		
		@Override
		public Builder category(WeaponCategory category) {
			super.category(category);
			return this;
		}
		
		public Builder styleProvider(Function<LivingEntityPatch<?>, Style> styleProvider) {
			this.styleProvider = styleProvider;
			return this;
		}
		
		public Builder passiveSkill(Skill passiveSkill) {
			this.passiveSkill = passiveSkill;
			return this;
		}
		
		public Builder swingSound(SoundEvent swingSound) {
			this.swingSound = swingSound;
			return this;
		}
		
		public Builder hitSound(SoundEvent hitSound) {
			this.hitSound = hitSound;
			return this;
		}
		
		public Builder hitParticle(HitParticleType hitParticle) {
			this.hitParticle = hitParticle;
			return this;
		}
		
		public Builder collider(Collider collider) {
			this.collider = collider;
			return this;
		}
		
		public Builder canBePlacedOffhand(boolean canBePlacedOffhand) {
			this.canBePlacedOffhand = canBePlacedOffhand;
			return this;
		}
		
		public Builder livingMotionModifier(Style wieldStyle, LivingMotion livingMotion, StaticAnimation animation) {
			if (this.livingMotionModifiers == null) {
				this.livingMotionModifiers = Maps.<Style, Map<LivingMotion, StaticAnimation>>newHashMap();
			}
			
			if (!this.livingMotionModifiers.containsKey(wieldStyle)) {
				this.livingMotionModifiers.put(wieldStyle, Maps.<LivingMotion, StaticAnimation>newHashMap());
			}
			
			this.livingMotionModifiers.get(wieldStyle).put(livingMotion, animation);
			
			return this;
		}
		
		public Builder addStyleAttibutes(Style style, Pair<Attribute, AttributeModifier> attributePair) {
			super.addStyleAttibutes(style, attributePair);
			return this;
		}
		
		public Builder newStyleCombo(Style style, StaticAnimation... animation) {
			this.autoAttackMotionMap.put(style, Lists.newArrayList(animation));
			return this;
		}
		
		public Builder weaponCombinationPredicator(Function<LivingEntityPatch<?>, Boolean> predicator) {
			this.weaponCombinationPredicator = predicator;
			return this;
		}
		
		public Builder innateSkill(Style style, Function<ItemStack, Skill> innateSkill) {
			this.innateSkillByStyle.put(style, innateSkill);
			return this;
		}
		
		public Builder comboCancel(Function<Style, Boolean> comboCancel) {
			this.comboCancel = comboCancel;
			return this;
		}
	}
}