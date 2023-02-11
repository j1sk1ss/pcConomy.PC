package economy.pcconomy.frontend.ui.windows.npcTrade;

import com.palmergames.bukkit.towny.TownyAPI;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.scripts.ItemWorker;
import economy.pcconomy.backend.town.scripts.TownWorker;

import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class NPCTraderWindow {
    public static Inventory GetNPCTraderWindow(Player player, NPC trader) {
        var town = PcConomy.GlobalTownWorker.
                GetTownObject(TownyAPI.getInstance().getTown(trader.getStoredLocation()).getName());
        var window = Bukkit.createInventory(player, 54, "Магазин " + town.Town.getName());

        if (town == null) return null;
        var townStorage = town.Storage;

        for (ItemStack item :
             townStorage) {
            window.addItem(ItemWorker.SetLore(new ItemStack(item.getType()),
                    ItemWorker.GetLore(item).get(0) + "\n" +  ItemWorker.GetLore(item).get(1)));
        }

        return window;
    }
}
