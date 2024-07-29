package yesman.epicfight.api.client.model;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * If you have any variables referencing AnimatedMesh, Use mesh provider to avoid drawing destroyed mesh.
 * See {@link AnimatedMesh#destroy()}
 * 
 * If you directly reference Mesh instance, you don't have to use mesh provider (e.g. Meshes.BIPED})
 **/
@OnlyIn(Dist.CLIENT)
@FunctionalInterface
public interface MeshProvider<T extends Mesh<?, ?>> {
	public T get();
}