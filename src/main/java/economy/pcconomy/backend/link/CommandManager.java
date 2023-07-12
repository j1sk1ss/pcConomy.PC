package economy.pcconomy.backend.link;

import com.palmergames.bukkit.towny.TownyAPI;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.items.Wallet;
import economy.pcconomy.backend.economy.town.NpcTown;
import economy.pcconomy.backend.npc.traits.*;
import economy.pcconomy.backend.economy.town.scripts.StorageManager;
import economy.pcconomy.frontend.ui.windows.Window;
import economy.pcconomy.frontend.ui.windows.mayor.MayorWindow;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

import static economy.pcconomy.backend.cash.CashManager.giveCashToPlayer;
import static economy.pcconomy.backend.cash.CashManager.takeCashFromInventory;

public class CommandManager implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        switch (command.getName()) {
            case "take_cash"          -> takeCashFromInventory(Double.parseDouble(args[0]), (Player) sender);
            case "create_cash"        -> giveCashToPlayer(Double.parseDouble(args[0]), (Player) sender);
            case "reload_towns"       -> PcConomy.GlobalTownManager.reloadTownObjects();
            case "reload_npc"         -> PcConomy.GlobalNPC.updateNPC();
            case "save_data"          -> PcConomy.SaveData();
            case "put_cash_to_bank"   -> PcConomy.GlobalBank.takeCashFromPlayer(Double.parseDouble(args[0]), (Player) sender);
            case "create_banker"      -> PcConomy.GlobalNPC.createNPC((Player) sender, new Banker());
            case "create_npc_loaner"  -> PcConomy.GlobalNPC.createNPC((Player) sender, new NpcLoaner());
            case "create_loaner"      -> PcConomy.GlobalNPC.createNPC((Player) sender, new Loaner());
            case "create_trader"      -> PcConomy.GlobalNPC.createNPC((Player) sender, new Trader());
            case "create_npc_trader"  -> PcConomy.GlobalNPC.createNPC((Player) sender, new NpcTrader());
            case "create_licensor"    -> PcConomy.GlobalNPC.createNPC((Player) sender, new Licensor());
            case "switch_town_to_npc" -> PcConomy.GlobalTownManager.changeNPCStatus(UUID.fromString(args[0]), true);

            case "town_menu" -> {
                if (!Objects.requireNonNull(TownyAPI.getInstance().getTown((Player) sender)).getMayor().getName().equals((sender).getName())) return true;
                Window.OpenWindow((Player) sender, new MayorWindow());
            }
            case "add_trade_to_town" -> StorageManager.addResource(Material.getMaterial(args[1]), Integer.parseInt(args[2]),
                    ((NpcTown)PcConomy.GlobalTownManager.getTown(UUID.fromString(args[0]))).Storage);
            case "full_info" -> sender.sendMessage("Bank budget: " + PcConomy.GlobalBank.BankBudget + "\n" +
                        "Global VAT: " + PcConomy.GlobalBank.VAT + "\n" +
                        "Registered towns count: " + PcConomy.GlobalTownManager.towns.size() + "\n" +
                        "Borrowers count: " + PcConomy.GlobalBorrowerManager.borrowers.size() + "\n" +
                        "NPC Traders count: " + PcConomy.GlobalNPC.Npc.size());

            case "set_day_bank_budget" -> PcConomy.GlobalBank.setUsefulBudgetPercent(Double.parseDouble(args[0]));
            case "create_wallet"       -> Wallet.giveWallet((Player) sender);
            case "create_shareholder"  -> PcConomy.GlobalNPC.createNPC((Player) sender, new Shareholder());
            case "transfer_share"      -> PcConomy.GlobalShareManager.changeShareOwner(Objects.requireNonNull(
                    TownyAPI.getInstance().getTown(args[0])).getUUID(), (Player) sender, Bukkit.getPlayer(args[1]));

        }

        return true;
    }
}
