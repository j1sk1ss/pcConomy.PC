package economy.pcconomy.frontend.ui.windows;

import com.palmergames.bukkit.towny.TownyAPI;
import economy.pcconomy.backend.cash.scripts.CashWorker;
import economy.pcconomy.backend.scripts.ItemWorker;
import economy.pcconomy.backend.town.objects.scripts.StorageWorker;
import economy.pcconomy.backend.town.scripts.TownWorker;

import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class NPCTraderWindow {
    public static Inventory GetNPCTraderWindow(Player player, NPC trader) {
        var town = TownWorker
                .GetTownObject(TownyAPI.getInstance().getTown(trader.getStoredLocation()).getName());
        var window = Bukkit.createInventory(player, 27, "Магазин " + town.Town.getName());

        if (town == null) return null;
        var townStorage = town.Storage;

        for (ItemStack item :
             townStorage) {
            window.addItem(ItemWorker.SetLore(StorageWorker.getPercentOfResource(.4d, item),
                    town.Prices.get(item) + CashWorker.currencySigh));
        }

        return window;
    }
}
