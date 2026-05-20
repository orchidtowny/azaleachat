package site.remlit.orchidchat.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.jetbrains.annotations.NotNull;

import static site.remlit.orchidchat.service.ComponentService.*;

public final class ChannelShortcutsCommand {
	public static void register(@NotNull CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("channelShortcuts")
				.executes(ChannelCommand::execute));
	}

	public static int execute(@NotNull CommandContext<CommandSourceStack> command) {
		return 0;
	}
}
