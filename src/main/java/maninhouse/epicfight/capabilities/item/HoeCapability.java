package maninhouse.epicfight.capabilities.item;

import java.util.ArrayList;
import java.util.List;

import com.mojang.datafixers.util.Pair;

import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.capabilities.entity.player.PlayerData;
import maninhouse.epicfight.entity.ai.attribute.ModAttributes;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.gamedata.Sounds;
import maninhouse.epicfight.particle.HitParticleType;
import maninhouse.epicfight.particle.Particles;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;

public class HoeCapability extends ToolCapability {
	
	protected static List<StaticAnimation> hoeAttackMotion;
	
	static {
		hoeAttackMotion = new ArrayList<StaticAnimation> ();
		hoeAttackMotion.add(Animations.TOOL_AUTO_1);
		hoeAttackMotion.add(Animations.TOOL_AUTO_2);
		hoeAttackMotion.add(Animations.TOOL_DASH);
		hoeAttackMotion.add(Animations.SWORD_AIR_SLASH);
	}
	
	public HoeCapability(Item item) {
		super(item, WeaponCategory.HOE);
	}
	
	@Override
	public List<StaticAnimation> getAutoAttckMotion(PlayerData<?> playerdata) {
		return hoeAttackMotion;
	}
	
	@Override
	protected void registerAttribute() {
		this.addStyleAttibute(HoldStyle.ONE_HAND, Pair.of(ModAttributes.IMPACT, ModAttributes.getImpactModifier(-0.4D + 0.1D * this.itemTier.getHarvestLevel())));
	}
	
	@Override
	public HitParticleType getHitParticle() {
		return Particles.HIT_BLADE.get();
	}
	
	@Override
	public SoundEvent getHitSound() {
		return Sounds.BLADE_HIT;
	}
}