package economy.pcconomy.bank.ui;

import economy.pcconomy.bank.ui.windows.BankerWindow;
import org.bukkit.entity.Player;

public class Window {

    public static void OpenBankerWindow(Player player) {
        // Открыть окно банкира
        player.openInventory(BankerWindow.GetBankerWindow(player));
    }

    public static void OpenLoanWindow(Player player) {

    }

}
