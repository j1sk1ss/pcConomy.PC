package economy.pcconomy.link;

import economy.pcconomy.PcConomy;
import economy.pcconomy.cash.Cash;
import economy.pcconomy.town.Town;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Manager implements CommandExecutor { // Тестовый класс для отладки работы плагина. Кидайте сюда команды
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (command.getName().equals("create")) {
            var amount = Double.parseDouble(args[0]);
            new Cash().GiveCashToPlayer(amount, (Player) sender);
        }

        if (command.getName().equals("take")) {
            var amount = Double.parseDouble(args[0]);
            new Cash().TakeCashFromInventory(amount, (Player) sender);
        }

        if (command.getName().equals("withdraw")) {
            var townName = args[0];
            var amount = Double.parseDouble(args[1]);

            PcConomy.GlobalBank.WithdrawCash(amount, (Player) sender, townName);
        }

        if (command.getName().equals("put")) {
            var townName = args[0];

            PcConomy.GlobalBank.PutCash(((Player) sender).getInventory().getItemInMainHand(), (Player) sender, townName);
        }
        return true;
    }
}
