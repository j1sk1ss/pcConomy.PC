package economy.pcconomy.frontend.windows.npcTrade;

import com.palmergames.bukkit.towny.TownyAPI;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.economy.town.NpcTown;

import lombok.experimental.ExtensionMethod;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.j1sk1ss.itemmanager.manager.Manager;
import org.j1sk1ss.menuframework.objects.MenuSizes;
import org.j1sk1ss.menuframework.objects.MenuWindow;
import org.j1sk1ss.menuframework.objects.interactive.components.ClickArea;
import org.j1sk1ss.menuframework.objects.interactive.components.Panel;

import java.util.Arrays;
import java.util.Objects;


@ExtensionMethod({Manager.class})
public class NPCTraderWindow {
    @SuppressWarnings("deprecation")
    public static MenuWindow NpcTradeWindow = new MenuWindow(
        Arrays.asList(
            new Panel(
                Arrays.asList(
                    new ClickArea(0, 53,
                        (event) -> {
                            var player = (Player)event.getWhoClicked();
                            var currentItem = event.getCurrentItem();
                            var title = event.getView().getTitle();
                            var town = TownyAPI.getInstance().getTown(title.split(" ")[1]);

                            if (!player.getInventory().contains(currentItem))
                                if (event.isLeftClick()) {
                                    ((NpcTown)(PcConomy.GlobalTownManager.getTown(Objects.requireNonNull(town).getUUID()))).buyResourceFromStorage(currentItem, player);
                                    player.openInventory(Objects.requireNonNull(NPCTraderWindow.generateWindow(player, CitizensAPI.getNPCRegistry().getById(Integer.parseInt(title.split(" ")[2])))));
                                }
                        })
                ), "Магазин", MenuSizes.SixLines
            )
        )   
    );

    public static Inventory generateWindow(Player player, NPC trader) {
        var town = PcConomy.GlobalTownManager.getTown(Objects.requireNonNull(TownyAPI.getInstance()
                .getTown(trader.getStoredLocation())).getUUID());
        if (town == null) return null;

        var window = Bukkit.createInventory(player, 54, Component.text("Магазин " +
                Objects.requireNonNull(TownyAPI.getInstance().getTown(town.getUUID())).getName() + " " + trader.getId()));

        var townStorage = ((NpcTown)town).Storage;
        for (var item : townStorage)
            window.addItem(item.setLore(String.join("\n", item.getLoreLines())));

        return window;
    }
}
