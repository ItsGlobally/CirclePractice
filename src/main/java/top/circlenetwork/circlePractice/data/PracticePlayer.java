package top.circlenetwork.circlePractice.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.circlenetwork.circlePractice.utils.LuckPermsUtil;

import java.util.*;

@RequiredArgsConstructor
public class PracticePlayer implements Global {

    /* ================= static ================= */

    private static final Map<UUID, PracticePlayer> PLAYERS = new HashMap<>();

    public static PracticePlayer of(Player player) {
        return PLAYERS.computeIfAbsent(
                player.getUniqueId(),
                uuid -> new PracticePlayer(player)
        );
    }

    public static PracticePlayer get(UUID uuid) {
        return PLAYERS.get(uuid);
    }

    public static void remove(UUID uuid) {
        PLAYERS.remove(uuid);
    }

    public static Collection<PracticePlayer> getPlayers() {
        return PLAYERS.values();
    }

    /* ================= instance ================= */

    @Getter
    private final Player player;

    @Setter
    @Getter
    private SpawnState state = SpawnState.SPAWN;

    @Setter
    @Getter
    private String queuedKit;

    @Getter
    private final PlayerData playerData;

    public PracticePlayer(Player player) {
        this.player = player;
        this.playerData = new PlayerData(player.getUniqueId());
    }
    @Getter
    @Setter
    private Game currentGame;



    public UUID getUuid() {
        return player.getUniqueId();
    }

    public String getName() {
        return player.getName();
    }

    public String getPrefixedName() {
        return LuckPermsUtil.getPrefix(player) + getName();
    }

    /* ================= state ================= */


    public enum SpawnState {
        SPAWN,
        QUEUING,
        EDITING
    }
    @Getter
    public static class PlayerData {

        private final UUID uuid;
        private final YamlFile sourceYml;
        private final FileConfiguration source;
        private final Map<String, ItemStack[]> kitInventories = new HashMap<>();
        private final Map<String, ItemStack[]> kitArmors = new HashMap<>();
        private long level = 0L;

        public PlayerData(UUID uuid) {
            this.uuid = uuid;
            this.sourceYml = new YamlFile("playerdata/" + uuid + ".yml");
            this.source = sourceYml.getConfig();
            load();
        }

        public void load() {
            level = source.getLong("level", 0);

            if (!source.contains("kits")) return;

            ConfigurationSection kitsSec = source.getConfigurationSection("kits");
            for (String kitName : kitsSec.getKeys(false)) {
                ConfigurationSection kitSec = kitsSec.getConfigurationSection(kitName);

                if (kitSec.contains("inventory")) {
                    List<?> list = kitSec.getList("inventory");
                    kitInventories.put(kitName, deserializeItemStackArray(list));
                }

                if (kitSec.contains("armor")) {
                    List<?> list = kitSec.getList("armor");
                    kitArmors.put(kitName, deserializeItemStackArray(list));
                }
            }
        }


        public static List<Map<String, Object>> serializeItemStackArray(ItemStack[] items) {
            List<Map<String, Object>> list = new ArrayList<>();

            for (ItemStack item : items) {
                if (item == null) {
                    list.add(null);
                } else {
                    list.add(item.serialize());
                }
            }
            return list;
        }
        @SuppressWarnings("unchecked")
        public static ItemStack[] deserializeItemStackArray(List<?> list) {
            ItemStack[] items = new ItemStack[list.size()];

            for (int i = 0; i < list.size(); i++) {
                Object obj = list.get(i);

                if (obj == null) continue;
                items[i] = ItemStack.deserialize((Map<String, Object>) obj);
            }
            return items;
        }
        public void save() {
            source.set("level", level);

            for (String kitName : kitInventories.keySet()) {
                source.set(
                        "kits." + kitName + ".inventory",
                        serializeItemStackArray(kitInventories.get(kitName))
                );
            }

            for (String kitName : kitArmors.keySet()) {
                source.set(
                        "kits." + kitName + ".armor",
                        serializeItemStackArray(kitArmors.get(kitName))
                );
            }

            sourceYml.save();
        }




    }

}
