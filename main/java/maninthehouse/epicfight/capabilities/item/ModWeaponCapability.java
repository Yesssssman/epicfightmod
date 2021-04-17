package maninthehouse.epicfight.capabilities.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import maninthehouse.epicfight.animation.LivingMotion;
import maninthehouse.epicfight.animation.types.StaticAnimation;
import maninthehouse.epicfight.capabilities.entity.LivingData;
import maninthehouse.epicfight.capabilities.entity.player.PlayerData;
import maninthehouse.epicfight.physics.Collider;
import maninthehouse.epicfight.skill.Skill;
import net.minecraft.util.SoundEvent;

public class ModWeaponCapability extends CapabilityItem {
	protected final Function<LivingData<?>, WieldStyle> stylegetter;
	protected final Skill weaponGimmick;
	protected final SoundEvent smashingSound;
	protected final SoundEvent hitSound;
	protected final Collider weaponCollider;
	protected HandProperty handProperty;
	protected Map<WieldStyle, List<StaticAnimation>> autoAttackMotionMap;
	protected Map<WieldStyle, Skill> specialAttackMap;
	protected Map<LivingMotion, StaticAnimation> livingMotionChangers;
	
	public ModWeaponCapability(WeaponCategory category, Function<LivingData<?>, WieldStyle> stylegetter, Skill weaponGimmick, SoundEvent smash, SoundEvent hit,
			Collider weaponCollider, HandProperty handProperty) {
		super(category);
		this.autoAttackMotionMap = Maps.<WieldStyle, List<StaticAnimation>>newHashMap();
		this.specialAttackMap = Maps.<WieldStyle, Skill>newHashMap();
		this.stylegetter = stylegetter;
		this.weaponGimmick = weaponGimmick;
		this.smashingSound = smash;
		this.hitSound = hit;
		this.handProperty = handProperty;
		this.weaponCollider = weaponCollider;
	}

	public void addLivingMotionChanger(LivingMotion livingMotion, StaticAnimation animation) {
		if (livingMotionChangers == null) {
			livingMotionChangers = new HashMap<LivingMotion, StaticAnimation>();
		}

		livingMotionChangers.put(livingMotion, animation);
	}

	public void addStyleCombo(WieldStyle style, StaticAnimation... animation) {
		this.autoAttackMotionMap.put(style, Lists.newArrayList(animation));
	}
	
	public void addStyleSpecialAttack(WieldStyle style, Skill specialAttack) {
		this.specialAttackMap.put(style, specialAttack);
	}
	
	@Override
	public final List<StaticAnimation> getAutoAttckMotion(PlayerData<?> playerdata) {
		return this.autoAttackMotionMap.get(this.getStyle(playerdata));
	}
	
	@Override
	public final Skill getSpecialAttack(PlayerData<?> playerdata) {
		return this.specialAttackMap.get(this.getStyle(playerdata));
	}
	
	@Override
	public Skill getPassiveSkill() {
		return this.weaponGimmick;
	}
	
	@Override
	public final List<StaticAnimation> getMountAttackMotion() {
		return this.autoAttackMotionMap.get(WieldStyle.MOUNT);
	}
	
	@Override
	public WieldStyle getStyle(LivingData<?> entitydata) {
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
	public Collider getWeaponCollider() {
		return this.weaponCollider != null ? weaponCollider : super.getWeaponCollider();
	}
	
	@Override
	public HandProperty getHandProperty() {
		return this.handProperty;
	}
	
	@Override
	public Map<LivingMotion, StaticAnimation> getLivingMotionChanges(PlayerData<?> player) {
		return this.livingMotionChangers;
	}
}