package economy.pcconomy.frontend.ui;

import economy.pcconomy.frontend.ui.windows.bank.BankerWindow;
import economy.pcconomy.frontend.ui.windows.license.LicensorWindow;
import economy.pcconomy.frontend.ui.windows.loan.LoanWindow;
import economy.pcconomy.frontend.ui.windows.mayor.MayorWindow;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class Window {
    public static void OpenBankerWindow(Player player) {
        player.openInventory(BankerWindow.GetWindow(player));
    }

    public static void OpenLoanWindow(Player player, boolean isNPC) {player.openInventory(LoanWindow.GetWindow(player, isNPC));}

    public static void OpenLicenseWindow(Player player) {player.openInventory(LicensorWindow.GetWindow(player));}

    public static void OpenMayorWindow(Player player) {player.openInventory(MayorWindow.GetWindow(player));}

    public static boolean isThisWindow(InventoryClickEvent event, Player player, String windowName){
        if (event.getCurrentItem() != null)
            if (event.getInventory().getHolder() instanceof Player player1)
                return ((TextComponent) event.getView().title()).content().equals(windowName) && player1.equals(player);

        return false;
    }

    public static boolean isThisWindow(InventoryClickEvent event, Player player){
        if (event.getCurrentItem() != null)
            if (event.getInventory().getHolder() instanceof Player player1)
                return player1.equals(player);

        return false;
    }
}
