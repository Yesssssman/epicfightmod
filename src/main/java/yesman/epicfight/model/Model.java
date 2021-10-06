package yesman.epicfight.model;

import java.io.IOException;

import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import yesman.epicfight.collada.ColladaModelLoader;

public class Model {
	protected Armature armature;
	protected ResourceLocation location;

	public Model(ResourceLocation location) {
		this.location = location;
	}

	public void loadArmatureData(IResourceManager resourceManager) {
		try {
			this.armature = ColladaModelLoader.getArmature(resourceManager, this.location);
		} catch (IOException e) {
			System.err.println(location.getNamespace() + " failed to load!");
		}
	}

	public void loadArmatureData(Armature armature) {
		this.armature = armature;
	}

	public Armature getArmature() {
		return armature;
	}
}