package yesman.epicfight.skill.mover;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillDataManager.SkillDataKey;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class PhantomAscentSkill extends Skill {
	private static final UUID EVENT_UUID = UUID.fromString("051a9bb2-7541-11ee-b962-0242ac120002");
	private static final SkillDataKey<Boolean> JUMP_KEY_PRESSED_LAST_TIME = SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
	private static final SkillDataKey<Integer> JUMP_COUNT = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	private final StaticAnimation[] animations = new StaticAnimation[2];
	private int maxJumpAmount;
	
	public PhantomAscentSkill(Builder<? extends Skill> builder) {
		super(builder);
		
		this.animations[0] = EpicFightMod.getInstance().animationManager.findAnimationByPath("epicfight:biped/skill/phantom_ascent_forward");
		this.animations[1] = EpicFightMod.getInstance().animationManager.findAnimationByPath("epicfight:biped/skill/phantom_ascent_backward");
	}
	
	@Override
	public void setParams(CompoundTag parameters) {
		super.setParams(parameters);
		this.maxJumpAmount = parameters.getInt("jump_amount");
		this.consumption = 0.2F;
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		super.onInitiate(container);
		
		PlayerEventListener listener = container.getExecuter().getEventListener();
		container.getDataManager().registerData(JUMP_KEY_PRESSED_LAST_TIME);
		container.getDataManager().registerData(JUMP_COUNT);
		
		listener.addEventListener(EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID, (event) -> {
			if (event.getPlayerPatch().getOriginal().getAbilities().flying) {
				return;
			}
			
			// Check directly from the keybind because event.getMovementInput().isJumping doesn't allow to be set as true while player's jumping
			boolean jumpPressed = Minecraft.getInstance().options.keyJump.isDown();
			boolean jumpPressedPrev = container.getDataManager().getDataValue(JUMP_KEY_PRESSED_LAST_TIME);
			
			if (jumpPressed && !jumpPressedPrev) {
				if (container.getStack() < 1) {
					return;
				}
				
				int jumpCounter = container.getDataManager().getDataValue(JUMP_COUNT);
				
				if (jumpCounter > 0 || !event.getPlayerPatch().getOriginal().isOnGround()) {
					if (jumpCounter < this.maxJumpAmount) {
						container.setResource(0.0F);
						container.getDataManager().setDataF(JUMP_COUNT, (v) -> v + 1);
						
						Input input = event.getMovementInput();
						input.tick(false);
						
				        int forward = event.getMovementInput().up ? 1 : 0;
				        int backward = event.getMovementInput().down ? -1 : 0;
				        int left = event.getMovementInput().left ? 1 : 0;
				        int right = event.getMovementInput().right ? -1 : 0;
						int vertic = forward + backward;
						int horizon = left + right;
						int degree = -(90 * horizon * (1 - Math.abs(vertic)) + 45 * vertic * horizon);
						
						int scale = forward == 0 && backward == 0 && left == 0 && right == 0 ? 0 : (vertic < 0 ? -1 : 1);
						Vec3 forwardHorizontal = Vec3.directionFromRotation(new Vec2(0, container.getExecuter().getOriginal().getViewYRot(1.0F)));
						Vec3 jumpDir = OpenMatrix4f.transform(OpenMatrix4f.createRotatorDeg(-degree, Vec3f.Y_AXIS), forwardHorizontal.scale(0.15D * scale));
						Vec3 deltaMove = container.getExecuter().getOriginal().getDeltaMovement();
						
						container.getExecuter().getOriginal().setDeltaMovement(deltaMove.x + jumpDir.x, 0.6D, deltaMove.z + jumpDir.z);
						container.getExecuter().playAnimationSynchronized(this.animations[vertic < 0 ? 1 : 0], 0.0F);
						event.getPlayerPatch().changeModelYRot(degree);
					};
				} else {
					container.getDataManager().setData(JUMP_COUNT, 1);
				}
			}
			
			container.getDataManager().setData(JUMP_KEY_PRESSED_LAST_TIME, jumpPressed);
		});
		
		listener.addEventListener(EventType.FALL_EVENT, EVENT_UUID, (event) -> {
			if (event.getPlayerPatch().isLogicalClient()) {
				container.getDataManager().setData(JUMP_COUNT, 0);
				container.getDataManager().setData(JUMP_KEY_PRESSED_LAST_TIME, false);
			}
		});
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		PlayerEventListener listener = container.getExecuter().getEventListener();
		listener.removeListener(EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID);
		listener.removeListener(EventType.FALL_EVENT, EVENT_UUID);
	}
	
	@Override
	public boolean canExecute(PlayerPatch<?> executer) {
		return false;
	}
}