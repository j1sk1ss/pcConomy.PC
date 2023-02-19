package economy.pcconomy.backend.link;

import com.palmergames.bukkit.towny.TownyAPI;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.bank.npc.Banker;
import economy.pcconomy.backend.bank.npc.Loaner;
import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.license.npc.Licensor;
import economy.pcconomy.backend.town.objects.scripts.StorageWorker;
import economy.pcconomy.backend.trade.npc.NPCTrader;
import economy.pcconomy.frontend.ui.Window;

import org.bukkit.Material;
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
            var amount = Double.parseDouble(args[0]);
            PcConomy.GlobalBank.PlayerWithdrawCash(amount, (Player) sender);
        }

        if (command.getName().equals("put")) {
            //PcConomy.GlobalBank.PlayerPutCash(((Player) sender).getInventory().getItemInMainHand(), (Player) sender);
            PcConomy.SaveData();
        }

        if (command.getName().equals("createB")) {
            PcConomy.GlobalNPC.CreateNPC((Player) sender, new Banker());
        }

        if (command.getName().equals("createL")) {
            PcConomy.GlobalNPC.CreateNPC((Player) sender, new Loaner());
        }

        if (command.getName().equals("createt")) {
            PcConomy.GlobalNPC.BuyTrader((Player) sender);
        }

        if (command.getName().equals("createnpct")) {
            PcConomy.GlobalNPC.CreateNPC((Player) sender, new NPCTrader());
        }

        if (command.getName().equals("createLic")) {
            PcConomy.GlobalNPC.CreateNPC((Player) sender, new Licensor());
        }

        if (command.getName().equals("swnpc")) {
            PcConomy.GlobalTownWorker.ChangeNPCStatus(args[0], true);
        }

        if (command.getName().equals("tmenu")) {
            if (!TownyAPI.getInstance().getTown((Player) sender).getMayor().getName().equals((sender).getName())) return true;
            Window.OpenMayorWindow((Player) sender);
        }

        if (command.getName().equals("addTrade")) {
            StorageWorker.addResource(Material.getMaterial(args[1]), Integer.parseInt(args[2]),
                    PcConomy.GlobalTownWorker.GetTownObject(args[0]).Storage);
        }
        return true;
    }
}
