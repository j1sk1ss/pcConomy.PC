package economy.pcconomy.frontend.ui;

import economy.pcconomy.frontend.ui.windows.bank.BankerWindow;
import economy.pcconomy.frontend.ui.windows.license.LicensorWindow;
import economy.pcconomy.frontend.ui.windows.loan.LoanWindow;
import org.bukkit.entity.Player;

public class Window {

    public static void OpenBankerWindow(Player player) {
        player.openInventory(BankerWindow.GetBankerWindow(player));
    }

    public static void OpenLoanWindow(Player player) {
        player.openInventory(LoanWindow.GetLoanWindow(player));
    }

    public static void OpenLicenseWindow(Player player) {player.openInventory(LicensorWindow.GetLicensorWindow(player));}
}
