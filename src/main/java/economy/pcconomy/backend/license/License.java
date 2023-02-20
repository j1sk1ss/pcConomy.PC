package economy.pcconomy.backend.license;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.license.objects.LicenseBody;
import economy.pcconomy.backend.license.objects.LicenseType;
import economy.pcconomy.backend.license.scripts.LicenseWorker;
import economy.pcconomy.backend.scripts.ItemWorker;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;

public class License {
    public final static double marketLicensePrice = 1200d;
    public final static double tradeLicensePrice = 500d;
    public final static double loanLicensePrice = 3200d;
    public final static double loanHistoryLicensePrice = 1600d;

    public static void GetMarketLicense(Player player) {
        var cash = new Cash();
        if (cash.AmountOfCashInInventory(player) < marketLicensePrice) return;

        if (PcConomy.GlobalLicenseWorker.GetLicense(player, LicenseType.Market) != null)
            PcConomy.GlobalLicenseWorker.Licenses.remove(PcConomy.GlobalLicenseWorker.GetLicense(player, LicenseType.Market));

        cash.TakeCashFromInventory(marketLicensePrice, player);
        PcConomy.GlobalBank.BankBudget += marketLicensePrice;

        PcConomy.GlobalLicenseWorker.CreateLicense(new LicenseBody(player, LocalDateTime.now().plusDays(1), LicenseType.Market));
        ItemWorker.giveItems(ItemWorker.SetName(ItemWorker.SetLore(new ItemStack(Material.PAPER),
                "Лицензия на создание торговой зоны\nВыдана: " + player.getName()), "Лицензия"), player);
    }

    public static void GetTradeLicense(Player player) {
        var cash = new Cash();
        if (cash.AmountOfCashInInventory(player) < tradeLicensePrice) return;

        if (PcConomy.GlobalLicenseWorker.GetLicense(player, LicenseType.Trade) != null)
            PcConomy.GlobalLicenseWorker.Licenses.remove(PcConomy.GlobalLicenseWorker.GetLicense(player, LicenseType.Trade));

        cash.TakeCashFromInventory(tradeLicensePrice, player);
        PcConomy.GlobalBank.BankBudget += tradeLicensePrice;

        PcConomy.GlobalLicenseWorker.CreateLicense(new LicenseBody(player, LocalDateTime.now().plusDays(1), LicenseType.Trade));
        ItemWorker.giveItems(ItemWorker.SetName(ItemWorker.SetLore(new ItemStack(Material.PAPER),
                "Лицензия на ведение торговой деятельности\nВыдана: " + player.getName()), "Лицензия"), player);
    }

    public static void GetLoanLicense(Player player) {
        var cash = new Cash();
        if (cash.AmountOfCashInInventory(player) < loanLicensePrice) return;

        if (PcConomy.GlobalLicenseWorker.GetLicense(player, LicenseType.Loan) != null)
            PcConomy.GlobalLicenseWorker.Licenses.remove(PcConomy.GlobalLicenseWorker.GetLicense(player, LicenseType.Loan));

        cash.TakeCashFromInventory(loanLicensePrice, player);
        PcConomy.GlobalBank.BankBudget += loanLicensePrice;

        PcConomy.GlobalLicenseWorker.CreateLicense(new LicenseBody(player, LocalDateTime.now().plusDays(1), LicenseType.Loan));
        ItemWorker.giveItems(ItemWorker.SetName(ItemWorker.SetLore(new ItemStack(Material.PAPER),
                "Лицензия на ведение кредитной деятельности\nВыдана: " + player.getName()), "Лицензия"), player);
    }

    public static void GetLoanHistoryLicense(Player player) {
        var cash = new Cash();
        if (cash.AmountOfCashInInventory(player) < loanHistoryLicensePrice) return;

        if (PcConomy.GlobalLicenseWorker.GetLicense(player, LicenseType.LoanHistory) != null)
            PcConomy.GlobalLicenseWorker.Licenses.remove(PcConomy.GlobalLicenseWorker.GetLicense(player, LicenseType.LoanHistory));

        cash.TakeCashFromInventory(loanHistoryLicensePrice, player);
        PcConomy.GlobalBank.BankBudget += loanHistoryLicensePrice;

        PcConomy.GlobalLicenseWorker.CreateLicense(new LicenseBody(player, LocalDateTime.now().plusDays(1), LicenseType.LoanHistory));
        ItemWorker.giveItems(ItemWorker.SetName(ItemWorker.SetLore(new ItemStack(Material.PAPER),
                "Лицензия на доступ к кредитной истории\nВыдана: " + player.getName()), "Лицензия"), player);
    }
}
