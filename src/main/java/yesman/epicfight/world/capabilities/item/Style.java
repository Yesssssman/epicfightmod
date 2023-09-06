package yesman.epicfight.world.capabilities.item;

import yesman.epicfight.api.utils.ExtendableEnum;
import yesman.epicfight.api.utils.ExtendableEnumManager;

public interface Style extends ExtendableEnum {
	public static final ExtendableEnumManager<Style> ENUM_MANAGER = new ExtendableEnumManager<> ("style");
	
	public boolean canUseOffhand();
}