package economy.pcconomy.frontend.ui.windows.mayor;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.npc.NpcManager;

import economy.pcconomy.frontend.ui.objects.Panel;
import economy.pcconomy.frontend.ui.objects.interactive.Button;

import economy.pcconomy.frontend.ui.windows.Window;
import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;

public class MayorWindow extends Window {
    public static final economy.pcconomy.frontend.ui.objects.Panel Panel = new Panel(Arrays.asList(
            new Button(0 ,21, "Установить торговца", (NpcManager.traderCost + NpcManager.traderCost * PcConomy.GlobalBank.VAT) + CashManager.currencySigh),
            new Button(5, 26, "Установить кредитора", (NpcManager.loanerCost + NpcManager.loanerCost * PcConomy.GlobalBank.VAT) + CashManager.currencySigh)
    ), "Panel");

    public Inventory generateWindow(Player mayor) {
        return Panel.placeComponents(Bukkit.createInventory(mayor, 27, Component.text("Меню")));
    }
}
