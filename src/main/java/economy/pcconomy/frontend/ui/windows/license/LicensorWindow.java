package economy.pcconomy.frontend.ui.windows.license;

import economy.pcconomy.backend.cash.scripts.CashWorker;
import economy.pcconomy.backend.license.License;
import economy.pcconomy.backend.scripts.ItemWorker;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class LicensorWindow {

    public static Inventory GetLicensorWindow(Player player) {
        var window = Bukkit.createInventory(player, 27, "Лицензии");

        window.setItem(0, ItemWorker.SetName(ItemWorker.SetLore(new ItemStack(Material.PURPLE_WOOL),
                License.marketLicensePrice + CashWorker.currencySigh), "Лицензия на создание т. зоны"));
        window.setItem(1, ItemWorker.SetName(ItemWorker.SetLore(new ItemStack(Material.RED_WOOL),
                License.tradeLicensePrice + CashWorker.currencySigh), "Лицензия на торговую деятельность"));
        window.setItem(2, ItemWorker.SetName(ItemWorker.SetLore(new ItemStack(Material.RED_WOOL),
                License.loanLicensePrice + CashWorker.currencySigh), "Лицензия на кредитную деятельность"));
        window.setItem(3, ItemWorker.SetName(ItemWorker.SetLore(new ItemStack(Material.RED_WOOL),
                License.loanHistoryLicensePrice + CashWorker.currencySigh), "Лицензия на доступ к кредитной истории"));

        return window;
    }

}
