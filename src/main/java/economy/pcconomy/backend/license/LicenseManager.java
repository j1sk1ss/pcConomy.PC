package economy.pcconomy.backend.license;

import com.google.gson.*;
import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.license.objects.License;
import economy.pcconomy.backend.license.objects.LicenseType;

import org.bukkit.entity.Player;
import org.j1sk1ss.itemmanager.manager.Item;
import org.j1sk1ss.itemmanager.manager.Manager;

import lombok.experimental.ExtensionMethod;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@ExtensionMethod({Manager.class, CashManager.class})
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

    public final List<License> Licenses = new ArrayList<>();

    /**
     * Creates new license
     * @param license License body
     */
    public void createLicense(License license) {
        Licenses.add(license);
    }

    /**
     * Gets license of player
     * @param player Player that should be checked
     * @return License body
     */
    public List<License> getLicenses(Player player) {
        var list = new ArrayList<License>();
        for (var lic : Licenses) if (lic.Owner.equals(player.getUniqueId())) list.add(lic);
        return list;
    }

    /**
     * Gets license body of player with specified type
     * @param player UUID of player
     * @param licenseType Specified license type
     * @return License body
     */
    public License getLicense(UUID player, LicenseType licenseType) {
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
        if (player.amountOfCashInInventory(false) < price) return;

        if (PcConomy.GlobalLicenseManager.getLicense(player.getUniqueId(), licenseType) != null)
            PcConomy.GlobalLicenseManager.Licenses.remove(PcConomy.GlobalLicenseManager.getLicense(player.getUniqueId(), licenseType));

        player.takeCashFromPlayer(price, false);
        PcConomy.GlobalBank.BankBudget += price;
        //TODO: DATA MODEL
        PcConomy.GlobalLicenseManager.createLicense(new License(player, LocalDateTime.now().plusDays(1), licenseType));
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

    /**
     * Loads license data from .json
     * @param fileName File name (without format)
     * @return License manager object
     * @throws IOException If something goes wrong
     */
    public static LicenseManager loadLicenses(String fileName) throws IOException {
        return new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create()
                .fromJson(new String(Files.readAllBytes(Paths.get(fileName + ".json"))), LicenseManager.class);
    }
}
