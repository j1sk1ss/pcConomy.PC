package economy.pcconomy.frontend.ui.windows.license;

import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.license.License;
import economy.pcconomy.frontend.ui.objects.Panel;
import economy.pcconomy.frontend.ui.objects.interactive.Button;
import economy.pcconomy.frontend.ui.windows.IWindow;

import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;

public class LicensorWindow implements IWindow {
    public static economy.pcconomy.frontend.ui.objects.Panel Panel = new Panel(Arrays.asList(
            new Button(Arrays.asList(
                    0, 1, 9, 10, 18, 19
            ), "Лицензия на создание торговой зоны", License.marketLicensePrice + CashManager.currencySigh),
            new Button(Arrays.asList(
                    2, 3, 11, 12, 20, 21
            ), "Лицензия на торговую деятельность", License.tradeLicensePrice + CashManager.currencySigh),
            new Button(Arrays.asList(
                    5, 6, 14, 15, 23, 24
            ), "Лицензия на кредитную деятельность", License.loanLicensePrice + CashManager.currencySigh),
            new Button(Arrays.asList(
                    7, 8, 16, 17, 25, 26
            ), "Лицензия на доступ к кредитной истории", License.loanHistoryLicensePrice + CashManager.currencySigh)
    ));

    public Inventory generateWindow(Player player) {
        return Panel.placeComponents(Bukkit.createInventory(player, 27, Component.text("Лицензии")));
    }
}
