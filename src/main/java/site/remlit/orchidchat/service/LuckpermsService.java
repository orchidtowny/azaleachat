package site.remlit.orchidchat.service;

import com.mojang.logging.LogUtils;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.slf4j.Logger;

public class LuckpermsService {

	public boolean enabled = false;
	private static final Logger LOGGER = LogUtils.getLogger();

	public LuckPerms api;

	public void register() {
		try {
			api = LuckPermsProvider.get();
			enabled = true;
		} catch (Exception e) {
			LOGGER.warn("Luckperms not found, related features will be disabled.");
		}
	}

}
