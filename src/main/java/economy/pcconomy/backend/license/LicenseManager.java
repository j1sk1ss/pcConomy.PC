package economy.pcconomy.backend.license;

import com.google.gson.*;
import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.license.objects.LicenseBody;
import economy.pcconomy.backend.license.objects.LicenseType;

import org.bukkit.entity.Player;
import org.j1sk1ss.itemmanager.manager.Item;
import org.j1sk1ss.itemmanager.manager.Manager;

import lombok.experimental.ExtensionMethod;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static economy.pcconomy.backend.cash.CashManager.amountOfCashInInventory;
import static economy.pcconomy.backend.cash.CashManager.takeCashFromPlayer;


@ExtensionMethod({Manager.class})
public class LicenseManager {
    public final static double marketLicensePrice      = PcConomy.Config.getDouble("license.market_license_price", 2400d);
    public final static double tradeLicensePrice       = PcConomy.Config.getDouble("license.trade_license_price", 650d);
    public final static double loanLicensePrice        = PcConomy.Config.getDouble("license.loan_license_price", 3500d);
    public final static double loanHistoryLicensePrice = PcConomy.Config.getDouble("license.loan_history_license_price", 1200d);

    private static final Map<LicenseType, String> licenseTypes = Map.of(
            LicenseType.Trade, "Лицензия на ведение торговой деятельности",
            LicenseType.Market, "Лицензия на создание торговой зоны",
            LicenseType.Loan, "Лицензия на ведение кредитной деятельности",
            LicenseType.LoanHistory, "Лицензия на доступ к кредитной истории"
    );

    public final List<LicenseBody> Licenses = new ArrayList<>();

    /**
     * Creates new license
     * @param licenseBody License body
     */
    public void createLicense(LicenseBody licenseBody) {
        Licenses.add(licenseBody);
    }

    /**
     * Gets license of player
     * @param player Player that should be checked
     * @return License body
     */
    public List<LicenseBody> getLicenses(Player player) {
        var list = new ArrayList<LicenseBody>();
        for (var lic : Licenses) if (lic.Owner.equals(player.getUniqueId())) list.add(lic);
        return list;
    }

    /**
     * Gets license body of player with specified type
     * @param player UUID of player
     * @param licenseType Specified license type
     * @return License body
     */
    public LicenseBody getLicense(UUID player, LicenseType licenseType) {
        for (var lic : Licenses) if (lic.Owner.equals(player)) if (lic.LicenseType.equals(licenseType)) return lic;
        return null;
    }

    /**
     * Gives license to player
     * @param player Player that will take license
     * @param licenseType License type
     * @param price Price of license
     */
    public static void giveLicenseToPlayer(Player player, LicenseType licenseType, double price) {
        if (amountOfCashInInventory(player, false) < price) return;

        if (PcConomy.GlobalLicenseManager.getLicense(player.getUniqueId(), licenseType) != null)
            PcConomy.GlobalLicenseManager.Licenses.remove(PcConomy.GlobalLicenseManager.getLicense(player.getUniqueId(), licenseType));

        takeCashFromPlayer(price, player, false);
        PcConomy.GlobalBank.BankBudget += price;
        //TODO: DATA MODEL
        PcConomy.GlobalLicenseManager.createLicense(new LicenseBody(player, LocalDateTime.now().plusDays(1), licenseType));
        new Item("Лицензия", licenseTypes.get(licenseType) + "\nВыдана: " + player.getName()).giveItems(player);
    }

    /**
     * Saves license
     * @param fileName File name
     * @throws IOException If something goes wrong
     */
    public void saveLicenses(String fileName) throws IOException {
        var writer = new FileWriter(fileName + ".json", false);
        new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create()
                .toJson(this, writer);
        writer.close();
    }
}
