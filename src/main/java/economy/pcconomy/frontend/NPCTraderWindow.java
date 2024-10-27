package economy.pcconomy.frontend;

import java.util.List;
import java.util.Objects;

import net.potolotcraft.gorodki.GorodkiUniverse;
import org.bukkit.entity.Player;
import org.j1sk1ss.itemmanager.manager.Manager;
import org.j1sk1ss.menuframework.objects.MenuSizes;
import org.j1sk1ss.menuframework.objects.MenuWindow;
import org.j1sk1ss.menuframework.objects.interactive.components.ClickArea;
import org.j1sk1ss.menuframework.objects.interactive.components.ItemArea;
import org.j1sk1ss.menuframework.objects.interactive.components.Panel;

import com.palmergames.bukkit.towny.TownyAPI;

import lombok.experimental.ExtensionMethod;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.j1sk1ss.menuframework.objects.nonInteractive.Margin;


@ExtensionMethod({Manager.class})
public class NPCTraderWindow {
    private static final MenuWindow NpcTradeWindow = new MenuWindow(
        List.of(
            new Panel(
                List.of(
                    new ClickArea(new Margin(0, 0, 4, 8),
                        (event, menu) -> {
                            var player = (Player) event.getWhoClicked();
                            var currentItem = event.getCurrentItem();
                            var title = Utils.getInventoryTitle(event);
                            var town = GorodkiUniverse.getInstance().getNPCGorod(TownyAPI.getInstance().getTown(title.split(" ")[1]));
                            if (town == null) return;

                            if (!player.getInventory().contains(currentItem))
                                if (event.isLeftClick()) {
                                    assert currentItem != null;
                                    town.buyResourceFromStorage(currentItem, player);
                                    NPCTraderWindow.generateWindow(
                                        player, CitizensAPI.getNPCRegistry().getById(Integer.parseInt(title.split(" ")[2]))
                                    );
                                } else {
                                    town.generateLocalPrices();
                                    town.sellResource2Storage(player.getInventory().getItemInMainHand(), player);
                                }
                        })
                ), "Магазин", MenuSizes.SixLines, "\u10D5"
            )
        )
    );

    public static void generateWindow(Player player, NPC trader) {
        var town = GorodkiUniverse.getInstance().getNPCGorod(TownyAPI.getInstance().getTown(trader.getStoredLocation()));
        if (town == null) return;

        var area = new ItemArea(new Margin(0, 0, 4, 8), town.getStorage(), null);
        NpcTradeWindow.getPanel("Магазин").getViewWith(
                player,
                Objects.requireNonNull(TownyAPI.getInstance().getTown(town.getUUID())).getName() + " " + trader.getId(),
                List.of(area)
        );
    }
}
