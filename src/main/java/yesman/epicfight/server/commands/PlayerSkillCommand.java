package yesman.epicfight.server.commands;

import java.util.Collection;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPChangeSkill;
import yesman.epicfight.network.server.SPClearSkills;
import yesman.epicfight.network.server.SPRemoveSkill;
import yesman.epicfight.server.commands.arguments.SkillArgument;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class PlayerSkillCommand {
	private static final SimpleCommandExceptionType ERROR_ADD_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.epicfight.skill.add.failed"));
	private static final SimpleCommandExceptionType ERROR_REMOVE_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.epicfight.skill.remove.failed"));
	private static final SimpleCommandExceptionType ERROR_CLEAR_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.epicfight.skill.clear.failed"));
	
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("skill").requires((commandSourceStack) -> commandSourceStack.hasPermission(2))
			.then(Commands.literal("clear").then(Commands.argument("targets", EntityArgument.players()).executes((commandContext) -> {
				return clearSkill(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"));
			}))).then(Commands.literal("add").then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("skill", SkillArgument.skill()).executes((commandContext) -> {
				return addSkill(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), SkillArgument.getSkill(commandContext, "skill"));
			})))).then(Commands.literal("remove").then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("skill", SkillArgument.skill()).executes((commandContext) -> {
				return removeSkill(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), SkillArgument.getSkill(commandContext, "skill"));
			}))));
		
		dispatcher.register(Commands.literal("epicfight").then(builder));
	}
	
	public static int clearSkill(CommandSourceStack commandSourceStack, Collection<? extends ServerPlayer> targets) throws CommandSyntaxException {
		int i = 0;
		
		for (ServerPlayer player : targets) {
			ServerPlayerPatch playerpatch = (ServerPlayerPatch)player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			playerpatch.getSkillCapability().clear();
			EpicFightNetworkManager.sendToPlayer(new SPClearSkills(), player);
			i++;
		}
		
		if (i > 0) {
			if (i == 1) {
				commandSourceStack.sendSuccess(new TranslatableComponent("commands.epicfight.skill.clear.success.single", targets.iterator().next().getDisplayName()), true);
			} else {
				commandSourceStack.sendSuccess(new TranslatableComponent("commands.epicfight.skill.clear.success.multiple", i), true);
			}
		} else {
			throw ERROR_CLEAR_FAILED.create();
		}
		
		return i;
	}
	
	public static int addSkill(CommandSourceStack commandSourceStack, Collection<? extends ServerPlayer> targets, Skill skill) throws CommandSyntaxException {
		int i = 0;
		
		for (ServerPlayer player : targets) {
			ServerPlayerPatch playerpatch = (ServerPlayerPatch)player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			
			if (playerpatch.getSkillCapability().skillContainers[skill.getCategory().universalOrdinal()].setSkill(skill)) {
				if (skill.getCategory().learnable()) {
					playerpatch.getSkillCapability().addLearnedSkill(skill);
				}
				
				EpicFightNetworkManager.sendToPlayer(new SPChangeSkill(skill.getCategory().universalOrdinal(), skill.toString(), SPChangeSkill.State.ENABLE), player);
				i++;
			}
		}
		
		if (i > 0) {
			if (i == 1) {
				commandSourceStack.sendSuccess(new TranslatableComponent("commands.epicfight.skill.add.success.single", skill.getRegistryName(), targets.iterator().next().getDisplayName()), true);
			} else {
				commandSourceStack.sendSuccess(new TranslatableComponent("commands.epicfight.skill.add.success.multiple", skill.getRegistryName(), i), true);
			}
		} else {
			throw ERROR_ADD_FAILED.create();
		}
		
		return i;
	}
	
	public static int removeSkill(CommandSourceStack commandSourceStack, Collection<? extends ServerPlayer> targets, Skill skill) throws CommandSyntaxException {
		int i = 0;
		
		for (ServerPlayer player : targets) {
			ServerPlayerPatch playerpatch = (ServerPlayerPatch)player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			
			if (playerpatch != null) {
				if (playerpatch.getSkillCapability().removeLearnedSkill(skill)) {
					SkillContainer skillContainer = playerpatch.getSkillCapability().skillContainers[skill.getCategory().universalOrdinal()];
					
					if (skillContainer.getSkill() == skill) {
						skillContainer.setSkill(null);
					}
					
					EpicFightNetworkManager.sendToPlayer(new SPRemoveSkill(skill.toString()), player);
					i++;
				}
				
			}
		}
		
		if (i > 0) {
			if (i == 1) {
				commandSourceStack.sendSuccess(new TranslatableComponent("commands.epicfight.skill.remove.success.single", skill.getRegistryName(), targets.iterator().next().getDisplayName()), true);
			} else {
				commandSourceStack.sendSuccess(new TranslatableComponent("commands.epicfight.skill.remove.success.multiple", skill.getRegistryName(), i), true);
			}
		} else {
			throw ERROR_REMOVE_FAILED.create();
		}
		
		return i;
	}
}
