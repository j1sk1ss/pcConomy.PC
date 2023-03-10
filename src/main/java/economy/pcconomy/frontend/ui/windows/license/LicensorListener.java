package economy.pcconomy.frontend.ui.windows.license;

import economy.pcconomy.backend.license.License;
import economy.pcconomy.backend.license.objects.LicenseType;
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
                        License.GetLicense(player, LicenseType.Market, License.marketLicensePrice);
                case "Лицензия на торговую деятельность" ->
                        License.GetLicense(player, LicenseType.Trade, License.tradeLicensePrice);
                case "Лицензия на кредитную деятельность" ->
                        License.GetLicense(player, LicenseType.Loan, License.loanLicensePrice);
                case "Лицензия на доступ к кредитной истории" ->
                        License.GetLicense(player, LicenseType.LoanHistory, License.loanHistoryLicensePrice);
            }

            event.setCancelled(true);

        }
    }

}
