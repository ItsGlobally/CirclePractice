package top.circlenetwork.circlePractice.events;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import top.circlenetwork.circlePractice.annotation.AutoListener;

import java.lang.reflect.Modifier;

public class ListenerManager {

    public static void registerAll(JavaPlugin plugin) {
        try (ScanResult scanResult = new ClassGraph()
                .enableAllInfo()
                .acceptPackages(plugin.getClass().getPackageName())
                .scan()) {

            for (ClassInfo classInfo : scanResult.getClassesWithAnnotation(AutoListener.class.getName())) {
                try {
                    Class<?> clazz = classInfo.loadClass();

                    if (!Listener.class.isAssignableFrom(clazz)) continue;

                    if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) continue;

                    Listener listenerInstance = (Listener) clazz.getDeclaredConstructor().newInstance();
                    Bukkit.getPluginManager().registerEvents(listenerInstance, plugin);


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
