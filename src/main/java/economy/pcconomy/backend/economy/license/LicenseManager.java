package economy.pcconomy.backend.economy.license;

import org.bukkit.entity.Player;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.db.Loadable;
import economy.pcconomy.backend.economy.license.objects.License;
import economy.pcconomy.backend.economy.license.objects.LicenseType;

import lombok.Getter;
import lombok.experimental.ExtensionMethod;

import org.j1sk1ss.itemmanager.manager.Item;
import org.j1sk1ss.itemmanager.manager.Manager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@ExtensionMethod({Manager.class, Cash.class})
public class LicenseManager extends Loadable {
    @Getter private static final double marketLicensePrice      = PcConomy.Config.getDouble("license.market_license_price", 2400d);
    @Getter private static final double tradeLicensePrice       = PcConomy.Config.getDouble("license.trade_license_price", 650d);
    @Getter private static final double loanLicensePrice        = PcConomy.Config.getDouble("license.loan_license_price", 3500d);
    @Getter private static final double loanHistoryLicensePrice = PcConomy.Config.getDouble("license.loan_history_license_price", 1200d);

    private static final Map<LicenseType, String> licenseTypes = Map.of(
        LicenseType.Trade, "Лицензия на ведение торговой деятельности",
        LicenseType.Market, "Лицензия на создание торговой зоны",
        LicenseType.Loan, "Лицензия на ведение кредитной деятельности",
        LicenseType.LoanHistory, "Лицензия на доступ к кредитной истории"
    );

    private final List<License> Licenses = new ArrayList<>();

    /**
     * Creates new license
     * @param license License body
     */
    public void createLicense(License license) {
        Licenses.add(license);
    }

    /**
     * Gets license body of player with specified type
     * @param player UUID of player
     * @param licenseType Specified license type
     * @return License body
     */
    public License getLicense(UUID player, LicenseType licenseType) {
        for (var lic : Licenses) if (lic.getOwner().equals(player)) if (lic.getLicenseType().equals(licenseType)) return lic;
        return null;
    }

    /**
     * Gives license to player
     * @param player Player that will take license
     * @param licenseType License type
     * @param price Price of license
     */
    public static void giveLicenseToPlayer(Player player, LicenseType licenseType, double price) {
        if (player.amountOfCashInInventory(false) < price) return;

        if (PcConomy.GlobalLicense.getLicense(player.getUniqueId(), licenseType) != null)
            PcConomy.GlobalLicense.Licenses.remove(PcConomy.GlobalLicense.getLicense(player.getUniqueId(), licenseType));

        player.takeCashFromPlayer(price, false);
        PcConomy.GlobalBank.getBank().changeBudget(price);

        PcConomy.GlobalLicense.createLicense(new License(player, LocalDateTime.now().plusDays(1), licenseType));
        new Item("Лицензия", licenseTypes.get(licenseType) + "\nВыдана: " + player.getName()).giveItems(player); //TODO: DATA MODEL
    }

    @Override
    public String getName() {
        return "license_data";
    }
}
