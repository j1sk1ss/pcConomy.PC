package economy.pcconomy.backend.link;

import com.palmergames.bukkit.towny.TownyAPI;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.economy.town.NpcTown;
import economy.pcconomy.backend.npc.traits.Banker;
import economy.pcconomy.backend.npc.traits.Loaner;
import economy.pcconomy.backend.npc.traits.NpcLoaner;
import economy.pcconomy.backend.npc.traits.Licensor;
import economy.pcconomy.backend.license.objects.LicenseType;
import economy.pcconomy.backend.npc.NpcManager;
import economy.pcconomy.backend.economy.town.scripts.StorageManager;
import economy.pcconomy.backend.npc.traits.NpcTrader;
import economy.pcconomy.frontend.ui.windows.Window;

import economy.pcconomy.frontend.ui.windows.mayor.MayorWindow;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CommandManager implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (command.getName().equals("take_cash"))
            new CashManager().takeCashFromInventory(Double.parseDouble(args[0]), (Player) sender);

        if (command.getName().equals("create_cash"))
            new CashManager().giveCashToPlayer(Double.parseDouble(args[0]), (Player) sender);

        if (command.getName().equals("reload_towns"))
            PcConomy.GlobalTownManager.reloadTownObjects();

        if (command.getName().equals("reload_npc"))
            PcConomy.GlobalNPC.updateNPC();

        if (command.getName().equals("save_data"))
            PcConomy.SaveData();

        if (command.getName().equals("put_cash_to_bank"))
            PcConomy.GlobalBank.takeCashFromPlayer(Double.parseDouble(args[0]), (Player) sender);

        if (command.getName().equals("create_banker"))
            PcConomy.GlobalNPC.createNPC((Player) sender, new Banker());

        if (command.getName().equals("create_npc_loaner"))
            PcConomy.GlobalNPC.createNPC((Player) sender, new NpcLoaner());

        if (command.getName().equals("create_loaner"))
            PcConomy.GlobalNPC.createNPC((Player) sender, new Loaner());

        if (command.getName().equals("create_trader"))
            PcConomy.GlobalNPC.buyNPC((Player) sender, LicenseType.Market, NpcManager.traderCost);

        if (command.getName().equals("create_npc_trader"))
            PcConomy.GlobalNPC.createNPC((Player) sender, new NpcTrader());

        if (command.getName().equals("create_licensor"))
            PcConomy.GlobalNPC.createNPC((Player) sender, new Licensor());

        if (command.getName().equals("switch_town_to_npc"))
            PcConomy.GlobalTownManager.changeNPCStatus(args[0], true);

        if (command.getName().equals("town_menu")) {
            if (!Objects.requireNonNull(TownyAPI.getInstance().getTown((Player) sender)).getMayor().getName().equals((sender).getName())) return true;
            Window.OpenWindow((Player) sender, new MayorWindow());
        }

        if (command.getName().equals("add_trade_to_town"))
            StorageManager.addResource(Material.getMaterial(args[1]), Integer.parseInt(args[2]),
                    ((NpcTown)PcConomy.GlobalTownManager.getTown(args[0])).Storage);

        if (command.getName().equals("full_info")) {
            sender.sendMessage("Bank budget: " + PcConomy.GlobalBank.BankBudget + "\n" +
                    "Global VAT: " + PcConomy.GlobalBank.VAT + "\n" +
                    "Registered towns count: " + PcConomy.GlobalTownManager.towns.size() + "\n" +
                    "Borrowers count: " + PcConomy.GlobalBorrowerManager.borrowers.size() + "\n" +
                    "NPC Traders count: " + PcConomy.GlobalNPC.Traders.size());
        }

        if (command.getName().equals("set_day_bank_budget"))
            PcConomy.GlobalBank.setUsefulBudgetPercent(Double.parseDouble(args[0]));

        return true;
    }
}
