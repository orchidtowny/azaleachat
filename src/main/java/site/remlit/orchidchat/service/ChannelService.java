package site.remlit.orchidchat.service;

import com.mojang.logging.LogUtils;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.PermissionNode;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Map;
import java.util.Objects;

public class ChannelService {

	public @Nullable LuckPermsService luckPermsService;

	public ChannelService(@Nullable LuckPermsService luckPermsService) {
		this.luckPermsService = luckPermsService;
	}


	private static final @NotNull Logger LOGGER = LogUtils.getLogger();

	public Map<@NotNull String, @Nullable PermissionNode> channels = Map.of();


	/**
	 * Register events and other essentials for this service
	 * */
	@ApiStatus.Internal
	public void register() {
		MinecraftForge.EVENT_BUS.register(this);
	}


	public void registerChannel(
			@NotNull String name,
			@Nullable PermissionNode perm
	) throws IllegalArgumentException {
		if (channels.containsKey(name))
			throw new IllegalArgumentException("This channel already is registered!");

		LOGGER.info("Created channel {}, permission: {}", name, (Objects.isNull(perm) ? "none" : perm));
		channels.put(name, perm);
	}

	/**
	 * Determine if a player can see a channel by permissions
	 *
	 * @param channel Channel being checked
	 * @param player Player viewing channel
	 *
	 * @return If that player can view that channel
	 * */
	public boolean canPlayerSee(
			@NotNull String channel,
			@NotNull Player player
	) {
		PermissionNode requiredPerm = channels.getOrDefault(channel, null);

		// No permission required if set null
		if (Objects.isNull(requiredPerm)) return true;

		if (Objects.isNull(luckPermsService) || luckPermsService.enabled || Objects.isNull(luckPermsService.api))
			return false;

		User user = luckPermsService.api.getUserManager().getUser(player.getUUID());
		if (Objects.isNull(user)) return false;

		return user.getCachedData().getPermissionData()
				.checkPermission(requiredPerm.getPermission())
				.asBoolean();
	}


	@ApiStatus.Internal
	public void setupChannels() {
		this.registerChannel("global", null);
	}

	@ApiStatus.Internal
	public void clearChannels() {
		channels = Map.of();
	}


	@SubscribeEvent
	public void onLoad(final ModConfigEvent event) {
		this.clearChannels();
		this.setupChannels();
	}

}
