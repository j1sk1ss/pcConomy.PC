package economy.pcconomy.backend.link;

import com.palmergames.bukkit.towny.TownyAPI;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.cash.Wallet;
import economy.pcconomy.backend.economy.town.towns.NpcTown;
import economy.pcconomy.backend.economy.town.TownManager;
import economy.pcconomy.backend.economy.town.towns.Storage;
import economy.pcconomy.backend.npc.NpcManager;
import economy.pcconomy.backend.npc.traits.*;

import economy.pcconomy.frontend.mayor.MayorManagerWindow;
import net.citizensnpcs.api.CitizensAPI;
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


@ExtensionMethod({Manager.class, Cash.class, Storage.class, TownManager.class})
public class CommandManager implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        switch (command.getName()) {
            case "take_cash"          -> ((Player)sender).takeCashFromPlayer(Double.parseDouble(args[0]), true);
            case "create_cash"        -> ((Player)sender).giveCashToPlayer(Double.parseDouble(args[0]), true);
            case "reload_towns"       -> TownManager.reloadTownObjects();
            case "reload_npc"         -> NpcManager.reloadNPC();
            case "put_cash2bank"      -> PcConomy.GlobalBank.getBank().takeCashFromPlayer(Double.parseDouble(args[0]), (Player)sender);
            case "create_banker"      -> NpcManager.createNPC((Player)sender, new Banker());
            case "create_npc_loaner"  -> NpcManager.createNPC((Player)sender, new NpcLoaner());
            case "create_trader"      -> NpcManager.createNPC((Player)sender, new Trader());
            case "create_npc_trader"  -> NpcManager.createNPC((Player)sender, new NpcTrader());
            case "create_licensor"    -> NpcManager.createNPC((Player)sender, new Licensor());
            case "create_shareholder" -> NpcManager.createNPC((Player) sender, new Shareholder());
            case "switch_town2npc"    -> Objects.requireNonNull(TownyAPI.getInstance().getTown(((Player) sender).getLocation())).changeNPCStatus(true);
            case "switch_town2player" -> Objects.requireNonNull(TownyAPI.getInstance().getTown(((Player) sender).getLocation())).changeNPCStatus(false);
            
            case "town_menu" -> {
                if (!Objects.requireNonNull(TownyAPI.getInstance().getTown((Player)sender)).getMayor().getName().equals((sender).getName())) return true;
                MayorManagerWindow.generateWindow((Player)sender);
            }

            case "add_trade2town" -> ((NpcTown)UUID.fromString(args[0]).getTown()).Storage
                    .addResource(Material.getMaterial(args[1]), Integer.parseInt(args[2]));

            case "full_info" -> sender.sendMessage("Bank budget: " + PcConomy.GlobalBank.getBank().getBudget() + "$\n" +
                        "Global VAT: " + PcConomy.GlobalBank.getBank().getVat() + "%\n" +
                        "Deposit percent: " + PcConomy.GlobalBank.getBank().getDepositPercent() + "%\n" +
                        "Registered towns count: " + PcConomy.GlobalTown.Towns.size() + "\n" +
                        "Borrowers count: " + PcConomy.GlobalBorrower.borrowers.size() + "\n");

            case "set_day_bank_budget" -> PcConomy.GlobalBank.getBank().setDayWithdrawBudget((Double.parseDouble(args[0])));
            case "create_wallet"       -> new Wallet().giveWallet((Player) sender);
            case "shares_rate" -> {
                StringBuilder message = new StringBuilder();
                for (var town : PcConomy.GlobalShare.Shares.keySet())
                    if (TownyAPI.getInstance().getTown(town) != null)
                        message.append(
                                Objects.requireNonNull(TownyAPI.getInstance().getTown(town)).getName()
                        ).append(": ").append(
                                PcConomy.GlobalShare.getMedianSharePrice(town) / PcConomy.GlobalShare.getTownShares(town).size()
                        ).append(Cash.currencySigh);

                sender.sendMessage(message.toString());
            }

            case "global_market_prices" -> {
                StringBuilder message = new StringBuilder();
                var prices = new HashMap<ItemStack, Double>();

                for (var trader : CitizensAPI.getNPCRegistry()) {
                    if (trader.hasTrait(Trader.class)) {
                        var trait = trader.getOrAddTrait(Trader.class);
                        for (var resource : trait.Storage) {
                            if (!prices.containsKey(resource)) prices.put(resource, resource.getDoubleFromContainer("item-price"));
                            else prices.put(resource, (prices.get(resource) + resource.getDoubleFromContainer("item-price")) / 2);
                        }
                    }
                }

                for (var resource : prices.keySet())
                    message.append("Товар: ").append(resource).append(", цена: ").append(prices.get(resource)).append(Cash.currencySigh);

                sender.sendMessage(message.toString());
            }
        }

        return true;
    }
}
