package economy.pcconomy.ui;

import economy.pcconomy.ui.windows.BankerWindow;
import economy.pcconomy.ui.windows.LoanWindow;
import org.bukkit.entity.Player;

public class Window {

    public static void OpenBankerWindow(Player player) {
        player.openInventory(BankerWindow.GetBankerWindow(player));
    }

    public static void OpenLoanWindow(Player player) {
        player.openInventory(LoanWindow.GetLoanWindow(player));
    }

}
