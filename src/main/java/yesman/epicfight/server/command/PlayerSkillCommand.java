package yesman.epicfight.server.command;

import java.util.Locale;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch.PlayerMode;

public class PlayerSkillCommand {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("setPlayerSkill").requires((commandSourceStack) -> commandSourceStack.hasPermission(2));
		
		for (PlayerMode mode : PlayerPatch.PlayerMode.values()) {
			builder.then(Commands.literal(mode.name().toLowerCase(Locale.ROOT)).executes((command) -> {
				return 0;
			}).then(Commands.argument("target", EntityArgument.players()).executes((p_137728_) -> {
	            return 0;
	        })));
		}
		
		dispatcher.register(Commands.literal("epicfight").then(builder));
	}
}
