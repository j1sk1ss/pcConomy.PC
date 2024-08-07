package economy.pcconomy.frontend;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.economy.license.LicenseManager;
import economy.pcconomy.backend.economy.license.objects.LicenseType;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import org.j1sk1ss.menuframework.common.LocalizationManager;
import org.j1sk1ss.menuframework.objects.MenuSizes;
import org.j1sk1ss.menuframework.objects.MenuWindow;
import org.j1sk1ss.menuframework.objects.interactive.components.Button;
import org.j1sk1ss.menuframework.objects.interactive.components.Panel;
import org.j1sk1ss.menuframework.objects.nonInteractive.Margin;

import java.util.Arrays;
import java.util.List;


public class LicensorWindow {
    public static final MenuWindow LicenseWindow = new MenuWindow(List.of(new Panel(Arrays.asList(
        new Button(new Margin(0, 0, 2, 1), "Лицензия на создание торговой зоны",LicenseManager.marketLicensePrice + Cash.currencySigh,
            (event, menu) -> LicenseManager.giveLicenseToPlayer((Player)event.getWhoClicked(),
                    LicenseType.Market, LicenseManager.marketLicensePrice), Material.GOLD_INGOT, 7000),

        new Button(new Margin(0, 2, 2, 1), "Лицензия на торговую деятельность", LicenseManager.tradeLicensePrice + Cash.currencySigh,
            (event, menu) -> LicenseManager.giveLicenseToPlayer((Player)event.getWhoClicked(),
                    LicenseType.Trade, LicenseManager.tradeLicensePrice), Material.GOLD_INGOT, 7000),

        new Button(new Margin(0, 5, 2, 1), "Лицензия на кредитную деятельность", LicenseManager.loanLicensePrice + Cash.currencySigh,
            (event, menu) -> LicenseManager.giveLicenseToPlayer((Player)event.getWhoClicked(),
                    LicenseType.Loan, LicenseManager.loanLicensePrice), Material.GOLD_INGOT, 7000),

        new Button(new Margin(0, 7, 2, 1), "Лицензия на доступ к кредитной истории", LicenseManager.loanHistoryLicensePrice + Cash.currencySigh,
            (event, menu) -> LicenseManager.giveLicenseToPlayer((Player)event.getWhoClicked(),
                    LicenseType.LoanHistory, LicenseManager.loanHistoryLicensePrice), Material.GOLD_INGOT, 7000)
    ), "Мир-Лицензии", MenuSizes.ThreeLines, "\u10D1")), "License",
            new LocalizationManager(PcConomy.Config.getString("ui.loc4lic")));

    public static void generateWindow(Player player) {
        LicenseWindow.getPanel("Мир-Лицензии", PcConomy.Config.getString("ui.language", "RU")).getView(player);
    }
}
