package yesman.epicfight.animation.types;

import javax.annotation.Nullable;

import net.minecraft.util.Hand;
import yesman.epicfight.animation.Pose;
import yesman.epicfight.animation.property.Property.AttackAnimationProperty;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.capabilities.entity.player.PlayerData;
import yesman.epicfight.model.Model;
import yesman.epicfight.physics.Collider;
import yesman.epicfight.utils.math.Vec3f;

public class BasicAttackAnimation extends AttackAnimation {
	public BasicAttackAnimation(float convertTime, float antic, float contact, float recovery, @Nullable Collider collider, String index, String path, Model model) {
		this(convertTime, antic, antic, contact, recovery, collider, index, path, model);
	}
	
	public BasicAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, String index, String path, Model model) {
		super(convertTime, antic, preDelay, contact, recovery, false, collider, index, path, model);
		this.addProperty(AttackAnimationProperty.DIRECTIONAL, true);
	}
	
	public BasicAttackAnimation(float convertTime, float antic, float contact, float recovery, Hand hand, @Nullable Collider collider,  String index, String path, Model model) {
		super(convertTime, antic, antic, contact, recovery, false, hand, collider, index, path, model);
		this.addProperty(AttackAnimationProperty.DIRECTIONAL, true);
	}
	
	public BasicAttackAnimation(float convertTime, boolean affectY, String path, Model model, Phase... phases) {
		super(convertTime, affectY, path, model, phases);
		this.addProperty(AttackAnimationProperty.DIRECTIONAL, true);
	}
	
	@Override
	public void setLinkAnimation(Pose pose1, float timeModifier, LivingData<?> entitydata, LinkAnimation dest) {
		float extTime = Math.max(this.convertTime + timeModifier, 0);
		if (entitydata instanceof PlayerData<?>) {
			PlayerData<?> playerdata = (PlayerData<?>) entitydata;
			extTime *= (float)(this.totalTime * playerdata.getAttackSpeed());
		}
		
		extTime = Math.max(extTime - this.convertTime, 0);
		super.setLinkAnimation(pose1, extTime, entitydata, dest);
	}
	
	@Override
	protected Vec3f getCoordVector(LivingData<?> entitydata, DynamicAnimation dynamicAnimation) {
		Vec3f vec3 = super.getCoordVector(entitydata, dynamicAnimation);
		if (entitydata.shouldBlockMoving()) {
			vec3.scale(0.0F);
		}
		
		return vec3;
	}
	
	@Override
	public boolean isBasicAttackAnimation() {
		return true;
	}
}