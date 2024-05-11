package economy.pcconomy.frontend.license;

import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.economy.license.LicenseManager;
import economy.pcconomy.backend.economy.license.objects.LicenseType;

import org.bukkit.entity.Player;
import org.j1sk1ss.menuframework.objects.MenuSizes;
import org.j1sk1ss.menuframework.objects.interactive.components.Button;
import org.j1sk1ss.menuframework.objects.interactive.components.Panel;

import java.util.Arrays;


public class LicensorWindow {
    public static final org.j1sk1ss.menuframework.objects.interactive.components.Panel Panel = new Panel(Arrays.asList(
        new Button(0, 19, "Лицензия на создание торговой зоны", LicenseManager.marketLicensePrice + Cash.currencySigh,
            (event) -> LicenseManager.giveLicenseToPlayer((Player)event.getWhoClicked(), LicenseType.Market, LicenseManager.marketLicensePrice)),

        new Button(2, 21, "Лицензия на торговую деятельность", LicenseManager.tradeLicensePrice + Cash.currencySigh,
            (event) -> LicenseManager.giveLicenseToPlayer((Player)event.getWhoClicked(), LicenseType.Trade, LicenseManager.tradeLicensePrice)),

        new Button(5, 24, "Лицензия на кредитную деятельность", LicenseManager.loanLicensePrice + Cash.currencySigh,
            (event) -> LicenseManager.giveLicenseToPlayer((Player)event.getWhoClicked(), LicenseType.Loan, LicenseManager.loanLicensePrice)),

        new Button(7, 26, "Лицензия на доступ к кредитной истории", LicenseManager.loanHistoryLicensePrice + Cash.currencySigh,
            (event) -> LicenseManager.giveLicenseToPlayer((Player)event.getWhoClicked(), LicenseType.LoanHistory, LicenseManager.loanHistoryLicensePrice))
    ), "Мир-Лицензии", MenuSizes.ThreeLines);

    public static void generateWindow(Player player) {
        Panel.getView(player);
    }
}
