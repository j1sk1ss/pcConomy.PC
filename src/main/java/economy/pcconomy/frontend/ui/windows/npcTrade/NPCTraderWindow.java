package economy.pcconomy.frontend.ui.windows.npcTrade;

import com.palmergames.bukkit.towny.TownyAPI;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.scripts.ItemWorker;

import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class NPCTraderWindow {
    public static Inventory GetNPCTraderWindow(Player player, NPC trader) {
        var town = PcConomy.GlobalTownWorker.
                GetTownObject(TownyAPI.getInstance().getTown(trader.getStoredLocation()).getName());
        var window = Bukkit.createInventory(player, 54, "Магазин " + town.TownName + " " + trader.getId());

        if (town == null) return null;
        var townStorage = town.Storage;

        for (ItemStack item :
             townStorage) {
            window.addItem(ItemWorker.SetLore(new ItemStack(item.getType()),
                    String.join("\n", ItemWorker.GetLore(item))));
        }

        return window;
    }
}
