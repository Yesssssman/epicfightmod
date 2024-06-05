package yesman.epicfight.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.projectile.ProjectilePatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

@Mixin(value = LivingEntity.class)
public abstract class MixinLivingEntity {
	@Shadow
	protected void hurtArmor(DamageSource p_21122_, float p_21123_) {}
	
	@Inject(at = @At(value = "TAIL"), method = "blockUsingShield(Lnet/minecraft/world/entity/LivingEntity;)V", cancellable = true)
	private void epicfight_blockUsingShield(LivingEntity p_21200_, CallbackInfo info) {
		LivingEntity self = (LivingEntity)((Object)this);
		LivingEntityPatch<?> opponentEntitypatch = EpicFightCapabilities.getEntityPatch(p_21200_, LivingEntityPatch.class);
		LivingEntityPatch<?> selfEntitypatch = EpicFightCapabilities.getEntityPatch(self, LivingEntityPatch.class);
		
		if (opponentEntitypatch != null) {
			opponentEntitypatch.setLastAttackResult(AttackResult.blocked(0.0F));
			
			if (selfEntitypatch != null && opponentEntitypatch.getEpicFightDamageSource() != null) {
				opponentEntitypatch.onAttackBlocked(opponentEntitypatch.getEpicFightDamageSource(), selfEntitypatch);
			}
		}
	}
	
	@Inject(at = @At(value = "RETURN"), method = "hurt", cancellable = true)
	private void epicfight_hurt(DamageSource damagesource, float amount, CallbackInfoReturnable<Boolean> info) {
		LivingEntity self = (LivingEntity)((Object)this);
		LivingEntityPatch<?> entitypatch = EpicFightCapabilities.getEntityPatch(damagesource.getEntity(), LivingEntityPatch.class);
		
		if (entitypatch != null) {
			if (info.getReturnValue()) {
				entitypatch.setLastAttackEntity(self);
			}
		}
	}
	
	@Inject(at = @At(value = "HEAD"), method = "push(Lnet/minecraft/world/entity/Entity;)V", cancellable = true)
	private void epicfight_push(Entity p_20293_, CallbackInfo info) {
		LivingEntity self = (LivingEntity)((Object)this);
		LivingEntityPatch<?> entitypatch = EpicFightCapabilities.getEntityPatch(self, LivingEntityPatch.class);
		
		if (entitypatch != null && !entitypatch.canPush(p_20293_)) {
			info.cancel();
		}
	}
	
	@Inject(at = @At(value = "HEAD"), method = "getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F", cancellable = true)
	private void epicfight_getDamageAfterArmorAbsorb(DamageSource source, float amount, CallbackInfoReturnable<Float> info) {
		EpicFightDamageSource epicFightDamageSource = null;
		LivingEntityPatch<?> attackerEntityPatch = EpicFightCapabilities.getEntityPatch(source.getEntity(), LivingEntityPatch.class);
		
		if (source instanceof EpicFightDamageSource instance) {
            epicFightDamageSource = instance;
		} else if (source.isIndirect() && source.getDirectEntity() != null) {
			ProjectilePatch<?> projectileCap = EpicFightCapabilities.getEntityPatch(source.getDirectEntity(), ProjectilePatch.class);
			
			if (projectileCap != null) {
				epicFightDamageSource = projectileCap.getEpicFightDamageSource(source);
			}
		} else if (attackerEntityPatch != null) {
			epicFightDamageSource = attackerEntityPatch.getEpicFightDamageSource();
		}
		
		if (epicFightDamageSource != null && !source.is(DamageTypeTags.BYPASSES_ARMOR)) {
			this.hurtArmor(source, amount);
			float armorNegationAmount = amount * epicFightDamageSource.getArmorNegation() * 0.01F;
			float amountElse = amount - armorNegationAmount;
			LivingEntity self = (LivingEntity)((Object)this);
			amountElse = CombatRules.getDamageAfterAbsorb(amountElse, (float)self.getArmorValue(), (float)self.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
			info.setReturnValue(armorNegationAmount + amountElse);
			info.cancel();
		}
	}
}