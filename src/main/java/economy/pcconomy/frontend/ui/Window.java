package economy.pcconomy.frontend.ui;

import economy.pcconomy.frontend.ui.windows.BankerWindow;
import economy.pcconomy.frontend.ui.windows.LoanWindow;
import org.bukkit.entity.Player;

public class Window {

    public static void OpenBankerWindow(Player player) {
        player.openInventory(BankerWindow.GetBankerWindow(player));
    }

    public static void OpenLoanWindow(Player player) {
        player.openInventory(LoanWindow.GetLoanWindow(player));
    }
}
