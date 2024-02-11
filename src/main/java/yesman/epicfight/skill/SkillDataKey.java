package yesman.epicfight.skill;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries.Keys;
import yesman.epicfight.main.EpicFightMod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryInternal;
import net.minecraftforge.registries.RegistryManager;

public class SkillDataKey<T> {
	private static class SkillDataKeyCallbacks implements IForgeRegistry.AddCallback<SkillDataKey<?>>, IForgeRegistry.ClearCallback<SkillDataKey<?>>, IForgeRegistry.CreateCallback<SkillDataKey<?>> {
		private static final ResourceLocation CLASS_TO_DATA_KEYS = new ResourceLocation(EpicFightMod.MODID, "classtodatakeys");
		static final SkillDataKeyCallbacks INSTANCE = new SkillDataKeyCallbacks();
		
		@Override
		public void onAdd(IForgeRegistryInternal<SkillDataKey<?>> owner, RegistryManager stage, int id, ResourceKey<SkillDataKey<?>> key, SkillDataKey<?> item, @Nullable SkillDataKey<?> oldItem) {
			
		}
		
		@Override
		public void onClear(IForgeRegistryInternal<SkillDataKey<?>> owner, RegistryManager stage) {
			owner.getSlaveMap(CLASS_TO_DATA_KEYS, Map.class).clear();
		}
		
		@Override
		public void onCreate(IForgeRegistryInternal<SkillDataKey<?>> owner, RegistryManager stage) {
			Map<?, ?> map = stage.getRegistry(Keys.BLOCKS).getSlaveMap(CLASS_TO_DATA_KEYS, Map.class);
			owner.setSlaveMap(CLASS_TO_DATA_KEYS, map);
		}
	}
	
	public static SkillDataKeyCallbacks getCallBack() {
		return SkillDataKeyCallbacks.INSTANCE;
	}
	
	/**
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
	}**/
	
	public static SkillDataKey<Integer> createIntKey(int defaultValue, boolean syncronizeTrackingPlayers, Class<?>... skillClass) {
		return createSkillDataKey((buffer, val) -> buffer.writeInt(val), (buffer) -> buffer.readInt(), defaultValue, syncronizeTrackingPlayers, skillClass);
	}
	
	public static SkillDataKey<Float> createFloatDataKey(float defaultValue, boolean syncronizeTrackingPlayers, Class<?>... skillClass) {
		return createSkillDataKey((buffer, val) -> buffer.writeFloat(val), (buffer) -> buffer.readFloat(), defaultValue, syncronizeTrackingPlayers, skillClass);
	}
	
	public static SkillDataKey<Boolean> createBooleanKey(boolean defaultValue, boolean syncronizeTrackingPlayers, Class<?>... skillClass) {
		return createSkillDataKey((buffer, val) -> buffer.writeBoolean(val), (buffer) -> buffer.readBoolean(), defaultValue, syncronizeTrackingPlayers, skillClass);
	}
	
	public static <T> SkillDataKey<T> createSkillDataKey(BiConsumer<ByteBuf, T> encoder, Function<ByteBuf, T> decoder, T defaultValue, boolean syncronizeTrackingPlayers, Class<?>... skillClass) {
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
