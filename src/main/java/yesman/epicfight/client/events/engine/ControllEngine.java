package yesman.epicfight.client.events.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.InputEvent.MouseInputEvent;
import net.minecraftforge.client.event.InputEvent.MouseScrollEvent;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.settings.KeyBindingMap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.client.gui.screen.SkillEditScreen;
import yesman.epicfight.client.input.EpicFightKeyMappings;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.entity.eventlistener.MovementInputEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

@OnlyIn(Dist.CLIENT)
public class ControllEngine {
	private Map<KeyMapping, BiConsumer<Integer, Integer>> keyFunctionMap;
	private GLFWCursorPosCallbackI blockRotationCallback = (handle, x, y) -> {
		Minecraft.getInstance().execute(()->{tracingMouseX = x; tracingMouseY = y;});
	};
	private Minecraft minecraft;
	private LocalPlayer player;
	private LocalPlayerPatch playerpatch;
	private KeyBindingMap keyHash;
	private double tracingMouseX;
	private double tracingMouseY;
	private int mouseLeftPressCounter = 0;
	private int sneakPressCounter = 0;
	private int reservedKey;
	private int reserveCounter;
	private boolean sneakPressToggle = false;
	private boolean mouseLeftPressToggle = false;
	private boolean lightPress;
	public Options options;
	
