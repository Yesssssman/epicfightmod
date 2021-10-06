package yesman.epicfight.capabilities.entity;

import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;

public class DataKeys {
	public static final DataParameter<Float> STUN_SHIELD = new DataParameter<Float> (252, DataSerializers.FLOAT);
	public static final DataParameter<Float> STAMINA = new DataParameter<Float> (253, DataSerializers.FLOAT);
}