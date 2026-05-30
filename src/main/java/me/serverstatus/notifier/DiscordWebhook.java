package me.serverstatus.notifier;

import javax.net.ssl.HttpsURLConnection;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DiscordWebhook {

    private final String url;
    private String username;
    private String avatarUrl;
    private final List<Embed> embeds = new ArrayList<>();

    public DiscordWebhook(String url) {
        this.url = url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void addEmbed(Embed embed) {
        embeds.add(embed);
    }

    public void execute() {
        try {
            HttpsURLConnection connection =
                    (HttpsURLConnection) new URL(url).openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String payload = buildJson();

            try (OutputStream stream = connection.getOutputStream()) {
                stream.write(payload.getBytes(StandardCharsets.UTF_8));
            }

            connection.getInputStream().close();
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String buildJson() {
        StringBuilder json = new StringBuilder("{");

        if (username != null) {
            json.append("\"username\":\"").append(escape(username)).append("\",");
        }

        if (avatarUrl != null) {
            json.append("\"avatar_url\":\"").append(escape(avatarUrl)).append("\",");
        }

        json.append("\"embeds\":[");
        for (int i = 0; i < embeds.size(); i++) {
            json.append(embeds.get(i).toJson());
            if (i + 1 < embeds.size()) json.append(",");
        }
        json.append("]}");

        return json.toString();
    }

    private static String escape(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }

    public static class Embed {
        private String title;
        private String description;
        private int color;
        private String footer;

        public Embed setTitle(String title) {
            this.title = title;
            return this;
        }

        public Embed setDescription(String description) {
            this.description = description;
            return this;
        }

        public Embed setColor(int color) {
            this.color = color;
            return this;
        }

        public Embed setFooter(String footer) {
            this.footer = footer;
            return this;
        }

        private String toJson() {
            StringBuilder json = new StringBuilder("{");

            if (title != null) {
                json.append("\"title\":\"").append(escape(title)).append("\",");
            }

            if (description != null) {
                json.append("\"description\":\"").append(escape(description)).append("\",");
            }

            json.append("\"color\":").append(color);

            if (footer != null) {
                json.append(",\"footer\":{\"text\":\"")
                        .append(escape(footer))
                        .append("\"}");
            }

            json.append("}");
            return json.toString();
        }
    }
}