	public ControllEngine() {
		Events.controllEngine = this;
		this.minecraft = Minecraft.getInstance();
		this.options = this.minecraft.options;
		this.keyFunctionMap = new HashMap<KeyMapping, BiConsumer<Integer, Integer>>();
		this.keyFunctionMap.put(this.options.keyAttack, this::attackKeyPressed);
		this.keyFunctionMap.put(this.options.keySwapOffhand, this::swapHandKeyPressed);
		this.keyFunctionMap.put(EpicFightKeyMappings.SWITCH_MODE, this::switchModeKeyPressed);
		this.keyFunctionMap.put(EpicFightKeyMappings.DODGE, this::dodgeKeyPressed);
		this.keyFunctionMap.put(EpicFightKeyMappings.SPECIAL_SKILL, this::specialSkillKeyPressed);
		
		try {
			this.keyHash = (KeyBindingMap) ObfuscationReflectionHelper.findField(KeyMapping.class, "f_90810_").get(null);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public void setPlayerPatch(LocalPlayerPatch playerpatch) {
		this.mouseLeftPressCounter = 0;
		this.mouseLeftPressToggle = false;
		this.sneakPressCounter = 0;
		this.sneakPressToggle = false;
		this.lightPress = false;
		this.player = playerpatch.getOriginal();
		this.playerpatch = playerpatch;
	}
	
	public boolean canPlayerMove(EntityState playerState) {
		return !playerState.movementLocked() || this.player.isRidingJumpable();
	}
	
	public boolean canPlayerRotate(EntityState playerState) {
		return !playerState.turningLocked() || this.player.isRidingJumpable();
	}
	
	private void attackKeyPressed(int key, int action) {
		if (action == 1) {
			if (this.playerpatch.isBattleMode()) {
				this.setKeyBind(this.options.keyAttack, false);
				while (this.options.keyAttack.consumeClick()) {
				}
				
				if (this.player.getUseItemRemainingTicks() == 0) {
					if (!this.mouseLeftPressToggle) {
						this.mouseLeftPressToggle = true;
					}
				}
			}
		}
	}
	
	private void dodgeKeyPressed(int key, int action) {
		if (action == 1) {
			if (key == this.options.keyShift.getKey().getValue()) {
				if (this.player.getVehicle() == null && this.playerpatch.isBattleMode()) {
					if (!this.sneakPressToggle) {
						this.sneakPressToggle = true;
					}
				}
			} else {
				SkillCategory skillCategory = (this.playerpatch.getEntityState().knockDown()) ? SkillCategories.KNOCKDOWN_WAKEUP : SkillCategories.DODGE;
				SkillContainer skill = this.playerpatch.getSkill(skillCategory);
				
				if (skill.canExecute(this.playerpatch) && skill.getSkill().isExecutableState(this.playerpatch)) {
					skill.sendExecuteRequest(this.playerpatch);
				}
			}
		}
	}
	
	private void swapHandKeyPressed(int key, int action) {
		if (this.playerpatch.getEntityState().inaction() || (!this.playerpatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).canBePlacedOffhand())) {
			while (this.options.keySwapOffhand.consumeClick()) {}
			this.setKeyBind(this.options.keySwapOffhand, false);
		}
	}
	
	private void switchModeKeyPressed(int key, int action) {
		if (action == 1) {
			this.playerpatch.toggleMode();
		}
	}
	
	private void specialSkillKeyPressed(int key, int action) {
		if (action == 1) {
			if (key != 0) {
				if (!this.playerpatch.getSkill(SkillCategories.WEAPON_SPECIAL_ATTACK).sendExecuteRequest(this.playerpatch)) {
					if (!this.player.isSpectator()) {
						this.reserveKey(SkillCategories.WEAPON_SPECIAL_ATTACK);
					}
				}
			} else {
				if (this.options.keyAttack.equals(EpicFightKeyMappings.SPECIAL_SKILL)) {
					KeyMapping.click(this.options.keyAttack.getKey());
				}
			}
		}
	}
	
	public void tick() {
		if (this.playerpatch == null) {
			return;
		}
		
		this.playerpatch.updateEntityState();
		
		if (this.mouseLeftPressToggle) {
			if (!this.isKeyDown(this.options.keyAttack)) {
				this.lightPress = true;
				this.mouseLeftPressToggle = false;
				this.mouseLeftPressCounter = 0;
			} else {
				if (EpicFightKeyMappings.SPECIAL_SKILL.getKey().equals(this.options.keyAttack.getKey())) {
					if (this.mouseLeftPressCounter > EpicFightMod.CLIENT_INGAME_CONFIG.longPressCount.getValue()) {
						if (!this.playerpatch.getSkill(SkillCategories.WEAPON_SPECIAL_ATTACK).sendExecuteRequest(this.playerpatch)) {
							if (!this.player.isSpectator()) {
								this.reserveKey(SkillCategories.WEAPON_SPECIAL_ATTACK);
							}
						}
						this.mouseLeftPressToggle = false;
						this.mouseLeftPressCounter = 0;
					} else {
						this.setKeyBind(this.options.keyAttack, false);
						this.mouseLeftPressCounter++;
					}
				}
			}
		}
		
		if (this.lightPress) {
			SkillCategory slot = (!this.player.isOnGround() && !this.player.isInWater() && this.player.getDeltaMovement().y > -0.05D) ? SkillCategories.AIR_ATTACK : SkillCategories.BASIC_ATTACK;
			
			if (this.playerpatch.getSkill(slot).sendExecuteRequest(this.playerpatch)) {
				this.player.resetAttackStrengthTicker();
				this.lightPress = false;
				this.resetReservedKey();
			} else {
				if (!(this.player.isSpectator() || slot == SkillCategories.AIR_ATTACK)) {
					this.reserveKey(slot);
				}
			}
			
			this.lightPress = false;
			this.mouseLeftPressToggle = false;
			this.mouseLeftPressCounter = 0;
		}
		
		if (this.sneakPressToggle) {
			if (!this.isKeyDown(this.options.keyShift)) {
				SkillCategory skillCategory = (this.playerpatch.getEntityState().knockDown()) ? SkillCategories.KNOCKDOWN_WAKEUP : SkillCategories.DODGE;
				SkillContainer skill = this.playerpatch.getSkill(skillCategory);
				
				if (!skill.sendExecuteRequest(this.playerpatch)) {
					this.reserveKey(SkillCategories.DODGE);
				}
				
				this.sneakPressToggle = false;
				this.sneakPressCounter = 0;
			} else {
				if (this.sneakPressCounter > EpicFightMod.CLIENT_INGAME_CONFIG.longPressCount.getValue()) {
					this.sneakPressToggle = false;
					this.sneakPressCounter = 0;
				} else {
					this.sneakPressCounter++;
				}
			}
		}
		
		if (this.reservedKey >= 0) {
			if (this.reserveCounter > 0) {
				SkillContainer skill = this.playerpatch.getSkill(this.reservedKey);
				this.reserveCounter--;
				if (skill.getSkill() != null && skill.sendExecuteRequest(this.playerpatch)) {
					this.resetReservedKey();
				}
			} else {
				this.resetReservedKey();
			}
		}
		
		for (int i = 0; i < 9; ++i) {
			if (isKeyDown(this.options.keyHotbarSlots[i])) {
				if (this.playerpatch.getEntityState().inaction()) {
					this.options.keyHotbarSlots[i].consumeClick();
				}
			}
		}
		
		if (EpicFightKeyMappings.SKILL_EDIT.consumeClick()) {
			if (this.playerpatch.getSkillCapability() != null) {
				Minecraft.getInstance().setScreen(new SkillEditScreen(this.playerpatch.getSkillCapability()));
			}
		}
		
		if (this.minecraft.isPaused()) {
			this.minecraft.mouseHandler.setup(Minecraft.getInstance().getWindow().getWindow());
		}
	}
	
	private void reserveKey(SkillCategory slot) {
		this.reservedKey = slot.universalOrdinal();
		this.reserveCounter = 8;
	}
	
	private void resetReservedKey() {
		this.reservedKey = -1;
		this.reserveCounter = -1;
	}
	
	public boolean isKeyDown(KeyMapping key) {
		if (key.getKey().getType() == InputConstants.Type.KEYSYM) {
			return GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), key.getKey().getValue()) > 0;
		} else if(key.getKey().getType() == InputConstants.Type.MOUSE) {
			return GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), key.getKey().getValue()) > 0;
		} else {
			return false;
		}
	}
	
	public void setKeyBind(KeyMapping key, boolean setter) {
		KeyMapping.set(key.getKey(), setter);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Mod.EventBusSubscriber(modid = EpicFightMod.MODID, value = Dist.CLIENT)
	public static class Events {
		static ControllEngine controllEngine;
		
		@SubscribeEvent
		public static void mouseEvent(MouseInputEvent event) {
			if (controllEngine.minecraft.player != null && Minecraft.getInstance().screen == null) {
				InputConstants.Key input = InputConstants.Type.MOUSE.getOrCreate(event.getButton());
				for (KeyMapping keybinding : controllEngine.keyHash.lookupAll(input)) {
					if (controllEngine.keyFunctionMap.containsKey(keybinding)) {
						controllEngine.keyFunctionMap.get(keybinding).accept(event.getButton(), event.getAction());
					}
				}
			}
		}
		
		@SubscribeEvent
		public static void mouseScrollEvent(MouseScrollEvent event) {
			if (controllEngine.minecraft.player != null && controllEngine.playerpatch != null && controllEngine.playerpatch.getEntityState().inaction()) {
				if (controllEngine.minecraft.screen == null) {
					event.setCanceled(true);
				}
			}
		}
		
		@SubscribeEvent
		public static void keyboardEvent(KeyInputEvent event) {
			if (controllEngine.minecraft.player != null && Minecraft.getInstance().screen == null) {
				InputConstants.Key input = InputConstants.Type.KEYSYM.getOrCreate(event.getKey());
				for (KeyMapping keybinding : controllEngine.keyHash.lookupAll(input)) {
					if (controllEngine.keyFunctionMap.containsKey(keybinding)) {
						controllEngine.keyFunctionMap.get(keybinding).accept(event.getKey(), event.getAction());
					}
				}
			}
		}
		
		@SubscribeEvent
		public static void moveInputEvent(MovementInputUpdateEvent event) {
			if (controllEngine.playerpatch == null) {
				return;
			}
			
			Minecraft minecraft = Minecraft.getInstance();
			EntityState playerState = controllEngine.playerpatch.getEntityState();
			
			if (!controllEngine.canPlayerRotate(playerState) && controllEngine.player.isAlive()) {
				GLFW.glfwSetCursorPosCallback(minecraft.getWindow().getWindow(), controllEngine.blockRotationCallback);
				minecraft.mouseHandler.xpos = controllEngine.tracingMouseX;
				minecraft.mouseHandler.ypos = controllEngine.tracingMouseY;
			} else {
				controllEngine.tracingMouseX = minecraft.mouseHandler.xpos();
				controllEngine.tracingMouseY = minecraft.mouseHandler.ypos();
				
				GLFW.glfwSetCursorPosCallback(minecraft.getWindow().getWindow(), (handle, x, y) -> {
					minecraft.execute(() -> {
						minecraft.mouseHandler.onMove(handle, x, y);
					});
				});
			}
			
			if (!controllEngine.canPlayerMove(playerState)) {
				event.getInput().forwardImpulse = 0F;
				event.getInput().leftImpulse = 0F;
				event.getInput().up = false;
				event.getInput().down = false;
				event.getInput().left = false;
				event.getInput().right = false;
				event.getInput().jumping = false;
				event.getInput().shiftKeyDown = false;
				LocalPlayer clientPlayer = ((LocalPlayer)event.getPlayer());
				clientPlayer.setSprinting(false);
				clientPlayer.sprintTriggerTime = -1;
				controllEngine.setKeyBind(controllEngine.options.keySprint, false);
			}
			
			if (event.getPlayer().isAlive()) {
				controllEngine.playerpatch.getEventListener().triggerEvents(EventType.MOVEMENT_INPUT_EVENT, new MovementInputEvent(controllEngine.playerpatch, event.getInput()));
			}
		}
		
		@SubscribeEvent
		public static void preProcessKeyBindings(TickEvent.ClientTickEvent event) {
			if (event.phase == TickEvent.Phase.START) {
				if (controllEngine.minecraft.player != null) {
					controllEngine.tick();
				}
			}
		}
	}
}