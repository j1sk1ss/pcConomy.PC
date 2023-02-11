package economy.pcconomy.backend.link;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.npc.NPC;
import economy.pcconomy.backend.town.scripts.TownWorker;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

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
            var amount = Double.parseDouble(args[0]);
            PcConomy.GlobalBank.PlayerWithdrawCash(amount, (Player) sender);
        }

        if (command.getName().equals("put")) {
            PcConomy.GlobalBank.PlayerPutCash(((Player) sender).getInventory().getItemInMainHand(), (Player) sender);
        }

        if (command.getName().equals("createB")) {
            PcConomy.GlobalNPC.CreateBanker((Player) sender);
        }

        if (command.getName().equals("createL")) {
            PcConomy.GlobalNPC.CreateLoaner((Player) sender);
        }

        if (command.getName().equals("createt")) {
            PcConomy.GlobalNPC.CreateTrader((Player) sender);
        }

        if (command.getName().equals("createnpct")) {
            PcConomy.GlobalNPC.CreateNPCTrader((Player) sender);
        }

        if (command.getName().equals("createLic")) {
            PcConomy.GlobalNPC.CreateLicensor((Player) sender);
        }

        if (command.getName().equals("swnpc")) {
            PcConomy.GlobalTownWorker.ChangeNPCStatus(args[0], true);
        }
        return true;
    }
}
