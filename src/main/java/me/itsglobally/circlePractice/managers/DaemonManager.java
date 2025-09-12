package me.itsglobally.circlePractice.managers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.itsglobally.circlePractice.CirclePractice;
import org.bukkit.Bukkit;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Objects;

@Deprecated
public class DaemonManager extends WebSocketClient {
    private static final String serverName = CirclePractice.serverName;
    private final Gson gson = new Gson();

    public DaemonManager(URI serverUri) {
        super(serverUri);
    }

    private JsonObject responseBasic(String target) {
        JsonObject obj = new JsonObject();
        obj.addProperty("server", serverName);
        obj.addProperty("target", target);
        obj.addProperty("messagecode", 2);
        return obj;
    }

    private void error(String message, String target) {
        JsonObject obj = new JsonObject();
        obj.addProperty("server", serverName);
        obj.addProperty("target", target);
        obj.addProperty("messagecode", 3);
        send(gson.toJson(obj));
    }

    private void unknowncmd(String target) {
        JsonObject obj = new JsonObject();
        obj.addProperty("server", serverName);
        obj.addProperty("target", target);
        obj.addProperty("messagecode", 4);
        send(gson.toJson(obj));
    }

    private JsonObject sendBasic(String message, String target) {
        JsonObject obj = new JsonObject();
        obj.addProperty("server", serverName);
        obj.addProperty("target", "all");
        obj.addProperty("messagecode", 1);
        return obj;
    }


    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Bukkit.getLogger().info("Connected to server");
    }

    @Override
    public void onMessage(String message) {
        JsonObject json = gson.fromJson(message, JsonObject.class);
        String server = json.has("server") ? json.get("server").getAsString() : "";
        String target = json.has("target") ? json.get("target").getAsString() : "";
        String cmd = json.has("cmd") ? json.get("cmd").getAsString() : "";
        Long messageId = json.has("id") ? json.get("id").getAsLong() : 0L;
        if (cmd.isEmpty()) return;
        if (!Objects.equals(target, serverName)) return;
        try {
            switch (cmd) {
                case "rualive" -> {
                    JsonObject jo = responseBasic(server);
                    jo.addProperty("alive", true);
                    send(gson.toJson(jo));
                }
                default -> unknowncmd(server);
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Error while processing message: " + message);
            e.printStackTrace();
            error(e.getMessage(), server);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Bukkit.getLogger().warning("Connection closed: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

}
