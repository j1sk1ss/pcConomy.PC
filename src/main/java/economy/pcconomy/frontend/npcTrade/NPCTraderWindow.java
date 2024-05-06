package economy.pcconomy.frontend.npcTrade;

import java.util.Arrays;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.j1sk1ss.itemmanager.manager.Manager;
import org.j1sk1ss.menuframework.objects.MenuSizes;
import org.j1sk1ss.menuframework.objects.MenuWindow;
import org.j1sk1ss.menuframework.objects.interactive.components.ClickArea;
import org.j1sk1ss.menuframework.objects.interactive.components.Panel;

import com.palmergames.bukkit.towny.TownyAPI;

import economy.pcconomy.backend.economy.town.NpcTown;
import economy.pcconomy.backend.economy.town.manager.TownManager;
import lombok.experimental.ExtensionMethod;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.Component;


@ExtensionMethod({Manager.class, TownManager.class})
public class NPCTraderWindow {
    @SuppressWarnings("deprecation")
    public static MenuWindow NpcTradeWindow = new MenuWindow(
        Arrays.asList(
            new Panel(
                Arrays.asList(
                    new ClickArea(0, 53,
                        (event) -> {
                            var player      = (Player)event.getWhoClicked();
                            var currentItem = event.getCurrentItem();
                            var title       = event.getView().getTitle();
                            var town        = (NpcTown)TownyAPI.getInstance().getTown(title.split(" ")[1]).getTown();
                            if (town == null) return;

                            if (!player.getInventory().contains(currentItem))
                                if (event.isLeftClick()) {
                                    town.buyResourceFromStorage(currentItem, player);
                                    player.openInventory(NPCTraderWindow.generateWindow(player, CitizensAPI.getNPCRegistry().getById(Integer.parseInt(title.split(" ")[2]))));
                                }
                                else {
                                    town.generateLocalPrices();
                                    town.sellResourceToStorage(player.getInventory().getItemInMainHand(), player);
                                }
                        })
                ), "Магазин", MenuSizes.SixLines
            )
        )   
    );

    public static Inventory generateWindow(Player player, NPC trader) {
        var town = (NpcTown)TownyAPI.getInstance().getTown(trader.getStoredLocation()).getTown();
        if (town == null) return null;

        var window = Bukkit.createInventory(player, 54, Component.text("Магазин " +
                Objects.requireNonNull(TownyAPI.getInstance().getTown(town.getUUID())).getName() + " " + trader.getId()));

        var townStorage = ((NpcTown)town).Storage;
        for (var item : townStorage)
            window.addItem(item.setLore(String.join("\n", item.getLoreLines())));

        return window;
    }
}
