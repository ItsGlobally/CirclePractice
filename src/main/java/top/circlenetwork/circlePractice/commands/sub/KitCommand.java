package top.circlenetwork.circlePractice.commands.sub;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import top.circlenetwork.circlePractice.annotation.CommandInfo;
import top.circlenetwork.circlePractice.commands.CommandBase;
import top.circlenetwork.circlePractice.data.Arena;
import top.circlenetwork.circlePractice.data.Kit;
import top.circlenetwork.circlePractice.data.PracticePlayer;
import top.circlenetwork.circlePractice.utils.Msg;

@CommandInfo(name = "kit", permission = "circlepractice.command.kit")
public class KitCommand extends CommandBase {
    @Override
    public void playerExecute(Player player, String label, String[] args) {
        if (args.length < 1) {
            Msg.send(player, "&c用法: /kit <副指令>");
        }
        String cmd = args[0];


        switch (cmd) {
            case "create" -> {
                if (args.length < 2) {
                    Msg.send(player, "&c用法: /kit create <名字>");
                }
                String value = args[1];
                if (Kit.getKit(value) != null) {
                    Msg.send(player, "該模式已存在!");
                    return;
                }
                Kit kit = new Kit(value);
                Msg.send(player, "模式已創建");
            }
            case "set" -> {
                if (args.length < 4) {
                    Msg.send(player, "&c用法: /kit set <模式> <選項> <值>");
                    return;
                }

                Kit kit = Kit.getKit(args[1]);
                if (kit == null) {
                    Msg.send(player, "&c該模式不存在!");
                    return;
                }

                Kit.KitOption option;
                try {
                    option = Kit.KitOption.valueOf(args[2].toUpperCase());
                } catch (IllegalArgumentException e) {
                    Msg.send(player, "&c未知的選項!");
                    return;
                }

                String rawValue = args[3];

                try {
                    Object def = option.getDefaultValue();

                    if (def instanceof Boolean) {
                        boolean value1 = Boolean.parseBoolean(rawValue);
                        kit.setBoolean(option, value1);

                    } else if (def instanceof Integer) {
                        int value2 = Integer.parseInt(rawValue);
                        kit.setInt(option, value2);

                    } else {
                        Msg.send(player, "&c此選項不支援修改");
                        return;
                    }

                    kit.save();
                    Msg.send(player, "&a已設定" + option.name() + "為" + rawValue);

                } catch (NumberFormatException e) {
                    Msg.send(player, "&c數值格式錯誤");
                } catch (Exception e) {
                    Msg.send(player, "&c設定失敗");
                    e.printStackTrace();
                }
            }

            case "allowedBreakBlocksAroundBed" -> {
                if (args.length < 3) {
                    Msg.send(player, "&c用法: /arena allowedBreakBlocksAroundBed <add/remove>");
                    return;
                }
                switch(args[1]) {
                    case "add" -> {
                        if (args.length < 4) {
                            Msg.send(player, "&c用法: /arena allowedBreakBlocksAroundBed add <模式> <方塊>");
                            return;
                        }

                        Kit kit = Kit.getKit(args[2]);
                        if (kit == null) {
                            Msg.send(player, "&c該模式不存在!");
                            return;
                        }

                        try {
                            Material target = Material.valueOf(args[3]);
                            if (kit.getAllowedBreakBlocksAroundBed().contains(target)) {
                                Msg.send(player, "&c該方塊已在名單內!");
                            }
                            kit.getAllowedBreakBlocksAroundBed().add(target);
                            Msg.send(player, "&a已將該方塊加入名單");
                        } catch (IllegalArgumentException e) {
                            Msg.send(player, "&c方塊不存在");
                            e.printStackTrace();
                        }

                    }
                    case "remove" -> {
                        if (args.length < 4) {
                            Msg.send(player, "&c用法: /arena allowedBreakBlocksAroundBed remove <模式> <方塊>");
                            return;
                        }
                        Kit kit = Kit.getKit(args[2]);
                        if (kit == null) {
                            Msg.send(player, "&c該模式不存在!");
                            return;
                        }

                        try {
                            Material target = Material.valueOf(args[3]);
                            if (!kit.getAllowedBreakBlocksAroundBed().contains(target)) {
                                Msg.send(player, "&c該方塊不在名單內!");
                            }
                            kit.getAllowedBreakBlocksAroundBed().remove(target);
                            Msg.send(player, "&a已將該方塊移出名單");
                        } catch (IllegalArgumentException e) {
                            Msg.send(player, "&c方塊不存在");
                            e.printStackTrace();
                        }
                    }
                }


            }

            case "edit" -> {
                if (args.length < 2) {
                    Msg.send(player, "&c用法: /kit edit <名字>");
                }
                String value = args[1];
                Kit kit = Kit.getKit(value);
                if (kit == null) {
                    Msg.send(player, "&c該模式不存在!");
                    return;
                }
                PracticePlayer practicePlayer = PracticePlayer.get(player.getUniqueId());
                if (practicePlayer.getCurrentGame() != null || practicePlayer.getState() != PracticePlayer.SpawnState.SPAWN) {
                    Msg.send(player, "&c你不在出生點");
                    return;
                }
                practicePlayer.setQueuedKit(kit.getName());
                practicePlayer.setState(PracticePlayer.SpawnState.EDITING);

                player.getInventory().setContents(kit.getInventory());
                player.getInventory().setArmorContents(kit.getArmor());

                Msg.send(player, "&a你現在正在編輯模式" + kit.getName() + "的排版, 使用/kit save來儲存");
            }

            case "save" -> {
                PracticePlayer practicePlayer = PracticePlayer.get(player.getUniqueId());

                if (practicePlayer.getCurrentGame() != null) {
                    Msg.send(player, "&c你正在遊戲中");
                    return;
                }

                if (practicePlayer.getState() != PracticePlayer.SpawnState.EDITING) {
                    Msg.send(player, "&c你沒有在編輯排版");
                    return;
                }

                Kit kit = Kit.getKit(practicePlayer.getQueuedKit());
                if (kit == null) {
                    Msg.send(player, "&c該模式不存在!");
                    practicePlayer.setQueuedKit(null);
                    practicePlayer.setState(PracticePlayer.SpawnState.SPAWN);
                    return;
                }

                kit.setInventory(player.getInventory().getContents());
                kit.setArmor(player.getInventory().getArmorContents());
                kit.save();

                practicePlayer.setQueuedKit(null);
                practicePlayer.setState(PracticePlayer.SpawnState.SPAWN);

                Msg.send(player, "&a已為所有玩家儲存模式 &e" + kit.getName() + " &a的排版");
            }
        }
    }
}
