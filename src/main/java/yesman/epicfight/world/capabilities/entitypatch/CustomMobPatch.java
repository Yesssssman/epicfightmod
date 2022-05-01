package yesman.epicfight.world.capabilities.entitypatch;

import com.mojang.datafixers.util.Pair;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.data.reloader.MobPatchReloadListener;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.api.utils.game.ExtendedDamageSource.StunType;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.world.capabilities.entitypatch.mob.Faction;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributeSupplier;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.world.entity.ai.goal.ChasingGoal;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviorGoal;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;

public class CustomMobPatch<T extends Mob> extends MobPatch<T> {
	private final MobPatchReloadListener.MobPatchProvider provider;
	
	public CustomMobPatch(Faction faction, MobPatchReloadListener.MobPatchProvider provider) {
		super(faction);
		this.provider = provider;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void initAI() {
		this.resetCombatAI();
		this.original.goalSelector.addGoal(0, new CombatBehaviorGoal<>(this, ((CombatBehaviors.Builder<CustomMobPatch<T>>)this.provider.getCombatBehaviorsBuilder()).build(this)));
		this.original.goalSelector.addGoal(1, new ChasingGoal(this, this.original, this.provider.getChasingSpeed(), false));
	}
	
	@Override
	protected void initAttributes() {
		this.original.getAttributes().supplier = new EpicFightAttributeSupplier(this.original.getAttributes().supplier);
		this.original.getAttribute(EpicFightAttributes.WEIGHT.get()).setBaseValue(this.original.getAttribute(Attributes.MAX_HEALTH).getBaseValue() * 2.0D);
		this.original.getAttribute(EpicFightAttributes.MAX_STRIKES.get()).setBaseValue(this.provider.getAttributeValues().get(EpicFightAttributes.MAX_STRIKES.get()));
		this.original.getAttribute(EpicFightAttributes.ARMOR_NEGATION.get()).setBaseValue(this.provider.getAttributeValues().get(EpicFightAttributes.ARMOR_NEGATION.get()));
		this.original.getAttribute(EpicFightAttributes.IMPACT.get()).setBaseValue(this.provider.getAttributeValues().get(EpicFightAttributes.IMPACT.get()));
	}
	
	@Override
	public void tick(LivingUpdateEvent event) {
		super.tick(event);
	}
	
	@Override
	public void initAnimator(ClientAnimator clientAnimator) {
		for (Pair<LivingMotion, StaticAnimation> pair : this.provider.getDefaultAnimations()) {
			clientAnimator.addLivingAnimation(pair.getFirst(), pair.getSecond());
		}
		
		clientAnimator.setCurrentMotionsAsDefault();
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		super.commonAggressiveMobUpdateMotion(considerInaction);
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.get(this.provider.getModelLocation());
	}
	
	@Override
	public StaticAnimation getHitAnimation(StunType stunType) {
		return this.provider.getStunAnimations().get(stunType);
	}
}