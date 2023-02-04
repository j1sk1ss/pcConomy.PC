package economy.pcconomy.link;

import economy.pcconomy.cash.Cash;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Manager implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (command.getName().equals("withdraw")) {
            try {
                double amount = Integer.parseInt(args[0]);
                new Cash().WithdrawCash(amount, (Player) sender);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        if (command.getName().equals("change")) {
            double amount = Integer.parseInt(args[0]);
            new Cash().GetChangeToPlayer(amount, (Player) sender);
        }
        if (command.getName().equals("put")) {
            new Cash().PutCash((Player) sender);
        }

        return true;
    }
}
