package yesman.epicfight.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.DataSerializerEntry;
import yesman.epicfight.main.EpicFightMod;

public class EpicFightDataSerializers {
	public static final IDataSerializer<Vector3d> VEC3 = new IDataSerializer<Vector3d>() {
		public void write(PacketBuffer buffer, Vector3d vec3) {
			buffer.writeDouble(vec3.x);
			buffer.writeDouble(vec3.y);
			buffer.writeDouble(vec3.z);
		}
		
		public Vector3d read(PacketBuffer buffer) {
			return new Vector3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
		}
		
		public Vector3d copy(Vector3d vec3) {
			return vec3;
		}
	};
	
	public static void register(RegistryEvent.Register<DataSerializerEntry> event) {
		event.getRegistry().registerAll(
				new DataSerializerEntry(VEC3).setRegistryName(new ResourceLocation(EpicFightMod.MODID, "vector_3_double"))
		);
	}
}