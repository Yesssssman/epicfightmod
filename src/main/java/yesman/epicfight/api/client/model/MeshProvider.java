package yesman.epicfight.api.client.model;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@FunctionalInterface
public interface MeshProvider<T extends Mesh<?, ?>> {
	public T get();
}