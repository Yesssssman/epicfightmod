package yesman.epicfight.api.client.model;

import java.util.function.Supplier;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.math.OpenMatrix4f;

@OnlyIn(Dist.CLIENT)
public interface MeshPartDefinition {
	String partName();
	Supplier<OpenMatrix4f> getModelPartAnimationProvider();
}