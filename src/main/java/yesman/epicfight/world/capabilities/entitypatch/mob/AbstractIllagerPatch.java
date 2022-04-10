package yesman.epicfight.world.capabilities.entitypatch.mob;

import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Models;

public abstract class AbstractIllagerPatch<T extends AbstractIllager> extends HumanoidMobPatch<T> {
	public AbstractIllagerPatch(Faction faction) {
		super(faction);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void initAnimator(ClientAnimator animatorClient) {
		animatorClient.addLivingMotion(LivingMotion.IDLE, Animations.ILLAGER_IDLE);
		animatorClient.addLivingMotion(LivingMotion.WALK, Animations.ILLAGER_WALK);
		animatorClient.addLivingMotion(LivingMotion.DEATH, Animations.BIPED_DEATH);
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.illager;
	}
}