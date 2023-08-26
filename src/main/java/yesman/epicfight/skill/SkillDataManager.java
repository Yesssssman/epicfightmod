package yesman.epicfight.skill;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.google.common.collect.Maps;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPAddOrRemoveSkillData;
import yesman.epicfight.network.server.SPModifySkillData;
import yesman.epicfight.skill.SkillDataManager.Data.BooleanData;
import yesman.epicfight.skill.SkillDataManager.Data.FloatData;
import yesman.epicfight.skill.SkillDataManager.Data.IntegerData;

public class SkillDataManager {
	private final Map<SkillDataKey<?>, Data> data = Maps.newHashMap();
	private final int slotIndex;
	private final SkillContainer container;
	
	public SkillDataManager(int slotIndex, SkillContainer container) {
		this.slotIndex = slotIndex;
		this.container = container;
	}
	
	public <T> void registerData(SkillDataKey<T> key) {
		this.data.put(key, key.valueType.create());
		
		if (key.shouldSyncAllClients() && !this.container.getExecuter().isLogicalClient()) {
			Player owner = this.container.getExecuter().getOriginal();
			Object initialValue =  key.valueType.get(this.data.get(key));
			
			EpicFightNetworkManager.sendToAllPlayerTrackingThisEntity(
					new SPAddOrRemoveSkillData(key, container.getSlot().universalOrdinal(), initialValue, SPAddOrRemoveSkillData.AddRemove.ADD, owner.getId()),
					owner);
		}
	}
	
	public <T> void removeData(SkillDataKey<T> key) {
		this.data.remove(key);
		
		if (key.shouldSyncAllClients() && !this.container.getExecuter().isLogicalClient()) {
			Player owner = this.container.getExecuter().getOriginal();
			
			EpicFightNetworkManager.sendToAllPlayerTrackingThisEntity(
					new SPAddOrRemoveSkillData(key, container.getSlot().universalOrdinal(), null, SPAddOrRemoveSkillData.AddRemove.REMOVE, owner.getId()),
					owner);
		}
	}
	
	public Set<SkillDataKey<?>> keySet() {
		return this.data.keySet();
	}
	
	/**
	 * Use setData() or setDataSync() that is type-safe
	 */
	@Deprecated
	public void setDataRawtype(SkillDataKey<?> key, Object data) {
		if (this.hasData(key)) {
			key.valueType.set(this.data.get(key), data);
		}
	}
	
	public <T> void setData(SkillDataKey<T> key, T data) {
		this.setDataRawtype(key, data);
	}
	
	public <T> void setDataF(SkillDataKey<T> key, Function<T, T> dataManipulator) {
		this.setDataRawtype(key, dataManipulator.apply(this.getDataValue(key)));
	}
	
	public <T> void setDataSync(SkillDataKey<T> key, T data, ServerPlayer player) {
		this.setData(key, data);
		SPModifySkillData msg2 = new SPModifySkillData(key, this.slotIndex, data, player.getId());
		EpicFightNetworkManager.sendToPlayer(msg2, player);
		
		if (key.shouldSyncAllClients()) {
			EpicFightNetworkManager.sendToAllPlayerTrackingThisEntity(msg2, player);
		}
	}
	
	public <T> void setDataSyncF(SkillDataKey<T> key, Function<T, T> dataManipulator, ServerPlayer player) {
		this.setDataF(key, dataManipulator);
		SPModifySkillData msg2 = new SPModifySkillData(key, this.slotIndex, this.getDataValue(key), player.getId());
		EpicFightNetworkManager.sendToPlayer(msg2, player);
		
		if (key.shouldSyncAllClients()) {
			EpicFightNetworkManager.sendToAllPlayerTrackingThisEntity(msg2, player);
		}
	}
	
	public <T> T getDataValue(SkillDataKey<T> key) {
		if (this.hasData(key)) {
			return key.valueType.get(this.data.get(key));
		}
		
		return null;
	}
	
