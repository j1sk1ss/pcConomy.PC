package economy.pcconomy.frontend.ui.windows.mayor;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.npc.NpcManager;
import economy.pcconomy.backend.scripts.ItemManager;

import economy.pcconomy.frontend.ui.objects.Panel;
import economy.pcconomy.frontend.ui.objects.interactive.Button;
import economy.pcconomy.frontend.ui.windows.IWindow;

import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class MayorWindow implements IWindow {
    public static economy.pcconomy.frontend.ui.objects.Panel Panel = new Panel(Arrays.asList(
            new Button(Arrays.asList(
                // TODO: cords
            ), "Установить торговца"),
            new Button(Arrays.asList(

            ), "Установить кредитора")
    ));

    public Inventory generateWindow(Player mayor) {
        var window = Bukkit.createInventory(mayor, 27, Component.text("Меню"));

        window.setItem(0, ItemManager.setName(ItemManager.setLore(new ItemStack(Material.PURPLE_WOOL),
                (NpcManager.traderCost + NpcManager.traderCost * PcConomy.GlobalBank.VAT) + CashManager.currencySigh),
                "Установить торговца"));
        window.setItem(1, ItemManager.setName(ItemManager.setLore(new ItemStack(Material.PURPLE_WOOL),
                (NpcManager.loanerCost + NpcManager.loanerCost * PcConomy.GlobalBank.VAT) + CashManager.currencySigh),
                "Установить кредитора"));

        return window;
    }

}
