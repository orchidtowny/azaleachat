package site.remlit.orchidchat;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;
import site.remlit.orchidchat.service.ChatService;
import site.remlit.orchidchat.service.LuckpermsService;

@Mod(OrchidChat.MODID)
public class OrchidChat {

	public static final String MODID = "orchidchat";
	private static final Logger LOGGER = LogUtils.getLogger();

	private LuckpermsService luckpermsService;
	private ChatService chatService;

	public OrchidChat() {
		try {
			luckpermsService = new LuckpermsService();
			luckpermsService.register();
		} catch (Exception e) {
			LOGGER.warn("Luckperms not found, related features will be disabled.");
		}

		chatService = new ChatService(luckpermsService);
		chatService.register();

		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SPEC);

		LOGGER.info("Finished startup!");
	}

}
