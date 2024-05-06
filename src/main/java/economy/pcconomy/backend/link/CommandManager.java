package economy.pcconomy.backend.link;

import com.palmergames.bukkit.towny.TownyAPI;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.cash.Wallet;
import economy.pcconomy.backend.economy.town.NpcTown;
import economy.pcconomy.backend.economy.town.manager.TownManager;
import economy.pcconomy.backend.economy.town.objects.StorageManager;
import economy.pcconomy.backend.npc.objects.TraderObject;
import economy.pcconomy.backend.npc.traits.*;

import economy.pcconomy.frontend.mayor.MayorManagerWindow;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.j1sk1ss.itemmanager.manager.Manager;

import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

import java.util.*;


@ExtensionMethod({Manager.class, CashManager.class, StorageManager.class, TownManager.class})
public class CommandManager implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        switch (command.getName()) {
            case "take_cash"          -> ((Player)sender).takeCashFromPlayer(Double.parseDouble(args[0]), true);
            case "create_cash"        -> ((Player)sender).giveCashToPlayer(Double.parseDouble(args[0]), true);
            case "reload_towns"       -> TownManager.reloadTownObjects();
            case "reload_npc"         -> PcConomy.GlobalNPC.reloadNPC();
            case "put_cash2bank"      -> PcConomy.GlobalBank.takeCashFromPlayer(Double.parseDouble(args[0]), (Player)sender);
            case "create_banker"      -> PcConomy.GlobalNPC.createNPC((Player)sender, new Banker());
            case "create_npc_loaner"  -> PcConomy.GlobalNPC.createNPC((Player)sender, new NpcLoaner());
            case "create_trader"      -> PcConomy.GlobalNPC.createNPC((Player)sender, new Trader());
            case "create_npc_trader"  -> PcConomy.GlobalNPC.createNPC((Player)sender, new NpcTrader());
            case "create_licensor"    -> PcConomy.GlobalNPC.createNPC((Player)sender, new Licensor());
            case "create_shareholder" -> PcConomy.GlobalNPC.createNPC((Player) sender, new Shareholder());
            case "switch_town2npc"    -> TownyAPI.getInstance().getTown(((Player)sender).getLocation()).changeNPCStatus(true);
            case "switch_town2player" -> TownyAPI.getInstance().getTown(((Player)sender).getLocation()).changeNPCStatus(false);
            
            case "town_menu" -> {
                if (!Objects.requireNonNull(TownyAPI.getInstance().getTown((Player)sender)).getMayor().getName().equals((sender).getName())) return true;
                MayorManagerWindow.generateWindow((Player)sender);
            }

            case "add_trade2town" -> ((NpcTown)UUID.fromString(args[0]).getTown()).Storage
                    .addResource(Material.getMaterial(args[1]), Integer.parseInt(args[2]));

            case "full_info" -> sender.sendMessage("Bank budget: " + PcConomy.GlobalBank.BankBudget + "$\n" +
                        "Global VAT: " + PcConomy.GlobalBank.VAT + "%\n" +
                        "Deposit percent: " + PcConomy.GlobalBank.DepositPercent + "%\n" +
                        "Registered towns count: " + PcConomy.GlobalTownManager.Towns.size() + "\n" +
                        "Borrowers count: " + PcConomy.GlobalBorrowerManager.borrowers.size() + "\n" +
                        "NPC Traders count: " + PcConomy.GlobalNPC.Npc.size());

            case "set_day_bank_budget" -> PcConomy.GlobalBank.DayWithdrawBudget = (Double.parseDouble(args[0]));
            case "create_wallet"       -> new Wallet().giveWallet((Player) sender);
            case "shares_rate" -> {
                var message = "";
                for (var town : PcConomy.GlobalShareManager.Shares.keySet())
                    if (TownyAPI.getInstance().getTown(town) != null)
                        message += Objects.requireNonNull(TownyAPI.getInstance().getTown(town)).getName() + ": " +
                                (PcConomy.GlobalShareManager.getMedianSharePrice(town) / PcConomy.GlobalShareManager.getTownShares(town).size())
                                + CashManager.currencySigh;

                sender.sendMessage(message);
            }

            case "global_market_prices" -> {
                var message = "";
                var prices = new HashMap<ItemStack, Double>();

                for (var trader : PcConomy.GlobalNPC.Npc.keySet())
                    if (PcConomy.GlobalNPC.Npc.get(trader) instanceof TraderObject currentTrader)
                        for (var resource : currentTrader.Storage)
                            if (!prices.containsKey(resource))
                                prices.put(resource, resource.getDoubleFromContainer("item-price"));
                            else prices.put(resource,
                                    (prices.get(resource) + resource.getDoubleFromContainer("item-price")) / 2);

                for (var resource : prices.keySet())
                    message += "Товар: " + resource + ", цена: " + prices.get(resource) + CashManager.currencySigh;

                sender.sendMessage(message);
            }
        }

        return true;
    }
}
