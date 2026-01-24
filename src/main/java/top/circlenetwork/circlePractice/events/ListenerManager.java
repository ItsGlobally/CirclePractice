package top.circlenetwork.circlePractice.events;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import top.circlenetwork.circlePractice.annotation.ListenerInfo;

import java.lang.reflect.Modifier;

public class ListenerManager {

    public static void registerAll(JavaPlugin plugin) {
        try (ScanResult scanResult = new ClassGraph()
                .enableAllInfo()
                .acceptPackages(plugin.getClass().getPackageName())
                .scan()) {

            for (ClassInfo classInfo : scanResult.getClassesWithAnnotation(ListenerInfo.class.getName())) {
                try {
                    Class<?> clazz = classInfo.loadClass();

                    // 確認是 Listener
                    if (!Listener.class.isAssignableFrom(clazz)) continue;

                    // 跳過 interface 或 abstract class
                    if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) continue;

                    ListenerInfo info = clazz.getAnnotation(ListenerInfo.class);
                    if (info == null || !info.shouldLoad()) continue;

                    Listener listenerInstance = (Listener) clazz.getDeclaredConstructor().newInstance();
                    Bukkit.getPluginManager().registerEvents(listenerInstance, plugin);

                    plugin.getLogger().info("Registered listener: " + clazz.getSimpleName());

                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to register listener: " + classInfo.getName());
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
