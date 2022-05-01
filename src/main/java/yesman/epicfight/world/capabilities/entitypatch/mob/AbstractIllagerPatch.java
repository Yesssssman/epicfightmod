package yesman.epicfight.world.capabilities.entitypatch.mob;

import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;

public abstract class AbstractIllagerPatch<T extends AbstractIllager> extends HumanoidMobPatch<T> {
	public AbstractIllagerPatch(Faction faction) {
		super(faction);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void initAnimator(ClientAnimator clientAnimator) {
		clientAnimator.addLivingAnimation(LivingMotion.IDLE, Animations.ILLAGER_IDLE);
		clientAnimator.addLivingAnimation(LivingMotion.WALK, Animations.ILLAGER_WALK);
		clientAnimator.addLivingAnimation(LivingMotion.DEATH, Animations.BIPED_DEATH);
		clientAnimator.setCurrentMotionsAsDefault();
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.illager;
	}
}