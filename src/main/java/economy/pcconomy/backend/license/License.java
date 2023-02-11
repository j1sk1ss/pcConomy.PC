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

    public static void GetMarketLicense(Player player) {
        var cash = new Cash();
        if (cash.AmountOfCashInInventory(player) < marketLicensePrice) return;

        if (PcConomy.GlobalLicenseWorker.GetLicense(player, LicenseType.Market) != null)
            PcConomy.GlobalLicenseWorker.Licenses.remove(PcConomy.GlobalLicenseWorker.GetLicense(player));

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
            PcConomy.GlobalLicenseWorker.Licenses.remove(PcConomy.GlobalLicenseWorker.GetLicense(player));

        cash.TakeCashFromInventory(tradeLicensePrice, player);
        PcConomy.GlobalBank.BankBudget += tradeLicensePrice;

        PcConomy.GlobalLicenseWorker.CreateLicense(new LicenseBody(player, LocalDateTime.now().plusDays(1), LicenseType.Trade));
        ItemWorker.giveItems(ItemWorker.SetName(ItemWorker.SetLore(new ItemStack(Material.PAPER),
                "Лицензия на ведение торговой деятельности\nВыдана: " + player.getName()), "Лицензия"), player);
    }

}
