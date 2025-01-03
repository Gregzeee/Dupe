package me.gregzee.moderndupe.commands;

import me.gregzee.moderndupe.ModernDupe;
import me.gregzee.moderndupe.config.ConfigManager;
import me.gregzee.moderndupe.util.DupeUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.Map;

public final class DupeCommand implements CommandExecutor {

	private final DupeUtil dupeUtil = new DupeUtil();

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
		if (!(sender instanceof Player player)) {
			sender.sendMessage(ConfigManager.Messages.getOnlyPlayers());
			return true;
		}

		if (!player.hasPermission(ConfigManager.Permissions.getDupe())) {
			sender.sendMessage(ConfigManager.Messages.getNoPermission());
			return true;
		}

		if (args.length == 1) {
			int count;
			int maxCount;

			maxCount = getMaxDupeCount(player);

			try {
				count = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				player.sendMessage(ConfigManager.Messages.getInvalidDupeCount());
				return true;
			}

			if (count > maxCount) {
				player.sendMessage(ConfigManager.Messages.getExceededMaxDupeCount());
				return true;
			}

			dupeUtil.dupe(player, count);
			return true;

		} else if (args.length == 0) {
			dupeUtil.dupe(player);
			return true;
		} else {
			player.sendMessage(Component.text("Invalid usage! Use: /dupe <count>").color(NamedTextColor.RED));
			return true;
		}
	}

	/**
	 * Utility method to check how much the given player is allowed to dupe at once
	 * @param player the player to check the max dupe count of
	 * @return the max amount the player is allowed to dupe
	 */
	private int getMaxDupeCount(Player player) {
		int maxCount = 0;

		ModernDupe.getInstance().getLogger().info(ConfigManager.getDupeCountLimits().toString());

		ModernDupe.getInstance().getLogger().info("Checking dupe count limits for player: " + player.getName());
		Map<String, Object> dupeCountLimits = ConfigManager.getDupeCountLimits();

		for (Map.Entry<String, Object> entry : dupeCountLimits.entrySet()) {
			String permission = entry.getKey();
			Object value = entry.getValue();

			ModernDupe.getInstance().getLogger().info("Permission: " + permission + ", Value: " + value);

			// Ensure the value is an integer
			if (!(value instanceof Integer)) {
				ModernDupe.getInstance().getLogger().info("Invalid value type for permission " + permission + ": " + value.getClass());
				continue;
			}

			int count = (int) value;

			if (player.hasPermission(permission)) {
				ModernDupe.getInstance().getLogger().info("Player has permission: " + permission + ", Count: " + count);
				maxCount = Math.max(maxCount, count);
			}
		}

		ModernDupe.getInstance().getLogger().info(dupeCountLimits.toString());
		ModernDupe.getInstance().getLogger().info("Max dupe count for player: " + player.getName() + " is " + maxCount);
		return maxCount;
	}

}
