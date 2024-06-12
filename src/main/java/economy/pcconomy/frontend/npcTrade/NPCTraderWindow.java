package economy.pcconomy.frontend.npcTrade;

import java.util.List;
import java.util.Objects;

import net.potolotcraft.gorodki.GorodkiUniverse;
import net.potolotcraft.gorodki.objects.goroda.NPCGorod;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.j1sk1ss.itemmanager.manager.Manager;
import org.j1sk1ss.menuframework.objects.MenuSizes;
import org.j1sk1ss.menuframework.objects.MenuWindow;
import org.j1sk1ss.menuframework.objects.interactive.components.ClickArea;
import org.j1sk1ss.menuframework.objects.interactive.components.Panel;

import com.palmergames.bukkit.towny.TownyAPI;

import economy.pcconomy.backend.economy.town.towns.NpcTown;
import economy.pcconomy.backend.economy.town.TownManager;
import lombok.experimental.ExtensionMethod;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.Component;


@ExtensionMethod({Manager.class})
public class NPCTraderWindow {
    @SuppressWarnings("deprecation")
    public static MenuWindow NpcTradeWindow = new MenuWindow(
        List.of(
            new Panel(
                List.of(
                    new ClickArea(0, 53,
                        (event) -> {
                            var player = (Player) event.getWhoClicked();
                            var currentItem = event.getCurrentItem();
                            var title = event.getView().getTitle();
                            var town = GorodkiUniverse.getInstance().getNPCGorod(TownyAPI.getInstance().getTown(title.split(" ")[1]));
                            if (town == null) return;

                            if (!player.getInventory().contains(currentItem))
                                if (event.isLeftClick()) {
                                    assert currentItem != null;
                                    town.buyResourceFromStorage(currentItem, player);
                                    player.openInventory(
                                        Objects.requireNonNull(
                                            NPCTraderWindow.generateWindow(
                                                player, CitizensAPI.getNPCRegistry().getById(Integer.parseInt(title.split(" ")[2]))
                                            )
                                        )
                                    );
                                } else {
                                    town.generateLocalPrices();
                                    town.sellResource2Storage(player.getInventory().getItemInMainHand(), player);
                                }
                        })
                ), "ვМагазин", MenuSizes.SixLines
            )
        )
    );

    public static Inventory generateWindow(Player player, NPC trader) {
        var town = GorodkiUniverse.getInstance().getNPCGorod(TownyAPI.getInstance().getTown(trader.getStoredLocation()));
        if (town == null) return null;

        var window = Bukkit.createInventory(
                player, 54, Component.text("ვМагазин " +
                Objects.requireNonNull(TownyAPI.getInstance().getTown(town.getUUID())).getName() + " " + trader.getId())
        );

        for (var item : town.getStorage())
            window.addItem(item.setLore(String.join("\n", item.getLoreLines())));

        return window;
    }
}
