package economy.pcconomy.link;

import economy.pcconomy.cash.Cash;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Manager implements CommandExecutor { // Тестовый класс для отладки работы валюты
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        return true;
    }
}
