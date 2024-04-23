package economy.pcconomy.frontend.ui.windows.mayor;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.license.objects.LicenseType;
import economy.pcconomy.backend.npc.NpcManager;

import economy.pcconomy.frontend.ui.windows.Window;
import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.j1sk1ss.menuframework.objects.interactive.components.Button;
import org.j1sk1ss.menuframework.objects.interactive.components.Panel;

import java.util.Arrays;

public class MayorWindow extends Window {
    public static final org.j1sk1ss.menuframework.objects.interactive.components.Panel Panel = new Panel(Arrays.asList(
            new Button(0 ,21, "Установить торговца", (NpcManager.traderCost + NpcManager.traderCost * PcConomy.GlobalBank.VAT) + CashManager.currencySigh,
                    (event) -> PcConomy.GlobalNPC.buyNPC((Player)event.getWhoClicked(), LicenseType.Market, NpcManager.traderCost + NpcManager.traderCost * PcConomy.GlobalBank.VAT)),

            new Button(5, 26, "Установить кредитора", (NpcManager.loanerCost + NpcManager.loanerCost * PcConomy.GlobalBank.VAT) + CashManager.currencySigh,
                    (event) -> PcConomy.GlobalNPC.buyNPC((Player)event.getWhoClicked(), LicenseType.Loan,NpcManager.loanerCost + NpcManager.loanerCost * PcConomy.GlobalBank.VAT))
    ), "Panel");

    public Inventory generateWindow(Player mayor) {
        var window = Bukkit.createInventory(mayor, 27, Component.text("Город-Меню"));
        Panel.place(window);

        return window;
    }
}
