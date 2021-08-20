package maninhouse.epicfight.client;

import maninhouse.epicfight.capabilities.ModCapabilities;
import maninhouse.epicfight.client.animation.BindingOption;
import maninhouse.epicfight.client.animation.ClientAnimationProperty;
import maninhouse.epicfight.client.animation.Layer;
import maninhouse.epicfight.client.capabilites.entity.ClientPlayerData;
import maninhouse.epicfight.client.events.engine.ControllEngine;
import maninhouse.epicfight.client.events.engine.RenderEngine;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.main.EpicFightMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.PointOfView;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientEngine {
	public static ClientEngine INSTANCE;
	public Minecraft minecraft;
	public RenderEngine renderEngine;
	public ControllEngine inputController;
	private PlayerActingMode playerActingMode = PlayerActingMode.MINING;
	
	public ClientEngine() {
		INSTANCE = this;
		this.minecraft = Minecraft.getInstance();
		this.renderEngine = new RenderEngine();
		this.inputController = new ControllEngine();
	}
	
	public void toggleActingMode() {
		if(this.playerActingMode == PlayerActingMode.MINING) {
			this.switchToBattleMode();
			if (EpicFightMod.CLIENT_INGAME_CONFIG.cameraAutoSwitch.getValue()) {
				Minecraft.getInstance().gameSettings.setPointOfView(PointOfView.THIRD_PERSON_BACK);
			}
		} else {
			this.switchToMiningMode();
			if (EpicFightMod.CLIENT_INGAME_CONFIG.cameraAutoSwitch.getValue()) {
				Minecraft.getInstance().gameSettings.setPointOfView(PointOfView.FIRST_PERSON);
			}
		}
	}
	
	private void switchToMiningMode() {
		this.playerActingMode = PlayerActingMode.MINING;
		this.renderEngine.guiSkillBar.slideDown();
	}
	
	private void switchToBattleMode() {
		this.playerActingMode = PlayerActingMode.BATTLE;
		this.renderEngine.guiSkillBar.slideUp();
	}
	
	public PlayerActingMode getPlayerActingMode() {
		return this.playerActingMode;
	}
	
	public boolean isBattleMode() {
		return this.playerActingMode == PlayerActingMode.BATTLE;
	}
	
	public ClientPlayerData getPlayerData() {
		return (ClientPlayerData) Minecraft.getInstance().player.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
	}
	
	public static enum PlayerActingMode {
		MINING, BATTLE
	}
	
	public static final BindingOption[] BIPED_UPPER_JOINTS = {
			BindingOption.of("Torso", BindingOption.DEFAULT),
			BindingOption.of("Chest", BindingOption.DEFAULT),
			BindingOption.of("Head", BindingOption.DEFAULT),
			BindingOption.of("Shoulder_R", BindingOption.DEFAULT),
			BindingOption.of("Arm_R", BindingOption.DEFAULT),
			BindingOption.of("Hand_R", BindingOption.DEFAULT),
			BindingOption.of("Elbow_R", BindingOption.DEFAULT),
			BindingOption.of("Tool_R", BindingOption.DEFAULT),
			BindingOption.of("Shoulder_L", BindingOption.DEFAULT),
			BindingOption.of("Arm_L", BindingOption.DEFAULT),
			BindingOption.of("Hand_L", BindingOption.DEFAULT),
			BindingOption.of("Elbow_L", BindingOption.DEFAULT),
			BindingOption.of("Tool_L", BindingOption.DEFAULT)
	};
	
	public static final BindingOption[] BIPED_UPPER_JOINTS_ROOT = {
			BindingOption.of("Root", BindingOption.ROOT_MIX),
			BindingOption.of("Torso", BindingOption.DEFAULT),
			BindingOption.of("Chest", BindingOption.DYNAMIC_TRANSFORM),
			BindingOption.of("Head", BindingOption.DYNAMIC_TRANSFORM),
			BindingOption.of("Shoulder_R", BindingOption.DEFAULT),
			BindingOption.of("Arm_R", BindingOption.DEFAULT),
			BindingOption.of("Hand_R", BindingOption.DEFAULT),
			BindingOption.of("Elbow_R", BindingOption.DEFAULT),
			BindingOption.of("Tool_R", BindingOption.DEFAULT),
			BindingOption.of("Shoulder_L", BindingOption.DEFAULT),
			BindingOption.of("Arm_L", BindingOption.DEFAULT),
			BindingOption.of("Hand_L", BindingOption.DEFAULT),
			BindingOption.of("Elbow_L", BindingOption.DEFAULT),
			BindingOption.of("Tool_L", BindingOption.DEFAULT)
	};
	
	public static final BindingOption[] BIPED_ARMS = {
			BindingOption.of("Shoulder_R", BindingOption.DEFAULT),
			BindingOption.of("Arm_R", BindingOption.DEFAULT),
			BindingOption.of("Hand_R", BindingOption.DEFAULT),
			BindingOption.of("Elbow_R", BindingOption.DEFAULT),
			BindingOption.of("Tool_R", BindingOption.DEFAULT),
			BindingOption.of("Shoulder_L", BindingOption.DEFAULT),
			BindingOption.of("Arm_L", BindingOption.DEFAULT),
			BindingOption.of("Hand_L", BindingOption.DEFAULT),
			BindingOption.of("Elbow_L", BindingOption.DEFAULT),
			BindingOption.of("Tool_L", BindingOption.DEFAULT),
	};
	
	public static void initAnimations() {
		Animations.BIPED_IDLE_GREATSWORD
			.addProperty(ClientAnimationProperty.JOINT_BINDING_OPTION, BIPED_ARMS)
			.addProperty(ClientAnimationProperty.PRIORITY, Layer.Priority.MIDDLE);
		Animations.BIPED_IDLE_LONGSWORD
			.addProperty(ClientAnimationProperty.PRIORITY, Layer.Priority.MIDDLE);
		Animations.BIPED_WALK_LONGSWORD
			.addProperty(ClientAnimationProperty.JOINT_BINDING_OPTION, BIPED_UPPER_JOINTS_ROOT)
			.addProperty(ClientAnimationProperty.PRIORITY, Layer.Priority.MIDDLE);
		Animations.BIPED_IDLE_UNSHEATHING
			.addProperty(ClientAnimationProperty.JOINT_BINDING_OPTION, BIPED_ARMS)
			.addProperty(ClientAnimationProperty.PRIORITY, Layer.Priority.MIDDLE);
		Animations.BIPED_WALK_UNSHEATHING
			.addProperty(ClientAnimationProperty.JOINT_BINDING_OPTION, BIPED_ARMS)
			.addProperty(ClientAnimationProperty.PRIORITY, Layer.Priority.MIDDLE);
		Animations.BIPED_RUN_UNSHEATHING
			.addProperty(ClientAnimationProperty.JOINT_BINDING_OPTION, BIPED_ARMS)
			.addProperty(ClientAnimationProperty.PRIORITY, Layer.Priority.MIDDLE);
		Animations.BIPED_IDLE_SHEATHING
			.addProperty(ClientAnimationProperty.PRIORITY, Layer.Priority.MIDDLE);
		Animations.BIPED_IDLE_SHEATHING_MIX
			.addProperty(ClientAnimationProperty.JOINT_BINDING_OPTION, BIPED_ARMS)
			.addProperty(ClientAnimationProperty.PRIORITY, Layer.Priority.MIDDLE);
		Animations.BIPED_MOVE_SHEATHING
			.addProperty(ClientAnimationProperty.JOINT_BINDING_OPTION, BIPED_UPPER_JOINTS_ROOT)
			.addProperty(ClientAnimationProperty.PRIORITY, Layer.Priority.MIDDLE);
		Animations.BIPED_IDLE_TACHI
			.addProperty(ClientAnimationProperty.JOINT_BINDING_OPTION, BIPED_ARMS)
			.addProperty(ClientAnimationProperty.PRIORITY, Layer.Priority.MIDDLE);
		Animations.BIPED_RUN_SPEAR
			.addProperty(ClientAnimationProperty.JOINT_BINDING_OPTION, BIPED_UPPER_JOINTS_ROOT)
			.addProperty(ClientAnimationProperty.PRIORITY, Layer.Priority.MIDDLE);
		
		Animations.BIPED_IDLE_CROSSBOW
			.addProperty(ClientAnimationProperty.JOINT_BINDING_OPTION, BIPED_ARMS)
			.addProperty(ClientAnimationProperty.PRIORITY, Layer.Priority.MIDDLE);
		Animations.BIPED_CLIMBING
			.addProperty(ClientAnimationProperty.PRIORITY, Layer.Priority.MIDDLE);
		Animations.BIPED_LAND_DAMAGE
			.addProperty(ClientAnimationProperty.PRIORITY, Layer.Priority.LOWEST);
		Animations.BIPED_KATANA_SCRAP
			.addProperty(ClientAnimationProperty.JOINT_BINDING_OPTION, BIPED_UPPER_JOINTS_ROOT)
			.addProperty(ClientAnimationProperty.PRIORITY, Layer.Priority.HIGHEST);
		Animations.BIPED_DIG
			.addProperty(ClientAnimationProperty.JOINT_BINDING_OPTION, BIPED_UPPER_JOINTS_ROOT)
			.addProperty(ClientAnimationProperty.PRIORITY, Layer.Priority.MIDDLE);
		Animations.BIPED_BLOCK
			.addProperty(ClientAnimationProperty.JOINT_BINDING_OPTION, BIPED_UPPER_JOINTS_ROOT)
			.addProperty(ClientAnimationProperty.PRIORITY, Layer.Priority.MIDDLE);
		
		Animations.SWORD_GUARD
			.addProperty(ClientAnimationProperty.JOINT_BINDING_OPTION, BIPED_UPPER_JOINTS_ROOT)
			.addProperty(ClientAnimationProperty.PRIORITY, Layer.Priority.MIDDLE);
		Animations.SWORD_DUAL_GUARD
			.addProperty(ClientAnimationProperty.JOINT_BINDING_OPTION, BIPED_UPPER_JOINTS_ROOT)
			.addProperty(ClientAnimationProperty.PRIORITY, Layer.Priority.MIDDLE);
		Animations.SPEAR_GUARD
			.addProperty(ClientAnimationProperty.JOINT_BINDING_OPTION, BIPED_UPPER_JOINTS_ROOT)
			.addProperty(ClientAnimationProperty.PRIORITY, Layer.Priority.MIDDLE);
		Animations.GREATSWORD_GUARD
			.addProperty(ClientAnimationProperty.JOINT_BINDING_OPTION, BIPED_UPPER_JOINTS_ROOT)
			.addProperty(ClientAnimationProperty.PRIORITY, Layer.Priority.MIDDLE);
		Animations.KATANA_GUARD
			.addProperty(ClientAnimationProperty.JOINT_BINDING_OPTION, BIPED_UPPER_JOINTS_ROOT)
			.addProperty(ClientAnimationProperty.PRIORITY, Layer.Priority.MIDDLE);
		Animations.LONGSWORD_GUARD
			.addProperty(ClientAnimationProperty.JOINT_BINDING_OPTION, BIPED_UPPER_JOINTS_ROOT)
			.addProperty(ClientAnimationProperty.PRIORITY, Layer.Priority.MIDDLE);
		
		Animations.BIPED_STEP_FORWARD
			.addProperty(ClientAnimationProperty.PRIORITY, Layer.Priority.LOWEST);
		Animations.BIPED_STEP_BACKWARD
			.addProperty(ClientAnimationProperty.PRIORITY, Layer.Priority.LOWEST);
		Animations.BIPED_STEP_LEFT
			.addProperty(ClientAnimationProperty.PRIORITY, Layer.Priority.LOWEST);
		Animations.BIPED_STEP_RIGHT
			.addProperty(ClientAnimationProperty.PRIORITY, Layer.Priority.LOWEST);
		
		Animations.ENDERMAN_RUSH
			.addProperty(ClientAnimationProperty.JOINT_BINDING_OPTION, BIPED_UPPER_JOINTS)
			.addProperty(ClientAnimationProperty.PRIORITY, Layer.Priority.MIDDLE);		
		
		Animations.WITCH_DRINKING
			.addProperty(ClientAnimationProperty.JOINT_BINDING_OPTION, BIPED_UPPER_JOINTS)
			.addProperty(ClientAnimationProperty.PRIORITY, Layer.Priority.MIDDLE);
		
		Animations.BIPED_CROSSBOW_RELOAD
			.addProperty(ClientAnimationProperty.JOINT_BINDING_OPTION, BIPED_ARMS)
			.addProperty(ClientAnimationProperty.PRIORITY, Layer.Priority.MIDDLE);
		Animations.BIPED_CROSSBOW_AIM
			.addProperty(ClientAnimationProperty.JOINT_BINDING_OPTION, BIPED_UPPER_JOINTS_ROOT)
			.addProperty(ClientAnimationProperty.PRIORITY, Layer.Priority.MIDDLE);
		Animations.BIPED_CROSSBOW_SHOT
			.addProperty(ClientAnimationProperty.JOINT_BINDING_OPTION, BIPED_UPPER_JOINTS_ROOT)
			.addProperty(ClientAnimationProperty.PRIORITY, Layer.Priority.MIDDLE);
		
		Animations.BIPED_BOW_AIM
			.addProperty(ClientAnimationProperty.JOINT_BINDING_OPTION, BIPED_UPPER_JOINTS_ROOT)
			.addProperty(ClientAnimationProperty.PRIORITY, Layer.Priority.MIDDLE);
		Animations.BIPED_BOW_SHOT
			.addProperty(ClientAnimationProperty.JOINT_BINDING_OPTION, BIPED_UPPER_JOINTS_ROOT)
			.addProperty(ClientAnimationProperty.PRIORITY, Layer.Priority.MIDDLE);
		
		Animations.BIPED_JAVELIN_AIM
			.addProperty(ClientAnimationProperty.JOINT_BINDING_OPTION, BIPED_UPPER_JOINTS_ROOT)
			.addProperty(ClientAnimationProperty.PRIORITY, Layer.Priority.MIDDLE);
		Animations.BIPED_JAVELIN_THROW
			.addProperty(ClientAnimationProperty.JOINT_BINDING_OPTION, BIPED_UPPER_JOINTS_ROOT)
			.addProperty(ClientAnimationProperty.PRIORITY, Layer.Priority.MIDDLE);
		
		Animations.OFF_ANIMATION_HIGHEST.addProperty(ClientAnimationProperty.PRIORITY, Layer.Priority.HIGHEST);
		Animations.OFF_ANIMATION_MIDDLE.addProperty(ClientAnimationProperty.PRIORITY, Layer.Priority.MIDDLE);
	}
}