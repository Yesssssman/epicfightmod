package yesman.epicfight.world.capabilities.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import yesman.epicfight.api.animation.AnimationProvider;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.gameasset.EpicFightSkills;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class TridentCapability extends RangedWeaponCapability {
	private List<AnimationProvider<?>> attackMotion;
	private List<AnimationProvider<?>> mountAttackMotion;
	
	public TridentCapability(CapabilityItem.Builder builder) {
		super(builder);
		
		this.attackMotion = List.of(Animations.TRIDENT_AUTO1, Animations.TRIDENT_AUTO2, Animations.TRIDENT_AUTO3, Animations.SPEAR_DASH, Animations.SPEAR_ONEHAND_AIR_SLASH);
		this.mountAttackMotion = List.of(Animations.SPEAR_MOUNT_ATTACK);
	}
	
	@Override
	public Style getStyle(LivingEntityPatch<?> entitypatch) {
		return Styles.ONE_HAND;
	}
	
	@Override
	public SoundEvent getHitSound() {
		return EpicFightSounds.BLADE_HIT.get();
	}
	
	@Override
	public HitParticleType getHitParticle() {
		return EpicFightParticles.HIT_BLADE.get();
	}
	
	@Override
	public Collider getWeaponCollider() {
		return ColliderPreset.SPEAR;
	}
	
	@Override
	public List<AnimationProvider<?>> getAutoAttckMotion(PlayerPatch<?> playerpatch) {
		return this.attackMotion;
	}
	
	@Override
	public List<AnimationProvider<?>> getMountAttackMotion() {
		return this.mountAttackMotion;
	}
	
	@Nullable
	@Override
	public Skill getInnateSkill(PlayerPatch<?> playerpatch, ItemStack itemstack) {
		if (EnchantmentHelper.getRiptide(itemstack) > 0) {
			return EpicFightSkills.TSUNAMI;
		} else if (EnchantmentHelper.hasChanneling(itemstack)) {
			return EpicFightSkills.WRATHFUL_LIGHTING;
		} else if (EnchantmentHelper.getLoyalty(itemstack) > 0) {
			return EpicFightSkills.EVERLASTING_ALLEGIANCE;
		} else {
			return null;
		}
	}
}