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
    private static final MenuWindow LicenseWindow = new MenuWindow(List.of(new Panel(Arrays.asList(
        new Button(new Margin(0, 0, 2, 1), "Лицензия на создание торговой зоны",LicenseManager.getMarketLicensePrice() + Cash.currencySigh,
            (event, menu) -> LicenseManager.giveLicenseToPlayer((Player)event.getWhoClicked(),
                    LicenseType.Market, LicenseManager.getMarketLicensePrice()), Material.GOLD_INGOT, 7000),

        new Button(new Margin(0, 2, 2, 1), "Лицензия на торговую деятельность", LicenseManager.getTradeLicensePrice() + Cash.currencySigh,
            (event, menu) -> LicenseManager.giveLicenseToPlayer((Player)event.getWhoClicked(),
                    LicenseType.Trade, LicenseManager.getTradeLicensePrice()), Material.GOLD_INGOT, 7000),

        new Button(new Margin(0, 5, 2, 1), "Лицензия на кредитную деятельность", LicenseManager.getLoanLicensePrice() + Cash.currencySigh,
            (event, menu) -> LicenseManager.giveLicenseToPlayer((Player)event.getWhoClicked(),
                    LicenseType.Loan, LicenseManager.getLoanLicensePrice()), Material.GOLD_INGOT, 7000),

        new Button(new Margin(0, 7, 2, 1), "Лицензия на доступ к кредитной истории", LicenseManager.getLoanHistoryLicensePrice() + Cash.currencySigh,
            (event, menu) -> LicenseManager.giveLicenseToPlayer((Player)event.getWhoClicked(),
                    LicenseType.LoanHistory, LicenseManager.getLoanHistoryLicensePrice()), Material.GOLD_INGOT, 7000)
    ), "Мир-Лицензии", MenuSizes.ThreeLines, "\u10D1")), "License");

    public static void generateWindow(Player player) {
        LicenseWindow.getPanel("Мир-Лицензии").getView(player);
    }
}
