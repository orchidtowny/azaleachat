package site.remlit.orchidchat;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(
		modid = OrchidChat.MODID,
		bus = Mod.EventBusSubscriber.Bus.MOD
)
public final class Config {

	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();


	private static final ForgeConfigSpec.ConfigValue<String> FORMAT = BUILDER
			.comment("""
					 Format to use for chat, MiniMessage allowed.
					
					 Placeholders:
					 %name% - Player's name
					 %msg% - Message
					
					 %luckperms_prefix% - LuckPerms prefix
					 %luckperms_suffix% - LuckPerms suffix
					 %luckperms_meta_<value>% - LuckPerms meta value
					""")
			.define(
					"format",
					"<gray>" +
							"<hover:show_text:\"<yellow>%luckperms_meta_rankDescription%\">%luckperms_prefix%</hover>" +
							"<click:run_command:\"msg %name%\">%name%</click>" +
							"%luckperms_suffix%" +
							" <dark_gray>» " +
							"<white>%luckperms_meta_chatcolor%%msg%"
			);

	private static final ForgeConfigSpec.ConfigValue<Map<@NotNull String, @Nullable String>> CHANNELS = BUILDER
			.comment("""
					 Chat channels to create. A global channel everyone can talk in is always available.
					 First string ID, second is required permission (leave blank to require no permission).
					""")
			.define(
					"channels",
					Map.of(
							"staff", "orchidchat.channel.staff"
					)
			);

	private static final ForgeConfigSpec.ConfigValue<Map<@NotNull String, @NotNull List<String>>> CHANNEL_SHORTCUTS = BUILDER
			.comment("""
					 Chat shortcuts to quickly send a message in a channel.
					 If a message starts with one of these, it will be sent in that channel.
					""")
			.define(
					"channel_shortcuts",
					Map.of(
							"global", List.of("g;", "global;", "gen;", "general;"),
							"staff", List.of("s;", "sc;", "st;", "staff;")
					)
			);


	static final ForgeConfigSpec SPEC = BUILDER.build();


	public static String format;
	public static Map<String, String> channels;
	public static Map<String, List<String>> channelShortcuts;


	@SubscribeEvent
	static void onLoad(final ModConfigEvent event) {
		format = FORMAT.get();
		channels = CHANNELS.get();
		channelShortcuts = CHANNEL_SHORTCUTS.get();
	}

}
