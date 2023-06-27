package economy.pcconomy.frontend.ui.windows.license;

import economy.pcconomy.backend.cash.scripts.CashManager;
import economy.pcconomy.backend.license.License;
import economy.pcconomy.backend.scripts.ItemManager;
import economy.pcconomy.frontend.ui.windows.IWindow;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class LicensorWindow implements IWindow {

    public Inventory generateWindow(Player player) {
        var window = Bukkit.createInventory(player, 27, Component.text("Лицензии"));

        window.setItem(0, ItemManager.setName(ItemManager.setLore(new ItemStack(Material.PURPLE_WOOL),
                License.marketLicensePrice + CashManager.currencySigh), "Лицензия на создание т. зоны"));
        window.setItem(1, ItemManager.setName(ItemManager.setLore(new ItemStack(Material.RED_WOOL),
                License.tradeLicensePrice + CashManager.currencySigh), "Лицензия на торговую деятельность"));
        window.setItem(2, ItemManager.setName(ItemManager.setLore(new ItemStack(Material.RED_WOOL),
                License.loanLicensePrice + CashManager.currencySigh), "Лицензия на кредитную деятельность"));
        window.setItem(3, ItemManager.setName(ItemManager.setLore(new ItemStack(Material.RED_WOOL),
                License.loanHistoryLicensePrice + CashManager.currencySigh), "Лицензия на доступ к кредитной истории"));

        return window;
    }

}
