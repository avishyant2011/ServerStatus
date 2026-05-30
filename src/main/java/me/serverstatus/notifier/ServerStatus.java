package me.serverstatus.notifier;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerStatus extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        Bukkit.getScheduler().runTaskLater(this, () -> {
            sendEmbed("startup");
        }, 20L);
    }

    @Override
    public void onDisable() {
        sendEmbed("shutdown");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("serverstatus.admin")) {
            sender.sendMessage("§cNo permission.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§e/serverstatus reload");
            sender.sendMessage("§e/serverstatus startup");
            sender.sendMessage("§e/serverstatus shutdown");
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "reload":
                reloadConfig();
                sender.sendMessage("§aConfig reloaded.");
                break;

            case "startup":
                sendEmbed("startup");
                sender.sendMessage("§aStartup embed sent.");
                break;

            case "shutdown":
                sendEmbed("shutdown");
                sender.sendMessage("§aShutdown embed sent.");
                break;
        }

        return true;
    }

    private void sendEmbed(String section) {

        if (!getConfig().getBoolean(section + ".enabled")) {
            return;
        }

        try {

            DiscordWebhook webhook =
                    new DiscordWebhook(getConfig().getString("webhook.url"));

            webhook.setUsername(
                    getConfig().getString("webhook.username")
            );

            webhook.setAvatarUrl(
                    getConfig().getString("webhook.avatar")
            );

            DiscordWebhook.EmbedObject embed =
                    new DiscordWebhook.EmbedObject()
                            .setTitle(
                                    getConfig().getString(section + ".title")
                            )
                            .setDescription(
                                    getConfig().getString(section + ".description")
                            )
                            .setColor(
                                    getConfig().getInt(section + ".color")
                            )
                            .setFooter(
                                    getConfig().getString(section + ".footer"),
                                    null
                            );

            webhook.addEmbed(embed);
            webhook.execute();

        } catch (Exception exception) {
            getLogger().warning("Failed to send Discord webhook.");
            exception.printStackTrace();
        }
    }
}
