package maninhouse.epicfight.capabilities.item;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.capabilities.ModCapabilities;
import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.capabilities.entity.player.PlayerData;
import maninhouse.epicfight.entity.ai.attribute.ModAttributes;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.gamedata.Colliders;
import maninhouse.epicfight.gamedata.Skills;
import maninhouse.epicfight.gamedata.Sounds;
import maninhouse.epicfight.particle.HitParticleType;
import maninhouse.epicfight.particle.Particles;
import maninhouse.epicfight.physics.Collider;
import maninhouse.epicfight.skill.Skill;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SwordCapability extends ToolCapability {
	static final Map<LivingMotion, StaticAnimation> MOTION_CHANGER_ONEHAND = ImmutableMap.of(LivingMotion.BLOCK, Animations.SWORD_GUARD);
	static final Map<LivingMotion, StaticAnimation> MOTION_CHANGER_TWOHAND = ImmutableMap.of(LivingMotion.BLOCK, Animations.SWORD_DUAL_GUARD);
	
	private static final List<StaticAnimation> SWORD_BASIC_ATTACK_MOTIONS;
	private static final List<StaticAnimation> DUAL_BASIC_ATTACK_MOTIONS;
	
	static {
		SWORD_BASIC_ATTACK_MOTIONS = Lists.<StaticAnimation>newArrayList();
		SWORD_BASIC_ATTACK_MOTIONS.add(Animations.SWORD_AUTO_1);
		SWORD_BASIC_ATTACK_MOTIONS.add(Animations.SWORD_AUTO_2);
		SWORD_BASIC_ATTACK_MOTIONS.add(Animations.SWORD_AUTO_3);
		SWORD_BASIC_ATTACK_MOTIONS.add(Animations.SWORD_DASH);
		SWORD_BASIC_ATTACK_MOTIONS.add(Animations.SWORD_AIR_SLASH);
		DUAL_BASIC_ATTACK_MOTIONS = Lists.<StaticAnimation>newArrayList();
		DUAL_BASIC_ATTACK_MOTIONS.add(Animations.SWORD_DUAL_AUTO_1);
		DUAL_BASIC_ATTACK_MOTIONS.add(Animations.SWORD_DUAL_AUTO_2);
		DUAL_BASIC_ATTACK_MOTIONS.add(Animations.SWORD_DUAL_AUTO_3);
		DUAL_BASIC_ATTACK_MOTIONS.add(Animations.SWORD_DUAL_DASH);
		DUAL_BASIC_ATTACK_MOTIONS.add(Animations.SWORD_DUAL_AIR_SLASH);
	}
	
	public SwordCapability(Item item) {
		super(item, WeaponCategory.SWORD);
	}
	
	@Override
	protected void registerAttribute() {
		int i = this.itemTier.getHarvestLevel();
		this.addStyleAttibute(HoldStyle.ONE_HAND, Pair.of(ModAttributes.MAX_STRIKES, ModAttributes.getMaxStrikesModifier(1)));
		this.addStyleAttibute(HoldStyle.ONE_HAND, Pair.of(ModAttributes.IMPACT, ModAttributes.getImpactModifier(0.5D + 0.2D * i)));
		this.addStyleAttibute(HoldStyle.TWO_HAND, Pair.of(ModAttributes.MAX_STRIKES, ModAttributes.getMaxStrikesModifier(1)));
		this.addStyleAttibute(HoldStyle.TWO_HAND, Pair.of(ModAttributes.IMPACT, ModAttributes.getImpactModifier(0.5D + 0.2D * i)));
	}
	
	@Override
	public Skill getSpecialAttack(PlayerData<?> playerdata) {
		if(this.getStyle(playerdata) == HoldStyle.ONE_HAND) {
			return Skills.SWEEPING_EDGE;
		} else {
			return Skills.DANCING_EDGE;
		}
	}
	
	@Override
	public List<StaticAnimation> getAutoAttckMotion(PlayerData<?> playerdata) {
		if(this.getStyle(playerdata) == HoldStyle.ONE_HAND) {
			return SWORD_BASIC_ATTACK_MOTIONS;
		} else {
			return DUAL_BASIC_ATTACK_MOTIONS;
		}
	}
	
	@Override
	public HoldStyle getStyle(LivingData<?> entitydata) {
		return entitydata.getHeldItemCapability(Hand.OFF_HAND).getWeaponCategory() == WeaponCategory.SWORD ? HoldStyle.TWO_HAND : HoldStyle.ONE_HAND;
	}
	
	@Override
	public SoundEvent getHitSound() {
		return this.itemTier == ItemTier.WOOD ? Sounds.BLUNT_HIT : Sounds.BLADE_HIT;
	}
	
	@Override
	public HitParticleType getHitParticle() {
		return this.itemTier == ItemTier.WOOD ? Particles.HIT_BLUNT.get() : Particles.HIT_BLADE.get();
	}
	
	@Override
	public Collider getWeaponCollider() {
		return Colliders.sword;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean isValidOffhandItem(ItemStack itemStack) {
		CapabilityItem cap =  ModCapabilities.getItemStackCapability(itemStack);
		return super.isValidOffhandItem(itemStack) || (cap.weaponCategory == WeaponCategory.SWORD);
	}
	
	@Override
	public Map<LivingMotion, StaticAnimation> getLivingMotionChanges(PlayerData<?> playerdata) {
		HoldStyle holdStyle = this.getStyle(playerdata);
		
		if (holdStyle == HoldStyle.TWO_HAND) {
			return MOTION_CHANGER_TWOHAND;
		} else {
			return MOTION_CHANGER_ONEHAND;
		}
	}
}