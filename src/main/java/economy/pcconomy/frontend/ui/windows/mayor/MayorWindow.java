package economy.pcconomy.frontend.ui.windows.mayor;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.npc.NpcManager;

import economy.pcconomy.frontend.ui.objects.Panel;
import economy.pcconomy.frontend.ui.objects.interactive.Button;
import economy.pcconomy.frontend.ui.windows.IWindow;

import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;

public class MayorWindow implements IWindow {
    public static economy.pcconomy.frontend.ui.objects.Panel Panel = new Panel(Arrays.asList(
            new Button(Arrays.asList(
                    0, 1, 2, 3, 9, 10, 11, 12, 18, 19, 20, 21
            ), "Установить торговца", (NpcManager.traderCost + NpcManager.traderCost * PcConomy.GlobalBank.VAT) + CashManager.currencySigh),
            new Button(Arrays.asList(
                    5, 6, 7, 8, 14, 15, 16, 17, 23, 24, 25, 26
            ), "Установить кредитора", (NpcManager.loanerCost + NpcManager.loanerCost * PcConomy.GlobalBank.VAT) + CashManager.currencySigh)
    ));

    public Inventory generateWindow(Player mayor) {
        return Panel.placeComponents(Bukkit.createInventory(mayor, 27, Component.text("Меню")));
    }

}
