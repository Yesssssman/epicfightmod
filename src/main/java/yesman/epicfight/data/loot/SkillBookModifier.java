package yesman.epicfight.data.loot;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.google.gson.JsonObject;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.config.ConfigManager;
import yesman.epicfight.gameasset.Skills;
import yesman.epicfight.world.item.EpicFightItems;
import yesman.epicfight.world.item.SkillBookItem;

public class SkillBookModifier extends LootModifier {
	public static final Supplier<Codec<SkillBookModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, SkillBookModifier::new)));
	protected SkillBookModifier(LootItemCondition[] conditionsIn) {
		super(conditionsIn);
	}

	@Override
	protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
		Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
		if (ConfigManager.SKILLBOOK_MOB_LOOT.get() && entity instanceof Monster) {
			Random random = new Random();
			if (random.nextFloat() < 0.025F) {
				ItemStack skillBook = new ItemStack(EpicFightItems.SKILLBOOK.get());
				SkillBookItem.setContainingSkill(Skills.getRandomLearnableSkillName(), skillBook);
				generatedLoot.add(skillBook);
			}
		}
		return generatedLoot;
	}

	@Override
	public Codec<SkillBookModifier> codec() {
		return CODEC.get();
	}
}