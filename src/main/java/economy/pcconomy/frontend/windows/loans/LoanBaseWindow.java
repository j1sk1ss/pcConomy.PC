package economy.pcconomy.frontend.windows.loans;

import com.palmergames.bukkit.towny.TownyAPI;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.license.objects.LicenseType;
import economy.pcconomy.frontend.windows.Window;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.j1sk1ss.itemmanager.manager.Manager;

import lombok.experimental.ExtensionMethod;
import java.util.Objects;


@ExtensionMethod({Manager.class})
public abstract class LoanBaseWindow extends Window {
    /**
     * Regenerate window with new info
     * @param window Past window
     * @param player Player
     * @param option New option
     * @return New window
     */
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

    /**
     * Credit option buttons placement
     * @param itemStack Chosen option
     * @param maxLoanSize Max loan size
     * @param chosen Chosen option
     * @param position Position of day
     * @return Button with status of credit
     */
    public abstract ItemStack creditOptionButton(ItemStack itemStack, double maxLoanSize, int chosen, int position);

    /***
     * Get selected duration from window
     * @param window Window
     * @return Duration
     */
    public static int getSelectedDuration(Inventory window) {
        for (ItemStack button : window) {
            if (button == null) return 20;
            if (button.getMaterial().equals(Material.PURPLE_WOOL)) {
                System.out.println(Integer.parseInt(button.getName().replace("дней", "")));
                return Integer.parseInt(button.getName().replace("дней", ""));
            }
        }

        return 20;
    }

    /***
     * Get selected credit size from window
     * @param button Pressed button
     * @return Credit size
     */
    public static double getSelectedAmount(ItemStack button) {
        return Double.parseDouble(button.getName().replace(CashManager.currencySigh, ""));
    }
}
