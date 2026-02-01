package top.circlenetwork.circlePractice.utils;

import top.circlenetwork.circlePractice.data.Global;
import top.circlenetwork.circlePractice.data.YamlFile;

import java.io.File;
import java.util.*;

public class FileUtil implements Global {
    public static Map<String, YamlFile> loadYamlFolder(String folder) {
        Map<String, YamlFile> map = new HashMap<>();
        File dir = new File(plugin.getDataFolder(), folder);

        if (!dir.exists()) {
            dir.mkdirs();
            return map;
        }

        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (!file.getName().endsWith(".yml")) continue;

            String name = file.getName().replace(".yml", "");
            YamlFile yml = new YamlFile(folder + "/" + file.getName());

            map.put(name.toLowerCase(), yml);
        }

        return map;
    }
}
