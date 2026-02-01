package top.circlenetwork.circlePractice.commands;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import top.circlenetwork.circlePractice.annotation.CommandInfo;
import top.circlenetwork.circlePractice.data.Global;

import java.lang.reflect.Modifier;
import java.util.*;

public class CommandManager implements Global {

    private static final Map<CommandBase, Command> registeredCommands = new HashMap<>();
    private static CommandMap commandMap;

    private static CommandMap getCommandMap() throws Exception {
        if (commandMap != null) return commandMap;
        var field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        field.setAccessible(true);
        commandMap = (CommandMap) field.get(Bukkit.getServer());
        return commandMap;
    }

    public static void registerAll(JavaPlugin plugin) {
        try (ScanResult scanResult = new ClassGraph()
                .enableAllInfo()
                .acceptPackages(plugin.getClass().getPackageName())
                .scan()) {

            List<ClassInfo> commandClasses = scanResult.getClassesWithAnnotation(CommandInfo.class.getName());
            for (ClassInfo classInfo : commandClasses) {
                try {
                    Class<?> clazz = classInfo.loadClass();
                    if (!CommandBase.class.isAssignableFrom(clazz)) continue;
                    if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) continue;

                    CommandInfo info = clazz.getAnnotation(CommandInfo.class);
                    if (info == null) continue;

                    CommandBase cmdInstance = (CommandBase) clazz.getDeclaredConstructor().newInstance();
                    registerCommand(plugin, cmdInstance, info);

                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to register command: " + classInfo.getName());
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void registerCommand(JavaPlugin plugin, CommandBase cmdInstance, CommandInfo info) throws Exception {
        CommandMap cm = getCommandMap();

        Command cmd = new Command(info.name()) {
            @Override
            public boolean execute(CommandSender sender, String label, String[] args) {

                if (!info.permission().isEmpty() && !sender.hasPermission(info.permission())) {
                    sender.sendMessage("§c你沒有足夠的權限!");
                    return true;
                }

                boolean isPlayer = sender instanceof Player;

                try {
                    if (isPlayer) {
                        cmdInstance.playerExecute((Player) sender, label, args);
                        cmdInstance.allExecute(sender, label, args);
                    } else {
                        boolean isAllExecuted = false;
                        try {
                            cmdInstance.allExecute(sender, label, args);
                            isAllExecuted = true;
                        } catch (UnsupportedOperationException ignored) {}

                        if (!isAllExecuted) {
                            sender.sendMessage("§c只有玩家可以執行這個指令");
                        }
                    }
                } catch (Exception e) {
                    sender.sendMessage("§c執行指令時發生錯誤!");
                    e.printStackTrace();
                }

                return true;
            }

            @Override
            public List<String> tabComplete(CommandSender sender, String alias, String[] args) {

                List<String> raw = cmdInstance.onTabComplete(sender, this, alias, args);

                if (raw == null || raw.isEmpty()) {
                    return Collections.emptyList();
                }
                int index = args.length - 1;

                return CommandManager.complete(
                        sender,
                        args,
                        index,
                        raw,
                        s -> s
                );
            }

        };



        cmd.setAliases(Arrays.asList(info.aliases()));
        if (!info.permission().isEmpty()) cmd.setPermission(info.permission());

        cm.register(plugin.getName().toLowerCase(), cmd);
        registeredCommands.put(cmdInstance, cmd);
    }

    public static void unregisterAll() {
        try {
            CommandMap cm = getCommandMap();
            for (Command cmd : registeredCommands.values()) {
                cmd.unregister(cm);
            }
            registeredCommands.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static <T> List<String> complete(
            CommandSender sender,
            String[] args,
            int index,
            Iterable<T> source,
            java.util.function.Function<T, String> nameMapper
    ) {
        List<String> result = new ArrayList<>();

        if (args.length <= index) return result;

        String input = args[index].toLowerCase();

        for (T t : source) {
            String name = nameMapper.apply(t);
            if (name != null && name.toLowerCase().startsWith(input)) {
                result.add(name);
            }
        }

        return result;
    }
}
