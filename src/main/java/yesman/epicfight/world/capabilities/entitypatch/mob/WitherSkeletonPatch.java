package yesman.epicfight.world.capabilities.entitypatch.mob;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.api.utils.game.ExtendedDamageSource;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class WitherSkeletonPatch<T extends PathfinderMob> extends SkeletonPatch<T> {
	public WitherSkeletonPatch() {
		super(Faction.WITHER);
	}
	
	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.original.getAttribute(EpicFightAttributes.STUN_ARMOR.get()).setBaseValue(6.0F);
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		super.commonAggressiveMobUpdateMotion(considerInaction);
	}
	
	@Override
	public void onHurtSomeone(Entity target, InteractionHand handIn, ExtendedDamageSource source, float amount, boolean succeed) {
		if (succeed && target instanceof LivingEntity && this.original.getRandom().nextInt(10) == 0) {
			((LivingEntity)target).addEffect(new MobEffectInstance(MobEffects.WITHER, 200));
		}
	}
	
	@Override
	public OpenMatrix4f getModelMatrix(float partialTicks) {
		OpenMatrix4f mat = super.getModelMatrix(partialTicks);
		return OpenMatrix4f.scale(new Vec3f(1.2F, 1.2F, 1.2F), mat, mat);
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.skeleton;
	}
}