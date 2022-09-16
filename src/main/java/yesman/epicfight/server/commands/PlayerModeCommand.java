package yesman.epicfight.server.commands;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch.PlayerMode;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class PlayerModeCommand {
	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		LiteralArgumentBuilder<CommandSource> builder = Commands.literal("mode").requires((commandSourceStack) -> commandSourceStack.hasPermission(2));
		
		for (PlayerMode mode : PlayerPatch.PlayerMode.values()) {
			builder.then(Commands.literal(mode.name().toLowerCase(Locale.ROOT)).executes((command) -> {
				return setMode(command, Collections.singleton(command.getSource().getPlayerOrException()), mode);
			}).then(Commands.argument("target", EntityArgument.players()).executes((p_137728_) -> {
	            return setMode(p_137728_, EntityArgument.getPlayers(p_137728_, "target"), mode);
	        })));
		}
		
		dispatcher.register(Commands.literal("epicfight").then(builder));
	}
	
	private static int setMode(CommandContext<CommandSource> command, Collection<ServerPlayerEntity> players, PlayerPatch.PlayerMode playerMode) {
		int i = 0;
		
		for (ServerPlayerEntity serverplayer : players) {
			ServerPlayerPatch playerpatch = (ServerPlayerPatch)serverplayer.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
			
			if (playerpatch != null) {
				logGamemodeChange(command.getSource(), serverplayer, playerMode);
				playerpatch.toMode(playerMode, true);
				++i;
			}
		}
		
		return i;
	}
	
	private static void logGamemodeChange(CommandSource command, ServerPlayerEntity serverPlayer, PlayerPatch.PlayerMode playerMode) {
		ITextComponent component = new TranslationTextComponent("gameMode.epicfight." + playerMode.name().toLowerCase(Locale.ROOT));
		
		if (command.getEntity() == serverPlayer) {
			command.sendSuccess(new TranslationTextComponent("commands.gamemode.success.self", component), true);
		} else {
			if (command.getLevel().getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK)) {
				serverPlayer.sendMessage(new TranslationTextComponent("gameMode.changed", component), Util.NIL_UUID);
			}
			command.sendSuccess(new TranslationTextComponent("commands.gamemode.success.other", serverPlayer.getDisplayName(), component), true);
		}

	}
}