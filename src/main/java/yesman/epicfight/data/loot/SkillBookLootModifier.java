package yesman.epicfight.data.loot;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.fml.ModLoader;
import yesman.epicfight.api.forgeevent.SkillLootTableRegistryEvent;
import yesman.epicfight.config.ConfigManager;
import yesman.epicfight.data.loot.function.SetSkillFunction;
import yesman.epicfight.world.item.EpicFightItems;

public class SkillBookLootModifier extends LootModifier {
	private static final Map<EntityType<?>, LootTable> SKILL_LOOT_TABLE = Maps.newHashMap();
	
	public static LootTable getLootTableFor(EntityType<?> entityType) {
		return SKILL_LOOT_TABLE.get(entityType);
	}
	
	/**
	 * Skill List
	 * Passive
	 * epicfight:berserker
	 * epicfight:stamina_pillager
	 * epicfight:swordmaster
	 * epicfight:technician
	 * epicfight:hypervitality
	 * epicfight:forbidden_strength
	 * epicfight:death_harvest
	 * epicfight:endurance
	 * epicfight:emergency_escape
	 * 
	 * Guard
	 * epicfight:guard
	 * epicfight:impact_guard
	 * epicfight:parrying
	 * 
	 * Dodge
	 * epicfight:roll
	 * epicfight:step
	 * 
	 * Mover
	 * epicfight:demolishing_leap
	 */
	public static void createSkillLootTable(Set<ResourceLocation> skillNames) {
		Map<EntityType<?>, LootTable.Builder> builders = Maps.newHashMap();
		SkillLootTableRegistryEvent lootTableRegistryEvent = new SkillLootTableRegistryEvent(builders);
		int modifier = ConfigManager.SKILL_BOOK_MOB_DROP_CHANCE_MODIFIER.get();
		int dropChance = 100 + modifier;
		int antiDropChance = 100 - modifier;
		float dropChanceModifier = (antiDropChance == 0) ? Float.MAX_VALUE : dropChance / (float)antiDropChance;
		
		lootTableRegistryEvent.add(EntityType.ZOMBIE, LootTable.lootTable().withPool(
			LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
			.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
					1.0F, "epicfight:berserker",
					1.0F, "epicfight:stamina_pillager",
					1.0F, "epicfight:roll",
					1.0F, "epicfight:step",
					1.0F, "epicfight:guard",
					0.5F, "epicfight:endurance"
			)))
    	)).add(EntityType.HUSK, LootTable.lootTable().withPool(
			LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
			.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
					1.0F, "epicfight:berserker",
					1.0F, "epicfight:stamina_pillager",
					1.0F, "epicfight:roll",
					1.0F, "epicfight:step",
					1.0F, "epicfight:guard",
					0.5F, "epicfight:endurance"
			)))
    	)).add(EntityType.DROWNED, LootTable.lootTable().withPool(
			LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
			.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
					1.0F, "epicfight:berserker",
					1.0F, "epicfight:stamina_pillager",
					1.0F, "epicfight:roll",
					1.0F, "epicfight:step",
					1.0F, "epicfight:guard",
					0.5F, "epicfight:endurance"
			)))
    	)).add(EntityType.SKELETON, LootTable.lootTable().withPool(
			LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
			.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
					1.0F, "epicfight:swordmaster",
					1.0F, "epicfight:technician",
					1.0F, "epicfight:roll",
					1.0F, "epicfight:step",
					1.0F, "epicfight:guard",
					0.5F, "epicfight:emergency_escape"
			)))
    	)).add(EntityType.STRAY, LootTable.lootTable().withPool(
			LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
			.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
					1.0F, "epicfight:swordmaster",
					1.0F, "epicfight:technician",
					1.0F, "epicfight:roll",
					1.0F, "epicfight:step",
					1.0F, "epicfight:guard",
					0.5F, "epicfight:emergency_escape"
			)))
    	)).add(EntityType.SPIDER, LootTable.lootTable().withPool(
			LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
			.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
					"epicfight:roll",
					"epicfight:step",
					"epicfight:guard"
			)))
    	)).add(EntityType.CAVE_SPIDER, LootTable.lootTable().withPool(
			LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
			.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
					"epicfight:roll",
					"epicfight:step",
					"epicfight:guard"
			)))
    	)).add(EntityType.CREEPER, LootTable.lootTable().withPool(
			LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
			.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
					"epicfight:hypervitality",
					"epicfight:impact_guard"
			)))
    	)).add(EntityType.ENDERMAN, LootTable.lootTable().withPool(
			LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
			.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
					"epicfight:hypervitality",
					"epicfight:forbidden_strength",
					"epicfight:endurance",
					"epicfight:emergency_escape",
					"epicfight:parrying",
					"epicfight:impact_guard"
			)))
    	)).add(EntityType.VINDICATOR, LootTable.lootTable().withPool(
			LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
			.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
					"epicfight:hypervitality",
					"epicfight:berserker",
					"epicfight:guard",
					"epicfight:step",
					"epicfight:roll"
			)))
    	)).add(EntityType.PILLAGER, LootTable.lootTable().withPool(
			LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
			.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
					"epicfight:hypervitality",
					"epicfight:stamina_pillager",
					"epicfight:guard",
					"epicfight:step",
					"epicfight:roll"
			)))
    	)).add(EntityType.WITCH, LootTable.lootTable().withPool(
			LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
			.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
					"epicfight:forbidden_strength",
					"epicfight:berserker"
			)))
    	)).add(EntityType.EVOKER, LootTable.lootTable().withPool(
			LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
			.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
					"epicfight:parrying",
					"epicfight:impact_guard"
			)))).withPool(
			LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(0.1F * dropChanceModifier))
			.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
					"epicfight:death_harvest",
					"epicfight:emergency_escape"
			)))
    	)).add(EntityType.PIGLIN, LootTable.lootTable().withPool(
			LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
			.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
					"epicfight:swordmaster",
					"epicfight:stamina_pillager",
					"epicfight:guard",
					"epicfight:step",
					"epicfight:roll"
			)))
    	)).add(EntityType.PIGLIN_BRUTE, LootTable.lootTable().withPool(
			LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
			.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
					"epicfight:hypervitality",
					"epicfight:parrying",
					"epicfight:endurance",
					"epicfight:impact_guard"
			)))
    	)).add(EntityType.ZOMBIFIED_PIGLIN, LootTable.lootTable().withPool(
			LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
			.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
					"epicfight:berserker",
					"epicfight:stamina_pillager",
					"epicfight:guard",
					"epicfight:step",
					"epicfight:roll"
			)))
    	)).add(EntityType.WITHER_SKELETON, LootTable.lootTable().withPool(
			LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(0.025F * dropChanceModifier))
			.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
					1.0F, "epicfight:swordmaster",
					1.0F, "epicfight:stamina_pillager",
					1.0F, "epicfight:guard",
					1.0F, "epicfight:step",
					1.0F, "epicfight:roll",
					0.75F, "epicfight:death_harvest"
			)))
    	)).add(EntityType.WITHER, LootTable.lootTable().withPool(
			LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
			.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
					"epicfight:death_harvest"
			)))
    	));/**.add(EntityType.ENDER_DRAGON, LootTable.lootTable().withPool(
			LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
			.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
					"epicfight:demolishing_leap"
			)))
    	));**/
		
		ModLoader.get().postEvent(lootTableRegistryEvent);
		
		builders.forEach((k, v) -> {
			SKILL_LOOT_TABLE.put(k, v.build());
		});
	}
	
	protected SkillBookLootModifier(LootItemCondition[] conditionsIn) {
		super(conditionsIn);
	}
	
	@Override
	protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
		Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
		LootTable lootTable = getLootTableFor(entity.getType());
		
		if (lootTable != null) {
			lootTable.getRandomItemsRaw(context, generatedLoot::add);
		}
		
		return generatedLoot;
	}
	
	public static class Serializer extends GlobalLootModifierSerializer<SkillBookLootModifier> {
        @Override
        public SkillBookLootModifier read(ResourceLocation name, JsonObject object, LootItemCondition[] conditionsIn) {
            return new SkillBookLootModifier(conditionsIn);
        }
        
        @Override
        public JsonObject write(SkillBookLootModifier instance) {
            JsonObject json = makeConditions(instance.conditions);
            return json;
        }
    }
}