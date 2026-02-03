package top.circlenetwork.circlePractice.commands.sub;

import org.bukkit.entity.Player;
import top.circlenetwork.circlePractice.annotation.CommandInfo;
import top.circlenetwork.circlePractice.commands.CommandBase;
import top.circlenetwork.circlePractice.data.Arena;
import top.circlenetwork.circlePractice.data.Kit;
import top.circlenetwork.circlePractice.utils.Msg;

@CommandInfo(name = "arena", permission = "circlepractice.command.arena")
public class ArenaCommand extends CommandBase {
    @Override
    public void playerExecute(Player player, String label, String[] args) {
        if (args.length < 2) {
            Msg.send(player, "&c用法: /arena <副指令> <值>");
        }
        String cmd = args[0];
        String value = args[1];

        switch (cmd) {
            case "create" -> {
                if (Arena.getArena(value) != null) {
                    Msg.send(player, "&c該場地已存在!");
                    return;
                }
                Arena arena = new Arena(value);
                Msg.send(player, "&a場地已創建");
            }
            case "setRedSpawn" -> {
                if (Arena.getArena(value) == null) {
                    Msg.send(player, "&c該場地不存在!");
                    return;
                }
                Arena arena = Arena.getArena(value);
                arena.setRedSpawn(player.getLocation());
                Msg.send(player, "&a已設定紅隊的出生點");
            }
            case "setBlueSpawn" -> {
                if (Arena.getArena(value) == null) {
                    Msg.send(player, "&c該場地不存在!");
                    return;
                }
                Arena arena = Arena.getArena(value);
                arena.setBlueSpawn(player.getLocation());
                Msg.send(player, "&a已設定藍隊的出生點");
            }
            case "setRedBed" -> {
                if (Arena.getArena(value) == null) {
                    Msg.send(player, "&c該場地不存在!");
                    return;
                }
                Arena arena = Arena.getArena(value);
                arena.setRedBed(player.getLocation());
                Msg.send(player, "&a已設定紅隊的床");
            }
            case "setBlueBed" -> {
                if (Arena.getArena(value) == null) {
                    Msg.send(player, "&c該場地不存在!");
                    return;
                }
                Arena arena = Arena.getArena(value);
                arena.setBlueBed(player.getLocation());
                Msg.send(player, "&a已設定藍隊的床");
            }
            case "kits" -> {
                if (args.length < 3) {
                    Msg.send(player, "&c用法: /arena kits <add/remove>");
                    return;
                }
                switch(args[1]) {
                    case "add" -> {
                        if (args.length < 4) {
                            Msg.send(player, "&c用法: /arena kits add <場地> <模式>");
                            return;
                        }
                        Arena arena = Arena.getArena(args[2]);
                        if (arena == null) {
                            Msg.send(player, "&c該模式不存在!");
                            return;
                        }

                        Kit kit = Kit.getKit(args[3]);
                        if (kit == null) {
                            Msg.send(player, "&c該模式不存在!");
                            return;
                        }

                        if (arena.getAllowedKits().contains(kit.getName())) {
                            Msg.send(player, "&c該模式已經被加入名單!");
                            return;
                        }

                        arena.getAllowedKits().add(kit.getName());
                        Msg.send(player, "&a已將該模式加入名單");
                    }
                    case "remove" -> {
                        if (args.length < 4) {
                            Msg.send(player, "&c用法: /arena kits remove <場地> <模式>");
                            return;
                        }
                        Arena arena = Arena.getArena(args[2]);
                        if (arena == null) {
                            Msg.send(player, "&c該模式不存在!");
                            return;
                        }

                        Kit kit = Kit.getKit(args[3]);
                        if (kit == null) {
                            Msg.send(player, "&c該模式不存在!");
                            return;
                        }

                        if (!arena.getAllowedKits().contains(kit.getName())) {
                            Msg.send(player, "&c該模式不在名單內!");
                            return;
                        }

                        arena.getAllowedKits().remove(kit.getName());
                        Msg.send(player, "&a已將該模式移出名單");
                    }
                }


            }
            case "set" -> {
                if (args.length < 4) {
                    Msg.send(player, "&c用法: /arena set <場地> <選項> <值>");
                    return;
                }

                Arena arena = Arena.getArena(args[1]);
                if (arena == null) {
                    Msg.send(player, "&c該模式不存在!");
                    return;
                }

                Arena.ArenaOption option;
                try {
                    option = Arena.ArenaOption.valueOf(args[2].toUpperCase());
                } catch (IllegalArgumentException e) {
                    Msg.send(player, "&c未知的選項!");
                    return;
                }

                String rawValue = args[3];

                try {
                    Object def = option.getDefaultValue();

                    if (def instanceof Boolean) {
                        boolean value1 = Boolean.parseBoolean(rawValue);
                        arena.setBoolean(option, value1);

                    } else if (def instanceof Integer) {
                        int value2 = Integer.parseInt(rawValue);
                        arena.setInt(option, value2);

                    } else {
                        Msg.send(player, "&c此選項不支援修改");
                        return;
                    }

                    arena.save();
                    Msg.send(player, "&a已設定" + option.name() + "為" + rawValue);

                } catch (NumberFormatException e) {
                    Msg.send(player, "&c數值格式錯誤");
                } catch (Exception e) {
                    Msg.send(player, "&c設定失敗");
                    e.printStackTrace();
                }
            }
        }
    }
}
