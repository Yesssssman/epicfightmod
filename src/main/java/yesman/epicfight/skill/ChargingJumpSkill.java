package yesman.epicfight.skill;

import java.util.UUID;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.skill.SkillDataManager.SkillDataKey;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class ChargingJumpSkill extends Skill {
	private static final SkillDataKey<Integer> CHARGE_TICKS = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	private static final UUID EVENT_UUID = UUID.fromString("0bbe389a-1622-11ed-861d-0242ac120002");
	private static final int LEAST_REQUIRED_TICKS = 10;
	
	public static Skill.Builder<ChargingJumpSkill> createBuilder() {
		return (new Builder<ChargingJumpSkill>(new ResourceLocation(EpicFightMod.MODID, "charging_jump")))
					.setCategory(SkillCategories.CHARGING_JUMP)
					.setConsumption(1.0F)
					.setActivateType(ActivateType.ONE_SHOT)
					.setResource(Resource.STAMINA);
	}
	
	public ChargingJumpSkill(Builder<? extends Skill> builder) {
		super(builder);
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		container.getDataManager().registerData(CHARGE_TICKS);
		
		container.getExecuter().getEventListener().addEventListener(EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID, (event) -> {
			if (event.getMovementInput().jumping) {
				int chargeTicks = container.getDataManager().getDataValue(CHARGE_TICKS);

				if (event.getMovementInput().shiftKeyDown || chargeTicks > 0) {
					container.getDataManager().setData(CHARGE_TICKS, chargeTicks + 1);
					System.out.println("up charge tick " + (chargeTicks + 1));
					event.getMovementInput().jumping = false;
					event.getMovementInput().forwardImpulse = 0.0F;
				}
			} else {
				int chargeTicks = container.getDataManager().getDataValue(CHARGE_TICKS);

				if (chargeTicks > LEAST_REQUIRED_TICKS) {
					System.out.println("super jump !");
					double amount = Math.floor(chargeTicks * 0.1D);
					
					Entity entity = event.getPlayerPatch().getOriginal();
					
					entity.setDeltaMovement(entity.getDeltaMovement().add(0, amount, 0));				
				}
				
				container.getDataManager().setData(CHARGE_TICKS, 0);
			}
		});
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		container.getExecuter().getEventListener().removeListener(EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID);
	}
}