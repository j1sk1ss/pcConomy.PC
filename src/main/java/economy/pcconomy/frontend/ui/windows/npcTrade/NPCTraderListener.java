package economy.pcconomy.frontend.ui.windows.npcTrade;

import com.palmergames.bukkit.towny.TownyAPI;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.town.Town;

import economy.pcconomy.frontend.ui.Window;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Objects;

public class NPCTraderListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        var player = (Player) event.getWhoClicked();
        var currentItem = event.getCurrentItem();

        if (Window.isThisWindow(event, player, "Магазин")) {
            var town = TownyAPI.getInstance().getTown(event.getView().getTitle().split(" ")[1]);

            if (!player.getInventory().contains(currentItem)) {
                if (event.isLeftClick()) {
                    Town.BuyResourceFromStorage(PcConomy.GlobalTownWorker.GetTownObject(town.getName()),
                            currentItem, player);
                    player.openInventory(Objects.requireNonNull(NPCTraderWindow.GetWindow(player,
                            CitizensAPI.getNPCRegistry().getById(Integer
                                    .parseInt(event.getView().getTitle().split(" ")[2])))));
                }
            }

            event.setCancelled(true);
        }
    }
}


