package economy.pcconomy.frontend.ui.windows.npcTrade;

import com.palmergames.bukkit.towny.TownyAPI;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.scripts.ItemWorker;

import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class NPCTraderWindow {
    public static Inventory GetWindow(Player player, NPC trader) {
        var town = PcConomy.GlobalTownWorker.
                GetTownObject(TownyAPI.getInstance().getTown(trader.getStoredLocation()).getName());
        if (town == null) return null;

        var window = Bukkit.createInventory(player, 54,
                Component.text("Магазин " + town.TownName + " " + trader.getId()));

        var townStorage = town.Storage;

        for (ItemStack item :
             townStorage) {
            window.addItem(ItemWorker.SetLore(new ItemStack(item.getType()),
                    String.join("\n", ItemWorker.GetLore(item))));
        }

        return window;
    }
}
