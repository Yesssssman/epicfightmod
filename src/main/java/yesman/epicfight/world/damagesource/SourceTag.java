package yesman.epicfight.world.damagesource;

import yesman.epicfight.api.utils.ExtendableEnum;
import yesman.epicfight.api.utils.ExtendableEnumManager;

public interface SourceTag extends ExtendableEnum {
	public static final ExtendableEnumManager<SourceTag> ENUM_MANAGER = new ExtendableEnumManager<> ("source_tag");
}