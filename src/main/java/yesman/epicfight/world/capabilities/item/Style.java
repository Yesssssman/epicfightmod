package yesman.epicfight.world.capabilities.item;

import yesman.epicfight.api.utils.game.ExtendableEnumManager;
import yesman.epicfight.api.utils.game.ExtendableEnum;

public interface Style extends ExtendableEnum {
	public static final ExtendableEnumManager<Style> ENUM_MANAGER = new ExtendableEnumManager<> ();
	
	public boolean canUseOffhand();
}