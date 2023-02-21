package economy.pcconomy.frontend.ui;

import economy.pcconomy.frontend.ui.windows.bank.BankerWindow;
import economy.pcconomy.frontend.ui.windows.license.LicensorWindow;
import economy.pcconomy.frontend.ui.windows.loan.LoanWindow;
import economy.pcconomy.frontend.ui.windows.mayor.MayorWindow;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.w3c.dom.events.Event;

public class Window {
    public static void OpenBankerWindow(Player player) {
        player.openInventory(BankerWindow.GetBankerWindow(player));
    }

    public static void OpenLoanWindow(Player player, boolean isNPC) {
        player.openInventory(LoanWindow.GetLoanWindow(player, isNPC));
    }

    public static void OpenLicenseWindow(Player player) {player.openInventory(LicensorWindow.GetLicensorWindow(player));}

    public static void OpenMayorWindow(Player player) {player.openInventory(MayorWindow.GetMayorWindow(player));}

    public static boolean isThisWindow(InventoryClickEvent event, Player player, String windowName){
        if (event.getCurrentItem() != null)
            if (event.getInventory().getHolder() instanceof Player player1)
                return event.getView().getTitle().equals(windowName) && player1.equals(player);

        return false;
    }

    public static boolean isThisWindow(InventoryClickEvent event, Player player){
        if (event.getCurrentItem() != null)
            if (event.getInventory().getHolder() instanceof Player player1)
                return player1.equals(player);

        return false;
    }
}
