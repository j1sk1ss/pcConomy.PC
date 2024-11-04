package economy.pcconomy.backend.link;

import com.palmergames.bukkit.towny.TownyAPI;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.cash.Wallet;
import economy.pcconomy.backend.npc.traits.*;
import economy.pcconomy.backend.npc.NpcManager;
import economy.pcconomy.backend.economy.town.towns.Storage;

import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.inventory.ItemStack;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.j1sk1ss.itemmanager.manager.Manager;
import economy.pcconomy.frontend.MayorManagerWindow;

import org.jetbrains.annotations.NotNull;
import lombok.experimental.ExtensionMethod;

import java.util.*;


@ExtensionMethod({Manager.class, Cash.class, Storage.class})
public class CommandManager implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        switch (command.getName()) {
            case "take_cash"          -> ((Player)sender).takeCashFromPlayer(Double.parseDouble(args[0]), true);
            case "create_cash"        -> ((Player)sender).giveCashToPlayer(Double.parseDouble(args[0]), true);
            case "reload_npc"         -> NpcManager.reloadNPC();
            case "put_cash2bank"      -> PcConomy.getInstance().bankManager.getBank().takeCashFromPlayer(Double.parseDouble(args[0]), (Player)sender);
            case "create_banker"      -> NpcManager.createNPC((Player)sender, new Banker());
            case "create_npc_loaner"  -> NpcManager.createNPC((Player)sender, new NpcLoaner());
            case "create_trader"      -> NpcManager.createNPC((Player)sender, new Trader());
            case "create_npc_trader"  -> NpcManager.createNPC((Player)sender, new NpcTrader());
            case "create_licensor"    -> NpcManager.createNPC((Player)sender, new Licensor());
            case "create_shareholder" -> NpcManager.createNPC((Player) sender, new Shareholder());
            
            case "town_menu" -> {
                if (!Objects.requireNonNull(TownyAPI.getInstance().getTown((Player)sender)).getMayor().getName().equals((sender).getName())) return true;
                MayorManagerWindow.generateWindow((Player)sender);
            }

            case "full_info" -> sender.sendMessage(
                    "Bank budget: " + PcConomy.getInstance().bankManager.getBank().getBudget() + "$\n" +
                        "Global VAT: " + PcConomy.getInstance().bankManager.getBank().getVat() * 100 + "%\n" +
                        "Deposit percent: " + PcConomy.getInstance().bankManager.getBank().getDepositPercent() * 100 + "%\n" +
                        "Borrowers count: " + PcConomy.getInstance().borrowerManager.borrowers.size() + "\n"
            );

            case "bank_new_day"        -> PcConomy.getInstance().bankManager.getBank().newDay();
            case "set_day_bank_budget" -> PcConomy.getInstance().bankManager.getBank().setDayWithdrawBudget((Double.parseDouble(args[0])));
            case "create_wallet"       -> new Wallet().giveWallet((Player) sender);
            case "global_market_prices" -> {
                StringBuilder message = new StringBuilder();
                var prices = new HashMap<ItemStack, Double>();

                for (var trader : CitizensAPI.getNPCRegistry()) {
                    if (trader.hasTrait(Trader.class)) {
                        var trait = trader.getOrAddTrait(Trader.class);
                        for (var resource : trait.getStorage()) {
                            if (!prices.containsKey(resource)) prices.put(resource, resource.getDoubleFromContainer("item-price"));
                            else prices.put(resource, ((prices.get(resource) + resource.getDoubleFromContainer("item-price")) / 2));
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
