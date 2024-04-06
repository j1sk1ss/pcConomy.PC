package economy.pcconomy.frontend.ui.windows.license;

import economy.pcconomy.backend.license.LicenseManager;
import economy.pcconomy.backend.license.objects.LicenseType;

import economy.pcconomy.frontend.ui.windows.IWindowListener;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;


public class LicensorListener implements IWindowListener {
    public void onClick(InventoryClickEvent event) {
        var player = (Player) event.getWhoClicked();
        switch (LicensorWindow.Panel.click(event.getSlot()).getName()) {
            case "Лицензия на создание торговой зоны" -> LicenseManager.giveLicenseToPlayer(player, LicenseType.Market, LicenseManager.marketLicensePrice);
            case "Лицензия на торговую деятельность" -> LicenseManager.giveLicenseToPlayer(player, LicenseType.Trade, LicenseManager.tradeLicensePrice);
            case "Лицензия на кредитную деятельность" -> LicenseManager.giveLicenseToPlayer(player, LicenseType.Loan, LicenseManager.loanLicensePrice);
            case "Лицензия на доступ к кредитной истории" -> LicenseManager.giveLicenseToPlayer(player, LicenseType.LoanHistory, LicenseManager.loanHistoryLicensePrice);
        }
    }
}
