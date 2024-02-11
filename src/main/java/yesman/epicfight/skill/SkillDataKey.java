package yesman.epicfight.skill;

import java.util.function.BiConsumer;
import java.util.function.Function;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.IdMapper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry.CreateCallback;
import net.minecraftforge.registries.IForgeRegistryInternal;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryManager;
import yesman.epicfight.main.EpicFightMod;

public class SkillDataKey<T> {
	public static class Registry {
		private static final ResourceLocation DATA_KEY_TO_ID = new ResourceLocation(EpicFightMod.MODID, "datakeytoid");
		
		
		private static IdMapper<SkillDataKey<?>> SKILL_DATA_KEY_ID;
		private static IForgeRegistry<SkillDataKey<?>> REGISTRY;
		
		@SuppressWarnings("unchecked")
		public static void createRegistry(final NewRegistryEvent event) {
			RegistryBuilder<SkillDataKey<?>> registryBuilder = RegistryBuilder.of(new ResourceLocation(EpicFightMod.MODID, "skill_data_key"));
			registryBuilder.onCreate(new CreateCallback<SkillDataKey<?>> () {
				@Override
				public void onCreate(IForgeRegistryInternal<SkillDataKey<?>> owner, RegistryManager stage) {
					final IdMapper<BlockState> idMap = new IdMapper<>()
		            {
		                @Override
		                public int getId(BlockState key)
		                {
		                    return this.tToId.containsKey(key) ? this.tToId.getInt(key) : -1;
		                }
		            };
		            
					owner.setSlaveMap(DATA_KEY_TO_ID, idMap);
				}
			});
			
			REGISTRY = event.create(registryBuilder).get();
			SKILL_DATA_KEY_ID = REGISTRY.getSlaveMap(DATA_KEY_TO_ID, IdMapper.class);
		}
	}
	
	public static SkillDataKey<Integer> createIntKey(Class<?> skillClass, int defaultValue, boolean syncronizeTrackingPlayers) {
		return createSkillDataKey(skillClass, (buffer, val) -> buffer.writeInt(val), (buffer) -> buffer.readInt(), defaultValue, syncronizeTrackingPlayers);
	}
	
	public static SkillDataKey<Float> createFloatDataKey(Class<?> skillClass, float defaultValue, boolean syncronizeTrackingPlayers) {
		return createSkillDataKey(skillClass, (buffer, val) -> buffer.writeFloat(val), (buffer) -> buffer.readFloat(), defaultValue, syncronizeTrackingPlayers);
	}
	
	public static SkillDataKey<Boolean> createBooleanKey(Class<?> skillClass, boolean defaultValue, boolean syncronizeTrackingPlayers) {
		return createSkillDataKey(skillClass, (buffer, val) -> buffer.writeBoolean(val), (buffer) -> buffer.readBoolean(), defaultValue, syncronizeTrackingPlayers);
	}
	
	public static <T> SkillDataKey<T> createSkillDataKey(Class<?> skillClass, BiConsumer<ByteBuf, T> encoder, Function<ByteBuf, T> decoder, T defaultValue, boolean syncronizeTrackingPlayers) {
		return new SkillDataKey<T>(encoder, decoder, defaultValue, syncronizeTrackingPlayers);
	}
	
	@SuppressWarnings("unchecked")
	public static SkillDataKey<Object> byId(int id) {
		return (SkillDataKey<Object>)Registry.SKILL_DATA_KEY_ID.byId(id);
	}
	
	private final BiConsumer<ByteBuf, T> encoder;
	private final Function<ByteBuf, T> decoder;
	private final T defaultValue;
	private final boolean syncronizeTrackingPlayers;
	
	public SkillDataKey(BiConsumer<ByteBuf, T> encoder, Function<ByteBuf, T> decoder, T defaultValue, boolean syncronizeTrackingPlayers) {
		this.encoder = encoder;
		this.decoder = decoder;
		this.defaultValue = defaultValue;
		this.syncronizeTrackingPlayers = syncronizeTrackingPlayers;
	}
	
	public T readFromBuffer(ByteBuf buffer) {
		return this.decoder.apply(buffer);
	}
	
	public void writeToBuffer(ByteBuf buffer, T value) {
		this.encoder.accept(buffer, value);
	}
	
	public T defaultValue() {
		return this.defaultValue;
	}
	
	public int getId() {
		return Registry.SKILL_DATA_KEY_ID.getId(this);
	}
	
	public boolean syncronizeTrackingPlayers() {
		return this.syncronizeTrackingPlayers;
	}
}
