package yesman.epicfight.client.events.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;

import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.util.InputMappings.Input;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.InputEvent.MouseInputEvent;
import net.minecraftforge.client.event.InputEvent.MouseScrollEvent;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.settings.KeyBindingMap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import yesman.epicfight.animation.types.EntityState;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.capabilites.player.ClientPlayerData;
import yesman.epicfight.client.input.ModKeys;
import yesman.epicfight.entity.eventlistener.MovementInputEvent;
import yesman.epicfight.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillContainer;

@OnlyIn(Dist.CLIENT)
public class ControllEngine {
	private Map<KeyBinding, BiConsumer<Integer, Integer>> keyFunctionMap;
	private GLFWCursorPosCallbackI blockRotationCallback = (handle, x, y) -> {Minecraft.getInstance().execute(()->{tracingMouseX = x; tracingMouseY = y;});};
	private ClientPlayerEntity player;
	private ClientPlayerData playerdata;
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
	public GameSettings gameSettings;
	
	public ControllEngine() {
		Events.controllEngine = this;
		this.gameSettings = Minecraft.getInstance().gameSettings;
		this.keyFunctionMap = new HashMap<KeyBinding, BiConsumer<Integer, Integer>>();
		this.keyFunctionMap.put(this.gameSettings.keyBindAttack, this::attackKeyPressed);
		this.keyFunctionMap.put(this.gameSettings.keyBindSwapHands, this::swapHandKeyPressed);
		this.keyFunctionMap.put(ModKeys.SWITCH_MODE, this::switchModeKeyPressed);
		this.keyFunctionMap.put(ModKeys.DODGE, this::sneakKeyPressed);
		this.keyFunctionMap.put(ModKeys.SPECIAL_SKILL, this::specialSkillKeyPressed);
		
		try {
			this.keyHash = (KeyBindingMap) ObfuscationReflectionHelper.findField(KeyBinding.class, "field_74514_b").get(null);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public void setPlayerData(ClientPlayerData playerdata) {
		this.mouseLeftPressCounter = 0;
		this.mouseLeftPressToggle = false;
		this.sneakPressCounter = 0;
		this.sneakPressToggle = false;
		this.lightPress = false;
		this.player = playerdata.getOriginalEntity();
		this.playerdata = playerdata;
	}
	
	public boolean canPlayerMove(EntityState playerState) {
		return !playerState.isMovementLocked() || this.player.isRidingHorse();
	}
	
	public boolean canPlayerRotate(EntityState playerState) {
		return !playerState.isCameraRotationLocked() || this.player.isRidingHorse();
	}
	
	private void attackKeyPressed(int key, int action) {
		if (action == 1) {
			if (ClientEngine.instance.isBattleMode()) {
				this.setKeyBind(this.gameSettings.keyBindAttack, false);
				while (this.gameSettings.keyBindAttack.isPressed()) {
				}

				if (this.player.getItemInUseCount() == 0) {
					if (!this.mouseLeftPressToggle) {
						this.mouseLeftPressToggle = true;
					}
				}
			}
		}
	}
	
	private void sneakKeyPressed(int key, int action) {
		if (action == 1) {
			if (key == this.gameSettings.keyBindSneak.getKey().getKeyCode()) {
				if (this.player.getRidingEntity() == null && ClientEngine.instance.isBattleMode()) {
					if (!this.sneakPressToggle) {
						this.sneakPressToggle = true;
					}
				}
			} else {
				SkillContainer skill = this.playerdata.getSkill(SkillCategory.DODGE);
				if (skill.canExecute(this.playerdata) && skill.getContaining().isExecutableState(this.playerdata)) {
					skill.sendExecuteRequest(this.playerdata);
				}
			}
		}
	}
	
	private void swapHandKeyPressed(int key, int action) {
		if (this.playerdata.getEntityState().isInaction() || (!this.playerdata.getHeldItemCapability(Hand.MAIN_HAND).canUsedInOffhand())) {
			while (this.gameSettings.keyBindSwapHands.isPressed()) {
			}
			this.setKeyBind(this.gameSettings.keyBindSwapHands, false);
		}
	}
	
	private void switchModeKeyPressed(int key, int action) {
		if (action == 1) {
			ClientEngine.instance.toggleActingMode();
		}
	}
	
	private void specialSkillKeyPressed(int key, int action) {
		if (action == 1) {
			if (key != 0) {
				if (!this.playerdata.getSkill(SkillCategory.WEAPON_SPECIAL_ATTACK).sendExecuteRequest(this.playerdata)) {
					if (!this.player.isSpectator()) {
						this.reserveKey(SkillCategory.WEAPON_SPECIAL_ATTACK);
					}
				}
			} else {
				if (this.gameSettings.keyBindAttack.equals(ModKeys.SPECIAL_SKILL)) {
					KeyBinding.onTick(this.gameSettings.keyBindAttack.getKey());
				}
			}
		}
	}
	
	public void tick() {
		if (this.playerdata == null) {
			return;
		}
		this.playerdata.updateEntityState();
		
		if (this.mouseLeftPressToggle) {
			if (!this.isKeyDown(this.gameSettings.keyBindAttack)) {
				this.lightPress = true;
				this.mouseLeftPressToggle = false;
				this.mouseLeftPressCounter = 0;
			} else {
				if (ModKeys.SPECIAL_SKILL.getKey().equals(this.gameSettings.keyBindAttack.getKey())) {
					if (this.mouseLeftPressCounter > EpicFightMod.CLIENT_INGAME_CONFIG.longPressCount.getValue()) {
						if (!this.playerdata.getSkill(SkillCategory.WEAPON_SPECIAL_ATTACK).sendExecuteRequest(this.playerdata)) {
							if (!this.player.isSpectator()) {
								this.reserveKey(SkillCategory.WEAPON_SPECIAL_ATTACK);
							}
						}
						this.mouseLeftPressToggle = false;
						this.mouseLeftPressCounter = 0;
					} else {
						this.setKeyBind(this.gameSettings.keyBindAttack, false);
						this.mouseLeftPressCounter++;
					}
				}
			}
		}
		
		if (this.lightPress) {
			SkillCategory slot = (!this.player.isOnGround() && !this.player.isInWater() && this.player.getMotion().y > -0.05D) ?
					SkillCategory.AIR_ATTACK : SkillCategory.BASIC_ATTACK;
			if (this.playerdata.getSkill(slot).sendExecuteRequest(this.playerdata)) {
				this.player.resetCooldown();
				this.lightPress = false;
				this.resetReservedKey();
			} else {
				if (!(this.player.isSpectator() || slot == SkillCategory.AIR_ATTACK)) {
					this.reserveKey(slot);
				}
			}
			this.lightPress = false;
			this.mouseLeftPressToggle = false;
			this.mouseLeftPressCounter = 0;
		}
		
		if (this.sneakPressToggle) {
			if (!this.isKeyDown(this.gameSettings.keyBindSneak)) {
				SkillContainer skill = this.playerdata.getSkill(SkillCategory.DODGE);
				if (!skill.sendExecuteRequest(this.playerdata)) {
					this.reserveKey(SkillCategory.DODGE);
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
				SkillContainer skill = this.playerdata.getSkill(this.reservedKey);
				this.reserveCounter--;
				if (skill.getContaining() != null && skill.sendExecuteRequest(this.playerdata)) {
					this.resetReservedKey();
				}
			} else {
				this.resetReservedKey();
			}
		}
		
		for (int i = 0; i < 9; ++i) {
			if (isKeyDown(this.gameSettings.keyBindsHotbar[i])) {
				if (this.playerdata.getEntityState().isInaction()) {
					this.gameSettings.keyBindsHotbar[i].isPressed();
				}
			}
		}
		
		if (Minecraft.getInstance().isGamePaused()) {
			Minecraft.getInstance().mouseHelper.registerCallbacks(Minecraft.getInstance().getMainWindow().getHandle());
		}
	}
	
	private void reserveKey(SkillCategory slot) {
		this.reservedKey = slot.getIndex();
		this.reserveCounter = 8;
	}
	
	private void resetReservedKey() {
		this.reservedKey = -1;
		this.reserveCounter = -1;
	}
	
	public boolean isKeyDown(KeyBinding key) {
		if(key.getKey().getType() == InputMappings.Type.KEYSYM) {
			return GLFW.glfwGetKey(Minecraft.getInstance().getMainWindow().getHandle(), key.getKey().getKeyCode()) > 0;
		} else if(key.getKey().getType() == InputMappings.Type.MOUSE) {
			return GLFW.glfwGetMouseButton(Minecraft.getInstance().getMainWindow().getHandle(), key.getKey().getKeyCode()) > 0;
		} else {
			return false;
		}
	}
	
	public void setKeyBind(KeyBinding key, boolean setter) {
		KeyBinding.setKeyBindState(key.getKey(), setter);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Mod.EventBusSubscriber(modid = EpicFightMod.MODID, value = Dist.CLIENT)
	public static class Events {
		static ControllEngine controllEngine;
		
		@SubscribeEvent
		public static void mouseEvent(MouseInputEvent event) {
			if (Minecraft.getInstance().player != null && Minecraft.getInstance().currentScreen == null) {
				Input input = InputMappings.Type.MOUSE.getOrMakeInput(event.getButton());
				for (KeyBinding keybinding : controllEngine.keyHash.lookupAll(input)) {
					if(controllEngine.keyFunctionMap.containsKey(keybinding)) {
						controllEngine.keyFunctionMap.get(keybinding).accept(event.getButton(), event.getAction());
					}
				}
			}
		}
		
		@SubscribeEvent
		public static void mouseScrollEvent(MouseScrollEvent event) {
			if (Minecraft.getInstance().player != null && controllEngine.playerdata != null && controllEngine.playerdata.getEntityState().isInaction()) {
				if(Minecraft.getInstance().currentScreen == null) {
					event.setCanceled(true);
				}
			}
		}
		
		@SubscribeEvent
		public static void keyboardEvent(KeyInputEvent event) {
			if (Minecraft.getInstance().player != null && Minecraft.getInstance().currentScreen == null) {
				Input input = InputMappings.Type.KEYSYM.getOrMakeInput(event.getKey());
				for (KeyBinding keybinding : controllEngine.keyHash.lookupAll(input)) {
					if(controllEngine.keyFunctionMap.containsKey(keybinding)) {
						controllEngine.keyFunctionMap.get(keybinding).accept(event.getKey(), event.getAction());
					}
				}
			}
		}
		
		@SubscribeEvent
		public static void moveInputEvent(InputUpdateEvent event) {
			if (controllEngine.playerdata == null) {
				return;
			}
			
			Minecraft minecraft = Minecraft.getInstance();
			EntityState playerState = controllEngine.playerdata.getEntityState();
			
			if (!controllEngine.canPlayerRotate(playerState) && controllEngine.player.isAlive()) {
				GLFW.glfwSetCursorPosCallback(minecraft.getMainWindow().getHandle(), controllEngine.blockRotationCallback);
				minecraft.mouseHelper.mouseX = controllEngine.tracingMouseX;
				minecraft.mouseHelper.mouseY = controllEngine.tracingMouseY;
			} else {
				controllEngine.tracingMouseX = minecraft.mouseHelper.getMouseX();
				controllEngine.tracingMouseY = minecraft.mouseHelper.getMouseY();
				
				GLFW.glfwSetCursorPosCallback(minecraft.getMainWindow().getHandle(), (handle, x, y) -> {
					minecraft.execute(() -> {
						minecraft.mouseHelper.cursorPosCallback(handle, x, y);
					});
				});
			}
			
			if (!controllEngine.canPlayerMove(playerState)) {
				event.getMovementInput().moveForward = 0F;
				event.getMovementInput().moveStrafe = 0F;
				event.getMovementInput().forwardKeyDown = false;
				event.getMovementInput().backKeyDown = false;
				event.getMovementInput().leftKeyDown = false;
				event.getMovementInput().rightKeyDown = false;
				event.getMovementInput().jump = false;
				event.getMovementInput().sneaking = false;
				ClientPlayerEntity clientPlayer = ((ClientPlayerEntity)event.getPlayer());
				clientPlayer.setSprinting(false);
				clientPlayer.sprintToggleTimer = -1;
				controllEngine.setKeyBind(controllEngine.gameSettings.keyBindSprint, false);
			}
			
			if (event.getPlayer().isAlive()) {
				controllEngine.playerdata.getEventListener().activateEvents(EventType.MOVEMENT_INPUT_EVENT, new MovementInputEvent(controllEngine.playerdata,
						event.getMovementInput()));
			}
		}
		
		@SubscribeEvent
		public static void preProcessKeyBindings(TickEvent.ClientTickEvent event) {
			if (event.phase == TickEvent.Phase.START) {
				if (Minecraft.getInstance().player != null) {
					controllEngine.tick();
				}
			}
		}
	}
}