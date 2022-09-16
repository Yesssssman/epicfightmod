package yesman.epicfight.api.model;

import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class Model {
	protected final ResourceLocation location;
	protected Armature armature;
	
	public Model(ResourceLocation location) {
		this.location = new ResourceLocation(location.getNamespace(), "animmodels/" + location.getPath() + ".json");
	}
	
	public void loadArmatureData(IResourceManager resourceManager) {
		JsonModelLoader loader = new JsonModelLoader(resourceManager, this.location);
		this.armature = loader.getArmature();
	}
	
	public void loadArmatureData(Armature armature) {
		this.armature = armature;
	}
	
	public ResourceLocation getLocation() {
		return this.location;
	}
	
	public Armature getArmature() {
		return this.armature;
	}
}