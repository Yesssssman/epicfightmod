package yesman.epicfight.gamedata;

import yesman.epicfight.physics.Collider;
import yesman.epicfight.physics.MultiOBBCollider;
import yesman.epicfight.physics.OBBCollider;

public class Colliders {
	public static Collider body = new OBBCollider(0.5F, 0.7F, 0.7F, 0F, 1.0F, -0.6F);
	public static Collider dagger = new MultiOBBCollider(3, 0.4F, 0.4F, 0.6F, 0.0F, 0.0F, -0.1F);
	public static Collider dualDaggerDash = new OBBCollider(0.8F, 0.5F, 1.0F, 0.0F, 1.0F, -0.6F);
	public static Collider bladeRush = new OBBCollider(0.8F, 0.5F, 1.9F, 0.0F, 1.0F, -1.2F);
	public static Collider dualSword = new OBBCollider(0.8F, 0.5F, 1.0F, 0.0F, 0.5F, -1.0F);
	public static Collider dualSwordDash = new OBBCollider(0.8F, 0.5F, 1.0F, 0F, 1.0F, -1.0F);
	public static Collider fatal_draw = new OBBCollider(1.75F, 0.25F, 1.35F, 0F, 1.0F, -1.0F);
	public static Collider fatal_draw_dash = new OBBCollider(0.7F, 0.7F, 5.0F, 0F, 1.0F, -4.0F);
	public static Collider fist = new MultiOBBCollider(2, 0.4F, 0.4F, 0.4F, 0F, 0F, 0F);
	public static Collider greatSword = new MultiOBBCollider(3, 0.5F, 0.8F, 1.0F, 0F, 0F, -1.0F);
	public static Collider headbutt = new OBBCollider(0.4F, 0.4F, 0.4F, 0F, 0F, -0.3F);
	public static Collider headbutt_ravager = new OBBCollider(0.8F, 0.8F, 0.8F, 0F, 0F, -0.3F);
	public static Collider katana = new MultiOBBCollider(3, 0.4F, 0.4F, 1.0F, 0F, 0F, -0.5F);
	public static Collider sword = new MultiOBBCollider(3, 0.4F, 0.4F, 0.7F, 0F, 0F, -0.35F);
	public static Collider longsword = new MultiOBBCollider(3, 0.4F, 0.4F, 0.8F, 0F, 0F, -0.75F);
	public static Collider spear = new MultiOBBCollider(3, 0.6F, 0.6F, 1.0F, 0F, 0F, -1.0F);
	public static Collider spiderRaid = new OBBCollider(0.8F, 0.8F, 0.8F, 0F, 0F, -0.4F);
	public static Collider tools = new MultiOBBCollider(3, 0.4F, 0.4F, 0.55F, 0F, 0.0F, -0.25F);
	public static Collider endermanStick = new OBBCollider(0.4F, 0.8F, 0.4F, 0F, 0F, 0F);
	public static Collider golemSmashDown = new MultiOBBCollider(3, 0.75F, 0.5F, 0.5F, 0.6F, 0.5F, 0F);
	public static Collider golemSwingArm = new OBBCollider(0.6F, 0.9F, 0.6F, 0F, 0F, 0F);
	public static Collider narrowFront = new OBBCollider(0.4F, 0.4F, 0.5F, 0F, 1.0F, -0.85F);
	public static Collider dualSwordAirslash = new OBBCollider(0.8F, 0.4F, 1.0F, 0F, 0.5F, -0.5F);
	public static Collider dualDaggerAirslash = new OBBCollider(0.8F, 0.4F, 0.75F, 0F, 0.5F, -0.5F);
	/**
	public static void update() {
		Collider newCOllider = new OBBCollider(0.8F, 0.4F, 0.75F, 0F, 0.5F, -0.5F);
		((AttackAnimation)Animations.DAGGER_DUAL_AIR_SLASH).changeCollider(newCOllider, 0);
	}**/
}