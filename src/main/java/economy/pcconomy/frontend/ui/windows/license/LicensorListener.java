package economy.pcconomy.frontend.ui.windows.license;

import economy.pcconomy.backend.license.License;
import economy.pcconomy.backend.scripts.ItemWorker;
import economy.pcconomy.frontend.ui.Window;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class LicensorListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        var player = (Player) event.getWhoClicked();
        var option = event.getCurrentItem();

        if (Window.isThisWindow(event, player, "Лицензии") && option != null) {
            switch (ItemWorker.GetName(option)) {
                case "Лицензия на создание т. зоны" ->
                        License.GetMarketLicense(player);
                case "Лицензия на торговую деятельность" ->
                        License.GetTradeLicense(player);
                case "Лицензия на кредитную деятельность" ->
                        License.GetLoanLicense(player);
                case "Лицензия на доступ к кредитной истории" ->
                        License.GetLoanHistoryLicense(player);
            }

            event.setCancelled(true);

        }
    }

}
