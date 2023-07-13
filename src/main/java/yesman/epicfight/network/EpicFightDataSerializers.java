package yesman.epicfight.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import yesman.epicfight.main.EpicFightMod;

public class EpicFightDataSerializers {
	public static final EntityDataSerializer<Vec3> VEC3 = new EntityDataSerializer<Vec3>() {
		public void write(FriendlyByteBuf buffer, Vec3 vec3) {
			buffer.writeDouble(vec3.x);
			buffer.writeDouble(vec3.y);
			buffer.writeDouble(vec3.z);
		}
		
		public Vec3 read(FriendlyByteBuf buffer) {
			return new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
		}
		
		public Vec3 copy(Vec3 vec3) {
			return vec3;
		}
	};
	
	public static void register(RegisterEvent event) {
		event.register(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS,
				new ResourceLocation(EpicFightMod.MODID, "vector_3_double"), () -> VEC3);
	}
}