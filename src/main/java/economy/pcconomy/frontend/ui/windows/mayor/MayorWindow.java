package economy.pcconomy.frontend.ui.windows.mayor;

import com.palmergames.bukkit.towny.TownyAPI;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.scripts.CashWorker;
import economy.pcconomy.backend.npc.NPC;
import economy.pcconomy.backend.scripts.ItemWorker;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MayorWindow {
    public static Inventory GetMayorWindow(Player mayor) {
        var town = PcConomy.GlobalTownWorker.
                GetTownObject(TownyAPI.getInstance().getTown(mayor.getLocation()).getName());
        var window = Bukkit.createInventory(mayor, 27, "Меню " + town.TownName);

        window.setItem(0, ItemWorker.SetName(ItemWorker.SetLore(new ItemStack(Material.PURPLE_WOOL),
                NPC.traderCost + CashWorker.currencySigh), "Установить торговца"));
        window.setItem(1, ItemWorker.SetName(ItemWorker.SetLore(new ItemStack(Material.PURPLE_WOOL),
                NPC.traderCost + CashWorker.currencySigh), "Установить кредитора"));

        return window;
    }

}
