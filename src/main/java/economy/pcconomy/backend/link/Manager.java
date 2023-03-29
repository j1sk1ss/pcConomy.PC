package economy.pcconomy.backend.link;

import com.palmergames.bukkit.towny.TownyAPI;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.bank.npc.Banker;
import economy.pcconomy.backend.bank.npc.NPCLoaner;
import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.license.npc.Licensor;
import economy.pcconomy.backend.license.objects.LicenseType;
import economy.pcconomy.backend.npc.NPC;
import economy.pcconomy.backend.town.objects.scripts.StorageWorker;
import economy.pcconomy.backend.trade.npc.NPCTrader;
import economy.pcconomy.frontend.ui.Window;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

public class Manager implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (command.getName().equals("take_cash"))
            new Cash().TakeCashFromInventory(Double.parseDouble(args[0]), (Player) sender);

        if (command.getName().equals("create_cash"))
            PcConomy.GlobalBank.GiveCashToPlayer(Double.parseDouble(args[0]), (Player) sender);

        if (command.getName().equals("reload_towns"))
            PcConomy.GlobalTownWorker.ReloadTownObjects();

        if (command.getName().equals("reload_npc"))
            PcConomy.GlobalNPC.UpdateNPC();

        if (command.getName().equals("save_data"))
            PcConomy.SaveData();

        if (command.getName().equals("put_cash_to_bank"))
            PcConomy.GlobalBank.TakeCashFromPlayer(Double.parseDouble(args[0]), (Player) sender);

        if (command.getName().equals("create_banker"))
            PcConomy.GlobalNPC.CreateNPC((Player) sender, new Banker());

        if (command.getName().equals("create_loaner"))
            PcConomy.GlobalNPC.CreateNPC((Player) sender, new NPCLoaner());

        if (command.getName().equals("create_trader"))
            PcConomy.GlobalNPC.BuyNPC((Player) sender, LicenseType.Market, NPC.traderCost);

        if (command.getName().equals("create_npc_trader"))
            PcConomy.GlobalNPC.CreateNPC((Player) sender, new NPCTrader());

        if (command.getName().equals("create_licensor"))
            PcConomy.GlobalNPC.CreateNPC((Player) sender, new Licensor());

        if (command.getName().equals("switch_town_to_npc"))
            PcConomy.GlobalTownWorker.ChangeNPCStatus(args[0], true);

        if (command.getName().equals("town_menu")) {
            if (!TownyAPI.getInstance().getTown((Player) sender).getMayor().getName().equals((sender).getName())) return true;
            Window.OpenMayorWindow((Player) sender);
        }

        if (command.getName().equals("add_trade_to_town"))
            StorageWorker.AddResource(Material.getMaterial(args[1]), Integer.parseInt(args[2]),
                    PcConomy.GlobalTownWorker.GetTownObject(args[0]).Storage);

        return true;
    }
}
