package economy.pcconomy.frontend.ui.windows.npcTrade;

import com.palmergames.bukkit.towny.TownyAPI;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.economy.town.NpcTown;

import lombok.experimental.ExtensionMethod;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.j1sk1ss.itemmanager.manager.Manager;

import java.util.Objects;


@ExtensionMethod({Manager.class})
public class NPCTraderWindow {
    public static Inventory generateWindow(Player player, NPC trader) {
        var town = PcConomy.GlobalTownManager.getTown(Objects.requireNonNull(TownyAPI.getInstance()
                .getTown(trader.getStoredLocation())).getUUID());
        if (town == null) return null;

        var window = Bukkit.createInventory(player, 54, Component.text("Магазин " +
                Objects.requireNonNull(TownyAPI.getInstance().getTown(town.getUUID())).getName() + " " + trader.getId()));

        var townStorage = ((NpcTown)town).Storage.StorageBody;
        for (var item : townStorage)
            window.addItem(item.setLore(String.join("\n", item.getLoreLines())));

        return window;
    }
}
