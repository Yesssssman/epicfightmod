package maninthehouse.epicfight.gamedata;

import maninthehouse.epicfight.physics.Collider;
import maninthehouse.epicfight.physics.ColliderOBB;

public class Colliders {
	public static Collider body = new ColliderOBB(0.5F, 0.7F, 0.7F, 0F, 1.0F, -0.6F);
	public static Collider dualSwordDash = new ColliderOBB(0.8F, 0.5F, 1.5F, 0F, 1.0F, 0.0F);
	public static Collider fatal_draw = new ColliderOBB(1.75F, 0.7F, 1.5F, 0F, 1.0F, -1.5F);
	public static Collider fatal_draw_dash = new ColliderOBB(0.7F, 0.7F, 5.0F, 0F, 1.0F, -4.0F);
	public static Collider fist = new ColliderOBB(0.4F, 0.4F, 0.4F, 0F, 0F, 0F);
	public static Collider greatSword = new ColliderOBB(0.5F, 0.8F, 1.2F, 0F, 0F, -1.15F);
	public static Collider headbutt = new ColliderOBB(0.4F, 0.4F, 0.4F, 0F, 0F, -0.3F);
	public static Collider headbutt_ravaber = new ColliderOBB(0.8F, 0.8F, 0.8F, 0F, 0F, -0.3F);
	public static Collider katana = new ColliderOBB(0.55F, 0.95F, 0.95F, 0F, -0.2F, -0.5F);
	public static Collider sword = new ColliderOBB(0.75F, 0.55F, 0.85F, 0F, 0F, -0.25F);
	public static Collider swordDash = new ColliderOBB(0.4F, 0.4F, 0.75F, 0F, 0F, -0.6F);
	public static Collider swordSwingFast = new ColliderOBB(0.4F, 1.0F, 0.75F, 0F, 0F, -0.3F);
	public static Collider spearNarrow = new ColliderOBB(0.4F, 0.4F, 0.9F, 0F, 0F, -1.35F);
	public static Collider spearSwing = new ColliderOBB(0.5F, 1.0F, 0.9F, 0F, 0F, -1.0F);
	public static Collider spiderRaid = new ColliderOBB(0.8F, 0.8F, 0.8F, 0F, 0F, -0.4F);
	public static Collider tools = new ColliderOBB(0.4F, 0.4F, 0.55F, 0F, 0.0F, 0F);
	public static Collider endermanStick = new ColliderOBB(0.4F, 0.8F, 0.4F, 0F, 0F, 0F);
	public static Collider golemSmashDown = new ColliderOBB(0.5F, 0.25F, 0.5F, 0F, -0.75F, 0F);
	public static Collider golemSwingArm = new ColliderOBB(0.6F, 0.9F, 0.6F, 0F, 0F, 0F);
	public static Collider shadowboxing = new ColliderOBB(0.4F, 0.4F, 0.5F, 0F, 1.0F, -0.85F);
	
	public static void update() {/**
		Collider newCOllider = new ColliderOBB(0.4F, 0.4F, 0.9F, 0F, 0F, -1.35F);
		((AttackAnimation)Animations.SPEAR_DASH).changeCollider(newCOllider, 0);
		((AttackAnimation)Animations.SPEAR_ONEHAND_AUTO).changeCollider(newCOllider, 0);**/
	}
}