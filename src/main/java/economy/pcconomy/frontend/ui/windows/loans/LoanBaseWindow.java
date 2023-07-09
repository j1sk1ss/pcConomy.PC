package economy.pcconomy.frontend.ui.windows.loans;

import com.palmergames.bukkit.towny.TownyAPI;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.license.objects.LicenseType;
import economy.pcconomy.backend.scripts.items.ItemManager;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public abstract class LoanBaseWindow {
    public abstract Inventory regenerateWindow(Inventory window, Player player, int option);

    /***
     * Checks license of reading credit history
     * @param player Player who wants to take credit
     * @return Status
     */
    protected static boolean canReadHistory(Player player) {
        var town = TownyAPI.getInstance().getTown(player.getLocation());
        var licenseHistory = PcConomy.GlobalLicenseManager
                .getLicense(Objects.requireNonNull(town).getMayor().getUUID(), LicenseType.LoanHistory);
        if (licenseHistory == null) return false;

        return !licenseHistory.isOverdue();
    }

    public abstract ItemStack creditOptionButton(ItemStack itemStack, double maxLoanSize, int chosen, int position);

    /***
     * Get selected duration from window
     * @param window Window
     * @return Duration
     */
    public static int getSelectedDuration(Inventory window) {
        for (ItemStack button : window) {
            if (button == null) return 20;
            if (ItemManager.getMaterial(button).equals(Material.PURPLE_WOOL)) {
                System.out.println(Integer.parseInt(ItemManager.getName(button).replace("дней", "")));
                return Integer.parseInt(ItemManager.getName(button).replace("дней", ""));
            }
        }

        return 20;
    }

    /***
     * Get selected credit size from window
     * @param window Window
     * @return Credit size
     */
    public static double getSelectedAmount(Inventory window) {
        for (ItemStack button : window) {
            if (button == null) return 0;
            if (ItemManager.getMaterial(button).equals(Material.LIGHT_BLUE_WOOL))
                return Double.parseDouble(ItemManager.getName(button).replace(CashManager.currencySigh, ""));
        }

        return 0;
    }
}
