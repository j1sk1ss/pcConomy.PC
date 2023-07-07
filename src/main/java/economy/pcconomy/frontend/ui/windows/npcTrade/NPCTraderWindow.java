package economy.pcconomy.frontend.ui.windows.npcTrade;

import com.palmergames.bukkit.towny.TownyAPI;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.economy.town.NpcTown;
import economy.pcconomy.backend.scripts.items.Item;
import economy.pcconomy.backend.scripts.items.ItemManager;

import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Objects;

public class NPCTraderWindow {
    public static Inventory generateWindow(Player player, NPC trader) {
        var town = PcConomy.GlobalTownManager.getTown(Objects.requireNonNull(TownyAPI.getInstance()
                .getTown(trader.getStoredLocation())).getName());
        if (town == null) return null;

        var window = Bukkit.createInventory(player, 54, Component.text("Магазин " +
                town.getName() + " " + trader.getId()));

        var townStorage = ((NpcTown)town).Storage;
        for (var item : townStorage)
            window.addItem(ItemManager.setLore(item, String.join("\n", ItemManager.getLore(item))));

        return window;
    }
}
