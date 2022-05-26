package yesman.epicfight.api.model;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class Model {
	protected final ResourceLocation location;
	protected Armature armature;
	
	public Model(ResourceLocation location) {
		this.location = new ResourceLocation(location.getNamespace(), "animmodels/" + location.getPath() + ".json");
	}
	
	public void loadArmatureData(ResourceManager resourceManager) {
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