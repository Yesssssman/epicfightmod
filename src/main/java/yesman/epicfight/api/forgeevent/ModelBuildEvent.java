package yesman.epicfight.api.forgeevent;

import java.util.Map;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.client.model.AnimatedMesh.AnimatedModelPart;
import yesman.epicfight.api.client.model.BlenderAnimatedVertexBuilder;
import yesman.epicfight.api.client.model.BlenderVertexBuilder;
import yesman.epicfight.api.client.model.Mesh;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.client.model.Meshes.MeshContructor;
import yesman.epicfight.api.client.model.RawMesh;
import yesman.epicfight.api.client.model.RawMesh.RawModelPart;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.Armatures.ArmatureContructor;

public abstract class ModelBuildEvent<T> extends Event implements IModBusEvent {
	protected final ResourceManager resourceManager;
	
	public ModelBuildEvent(ResourceManager resourceManager, Map<ResourceLocation, T> registerMap) {
		this.resourceManager = resourceManager;
	}
	
	public static class ArmatureBuild extends ModelBuildEvent<Armature> {
		public ArmatureBuild(ResourceManager resourceManager, Map<ResourceLocation, Armature> registerMap) {
			super(resourceManager, registerMap);
		}
		
		public <T extends Armature> T get(String modid, String path, ArmatureContructor<T> constructor) {
			return Armatures.getOrCreateArmature(this.resourceManager, new ResourceLocation(modid, path), constructor);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class MeshBuild extends ModelBuildEvent<Mesh<?, ?>> {
		public MeshBuild(ResourceManager resourceManager, Map<ResourceLocation, Mesh<?, ?>> registerMap) {
			super(resourceManager, registerMap);
		}
		
		public <M extends RawMesh> M getRaw(String modid, String path, MeshContructor<RawModelPart, BlenderVertexBuilder, M> constructor) {
			return Meshes.getOrCreateRawMesh(this.resourceManager, new ResourceLocation(modid, path), constructor);
		}
		
		public <M extends AnimatedMesh> M getAnimated(String modid, String path, MeshContructor<AnimatedModelPart, BlenderAnimatedVertexBuilder, M> constructor) {
			return Meshes.getOrCreateAnimatedMesh(this.resourceManager, new ResourceLocation(modid, path), constructor);
		}
	}
}