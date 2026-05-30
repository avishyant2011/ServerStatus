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

            if (Bukkit.hasWhitelist()) {
                getLogger().info("Whitelist enabled. Startup embed skipped.");
                return;
            }

            sendEmbed("startup");
        }, 20L);
    }

    @Override
    public void onDisable() {

        if (Bukkit.hasWhitelist()) {
            getLogger().info("Whitelist enabled. Shutdown embed skipped.");
            return;
        }

        sendEmbed("shutdown");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!command.getName().equalsIgnoreCase("sendembed")) return false;

        if (!sender.hasPermission("serverstatus.send")) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("Usage: /sendembed <start|stop>");
            return true;
        }

        if (args[0].equalsIgnoreCase("start")) {
            sendEmbed("startup");
            sender.sendMessage("Startup embed sent.");
        } else if (args[0].equalsIgnoreCase("stop")) {
            sendEmbed("shutdown");
            sender.sendMessage("Shutdown embed sent.");
        } else {
            sender.sendMessage("Invalid argument. Use start or stop.");
        }

        return true;
    }

    private void sendEmbed(String path) {
        if (!getConfig().getBoolean(path + ".enabled")) return;

        String webhookUrl = getConfig().getString("webhook.url");
        if (webhookUrl == null || webhookUrl.isEmpty()) return;

        DiscordWebhook webhook = new DiscordWebhook(webhookUrl);
        webhook.setUsername(getConfig().getString("webhook.username"));
        webhook.setAvatarUrl(getConfig().getString("webhook.avatar"));

        DiscordWebhook.Embed embed = new DiscordWebhook.Embed()
                .setTitle(getConfig().getString(path + ".title"))
                .setDescription(getConfig().getString(path + ".description"))
                .setColor(getConfig().getInt(path + ".color"))
                .setFooter(getConfig().getString(path + ".footer"));

        webhook.addEmbed(embed);
        webhook.execute();
    }
}
