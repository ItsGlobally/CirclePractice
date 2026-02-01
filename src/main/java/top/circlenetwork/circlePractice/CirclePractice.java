package top.circlenetwork.circlePractice;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import top.circlenetwork.circlePractice.commands.CommandManager;
import top.circlenetwork.circlePractice.data.Arena;
import top.circlenetwork.circlePractice.data.Kit;
import top.circlenetwork.circlePractice.events.ListenerManager;

public final class CirclePractice extends JavaPlugin {

    @Getter
    private static CirclePractice instance;

    @Override
    public void onEnable() {
        instance = this;
        CommandManager.registerAll(this);
        ListenerManager.registerAll(this);
        Arena.loadAll();
        Kit.loadAll();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
