package site.remlit.orchidchat.service;

import com.mojang.logging.LogUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;
import site.remlit.orchidchat.Config;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class ChatService {

	public @Nullable LuckPermsService luckpermsService;

	public ChatService(@Nullable LuckPermsService luckpermsService) {
		this.luckpermsService = luckpermsService;
	}


	private static final Logger LOGGER = LogUtils.getLogger();

	/**
	 * Registers events for the chat service.
	 * */
	public void register() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	private static final MiniMessage MM = MiniMessage.miniMessage();
	private static final GsonComponentSerializer JCS = GsonComponentSerializer.gson();
	private static final Pattern LUCKPERMS_META_PATTERN = Pattern.compile("%luckperms_meta_([a-zA-Z0-9]*)%");

	@SubscribeEvent
	public void onServerChatEvent(ServerChatEvent event) {
		try {
			String formatted;
			String format = Config.format;

			String name = event.getUsername();
			String message = event.getRawText();


			formatted = format
					.replace("%name%", name)
					.replace("%msg%", message);


			if (!Objects.isNull(luckpermsService) && luckpermsService.enabled) {
				User lpUser = luckpermsService.api.getUserManager()
						.getUser(event.getPlayer().getUUID());
				if (Objects.isNull(lpUser)) return;

				CachedMetaData cachedMetaData = lpUser.getCachedData().getMetaData();

				// Prefix and Suffix
				String prefix = cachedMetaData.getPrefix();
				if (Objects.isNull(prefix)) prefix = "";

				String suffix = cachedMetaData.getSuffix();
				if (Objects.isNull(suffix)) suffix = "";

				formatted = format
						.replace("%luckperms_prefix%", prefix)
						.replace("%luckperms_suffix%", suffix);

				// Meta
				List<String> metaMatches = LUCKPERMS_META_PATTERN.matcher(formatted)
						.results()
						.map(MatchResult::group)
						.toList();

				for (String metaMatch : metaMatches) {
					String meta = metaMatch
							.replace("%luckperms_meta_", "")
							.replace("%", "");

					String metaValue = cachedMetaData.getMetaValue(meta);
					if (Objects.isNull(metaValue)) metaValue = "";

					formatted = formatted.replace(metaMatch, metaValue);
				}
			}


			// Conversion
			Component miniMessage = MM.deserialize(formatted);
			String rawJson = JCS.serialize(miniMessage);
			net.minecraft.network.chat.Component finalMessage = net.minecraft.network.chat.Component.Serializer
					.fromJson(rawJson);


			event.cancel();

			if (Objects.isNull(finalMessage)) return;

			MinecraftServer server = event.getPlayer().getServer();
			if (Objects.isNull(server)) return;


			// Sending
			LOGGER.info(finalMessage.getString());

			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				player.sendSystemMessage(finalMessage);
			}
		} catch (Exception e) {
			LOGGER.error("Failed to modify chat! " + e.getLocalizedMessage(), e);
		}
	}

}