	public boolean hasData(SkillDataKey<?> key) {
		return this.data.containsKey(key);
	}
	
	public void reset() {
		this.data.clear();
	}
	
	static abstract class Data {
		static class IntegerData extends Data {
			int data;
		}
		
		static class BooleanData extends Data {
			boolean data;
		}
		
		static class FloatData extends Data {
			float data;
		}
	}
	
	public static abstract class ValueType<T> {
		public static final IntegerType INTEGER = new IntegerType();
		public static final FloatType FLOAT = new FloatType();
		public static final BooleanType BOOLEAN = new BooleanType();
		
		public abstract Data create();
		public abstract void set(Data data, Object value);
		public abstract T get(Data data);
		public abstract void writeToBuffer(FriendlyByteBuf buf, Object data);
		public abstract T readFromBuffer(FriendlyByteBuf buf);
		
		private static class IntegerType extends ValueType<Integer> {
			@Override
			public IntegerData create() {
				return new IntegerData();
			}
			
			@Override
			public void set(Data data, Object value) {
				((IntegerData)data).data = (int)value;
			}
			
			@Override
			public Integer get(Data data) {
				return data != null ? ((IntegerData)data).data : 0;
			}
			
			@Override
			public void writeToBuffer(FriendlyByteBuf buf, Object data) {
				buf.writeInt((int)data);
			}

			@Override
			public Integer readFromBuffer(FriendlyByteBuf buf) {
				return buf.readInt();
			}
		}
		
		private static class BooleanType extends ValueType<Boolean> {
			@Override
			public BooleanData create() {
				return new BooleanData();
			}
			
			@Override
			public void set(Data data, Object value) {
				((BooleanData)data).data = (boolean)value;
			}
			
			@Override
			public Boolean get(Data data) {
				return data != null ? ((BooleanData)data).data : false;
			}
			
			@Override
			public void writeToBuffer(FriendlyByteBuf buf, Object data) {
				buf.writeBoolean((boolean)data);
			}

			@Override
			public Boolean readFromBuffer(FriendlyByteBuf buf) {
				return buf.readBoolean();
			}
		}
		
		private static class FloatType extends ValueType<Float> {
			@Override
			public FloatData create() {
				return new FloatData();
			}
			
			@Override
			public void set(Data data, Object value) {
				((FloatData)data).data = (float)value;
			}
			
			@Override
			public Float get(Data data) {
				return data != null ? ((FloatData)data).data : 0.0F;
			}
			
			@Override
			public void writeToBuffer(FriendlyByteBuf buf, Object data) {
				buf.writeFloat((float)data);
			}

			@Override
			public Float readFromBuffer(FriendlyByteBuf buf) {
				return buf.readFloat();
			}
		}
	}
	
	public static class SkillDataKey<T> {
		private static int NEXT_ID;
		private static final Map<Integer, SkillDataKey<?>> KEYS = Maps.<Integer, SkillDataKey<?>>newHashMap();
		
		public static <V> SkillDataKey<V> createDataKey(ValueType<V> valueType) {
			return createDataKey(valueType, false);
		}
		
		public static <V> SkillDataKey<V> createDataKey(ValueType<V> valueType, boolean syncAllClients) {
			int id = NEXT_ID++;
			SkillDataKey<V> key = new SkillDataKey<>(valueType, id, syncAllClients);
			KEYS.put(id, key);
			
			return key;
		}
		
		public static SkillDataKey<?> findById(int id) {
			return KEYS.get(id);
		}
		
		private final ValueType<T> valueType;
		private final int id;
		private final boolean syncAllClients;
		
		private SkillDataKey(ValueType<T> valueType, int id, boolean syncAllClients) {
			this.valueType = valueType;
			this.id = id;
			this.syncAllClients = syncAllClients;
		}
		
		public int getId() {
			return this.id;
		}
		
		public ValueType<T> getValueType() {
			return this.valueType;
		}
		
		public boolean shouldSyncAllClients() {
			return this.syncAllClients;
		}
	}
}