package economy.pcconomy.frontend.ui;

import economy.pcconomy.frontend.ui.windows.bank.BankerWindow;
import economy.pcconomy.frontend.ui.windows.license.LicensorWindow;
import economy.pcconomy.frontend.ui.windows.loan.LoanWindow;
import economy.pcconomy.frontend.ui.windows.mayor.MayorWindow;
import org.bukkit.entity.Player;

public class Window {
    public static void OpenBankerWindow(Player player) {
        player.openInventory(BankerWindow.GetBankerWindow(player));
    }

    public static void OpenLoanWindow(Player player, boolean isNPC) {
        player.openInventory(LoanWindow.GetLoanWindow(player, isNPC));
    }

    public static void OpenLicenseWindow(Player player) {player.openInventory(LicensorWindow.GetLicensorWindow(player));}

    public static void OpenMayorWindow(Player player) {player.openInventory(MayorWindow.GetMayorWindow(player));}
}
