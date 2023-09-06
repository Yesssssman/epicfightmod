package yesman.epicfight.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EpicFightDataSerializers {

	public static DeferredRegister<EntityDataSerializer<?>> VEC = DeferredRegister.create(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, "epicfight");
	public static final RegistryObject<EntityDataSerializer<Vec3>> VEC3 = VEC.register("vector_3_double", () -> {
		return new EntityDataSerializer<Vec3>() {
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
	});
}