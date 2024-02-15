package yesman.epicfight.skill;

import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.IdMapper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryInternal;
import net.minecraftforge.registries.RegistryManager;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.main.EpicFightMod;

public class SkillDataKey<T> {
	private static final HashMultimap<Class<?>, SkillDataKey<?>> SKILL_DATA_KEYS = HashMultimap.create();
	private static final ResourceLocation CLASS_TO_DATA_KEYS = new ResourceLocation(EpicFightMod.MODID, "classtodatakeys");
	private static final ResourceLocation DATA_KEY_TO_ID = new ResourceLocation(EpicFightMod.MODID, "datakeytoid");
	
	private static class SkillDataKeyCallbacks implements IForgeRegistry.BakeCallback<SkillDataKey<?>>, IForgeRegistry.ClearCallback<SkillDataKey<?>>, IForgeRegistry.CreateCallback<SkillDataKey<?>> {
		static final SkillDataKeyCallbacks INSTANCE = new SkillDataKeyCallbacks();
		
		@Override
		@SuppressWarnings("unchecked")
        public void onBake(IForgeRegistryInternal<SkillDataKey<?>> owner, RegistryManager stage) {
            IdMapper<SkillDataKey<?>> skillDataKeyMap = owner.getSlaveMap(DATA_KEY_TO_ID, IdMapper.class);
            
			for (SkillDataKey<?> block : owner) {
				skillDataKeyMap.add(block);
			}
            
			Map<Class<?>, Set<SkillDataKey<?>>> skillDataKeys = owner.getSlaveMap(CLASS_TO_DATA_KEYS, Map.class);
			
			for (Class<?> key : SKILL_DATA_KEYS.keySet()) {
				if (SKILL_DATA_KEYS.containsKey(key)) {
					Set<SkillDataKey<?>> dataKeySet = Sets.newHashSet();
					dataKeySet.addAll(SKILL_DATA_KEYS.get(key));
					skillDataKeys.put(key, dataKeySet);
				}
				
				Class<?> superKey = key.getSuperclass();
				
				while (superKey != null) {
					if (SKILL_DATA_KEYS.containsKey(superKey)) {
						Set<SkillDataKey<?>> dataKeySet = skillDataKeys.get(key);
						dataKeySet.addAll(SKILL_DATA_KEYS.get(superKey));
					}
					
					superKey = superKey.getSuperclass();
				}
			}
			
			SKILL_DATA_KEYS.clear();
        }
		
		@Override
		public void onClear(IForgeRegistryInternal<SkillDataKey<?>> owner, RegistryManager stage) {
			//owner.getSlaveMap(CLASS_TO_DATA_KEYS, Map.class).clear();
		}
		
		@Override
		public void onCreate(IForgeRegistryInternal<SkillDataKey<?>> owner, RegistryManager stage) {
			owner.setSlaveMap(CLASS_TO_DATA_KEYS, Maps.newHashMap());
			owner.setSlaveMap(DATA_KEY_TO_ID, new IdMapper<SkillDataKey<?>> (owner.getKeys().size()));
		}
	}
	
	public static SkillDataKeyCallbacks getCallBack() {
		return SkillDataKeyCallbacks.INSTANCE;
	}
	
	public static SkillDataKey<Integer> createIntKey(int defaultValue, boolean syncronizeTrackingPlayers, Class<?>... skillClass) {
		return createSkillDataKey((buffer, val) -> buffer.writeInt(val), (buffer) -> buffer.readInt(), defaultValue, syncronizeTrackingPlayers, skillClass);
	}
	
	public static SkillDataKey<Float> createFloatKey(float defaultValue, boolean syncronizeTrackingPlayers, Class<?>... skillClass) {
		return createSkillDataKey((buffer, val) -> buffer.writeFloat(val), (buffer) -> buffer.readFloat(), defaultValue, syncronizeTrackingPlayers, skillClass);
	}
	
	public static SkillDataKey<Double> createDoubleKey(double defaultValue, boolean syncronizeTrackingPlayers, Class<?>... skillClass) {
		return createSkillDataKey((buffer, val) -> buffer.writeDouble(val), (buffer) -> buffer.readDouble(), defaultValue, syncronizeTrackingPlayers, skillClass);
	}
	
	public static SkillDataKey<Boolean> createBooleanKey(boolean defaultValue, boolean syncronizeTrackingPlayers, Class<?>... skillClass) {
		return createSkillDataKey((buffer, val) -> buffer.writeBoolean(val), (buffer) -> buffer.readBoolean(), defaultValue, syncronizeTrackingPlayers, skillClass);
	}
	
	public static SkillDataKey<Vec3f> createVector3fKey(Vec3f defaultValue, boolean syncronizeTrackingPlayers, Class<?>... skillClass) {
		return createSkillDataKey((buffer, val) -> {
				buffer.writeFloat(val.x);
				buffer.writeFloat(val.y);
				buffer.writeFloat(val.z);
			}, (buffer) -> new Vec3f(buffer.readFloat(), buffer.readFloat(), buffer.readFloat()), defaultValue, syncronizeTrackingPlayers, skillClass);
	}
	
	public static SkillDataKey<Vec3> createVector3dKey(Vec3 defaultValue, boolean syncronizeTrackingPlayers, Class<?>... skillClass) {
		return createSkillDataKey((buffer, val) -> {
				buffer.writeDouble(val.x);
				buffer.writeDouble(val.y);
				buffer.writeDouble(val.z);
			}, (buffer) -> new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()), defaultValue, syncronizeTrackingPlayers, skillClass);
	}
	
	public static <T> SkillDataKey<T> createSkillDataKey(BiConsumer<ByteBuf, T> encoder, Function<ByteBuf, T> decoder, T defaultValue, boolean syncronizeTrackingPlayers, Class<?>... skillClass) {
		SkillDataKey<T> key = new SkillDataKey<T>(encoder, decoder, defaultValue, syncronizeTrackingPlayers);
		
		for (Class<?> cls : skillClass) {
			SKILL_DATA_KEYS.put(cls, key);
		}
		
		return key;
	}
	
	@SuppressWarnings("unchecked")
	public static IdMapper<SkillDataKey<?>> getIdMap() {
		return SkillDataKeys.REGISTRY.get().getSlaveMap(DATA_KEY_TO_ID, IdMapper.class);
	}
	
	@SuppressWarnings("unchecked")
	public static Map<Class<?>, Set<SkillDataKey<?>>> getSkillDataKeyMap() {
		return SkillDataKeys.REGISTRY.get().getSlaveMap(CLASS_TO_DATA_KEYS, Map.class);
	}
	
	@SuppressWarnings("unchecked")
	public static SkillDataKey<Object> byId(int id) {
		return (SkillDataKey<Object>)getIdMap().byId(id);
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
		return getIdMap().getId(this);
	}
	
	public boolean syncronizeTrackingPlayers() {
		return this.syncronizeTrackingPlayers;
	}
}
