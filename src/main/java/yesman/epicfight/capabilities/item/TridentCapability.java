package yesman.epicfight.capabilities.item;

import java.util.ArrayList;
import java.util.List;

import com.mojang.datafixers.util.Pair;

import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;
import yesman.epicfight.animation.types.StaticAnimation;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.capabilities.entity.player.PlayerData;
import yesman.epicfight.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.gamedata.Animations;
import yesman.epicfight.gamedata.Colliders;
import yesman.epicfight.gamedata.Sounds;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.particle.Particles;
import yesman.epicfight.physics.Collider;

public class TridentCapability extends RangedWeaponCapability {
	private static List<StaticAnimation> attackMotion;
	private static List<StaticAnimation> mountAttackMotion;
	
	public TridentCapability(Item item) {
		super(item, null, Animations.BIPED_JAVELIN_AIM, Animations.BIPED_JAVELIN_THROW);
		
		if (attackMotion == null) {
			attackMotion = new ArrayList<StaticAnimation> ();
			attackMotion.add(Animations.SPEAR_ONEHAND_AUTO);
			attackMotion.add(Animations.SPEAR_DASH);
			attackMotion.add(Animations.SPEAR_ONEHAND_AIR_SLASH);
		}
		
		if (mountAttackMotion == null) {
			mountAttackMotion = new ArrayList<StaticAnimation> ();
			mountAttackMotion.add(Animations.SPEAR_MOUNT_ATTACK);
		}
	}
	
	@Override
	public Style getStyle(LivingData<?> entitydata) {
		return Style.ONE_HAND;
	}
	
	@Override
	protected void registerAttribute() {
		this.addStyleAttibute(Style.ONE_HAND, Pair.of(EpicFightAttributes.IMPACT.get(), EpicFightAttributes.getImpactModifier(2.25D)));
	}
	
	@Override
	public SoundEvent getHitSound() {
		return Sounds.BLADE_HIT;
	}
	
	@Override
	public HitParticleType getHitParticle() {
		return Particles.HIT_BLADE.get();
	}

	@Override
	public Collider getWeaponCollider() {
		return Colliders.spear;
	}
	
	@Override
	public List<StaticAnimation> getAutoAttckMotion(PlayerData<?> playerdata) {
		return attackMotion;
	}
	
	@Override
	public List<StaticAnimation> getMountAttackMotion() {
		return mountAttackMotion;
	}
}