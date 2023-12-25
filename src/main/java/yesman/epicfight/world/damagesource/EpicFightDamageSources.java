package yesman.epicfight.world.damagesource;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface EpicFightDamageSources {
	static EpicFightDamageSources of(Level level) {
		return () -> level;
	}

	static EpicFightDamageSource copy(DamageSource damageSource) {
		return new EpicFightDamageSource(damageSource);
	}

	default EpicFightDamageSource shockwave(LivingEntity owner) {
		Holder<DamageType> damageType = getDamageTypeHolder(EpicFightDamageTypes.SHOCKWAVE);
		return new EpicFightDamageSource(damageType, owner, owner, null);
	}

	default EpicFightDamageSource witherBeam(LivingEntity owner) {
		Holder<DamageType> damageType = getDamageTypeHolder(EpicFightDamageTypes.WITHER_BEAM);
		EpicFightDamageSource damageSource = new EpicFightDamageSource(damageType, owner, owner, null);
		damageSource.addRuntimeTag(DamageTypes.MAGIC);
		return damageSource;
	}

	default EpicFightDamageSource trident(Entity owner, Entity causingEntity) {
		return copy(getDamageSources().trident(owner, causingEntity));
	}

	default EpicFightDamageSource mobAttack(LivingEntity owner) {
		return copy(getDamageSources().mobAttack(owner));
	}

	default EpicFightDamageSource playerAttack(Player owner) {
		return copy(getDamageSources().playerAttack(owner));
	}

	default EpicFightDamageSource indirectMagic(LivingEntity owner, Entity causingEntity) {
		EpicFightDamageSource damageSource = copy(getDamageSources().indirectMagic(owner, causingEntity));
		damageSource.addRuntimeTag(DamageTypeTags.BYPASSES_INVULNERABILITY);
		damageSource.addRuntimeTag(DamageTypes.MAGIC);
		return damageSource;
	}

	default DamageSources getDamageSources() {
		return getLevel().damageSources();
	}

	default Holder<DamageType> getDamageTypeHolder(ResourceKey<DamageType> key) {
		return getLevel().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key);
	}

	Level getLevel();
}