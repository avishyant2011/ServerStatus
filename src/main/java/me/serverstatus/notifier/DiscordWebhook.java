package me.serverstatus.notifier;

import javax.net.ssl.HttpsURLConnection;
import java.awt.Color;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscordWebhook {

    private final String url;
    private String username;
    private String avatarUrl;
    private final List<EmbedObject> embeds = new ArrayList<>();

    public DiscordWebhook(String url) {
        this.url = url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void addEmbed(EmbedObject embed) {
        embeds.add(embed);
    }

    public void execute() throws Exception {

        Map<String, Object> json = new HashMap<>();

        json.put("username", username);
        json.put("avatar_url", avatarUrl);

        if (!embeds.isEmpty()) {

            List<Map<String, Object>> embedObjects = new ArrayList<>();

            for (EmbedObject embed : embeds) {
                embedObjects.add(embed.toMap());
            }

            json.put("embeds", embedObjects);
        }

        String payload = JsonUtil.toJson(json);

        HttpsURLConnection connection =
                (HttpsURLConnection) new URL(url).openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty(
                "Content-Type",
                "application/json"
        );

        connection.setDoOutput(true);

        try (OutputStream stream = connection.getOutputStream()) {
            stream.write(payload.getBytes(StandardCharsets.UTF_8));
        }

        connection.getInputStream().close();
        connection.disconnect();
    }

    public static class EmbedObject {

        private String title;
        private String description;
        private Color color;
        private String footer;

        public EmbedObject setTitle(String title) {
            this.title = title;
            return this;
        }

        public EmbedObject setDescription(String description) {
            this.description = description;
            return this;
        }

        public EmbedObject setColor(int color) {
            this.color = new Color(color);
            return this;
        }

        public EmbedObject setFooter(String footer, String iconUrl) {
            this.footer = footer;
            return this;
        }

        public Map<String, Object> toMap() {

            Map<String, Object> map = new HashMap<>();

            map.put("title", title);
            map.put("description", description);

            if (color != null) {
                map.put("color", color.getRGB() & 0xFFFFFF);
            }

            if (footer != null) {
                Map<String, Object> footerMap = new HashMap<>();
                footerMap.put("text", footer);
                map.put("footer", footerMap);
            }

            return map;
        }
    }
}
