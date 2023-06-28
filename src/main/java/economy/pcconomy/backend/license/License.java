package economy.pcconomy.backend.license;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.license.objects.LicenseBody;
import economy.pcconomy.backend.license.objects.LicenseType;
import economy.pcconomy.backend.scripts.ItemManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.util.Map;

public class License {
    public final static double marketLicensePrice = PcConomy.Config.getDouble("license.market_license_price", 2400d);
    public final static double tradeLicensePrice = PcConomy.Config.getDouble("license.trade_license_price", 650d);
    public final static double loanLicensePrice = PcConomy.Config.getDouble("license.loan_license_price", 3500d);
    public final static double loanHistoryLicensePrice = PcConomy.Config.getDouble("license.loan_history_license_price", 1200d);

    private static final Map<LicenseType, String> licenseTypes = Map.of(
            LicenseType.Trade, "Лицензия на ведение торговой деятельности",
            LicenseType.Market, "Лицензия на создание торговой зоны",
            LicenseType.Loan, "Лицензия на ведение кредитной деятельности",
            LicenseType.LoanHistory, "Лицензия на доступ к кредитной истории"
    );

    /**
     * Gives license to player
     * @param player Player that will take license
     * @param licenseType License type
     * @param price Price of license
     */
    public static void getLicense(Player player, LicenseType licenseType, double price) {
        var cash = new CashManager();
        if (cash.amountOfCashInInventory(player) < price) return;

        if (PcConomy.GlobalLicenseWorker.getLicense(player.getUniqueId(), licenseType) != null)
            PcConomy.GlobalLicenseWorker.Licenses.remove(PcConomy.GlobalLicenseWorker.getLicense(player.getUniqueId(), licenseType));

        cash.takeCashFromInventory(price, player);
        PcConomy.GlobalBank.BankBudget += price;

        PcConomy.GlobalLicenseWorker.createLicense(new LicenseBody(player, LocalDateTime.now().plusDays(1), licenseType));
        ItemManager.giveItems(ItemManager.setName(ItemManager.setLore(new ItemStack(Material.PAPER),
                licenseTypes.get(licenseType) + "\nВыдана: " + player.getName()), "Лицензия"), player);
    }
}
