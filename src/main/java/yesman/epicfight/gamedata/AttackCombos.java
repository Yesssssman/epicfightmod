package yesman.epicfight.gamedata;

import java.util.ArrayList;
import java.util.List;

import yesman.epicfight.animation.types.AttackAnimation;

public class AttackCombos {
	public static List<AttackAnimation> BIPED_ARMED = new ArrayList<AttackAnimation> ();
	public static List<AttackAnimation> BIPED_UNARMED = new ArrayList<AttackAnimation> ();
	public static List<AttackAnimation> DROWNED_ARMED_SPEAR = new ArrayList<AttackAnimation> ();
	public static List<AttackAnimation> BIPED_MOUNT_SWORD = new ArrayList<AttackAnimation> ();
	public static List<AttackAnimation> ENDERMAN_KNEE = new ArrayList<AttackAnimation> ();
	public static List<AttackAnimation> ENDERMAN_KICK_COMBO = new ArrayList<AttackAnimation> ();
	public static List<AttackAnimation> ENDERMAN_SPINKICK = new ArrayList<AttackAnimation> ();
	public static List<AttackAnimation> ENDERMAN_JUMPKICK = new ArrayList<AttackAnimation> ();
	public static List<AttackAnimation> GOLEM_SWINGARM = new ArrayList<AttackAnimation> ();
	public static List<AttackAnimation> GOLEM_HEADBUTT = new ArrayList<AttackAnimation> ();
	public static List<AttackAnimation> GOLEM_SMASH_GROUND = new ArrayList<AttackAnimation> ();
	public static List<AttackAnimation> HOGLIN_HEADBUTT = new ArrayList<AttackAnimation> ();
	public static List<AttackAnimation> RAVAGER_HEADBUTT = new ArrayList<AttackAnimation> ();
	public static List<AttackAnimation> RAVAGER_SMASHING_GROUND = new ArrayList<AttackAnimation> ();
	public static List<AttackAnimation> SPIDER = new ArrayList<AttackAnimation> ();
	public static List<AttackAnimation> SPIDER_JUMP = new ArrayList<AttackAnimation> ();
	public static List<AttackAnimation> VINDICATOR_AXE = new ArrayList<AttackAnimation> ();
	public static List<AttackAnimation> SKELETON_ARMED = new ArrayList<AttackAnimation> ();
	
	public static void setAttackCombos() {
		BIPED_ARMED.add((AttackAnimation) Animations.BIPED_ARMED_MOB_ATTACK1);
		BIPED_ARMED.add((AttackAnimation) Animations.BIPED_ARMED_MOB_ATTACK2);
		DROWNED_ARMED_SPEAR.add((AttackAnimation) Animations.SPEAR_ONEHAND_AUTO);
		BIPED_MOUNT_SWORD.add((AttackAnimation) Animations.SWORD_MOUNT_ATTACK);
		ENDERMAN_KNEE.add((AttackAnimation) Animations.ENDERMAN_KNEE);
		ENDERMAN_KICK_COMBO.add((AttackAnimation) Animations.ENDERMAN_KICK_COMBO);
		ENDERMAN_SPINKICK.add((AttackAnimation) Animations.ENDERMAN_KICK1);
		ENDERMAN_JUMPKICK.add((AttackAnimation) Animations.ENDERMAN_KICK2);
		GOLEM_SWINGARM.add((AttackAnimation)Animations.GOLEM_ATTACK1);
		GOLEM_HEADBUTT.add((AttackAnimation)Animations.GOLEM_ATTACK2);
		GOLEM_SMASH_GROUND.add((AttackAnimation)Animations.GOLEM_ATTACK3);
		GOLEM_SMASH_GROUND.add((AttackAnimation)Animations.GOLEM_ATTACK4);
		RAVAGER_HEADBUTT.add((AttackAnimation)Animations.RAVAGER_ATTACK1);
		RAVAGER_HEADBUTT.add((AttackAnimation)Animations.RAVAGER_ATTACK2);
		RAVAGER_SMASHING_GROUND.add((AttackAnimation)Animations.RAVAGER_ATTACK3);
		SPIDER.add((AttackAnimation) Animations.SPIDER_ATTACK);
		SPIDER_JUMP.add((AttackAnimation) Animations.SPIDER_JUMP_ATTACK);
		VINDICATOR_AXE.add((AttackAnimation) Animations.VINDICATOR_SWING_AXE1);
		VINDICATOR_AXE.add((AttackAnimation) Animations.VINDICATOR_SWING_AXE2);
		VINDICATOR_AXE.add((AttackAnimation) Animations.VINDICATOR_SWING_AXE3);
		SKELETON_ARMED.add((AttackAnimation) Animations.WITHER_SKELETON_ATTACK1);
		SKELETON_ARMED.add((AttackAnimation) Animations.WITHER_SKELETON_ATTACK2);
		SKELETON_ARMED.add((AttackAnimation) Animations.WITHER_SKELETON_ATTACK3);
		BIPED_UNARMED.add((AttackAnimation) Animations.ZOMBIE_ATTACK1);
		BIPED_UNARMED.add((AttackAnimation) Animations.ZOMBIE_ATTACK2);
		BIPED_UNARMED.add((AttackAnimation) Animations.ZOMBIE_ATTACK3);
		HOGLIN_HEADBUTT.add((AttackAnimation) Animations.HOGLIN_ATTACK);
	}
}