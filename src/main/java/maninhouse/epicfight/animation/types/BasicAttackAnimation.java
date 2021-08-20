package maninhouse.epicfight.animation.types;

import javax.annotation.Nullable;

import maninhouse.epicfight.animation.Pose;
import maninhouse.epicfight.animation.property.Property.AttackAnimationProperty;
import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.capabilities.entity.player.PlayerData;
import maninhouse.epicfight.physics.Collider;
import maninhouse.epicfight.utils.math.Vec3f;
import net.minecraft.util.Hand;

public class BasicAttackAnimation extends AttackAnimation {
	public BasicAttackAnimation(int id, float convertTime, float antic, float contact, float recovery, @Nullable Collider collider, String index, String path) {
		this(id, convertTime, antic, antic, contact, recovery, collider, index, path);
	}
	
	public BasicAttackAnimation(int id, float convertTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, String index, String path) {
		super(id, convertTime, antic, preDelay, contact, recovery, false, collider, index, path);
		this.addProperty(AttackAnimationProperty.DIRECTIONAL, true);
	}
	
	public BasicAttackAnimation(int id, float convertTime, float antic, float contact, float recovery, Hand hand, @Nullable Collider collider,  String index, String path) {
		super(id, convertTime, antic, antic, contact, recovery, false, hand, collider, index, path);
		this.addProperty(AttackAnimationProperty.DIRECTIONAL, true);
	}
	
	public BasicAttackAnimation(int id, float convertTime, boolean affectY, String path, Phase... phases) {
		super(id, convertTime, affectY, path, phases);
		this.addProperty(AttackAnimationProperty.DIRECTIONAL, true);
	}
	
	@Override
	public void getLinkAnimation(Pose pose1, float timeModifier, LivingData<?> entitydata, LinkAnimation dest) {
		float extTime = Math.max(this.convertTime + timeModifier, 0);
		if (entitydata instanceof PlayerData<?>) {
			PlayerData<?> playerdata = (PlayerData<?>) entitydata;
			extTime *= (float)(this.totalTime * playerdata.getAttackSpeed());
		}
		
		extTime = Math.max(extTime - this.convertTime, 0);
		super.getLinkAnimation(pose1, extTime, entitydata, dest);
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