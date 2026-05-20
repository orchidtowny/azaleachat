package site.remlit.orchidchat.subscriber;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import site.remlit.orchidchat.Config;
import site.remlit.orchidchat.service.ChannelService;

public final class ModConfigEventSubscriber {
	public static void register() {
		MinecraftForge.EVENT_BUS.register(new ModConfigEventSubscriber());
	}

	@SubscribeEvent
	public void onEvent(ModConfigEvent event) {
		Config.loadConfig();

		ChannelService.clearChannels();
		ChannelService.setupChannels();
	}
}
