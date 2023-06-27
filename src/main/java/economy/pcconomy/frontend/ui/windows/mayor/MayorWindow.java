package economy.pcconomy.frontend.ui.windows.mayor;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.cash.scripts.CashManager;
import economy.pcconomy.backend.npc.NPC;
import economy.pcconomy.backend.scripts.ItemManager;

import economy.pcconomy.frontend.ui.windows.IWindow;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MayorWindow implements IWindow {
    public Inventory generateWindow(Player mayor) {
        var window = Bukkit.createInventory(mayor, 27, Component.text("Меню"));

        window.setItem(0, ItemManager.setName(ItemManager.setLore(new ItemStack(Material.PURPLE_WOOL),
                (NPC.traderCost + NPC.traderCost * PcConomy.GlobalBank.VAT) + CashManager.currencySigh),
                "Установить торговца"));
        window.setItem(1, ItemManager.setName(ItemManager.setLore(new ItemStack(Material.PURPLE_WOOL),
                (NPC.loanerCost + NPC.loanerCost * PcConomy.GlobalBank.VAT) + CashManager.currencySigh),
                "Установить кредитора"));

        return window;
    }

}
