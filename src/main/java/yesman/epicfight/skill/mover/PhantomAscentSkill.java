package yesman.epicfight.skill.mover;

import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillDataManager.SkillDataKey;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;



public class PhantomAscentSkill extends Skill {

	private static final UUID EVENT_UUID = UUID.fromString("051a9bb2-7541-11ee-b962-0242ac120002");
	private static final SkillDataKey<Boolean> LAST_JUMP = SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
	private static final SkillDataKey<Integer> JUMP_COUNT = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	public static final int PARRY_WINDOW = 8; 
	private int jumpAmmount;
	
	public PhantomAscentSkill(Builder<? extends Skill> builder) {
		super(builder);
	}
	
	@Override
	public void setParams(CompoundTag parameters) {
		super.setParams(parameters);
		this.jumpAmmount = parameters.getInt("jump_ammount");
		//System.out.println(this.consumption + "forge sucks");
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		super.onInitiate(container);
		
		PlayerEventListener listener = container.getExecuter().getEventListener();
		
		container.getDataManager().registerData(LAST_JUMP);
		container.getDataManager().registerData(JUMP_COUNT);
		
		listener.addEventListener(EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID, (event) -> {
			if (event.getMovementInput().jumping) {
				
				if (container.getStack() < this.consumption) {
					return;
				}
				
				boolean lastActive = container.getDataManager().getDataValue(LAST_JUMP);
				
				if (lastActive) {
					int jumpData = container.getDataManager().getDataValue(JUMP_COUNT);
					
					if (jumpData < jumpAmmount) {
						container.setResource(0.0F);
						container.getDataManager().setDataF(JUMP_COUNT, (v) -> v + 1);
						container.getExecuter().getOriginal().setDeltaMovement(0, 1, 0);
					};
				} else {
					container.getDataManager().setData(LAST_JUMP, true);
					container.setResource(0.0F);
					container.getDataManager().setData(JUMP_COUNT, 0);
				}
			}
		});
		
		listener.addEventListener(EventType.FALL_EVENT, EVENT_UUID, (event) -> {
			container.getDataManager().setData(LAST_JUMP, false);
			container.getDataManager().setData(JUMP_COUNT, 0);
		});
		
	}
}
