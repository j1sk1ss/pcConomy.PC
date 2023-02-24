package economy.pcconomy.backend.license;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.license.objects.LicenseBody;
import economy.pcconomy.backend.license.objects.LicenseType;
import economy.pcconomy.backend.scripts.ItemWorker;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.util.Map;

public class License {
    public final static double marketLicensePrice = PcConomy.Config.getDouble("license.market_license_price");
    public final static double tradeLicensePrice = PcConomy.Config.getDouble("license.trade_license_price");
    public final static double loanLicensePrice = PcConomy.Config.getDouble("license.loan_license_price");
    public final static double loanHistoryLicensePrice = PcConomy.Config.getDouble("license.loan_history_license_price");

    private static final Map<LicenseType, String> licenseTypes = Map.of(
            LicenseType.Trade, "Лицензия на ведение торговой деятельности",
            LicenseType.Market, "Лицензия на создание торговой зоны",
            LicenseType.Loan, "Лицензия на ведение кредитной деятельности",
            LicenseType.LoanHistory, "Лицензия на доступ к кредитной истории"
    );

    public static void GetLicense(Player player, LicenseType licenseType, double price) {
        var cash = new Cash();
        if (cash.AmountOfCashInInventory(player) < price) return;

        if (PcConomy.GlobalLicenseWorker.GetLicense(player.getUniqueId(), licenseType) != null)
            PcConomy.GlobalLicenseWorker.Licenses.remove(PcConomy.GlobalLicenseWorker.GetLicense(player.getUniqueId(), licenseType));

        cash.TakeCashFromInventory(price, player);
        PcConomy.GlobalBank.BankBudget += price;

        PcConomy.GlobalLicenseWorker.CreateLicense(new LicenseBody(player, LocalDateTime.now().plusDays(1), licenseType));
        ItemWorker.GiveItems(ItemWorker.SetName(ItemWorker.SetLore(new ItemStack(Material.PAPER),
                licenseTypes.get(licenseType) + "\nВыдана: " + player.getName()), "Лицензия"), player);
    }
}
