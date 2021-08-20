package maninhouse.epicfight.capabilities.item;

import java.util.Map;

import com.mojang.datafixers.util.Pair;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.capabilities.entity.player.PlayerData;
import maninhouse.epicfight.entity.ai.attribute.ModAttributes;
import maninhouse.epicfight.gamedata.Skills;
import maninhouse.epicfight.gamedata.Sounds;
import maninhouse.epicfight.particle.HitParticleType;
import maninhouse.epicfight.particle.Particles;
import maninhouse.epicfight.skill.Skill;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;

public class AxeCapability extends ToolCapability {
	public AxeCapability(Item item) {
		super(item, WeaponCategory.AXE);
	}

	@Override
	public Skill getSpecialAttack(PlayerData<?> playerdata) {
		return Skills.GUILLOTINE_AXE;
	}
	
	@Override
	protected void registerAttribute() {
		int i = this.itemTier.getHarvestLevel();
		
		if (i != 0) {
			this.addStyleAttibute(HoldStyle.ONE_HAND, Pair.of(ModAttributes.ARMOR_NEGATION, ModAttributes.getArmorNegationModifier(10.0D * i)));
		}
		
		this.addStyleAttibute(HoldStyle.ONE_HAND, Pair.of(ModAttributes.IMPACT, ModAttributes.getImpactModifier(0.7D + 0.3D * i)));
	}
	
	@Override
	public HitParticleType getHitParticle() {
		return Particles.HIT_BLADE.get();
	}
	
	@Override
	public SoundEvent getHitSound() {
		return Sounds.BLADE_HIT;
	}
	
	@Override
	public Map<LivingMotion, StaticAnimation> getLivingMotionChanges(PlayerData<?> player) {
		HoldOption holdOption = this.getHoldOption();
		
		if (holdOption == HoldOption.TWO_HANDED) {
			return SwordCapability.MOTION_CHANGER_TWOHAND;
		} else {
			return SwordCapability.MOTION_CHANGER_ONEHAND;
		}
	}
}