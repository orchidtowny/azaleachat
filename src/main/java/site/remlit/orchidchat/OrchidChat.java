package site.remlit.orchidchat;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import site.remlit.orchidchat.command.ChannelCommand;
import site.remlit.orchidchat.command.ChannelShortcutsCommand;
import site.remlit.orchidchat.service.ChannelService;
import site.remlit.orchidchat.service.ChatService;
import site.remlit.orchidchat.service.ComponentService;
import site.remlit.orchidchat.service.LuckPermsService;
import site.remlit.orchidchat.subscriber.ModConfigEventSubscriber;
import site.remlit.orchidchat.subscriber.ServerChatEventSubscriber;

@Mod(OrchidChat.MODID)
public final class OrchidChat {

	public static final @NotNull String MODID = "orchidchat";
	private static final @NotNull Logger LOGGER = LogUtils.getLogger();


	public OrchidChat() {
		MinecraftForge.EVENT_BUS.register(this);
	}


	@SubscribeEvent
	public void onServerStart(ServerStartedEvent event) {
		Config.loadConfig();

		try {
			LuckPermsService.register();
		} catch (NoClassDefFoundError e) {
			LOGGER.warn("LuckPerms not found, related features will be disabled.");
		}

		ChannelService.setupChannels();

		ModConfigEventSubscriber.register();
		ServerChatEventSubscriber.register();

		LOGGER.info("Finished startup!");
	}

	@SubscribeEvent
	public void onRegisterCommands(RegisterCommandsEvent event) {
		CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

		ChannelCommand.register(dispatcher);
		ChannelShortcutsCommand.register(dispatcher);

		LOGGER.info("Registered commands");
	}

	@SubscribeEvent
	public void onServerStopping(ServerStoppingEvent event) {
		LOGGER.info("Writing config before shutdown...");
		Config.loadConfig();
		Config.writeMemoryConfig();
	}
}
